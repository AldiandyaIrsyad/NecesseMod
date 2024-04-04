package necesse.engine.network.server;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;
import necesse.engine.GameAuth;
import necesse.engine.GameCache;
import necesse.engine.GameCrashLog;
import necesse.engine.GameEvents;
import necesse.engine.ServerTickThread;
import necesse.engine.Settings;
import necesse.engine.commands.CommandsManager;
import necesse.engine.commands.ParsedCommand;
import necesse.engine.events.ServerClientConnectedEvent;
import necesse.engine.events.ServerClientDisconnectEvent;
import necesse.engine.events.ServerStopEvent;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.PacketManager;
import necesse.engine.network.client.Client;
import necesse.engine.network.networkInfo.NetworkInfo;
import necesse.engine.network.packet.PacketConnectApproved;
import necesse.engine.network.packet.PacketDisconnect;
import necesse.engine.network.server.network.ServerNetwork;
import necesse.engine.network.server.network.ServerOpenNetwork;
import necesse.engine.network.server.network.ServerSingleplayerNetwork;
import necesse.engine.registries.PacketRegistry;
import necesse.engine.save.SaveData;
import necesse.engine.save.WorldSave;
import necesse.engine.seasons.GameSeasons;
import necesse.engine.state.MainMenu;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.world.FileSystemClosedException;
import necesse.engine.world.World;
import necesse.engine.world.levelCache.LevelCache;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameColor;
import necesse.gfx.forms.components.ContinueComponent;
import necesse.gfx.forms.presets.ConfirmationContinueForm;
import necesse.gfx.forms.presets.NoticeForm;
import necesse.level.maps.Level;

public class Server {
   private static int autoSaveIntervalInSec = 60;
   private static int backupEachAutoSave = 15;
   public static int clientMoveTolerance = 30;
   private boolean isPaused;
   private ServerClient[] clients;
   private HashMap<Long, ServerClient> authClientMap = new HashMap();
   public ServerNetwork network;
   public final ServerSettings settings;
   public World world;
   public HashMap<Long, String> usedNames;
   public LevelCache levelCache;
   private boolean isStopped;
   private boolean stopCalled;
   private PacketDisconnect stopPacket;
   private List<Consumer<Server>> stopEvents = new LinkedList();
   public List<ContinueComponent> stopErrors;
   int playersOnline;
   private int pauseCounter;
   private long saveTime;
   private TickManager tickManager;
   public final PacketManager packetManager;
   public final CommandsManager commandsManager;
   public ServerTickThread serverThread;
   private Client client;
   private boolean isSinglePlayer;
   private boolean isHosted;
   private int autoSaves;

   public Server(ServerSettings var1) throws IOException, FileSystemClosedException {
      this.settings = var1;
      GameSeasons.loadSeasons();
      this.serverThread = new ServerTickThread(this, "Server Thread", 60);
      this.packetManager = this.newPacketManager();
      this.commandsManager = new CommandsManager(this);
      if (var1.port == -1) {
         this.network = new ServerSingleplayerNetwork(this);
      } else {
         this.network = new ServerOpenNetwork(this, var1.port);
      }

      this.clients = new ServerClient[var1.slots];
      this.world = new World(this);
   }

   public void start(ServerHostSettings var1) throws IOException {
      this.usedNames = this.world.getUsedPlayerNames();
      this.levelCache = new LevelCache(this.world);
      this.world.init();
      if (var1 != null) {
         var1.apply(this, false);
      }

      try {
         this.network.open();
      } catch (IOException var3) {
         this.stop((PacketDisconnect)PacketDisconnect.networkError(-1, var3.getMessage()), (Consumer)null);
         throw var3;
      }

      this.serverThread.start();
      this.startSaveTimer();
   }

   public void startHostFromSingleplayer(int var1, int var2, boolean var3, ServerSettings.SteamLobbyType var4, ServerHostSettings var5) {
      if (this.settings.port == -1 && this.client != null) {
         this.settings.slots = var1;
         this.settings.port = var2;
         this.settings.allowConnectByIP = var3;
         this.settings.steamLobbyType = var4;
         var5.apply(this, true);
         if (this.settings.port != -1) {
            if (this.settings.slots < this.clients.length) {
               throw new IllegalArgumentException("Cannot decrease slots");
            }

            this.clients = (ServerClient[])Arrays.copyOf(this.clients, this.settings.slots);
            this.client.startedHosting(this);
            this.network = new ServerOpenNetwork(this, this.settings.port);

            try {
               this.network.open();
            } catch (IOException var8) {
               this.stop((PacketDisconnect)PacketDisconnect.networkError(-1, var8.getMessage()), (Consumer)null);
               var8.printStackTrace();
            }

            this.resume();
            SaveData var6 = MainMenu.getContinueCacheSaveBase(this.settings.worldFilePath.getAbsolutePath(), MainMenu.ContinueMode.HOST);
            SaveData var7 = new SaveData("host");
            this.settings.addSaveData(var7);
            var6.addSaveData(var7);
            GameCache.cacheSave(var6, "continueLast");
         } else {
            System.err.println("Start host did not work, because provided port is still singleplayer");
         }
      } else {
         System.err.println("Cannot start host, because world is already hosting");
      }

   }

   public void makeSingleplayer(Client var1) {
      this.client = var1;
      this.isSinglePlayer = true;
   }

   public void makeHosted(Client var1) {
      this.client = var1;
      this.isHosted = true;
   }

   public void setTickManager(TickManager var1) {
      this.tickManager = var1;
   }

   public void frameTick(TickManager var1) {
      if (!this.isStopped && !this.stopCalled) {
         this.packetManager.tickNetworkManager();

         for(NetworkPacket var2 = this.packetManager.nextPacket(); var2 != null; var2 = this.packetManager.nextPacket()) {
            if (!GameRandom.globalRandom.getChance(this.packetManager.dropPacketChance)) {
               this.processPacket(var2);
            }
         }

         if (!this.isPaused()) {
            this.world.frameTick(var1);

            for(int var3 = 0; var3 < this.getSlots(); ++var3) {
               if (this.getClient(var3) != null) {
                  this.getClient(var3).tickMovement(var1.getDelta());
               }
            }
         }

      }
   }

   public void tick() {
      if (!this.isStopped) {
         if (this.stopCalled) {
            this.privateStop(true);
         } else {
            if (this.isSingleplayer() && this.getLocalClient().hasDisconnected() && !this.isStopped()) {
               this.stop();
            }

            if (this.isHosted() && this.getLocalClient().hasDisconnected() && !this.isStopped()) {
               this.stop();
            }

            this.tickAutoSave();
            this.network.tickUnknownPacketTimeouts();
            int var1 = 0;

            int var2;
            ServerClient var3;
            for(var2 = 0; var2 < this.getSlots(); ++var2) {
               var3 = this.getClient(var2);
               if (var3 != null) {
                  ++var1;
                  var3.tickMapDiscovery();
                  var3.tickTimeConnected();
               }
            }

            this.playersOnline = var1;
            if (var1 == 0) {
               ++this.pauseCounter;
            } else {
               this.pauseCounter = 0;
            }

            if (!this.isPaused()) {
               this.world.serverTick();

               for(var2 = 0; var2 < this.getSlots(); ++var2) {
                  var3 = this.getClient(var2);
                  if (var3 != null) {
                     try {
                        var3.tick();
                     } catch (Exception var5) {
                        System.err.println("Error ticking client \"" + var3.getName() + "\" resulted in a kick.");
                        var5.printStackTrace();
                        this.disconnectClient(var3.slot, PacketDisconnect.Code.INTERNAL_ERROR);
                     }
                  }
               }

               ((Stream)this.world.levelManager.getLoadedLevels().stream().filter((var0) -> {
                  return var0.unloadLevelBuffer > 20 * Math.max(2, Settings.unloadLevelsCooldown);
               }).sorted(Comparator.comparingInt((var0) -> {
                  return var0.shouldSave() ? 1 : 0;
               })).sequential()).forEach((var1x) -> {
                  this.world.levelManager.unloadLevel(var1x);
                  this.world.saveLevel(var1x);
                  System.out.println("Unloaded level " + var1x.getIdentifier());
               });
            }

         }
      }
   }

   public ServerClient getPacketClient(NetworkInfo var1) {
      return var1 == null ? this.getLocalServerClient() : var1.getClient(this.streamClients());
   }

   private void processPacket(NetworkPacket var1) {
      int var2 = var1.type;
      ServerClient var3 = this.getPacketClient(var1.networkInfo);
      if (var3 == null && PacketRegistry.onlyConnectedClients(var2)) {
         this.network.submitUnknownPacket(var1);
      } else {
         try {
            if (var3 != null) {
               var3.submitInPacket(var1);
            }

            var1.processServer(this, var3);
         } catch (Exception var6) {
            System.err.println("Error processing client \"" + (var3 == null ? "NULL" : var3.getName()) + "\" packet (" + PacketRegistry.getPacketSimpleName(var1.type) + ") resulted in a kick.");
            var6.printStackTrace();
            Client var5 = this.getLocalClient();
            if (var5 != null && var3 != null && var5.getSlot() == var3.slot) {
               GameCrashLog.printCrashLog(var6, var5, this, "Server packet crash", false);
               this.stop(PacketDisconnect.Code.SERVER_ERROR);
            } else if (var3 != null) {
               this.disconnectClient(var3.slot, PacketDisconnect.Code.INTERNAL_ERROR);
            } else {
               this.stop(PacketDisconnect.Code.INTERNAL_ERROR);
            }
         }

      }
   }

   public void pause() {
      this.isPaused = true;
   }

   public void resume() {
      this.isPaused = false;
   }

   public boolean isPaused() {
      return Settings.pauseWhenEmpty && this.pauseCounter >= 200 ? true : this.isPaused;
   }

   public Stream<ServerClient> streamClients() {
      return Arrays.stream(this.clients).filter(Objects::nonNull);
   }

   public Iterable<ServerClient> getClients() {
      return () -> {
         return this.streamClients().iterator();
      };
   }

   public ServerClient getLocalServerClient() {
      return (ServerClient)this.streamClients().filter((var0) -> {
         return var0.networkInfo == null;
      }).findFirst().orElse((Object)null);
   }

   public ServerClient getClient(int var1) {
      return var1 >= 0 && var1 < this.clients.length ? this.clients[var1] : null;
   }

   public PlayerMob getPlayer(int var1) {
      ServerClient var2 = this.getClient(var1);
      return var2 != null ? var2.playerMob : null;
   }

   public ServerClient getClientByAuth(long var1) {
      return (ServerClient)this.authClientMap.get(var1);
   }

   public PlayerMob getPlayerByAuth(long var1) {
      ServerClient var3 = this.getClientByAuth(var1);
      return var3 != null ? var3.playerMob : null;
   }

   public String getNameByAuth(long var1, String var3) {
      ServerClient var4 = this.getClientByAuth(var1);
      return var4 != null ? var4.getName() : (String)this.usedNames.getOrDefault(var1, var3);
   }

   public boolean addClient(NetworkInfo var1, long var2, String var4, boolean var5) {
      if (!this.isSingleplayer()) {
         System.out.println("Client \"" + var2 + "\" with address " + (var1 == null ? "LOCAL" : var1.getDisplayName()) + " is connecting with version " + var4 + ".");
      }

      boolean var6 = this.world.hasClient(var2);
      String var7 = (String)this.usedNames.getOrDefault(var2, "N/A");
      String var8;
      if (Settings.isBanned(var2) || var6 && Settings.isBanned(var7)) {
         if (!this.isSingleplayer() && !this.isHosted() || var2 != GameAuth.getAuthentication()) {
            this.network.sendPacket(new NetworkPacket(new PacketDisconnect(-1, PacketDisconnect.Code.BANNED_CLIENT), var1));
            var8 = var6 ? " (" + var7 + ")." : ".";
            System.out.println("Client " + var2 + " is banned" + var8);
            return false;
         }

         System.out.println("The singleplayer/host client was banned, unbanning now.");
         Settings.removeBanned(String.valueOf(var2));
         if (var6) {
            Settings.removeBanned(var7);
         }
      }

      var8 = var6 ? "\"" + var7 + "\"" : "\"" + var2 + "\"";
      if (!var4.equals("0.24.2")) {
         System.out.println("Client " + var8 + " had wrong version (" + var4 + ").");
         this.network.sendPacket(new NetworkPacket(new PacketDisconnect(-1, PacketDisconnect.Code.WRONG_VERSION), var1));
         return false;
      } else {
         for(int var9 = 0; var9 < this.getSlots(); ++var9) {
            if (this.getClient(var9) != null && this.getClient(var9).authentication == var2) {
               if (Objects.equals(this.getClient(var9).networkInfo, var1)) {
                  return false;
               }

               System.out.println("Client " + var8 + " was already playing.");
               this.network.sendPacket(new NetworkPacket(new PacketDisconnect(-1, PacketDisconnect.Code.ALREADY_PLAYING), var1));
               return false;
            }
         }

         byte var14 = 0;

         for(int var10 = 0; var10 < this.getSlots(); ++var10) {
            int var11 = (var10 + var14) % this.getSlots();
            if (this.getClient(var11) == null) {
               System.out.println("Client " + var8 + " connected on slot " + (var11 + 1) + "/" + this.getSlots() + ".");
               long var12 = GameRandom.globalRandom.nextLong();
               if (!var6) {
                  System.out.println("Creating new player: " + var2);
                  this.clients[var11] = ServerClient.getNewPlayerClient(this, var12, var1, var11, var2);
                  this.clients[var11].saveClient();
               } else {
                  this.clients[var11] = this.world.loadClient(var12, var2, var1, var11);
                  System.out.println("Loaded player: " + var2);
                  if (!this.clients[var11].needAppearance()) {
                     this.clients[var11].sendConnectingMessage();
                  }
               }

               this.authClientMap.put(var2, this.clients[var11]);
               this.clients[var11].sendPacket(new PacketConnectApproved(this, this.clients[var11]));
               this.clients[var11].craftingUsesNearbyInventories = var5;
               if (!Settings.serverMOTD.isEmpty()) {
                  this.clients[var11].sendChatMessage(GameColor.PURPLE.getColorCode() + Settings.serverMOTD);
               }

               GameEvents.triggerEvent(new ServerClientConnectedEvent(this.clients[var11]));
               return true;
            }
         }

         System.out.println("Could not find a slot for client \"" + var2 + "\".");
         this.network.sendPacket(new NetworkPacket(new PacketDisconnect(-1, PacketDisconnect.Code.SERVER_FULL), var1));
         return false;
      }
   }

   public int getSlots() {
      return this.settings.slots;
   }

   public boolean isSingleplayer() {
      return this.isSinglePlayer;
   }

   public boolean isHosted() {
      return this.isHosted;
   }

   public Client getLocalClient() {
      return this.client;
   }

   public void sendCommand(String var1, ServerClient var2) {
      if (this.commandsManager.runServerCommand(new ParsedCommand(var1), var2)) {
         if (var2 != null) {
            System.out.println(var2.getName() + " issued command: /" + var1);
         }
      } else if (var2 != null) {
         System.out.println(var2.getName() + " tried to issue command: /" + var1);
      }

   }

   public void startSaveTimer() {
      this.saveTime = this.world.worldEntity.getTime() + (long)Math.max(1, autoSaveIntervalInSec) * 1000L;
   }

   public void tickAutoSave() {
      if (autoSaveIntervalInSec > 0 && this.saveTime <= this.world.worldEntity.getTime()) {
         this.saveAll();
         if (backupEachAutoSave > 0 && this.autoSaves % backupEachAutoSave == 0) {
            Thread var1 = new Thread(() -> {
               try {
                  boolean var1 = World.isWorldADirectory(this.world.filePath);
                  File var2 = WorldSave.getNextBackupPath(var1);
                  if (var2.exists()) {
                     GameUtils.deleteFileOrFolder(var2);
                  }

                  World.copyWorld(this.world.filePath, var2, false);
                  File var3 = new File(World.getSavesPath() + var2.getName());
                  if (var3.exists()) {
                     World.deleteWorld(var3);
                  }
               } catch (Exception var4) {
                  System.err.println("Error saving latest backup");
                  var4.printStackTrace();
               }

            }, "Latest backup");
            var1.start();
         }

         ++this.autoSaves;
         this.startSaveTimer();
      }

   }

   public void saveAll() {
      try {
         this.saveAll(false);
      } catch (IOException var2) {
         var2.printStackTrace();
      }

   }

   private void saveAll(boolean var1) throws IOException {
      this.world.saveWorldEntity();
      this.world.settings.saveSettings();

      for(int var2 = 0; var2 < this.getSlots(); ++var2) {
         ServerClient var3 = this.getClient(var2);
         if (var3 != null) {
            var3.saveClient();
         }
      }

      Iterator var6 = this.world.levelManager.getLoadedLevels().iterator();

      while(var6.hasNext()) {
         Level var7 = (Level)var6.next();
         this.world.saveLevel(var7);
      }

      if (var1) {
         this.world.closeFileSystem();
      } else {
         try {
            this.world.reloadFileSystem();
         } catch (Exception var5) {
            Client var8 = this.getLocalClient();
            if (var8 != null) {
               var5.printStackTrace();
               if (!this.world.fileSystem.isOpen()) {
                  var8.overrideDisconnectMessage = Localization.translate("misc", "savefailed1") + "\n\n" + var5.getMessage();
                  this.stop(PacketDisconnect.Code.SERVER_ERROR);
                  this.privateStop(false);
                  if (this.world.filePath.getName().endsWith("zip")) {
                     GameCache.removeCache("continueLast");
                     ConfirmationContinueForm var4 = new ConfirmationContinueForm("");
                     LocalMessage var10001 = new LocalMessage("misc", "savecompressfailed");
                     LocalMessage var10002 = new LocalMessage("ui", "acceptbutton");
                     LocalMessage var10003 = new LocalMessage("ui", "declinebutton");
                     Runnable var10004 = () -> {
                        Settings.zipSaves = false;
                        var4.applyContinue();
                     };
                     Objects.requireNonNull(var4);
                     var4.setupConfirmation((GameMessage)var10001, var10002, var10003, var10004, var4::applyContinue);
                     var8.additionalDisconnectContinueForms.add(var4);
                  }
               } else {
                  var8.chat.addMessage(GameColor.RED.getColorCode() + Localization.translate("misc", "savefailed1"));
                  var8.chat.addMessage(GameColor.RED.getColorCode() + var5.getMessage());
                  var8.chat.addMessage(GameColor.RED.getColorCode() + Localization.translate("misc", "savefailed2"));
               }
            } else {
               System.err.println(Localization.translate("misc", "savefailed1"));
               var5.printStackTrace();
               System.err.println(Localization.translate("misc", "savefailed2"));
               if (!this.world.fileSystem.isOpen()) {
                  this.stop(PacketDisconnect.Code.SERVER_ERROR);
                  this.privateStop(false);
               }
            }
         }
      }

   }

   public int getSlot(PlayerMob var1) {
      for(int var2 = 0; var2 < this.getSlots(); ++var2) {
         ServerClient var3 = this.getClient(var2);
         if (var3 != null && var1 == var3.playerMob) {
            return var2;
         }
      }

      return -1;
   }

   public boolean disconnectClient(int var1, PacketDisconnect.Code var2) {
      return this.disconnectClient(this.getClient(var1), var2);
   }

   public boolean disconnectClient(ServerClient var1, PacketDisconnect.Code var2) {
      return this.disconnectClient(var1, new PacketDisconnect(var1.slot, var2));
   }

   public boolean disconnectClient(int var1, PacketDisconnect var2) {
      return this.disconnectClient(this.getClient(var1), var2);
   }

   public boolean disconnectClient(ServerClient var1, PacketDisconnect var2) {
      if (var1 != null) {
         this.network.sendToAllClients(var2);
         this.disconnectClient(var1, true);
         return true;
      } else {
         return false;
      }
   }

   private void disconnectClient(ServerClient var1, boolean var2) {
      var1.onUnloading();
      if (var2) {
         var1.saveClient();
      }

      GameEvents.triggerEvent(new ServerClientDisconnectEvent(var1));
      var1.dispose();
      this.clients[var1.slot] = null;
      this.authClientMap.remove(var1.authentication);
   }

   public int getPlayersOnline() {
      return this.playersOnline;
   }

   private void privateStop(boolean var1) {
      int var2;
      ServerClient var3;
      for(var2 = 0; var2 < this.getSlots(); ++var2) {
         var3 = this.getClient(var2);
         if (var3 != null) {
            var3.sendPacket(new PacketDisconnect(this.stopPacket, var2));
            var3.onUnloading();
         }
      }

      ((Stream)this.world.levelManager.getLoadedLevels().stream().sorted(Comparator.comparingInt((var0) -> {
         return var0.shouldSave() ? 1 : 0;
      })).sequential()).forEach(Level::onUnloading);
      if (var1) {
         try {
            this.saveAll(true);
            System.out.println("Saved all data.");
            System.out.println("World time: " + this.world.worldEntity.getDayTimeInt() + ", day " + this.world.worldEntity.getDay());
            System.out.println("Game time: " + this.world.worldEntity.getTime());
            System.out.println("Total ticks: " + this.tickManager().getTotalTicks());
            System.out.println("Received: " + this.packetManager.getTotalIn() + " (" + this.packetManager.getTotalInPackets() + " packets)");
            System.out.println("Sent: " + this.packetManager.getTotalOut() + " (" + this.packetManager.getTotalOutPackets() + " packets)");
            System.out.println("Stopped server on " + (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date()) + " with code: " + this.stopPacket.code);
         } catch (IOException var5) {
            System.err.println(Localization.translate("misc", "savefailed1"));
            var5.printStackTrace();
            GameCache.removeCache("continueLast");
            this.stopErrors = new LinkedList();
            NoticeForm var6 = new NoticeForm("notice");
            var6.setupNotice((GameMessage)(new GameMessageBuilder()).append("misc", "savefailed1").append("\n\n" + var5.getMessage()));
            this.stopErrors.add(var6);
            if (!this.world.fileSystem.isOpen()) {
               ConfirmationContinueForm var4 = new ConfirmationContinueForm("");
               LocalMessage var10001 = new LocalMessage("misc", "savecompressfailed");
               LocalMessage var10002 = new LocalMessage("ui", "acceptbutton");
               LocalMessage var10003 = new LocalMessage("ui", "declinebutton");
               Runnable var10004 = () -> {
                  Settings.zipSaves = false;
                  var4.applyContinue();
               };
               Objects.requireNonNull(var4);
               var4.setupConfirmation((GameMessage)var10001, var10002, var10003, var10004, var4::applyContinue);
               this.stopErrors.add(var4);
            }
         }
      }

      for(var2 = 0; var2 < this.getSlots(); ++var2) {
         var3 = this.getClient(var2);
         if (var3 != null) {
            var3.dispose();
         }
      }

      GameEvents.triggerEvent(new ServerStopEvent(this));
      this.network.close();
      this.world.dispose();
      this.isStopped = true;
      this.stopEvents.forEach((var1x) -> {
         var1x.accept(this);
      });
   }

   public boolean isStopped() {
      return this.stopCalled || this.isStopped;
   }

   public boolean hasClosed() {
      return this.isStopped;
   }

   public void stop() {
      this.stop((PacketDisconnect)((PacketDisconnect)null), (Consumer)null);
   }

   public void stop(Consumer<Server> var1) {
      this.stop((PacketDisconnect)null, var1);
   }

   public void stop(PacketDisconnect.Code var1) {
      this.stop((PacketDisconnect.Code)var1, (Consumer)null);
   }

   public void stop(PacketDisconnect.Code var1, Consumer<Server> var2) {
      this.stop(var1 == null ? null : new PacketDisconnect(0, var1), var2);
   }

   public void stop(PacketDisconnect var1, Consumer<Server> var2) {
      if (var1 == null) {
         var1 = new PacketDisconnect(0, PacketDisconnect.Code.SERVER_STOPPED);
      }

      this.stopPacket = var1;
      if (var2 != null) {
         this.stopEvents.add(var2);
      }

      this.stopCalled = true;
   }

   public TickManager tickManager() {
      return this.tickManager;
   }

   private PacketManager newPacketManager() {
      return new PacketManager() {
         public void processInstantly(NetworkPacket var1) {
            Server.this.processPacket(var1);
         }
      };
   }
}
