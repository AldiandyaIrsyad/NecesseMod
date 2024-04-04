package necesse.engine.network.client;

import com.codedisaster.steamworks.SteamID;
import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import necesse.engine.CameraShake;
import necesse.engine.GameCrashLog;
import necesse.engine.GameLog;
import necesse.engine.GlobalData;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.commands.CommandsManager;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.DuplicateRichPresenceKeyException;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.modLoader.ModLoader;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.PacketManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.client.loading.ClientLoading;
import necesse.engine.network.client.network.ClientAddressNetwork;
import necesse.engine.network.client.network.ClientNetwork;
import necesse.engine.network.client.network.ClientSingleplayerNetwork;
import necesse.engine.network.client.network.ClientSteamNetworkMessages;
import necesse.engine.network.packet.PacketCloseContainer;
import necesse.engine.network.packet.PacketConnectApproved;
import necesse.engine.network.packet.PacketCraftUseNearbyInventories;
import necesse.engine.network.packet.PacketDisconnect;
import necesse.engine.network.packet.PacketPermissionUpdate;
import necesse.engine.network.packet.PacketPing;
import necesse.engine.network.packet.PacketPlayerGeneral;
import necesse.engine.network.packet.PacketPlayerInventoryAction;
import necesse.engine.network.packet.PacketPlayerMovement;
import necesse.engine.network.packet.PacketPlayerPvP;
import necesse.engine.network.packet.PacketPlayerRespawn;
import necesse.engine.network.packet.PacketSpawnPlayer;
import necesse.engine.network.server.AdventureParty;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerSettings;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.quest.QuestManager;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.save.CharacterSave;
import necesse.engine.seasons.GameSeasons;
import necesse.engine.sound.PrimitiveSoundEmitter;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.state.MainGame;
import necesse.engine.state.MainMenu;
import necesse.engine.steam.SteamData;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.engine.world.WorldEntity;
import necesse.engine.world.WorldSettings;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.FormManager;
import necesse.gfx.forms.MainGameFormManager;
import necesse.gfx.forms.MainMenuFormManager;
import necesse.gfx.forms.components.ContinueComponent;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.chat.ChatMessageList;
import necesse.gfx.forms.presets.ConfirmationContinueForm;
import necesse.gfx.forms.presets.CrashDetailsContinueForm;
import necesse.gfx.forms.presets.CrashReportContinueForm;
import necesse.gfx.forms.presets.ModCrashReportContinueForm;
import necesse.gfx.forms.presets.NoticeForm;
import necesse.gfx.forms.presets.sidebar.TrackedSidebarForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.container.Container;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.floatText.UniqueFloatText;
import necesse.reports.CrashReportData;
import necesse.reports.ReportUtils;

public class Client {
   private static int characterAutoSaveIntervalInSec = 60;
   private static int backupEachCharacterAutoSave = 15;
   private boolean isPaused;
   public boolean isDead;
   public long respawnTime;
   public ClientNetwork network;
   public GameMessage playingOnDisplayName;
   private long worldUniqueID;
   private int characterUniqueID;
   public File characterFilePath;
   private long characterSaveTime;
   private int characterSavesCounter;
   public long sessionID;
   int slot;
   int slots;
   PermissionLevel permissionLevel;
   private long waitTimer;
   private int waitAttempts;
   public GameMessage serverRespondError;
   private long movementTimer;
   public WorldEntity worldEntity;
   public final QuestManager quests;
   private ClientClient[] players;
   private HashMap<Long, ClientClient> authPlayerMap;
   public final ClientLevelManager levelManager;
   private Point positionUpdatePoint;
   public WorldSettings worldSettings;
   public long pvpChangeTime;
   private boolean allowClientsPower;
   private long messageTime;
   private Color messageColor;
   private GameMessage message;
   public final ClientLoading loading;
   public ClientTutorial tutorial;
   public ClientIslandNotes islandNotes;
   public final ChatMessageList chat;
   public ArrayList<String> chatSubmits;
   private LinkedList<CameraShake> cameraShakes;
   public PlayerStats characterStats;
   public final AdventureParty adventureParty;
   private TickManager tickManager;
   private ClientSteamLobby steamLobby;
   private ServerSettings.SteamLobbyType steamLobbyType;
   public final PacketManager packetManager;
   public final CommandsManager commandsManager;
   public boolean hasSentSpawnPacket;
   private boolean hasDisconnected;
   private String disconnectMessage;
   private boolean disconnectCalled;
   private Container inventoryContainer;
   private Container openContainer;
   private boolean shouldCloseInventoryOnContainerClose;
   private long lastHungryThought;
   private long lastStarvingThought;
   private boolean isSingleplayer;
   private Server server;
   private ExecutorService singlePlayerSocket;
   public CrashReportData serverCrashReport;
   public String overrideDisconnectMessage;
   public LinkedList<ContinueComponent> additionalDisconnectContinueForms;
   public ClientPerformanceDumpCache performanceDumpCache;

   private Client(TickManager var1) {
      this.quests = new QuestManager(this);
      this.authPlayerMap = new HashMap();
      this.levelManager = new ClientLevelManager(this);
      this.chatSubmits = new ArrayList();
      this.cameraShakes = new LinkedList();
      this.adventureParty = new AdventureParty(this);
      this.additionalDisconnectContinueForms = new LinkedList();
      this.performanceDumpCache = new ClientPerformanceDumpCache(this);
      this.tickManager = var1;
      this.singlePlayerSocket = Executors.newSingleThreadExecutor((var0) -> {
         return new Thread((ThreadGroup)null, var0, "Singleplayer socket");
      });
      this.packetManager = this.newPacketManager();
      this.commandsManager = new CommandsManager(this);
      this.loading = new ClientLoading(this);
      this.chat = new ChatMessageList();
      Settings.craftingUseNearby.addChangeListener((var1x) -> {
         this.network.sendPacket(new PacketCraftUseNearbyInventories());
      }, this::isDisconnected);
   }

   public Client(TickManager var1, String var2, int var3, GameMessage var4) {
      this(var1);
      this.network = new ClientAddressNetwork(this, var2, var3);
      this.playingOnDisplayName = var4;
   }

   public Client(TickManager var1, Server var2, boolean var3) {
      this(var1);
      this.network = new ClientSingleplayerNetwork(this);
      this.server = var2;
      this.isSingleplayer = var3;
      this.playingOnDisplayName = new StaticMessage(var2.world.displayName);
   }

   public Client(TickManager var1, SteamID var2, GameMessage var3) {
      this(var1);
      this.network = new ClientSteamNetworkMessages(this, var2);
      this.playingOnDisplayName = var3;
   }

   public void start() {
      if (this.network.openConnection()) {
         GameLog.debug.println("Opened connection to " + this.network.getDebugString());
         this.loading.init();
         this.slot = -1;
         this.worldUniqueID = -1L;
         this.positionUpdatePoint = new Point(0, 0);
         this.worldEntity = null;
         this.levelManager.setLevel((Level)null);
         this.waitTimer = 0L;
         this.waitAttempts = 0;
      } else {
         System.err.println("Failed to open connection to " + this.network.getDebugString());
         this.error(Localization.translate("disconnect", "networkerror", "msg", this.network.getOpenError()), false);
      }

   }

   public void reset() {
      this.positionUpdatePoint = new Point(0, 0);
      this.loading.reset();
      this.waitTimer = 0L;
      this.waitAttempts = 0;
      this.hasSentSpawnPacket = false;
      this.closeContainer(true);
   }

   public void submitConnectionPacket(PacketConnectApproved var1) {
      this.sessionID = var1.sessionID;
      this.players = new ClientClient[var1.slots];
      this.slots = var1.slots;
      this.slot = var1.slot;
      this.worldUniqueID = var1.uniqueID;
      this.worldSettings = var1.getWorldSettings(this);
      this.allowClientsPower = var1.allowClientsPower;
      this.permissionLevel = var1.permissionLevel;
      this.loading.submitApprovedPacket(var1);
      this.tutorial = new ClientTutorial(this, this.worldUniqueID);
      this.islandNotes = new ClientIslandNotes(this.worldUniqueID);
      TrackedSidebarForm.loadTrackedQuests(this);
      GameSeasons.readSeasons(new PacketReader(var1.activeSeasonsContent));
      this.steamLobbyType = var1.steamLobbyType;
      if (this.steamLobby != null) {
         this.steamLobby.dispose();
      }

      this.steamLobby = null;
      if (!this.isSingleplayer() && this.steamLobbyType != null) {
         this.steamLobby = new ClientSteamLobby(this);
         this.steamLobby.createLobby(this.steamLobbyType);
      }

   }

   public void startedHosting(Server var1) {
      this.server = var1;
      this.players = (ClientClient[])Arrays.copyOf(this.players, var1.getSlots());
      this.slots = var1.getSlots();
      this.isSingleplayer = false;
      this.steamLobbyType = var1.settings.steamLobbyType;
      if (this.steamLobby != null) {
         this.steamLobby.dispose();
      }

      this.steamLobby = null;
      if (!this.isSingleplayer() && this.steamLobbyType != null) {
         this.steamLobby = new ClientSteamLobby(this);
         this.steamLobby.createLobby(this.steamLobbyType);
      }

      this.resume();
      this.updateSteamRichPresence();
   }

   public ServerSettings.SteamLobbyType getSteamLobbyType() {
      return this.steamLobbyType;
   }

   public void frameTick(TickManager var1) {
      if (!this.hasDisconnected && !this.disconnectCalled) {
         this.packetManager.tickNetworkManager();

         for(NetworkPacket var2 = this.packetManager.nextPacket(); var2 != null; var2 = this.packetManager.nextPacket()) {
            this.waitAttempts = 0;
            if (!GameRandom.globalRandom.getChance(this.packetManager.dropPacketChance)) {
               var2.processClient(this);
            }

            if (this.isDisconnected()) {
               return;
            }
         }

         if (!this.isPaused() && this.worldEntity != null) {
            this.worldEntity.clientFrameTick(var1);
         }

         if (this.loading.isDone()) {
            if (!this.isPaused()) {
               this.getLevel().frameTick(var1);

               for(int var3 = 0; var3 < this.getSlots(); ++var3) {
                  ClientClient var4 = this.getClient(var3);
                  if (var4 != null) {
                     var4.tickMovement(this, var1.getDelta());
                  }
               }
            }

         }
      }
   }

   public void tick() {
      if (!this.hasDisconnected) {
         if (this.disconnectCalled) {
            this.privateDisconnect(true);
         } else {
            this.performanceDumpCache.tickTimeouts();
            this.waitTimer += 50L;
            if (this.waitTimer > 1000L) {
               this.waitTimer = 0L;
               ++this.waitAttempts;
               if (this.getSlot() != -1 && this.waitAttempts >= 5) {
                  this.network.sendPacket(new PacketPing(-1));
               }

               if (this.waitAttempts >= 10) {
                  this.setMessage((GameMessage)(new LocalMessage("disconnect", "notresponding", new Object[]{"seconds", this.waitAttempts})), Color.RED, 1.0F);
                  this.serverRespondError = this.getMessage();
                  System.err.println(this.serverRespondError.translate());
               } else {
                  this.serverRespondError = null;
               }

               if (this.waitAttempts >= 60) {
                  this.error(Localization.translate("disconnect", "respondingdc"), true);
               }
            }

            if (this.steamLobby != null && !this.steamLobby.isLobbyCreated() && !this.steamLobby.isWaitingForLobbyCreate()) {
               this.steamLobby.createLobby(this.steamLobbyType);
               GameLog.debug.println("Creating lobby again");
            }

            this.loading.tick();
            this.levelManager.tick();
            if (this.loading.isDone()) {
               this.tickAutoSave();
               this.tutorial.tick();
               final PlayerMob var1 = this.getPlayer();
               if (var1 != null) {
                  var1.setLevel(this.getLevel());
                  this.levelManager.map().tickDiscovery(var1);
               }

               if (!this.isPaused()) {
                  this.cameraShakes.removeIf((var1x) -> {
                     return var1x.isOver(this.worldEntity.getTime());
                  });
                  this.worldEntity.clientTick();
                  this.levelManager.clientTick();

                  for(int var2 = 0; var2 < this.getSlots(); ++var2) {
                     ClientClient var3 = this.getClient(var2);
                     if (var3 != null) {
                        var3.tick(this);
                     }
                  }

                  this.adventureParty.clientTick();
                  if (var1 != null && !this.isDead) {
                     var1.refreshClientUpdateTime();
                     if (var1.removed()) {
                        var1.restore();
                     }

                     var1.tickOpenedDoors();
                     this.getContainer().tick();
                     UniqueFloatText var4;
                     long var5;
                     if (var1.buffManager.hasBuff(BuffRegistry.STARVING_BUFF)) {
                        var5 = this.worldEntity.getLocalTime() - this.lastStarvingThought;
                        if (this.lastStarvingThought == 0L || var5 >= 60000L) {
                           var4 = new UniqueFloatText(var1.getX(), var1.getY(), Localization.translate("misc", "starvingthought"), (new FontOptions(16)).outline(), "thought") {
                              public int getAnchorX() {
                                 return var1.getX();
                              }

                              public int getAnchorY() {
                                 return var1.getY();
                              }
                           };
                           var4.hoverTime = 6000;
                           this.getLevel().hudManager.addElement(var4);
                           this.lastStarvingThought = this.worldEntity.getLocalTime();
                           this.lastHungryThought = this.worldEntity.getLocalTime();
                        }
                     } else if (var1.buffManager.hasBuff(BuffRegistry.HUNGRY_BUFF)) {
                        var5 = this.worldEntity.getLocalTime() - this.lastHungryThought;
                        if (this.lastHungryThought == 0L || var5 >= 60000L) {
                           var4 = new UniqueFloatText(var1.getX(), var1.getY(), Localization.translate("misc", "hungrythought"), (new FontOptions(16)).outline(), "thought") {
                              public int getAnchorX() {
                                 return var1.getX();
                              }

                              public int getAnchorY() {
                                 return var1.getY();
                              }
                           };
                           var4.hoverTime = 6000;
                           this.getLevel().hudManager.addElement(var4);
                           this.lastHungryThought = this.worldEntity.getLocalTime();
                        }
                     } else {
                        this.lastStarvingThought = 0L;
                        this.lastHungryThought = 0L;
                     }

                     this.movementTimer += 50L;
                     if (this.movementTimer > 2000L) {
                        this.movementTimer = 0L;
                        this.sendMovementPacket(false);
                     }

                     if (this.positionUpdatePoint.distance((double)var1.getX(), (double)var1.getY()) > 50.0) {
                        this.sendMovementPacket(false);
                     }

                     if (var1.shouldUpdateInventoryAction()) {
                        this.network.sendPacket(new PacketPlayerInventoryAction(this.getSlot(), var1));
                     }
                  }
               }

            }
         }
      }
   }

   public void updateSteamRichPresence() {
      PlayerMob var1 = this.getPlayer();
      if (var1 != null && var1.getLevel() != null) {
         LocalMessage var2 = this.network.getPlayingMessage();
         if (var2 != null) {
            try {
               var2.addReplacement("location", var1.getLevel().getLocationMessage());
               HashMap var3 = new HashMap();
               String var4 = var2.setSteamRichPresence(var3, (String)null, 0);
               Iterator var5 = var3.entrySet().iterator();

               while(var5.hasNext()) {
                  Map.Entry var6 = (Map.Entry)var5.next();
                  SteamData.setRichPresence((String)var6.getKey(), (String)var6.getValue());
               }

               SteamData.setRichPresence("steam_display", var4);
            } catch (DuplicateRichPresenceKeyException var7) {
               GameLog.debug.println(var7.getMessage());
               SteamData.setRichPresence("location", "#richpresence_unknownlocation");
               SteamData.setRichPresence("steam_display", "#" + var2.category + "_" + var2.key);
            }

            String var8 = this.network.getRichPresenceGroup();
            if (var8 != null) {
               SteamData.setRichPresence("steam_player_group", var8);
               long var9 = Arrays.stream(this.players).filter(Objects::nonNull).count();
               SteamData.setRichPresence("steam_player_group_size", String.valueOf(var9));
            }

            return;
         }
      }

      SteamData.clearRichPresence();
   }

   public void respawn(PacketPlayerRespawn var1) {
      ClientClient var2 = this.getClient();
      var2.respawn(var1);
      this.isDead = false;
      Level var3 = this.getLevel();
      if (var3 != null && var3.getIdentifier().equals(var1.levelIdentifier)) {
         this.network.sendPacket(new PacketSpawnPlayer(this));
      } else {
         GlobalData.setCurrentState(new MainMenu(this));
      }

   }

   public long getRespawnTimeLeft() {
      return this.respawnTime - this.worldEntity.getTime();
   }

   public boolean canRespawn() {
      return this.respawnTime <= this.worldEntity.getTime();
   }

   public Level getLevel() {
      return this.levelManager.getLevel();
   }

   public boolean isSingleplayer() {
      return this.isSingleplayer;
   }

   public void submitSinglePlayerPacket(PacketManager var1, NetworkPacket var2) {
      if (!this.singlePlayerSocket.isShutdown()) {
         this.singlePlayerSocket.submit(() -> {
            var1.submitInPacket(new NetworkPacket(var2));
         });
      }
   }

   public Server getLocalServer() {
      return this.server;
   }

   public void pause() {
      this.isPaused = true;
      if (this.server != null) {
         this.server.pause();
      }

   }

   public void resume() {
      this.isPaused = false;
      if (this.server != null) {
         this.server.resume();
      }

   }

   public boolean isPaused() {
      return this.isPaused;
   }

   public boolean allowClientsPower() {
      return this.allowClientsPower;
   }

   private void privateDisconnect(boolean var1) {
      if (this.worldSettings != null && this.worldSettings.allowOutsideCharacters && this.loading.isDone()) {
         CharacterSave.saveCharacter(this, false);
      }

      if (this.getSlot() != -1 && var1) {
         this.network.sendPacket(PacketDisconnect.clientDisconnect(this.getSlot(), this.disconnectMessage));
      }

      this.network.close();
      this.singlePlayerSocket.shutdown();
      this.levelManager.dispose();
      if (this.steamLobby != null) {
         this.steamLobby.dispose();
      }

      this.hasDisconnected = true;
   }

   public boolean inviteToSteamLobby(SteamID var1) {
      return this.steamLobby != null ? this.steamLobby.inviteUser(var1) : false;
   }

   public void instantDisconnect(String var1) {
      this.disconnect(var1);
      this.privateDisconnect(true);
   }

   public void disconnect(String var1) {
      this.disconnectCalled = true;
      this.disconnectMessage = var1;
   }

   public boolean hasDisconnected() {
      return this.hasDisconnected;
   }

   public boolean isDisconnected() {
      return this.disconnectCalled || this.hasDisconnected;
   }

   public void error(String var1, boolean var2) {
      this.error(var1, var2, (ContinueComponent)null);
   }

   public void error(String var1, boolean var2, ContinueComponent var3) {
      System.err.println(var1);
      this.disconnect(var1);
      this.privateDisconnect(var2);
      if (this.getLocalServer() != null) {
         this.getLocalServer().stop(PacketDisconnect.Code.SERVER_ERROR);
      }

      try {
         if (this.overrideDisconnectMessage != null) {
            var1 = this.overrideDisconnectMessage;
         }

         if (this.serverCrashReport != null) {
            ArrayList var4 = ModLoader.getResponsibleMods(this.serverCrashReport.errors, true);
            if (!var4.isEmpty()) {
               var3 = new ModCrashReportContinueForm(this.serverCrashReport, var4, 600, 300, (var0) -> {
                  MainMenu var1 = (MainMenu)GlobalData.getCurrentState();
                  FormManager var2 = var1.getFormManager();
                  if (var2 instanceof MainMenuFormManager) {
                     MainMenuFormManager var3 = (MainMenuFormManager)var2;
                     var3.mainForm.mods.resetModsList();
                     var3.mainForm.makeCurrent(var3.mainForm.mods);
                  }

                  var0.applyContinue();
               }, (var0) -> {
               });
            } else if (GameCrashLog.checkAnyCause((Iterable)this.serverCrashReport.errors, (var0) -> {
               return var0 instanceof OutOfMemoryError;
            })) {
               NoticeForm var5 = new NoticeForm("outofmemory");
               var5.setupNotice((GameMessage)(new LocalMessage("misc", "outofmemory")));
               var3 = var5;
            } else {
               var3 = new CrashReportContinueForm(this.serverCrashReport, "crashsorry", 600, 300, (var1x) -> {
                  MainMenu var2 = (MainMenu)GlobalData.getCurrentState();
                  var2.continueForm(new CrashDetailsContinueForm(Localization.translate("ui", "crashdetailshere"), "crashgivedetails", 500, 400, 150, (var2x, var3) -> {
                     this.sendReport(var2, this.serverCrashReport, var3);
                     var2x.applyContinue();
                  }, (var0) -> {
                  }));
                  var1x.applyContinue();
               }, (var0) -> {
               });
            }
         }

         MainMenu var8;
         if (GlobalData.getCurrentState() instanceof MainMenu) {
            var8 = (MainMenu)GlobalData.getCurrentState();
            if (var3 == null) {
               var8.notice(var1);
            } else {
               var8.continueForm((ContinueComponent)var3);
            }
         } else if (var3 == null) {
            GlobalData.setCurrentState(var8 = new MainMenu(var1, this));
         } else {
            GlobalData.setCurrentState(var8 = new MainMenu((ContinueComponent)var3, this));
         }

         Iterator var9 = this.additionalDisconnectContinueForms.iterator();

         while(var9.hasNext()) {
            ContinueComponent var6 = (ContinueComponent)var9.next();
            var8.continueForm(var6);
         }
      } catch (Exception var7) {
         if (this.serverCrashReport == null) {
            throw var7;
         }

         GameCrashLog.openCrashFrame(this.serverCrashReport);
         Screen.dispose();
      }

   }

   private void sendReport(MainMenu var1, CrashReportData var2, String var3) {
      AtomicBoolean var4 = new AtomicBoolean(false);
      NoticeForm var5 = new NoticeForm("sendingreport");
      var5.setupNotice((GameMessage)(new LocalMessage("ui", "sendingreport")), new LocalMessage("ui", "cancelbutton"));
      var5.onContinue(() -> {
         var4.set(true);
      });
      var1.continueForm(var5);
      (new Thread(() -> {
         String var6 = ReportUtils.sendCrashReport(var2, var3);
         if (!var4.get()) {
            if (var6 != null) {
               ConfirmationContinueForm var7 = new ConfirmationContinueForm("sendreportretry", 300, 1000);
               StaticMessage var10001 = new StaticMessage(var6);
               LocalMessage var10002 = new LocalMessage("ui", "sendreportretry");
               LocalMessage var10003 = new LocalMessage("ui", "continuebutton");
               Runnable var10004 = () -> {
                  this.sendReport(var1, var2, var3);
                  var7.applyContinue();
               };
               Objects.requireNonNull(var7);
               var7.setupConfirmation((GameMessage)var10001, var10002, var10003, var10004, var7::applyContinue);
               var1.continueForm(var7);
            } else {
               NoticeForm var8 = new NoticeForm("thankyoureport");
               var8.setupNotice((GameMessage)(new LocalMessage("ui", "sendreportthanks")));
               var1.continueForm(var8);
            }
         }

         var5.applyContinue();
      })).start();
   }

   public int getSlot() {
      return this.slot;
   }

   public int getSlots() {
      return this.slots;
   }

   public long getWorldUniqueID() {
      return this.worldUniqueID;
   }

   public int getCharacterUniqueID() {
      return this.characterUniqueID;
   }

   public void sendMovementPacket(boolean var1) {
      ClientClient var2 = this.getClient();
      if (var2 != null && var2.playerMob != null) {
         this.network.sendPacket(new PacketPlayerMovement(this, var2, var1));
         this.positionUpdatePoint = new Point(var2.playerMob.getX(), var2.playerMob.getY());
      }

   }

   public void resetPositionPointUpdate() {
      PlayerMob var1 = this.getPlayer();
      if (var1 != null) {
         this.positionUpdatePoint = var1.getPositionPoint();
      }

   }

   public Stream<ClientClient> streamClients() {
      return this.players == null ? Stream.empty() : Arrays.stream(this.players).filter(Objects::nonNull);
   }

   public ClientClient getClient(int var1) {
      return this.players != null && var1 >= 0 && var1 < this.players.length ? this.players[var1] : null;
   }

   public PlayerMob getPlayer(int var1) {
      ClientClient var2 = this.getClient(var1);
      return var2 != null ? var2.playerMob : null;
   }

   public ClientClient getClientByAuth(long var1) {
      return (ClientClient)this.authPlayerMap.get(var1);
   }

   public PlayerMob getPlayerByAuth(long var1) {
      ClientClient var3 = this.getClientByAuth(var1);
      return var3 != null ? var3.playerMob : null;
   }

   private void setClient(int var1, ClientClient var2) {
      if (this.players != null) {
         ClientClient var3 = this.getClient(var1);
         if (var3 != null) {
            this.authPlayerMap.remove(var3.authentication);
            var3.dispose();
         }

         this.players[var1] = var2;
         if (var2 != null) {
            this.authPlayerMap.put(var2.authentication, var2);
         }

         MainGameFormManager var4 = this.getMainFormManager();
         if (var4 != null) {
            var4.scoreboard.slotChanged(var1, var2);
         }

      }
   }

   public void applyPlayerGeneralPacket(PacketPlayerGeneral var1) {
      if (!this.hasDisconnected()) {
         if (this.slot == var1.slot) {
            this.characterUniqueID = var1.characterUniqueID;
         }

         ClientClient var2 = this.getClient(var1.slot);
         if (var2 == null) {
            this.setClient(var1.slot, new ClientClient(this, var1.slot, var1));
         } else {
            var2.applyGeneralPacket(var1);
         }

         this.loading.playersPhase.submitLoadedPlayer(this.slot);
      }
   }

   public void clearClient(int var1) {
      this.setClient(var1, (ClientClient)null);
   }

   public PlayerMob getPlayer() {
      return this.getSlot() == -1 ? null : this.getPlayer(this.getSlot());
   }

   public ClientClient getClient() {
      return this.getSlot() == -1 ? null : this.getClient(this.getSlot());
   }

   private MainGameFormManager getMainFormManager() {
      return GlobalData.getCurrentState() instanceof MainGame ? ((MainGame)GlobalData.getCurrentState()).formManager : null;
   }

   public boolean hasFocusForm() {
      MainGameFormManager var1 = this.getMainFormManager();
      return var1 != null && var1.hasFocusForm();
   }

   public FormComponent getFocusForm() {
      MainGameFormManager var1 = this.getMainFormManager();
      return var1 != null ? var1.getFocusForm() : null;
   }

   public void setFocusForm(ContainerComponent<?> var1) {
      MainGameFormManager var2 = this.getMainFormManager();
      if (var2 != null) {
         var2.setFocusForm(var1);
      }

   }

   public void resetFocusForm() {
      MainGameFormManager var1 = this.getMainFormManager();
      if (var1 != null) {
         var1.removeFocusForm();
      }

   }

   public void setPvP(boolean var1) {
      if (!this.worldSettings.forcedPvP && this.getClient().pvpEnabled != var1) {
         this.pvpChangeTime = System.currentTimeMillis();
         this.network.sendPacket(new PacketPlayerPvP(this.getSlot(), var1));
      }

   }

   public boolean pvpEnabled() {
      return this.worldSettings.forcedPvP || this.getClient().pvpEnabled;
   }

   public int getTeam() {
      ClientClient var1 = this.getClient();
      return var1 != null && var1.playerMob != null ? var1.playerMob.getTeam() : -1;
   }

   public void setMessage(GameMessage var1, Color var2) {
      this.setMessage(var1, var2, 5.0F);
   }

   public void setMessage(String var1, Color var2) {
      this.setMessage(var1, var2, 5.0F);
   }

   public void setMessage(GameMessage var1, Color var2, float var3) {
      this.messageTime = System.currentTimeMillis() + (long)(var3 * 1000.0F);
      this.message = var1;
      this.messageColor = var2;
   }

   public void setMessage(String var1, Color var2, float var3) {
      this.setMessage((GameMessage)(new StaticMessage(var1)), var2, var3);
   }

   public GameMessage getMessage() {
      return this.message;
   }

   public float getMessageAlpha() {
      float var1 = 1.0F;
      if (this.messageTime < System.currentTimeMillis()) {
         var1 = Math.abs((float)(System.currentTimeMillis() - this.messageTime) / 2000.0F - 1.0F);
      }

      return var1;
   }

   public Color getMessageColor() {
      return new Color((float)this.messageColor.getRed() / 255.0F, (float)this.messageColor.getGreen() / 255.0F, (float)this.messageColor.getBlue() / 255.0F, this.getMessageAlpha());
   }

   public boolean messageShown() {
      return this.messageTime + 2000L > System.currentTimeMillis();
   }

   public CameraShake startCameraShake(PrimitiveSoundEmitter var1, long var2, int var4, int var5, float var6, float var7, boolean var8) {
      CameraShake var9 = new CameraShake(var2, var4, var5, var6, var7, var8, this.getCurrentCameraShake());
      if (Settings.cameraShake) {
         this.cameraShakes.add(var9);
      }

      if (var1 != null) {
         var9.from(var1);
      }

      return var9;
   }

   public CameraShake startCameraShake(PrimitiveSoundEmitter var1, int var2, int var3, float var4, float var5, boolean var6) {
      return this.startCameraShake(var1, this.worldEntity.getTime(), var2, var3, var4, var5, var6);
   }

   public CameraShake startCameraShake(float var1, float var2, int var3, int var4, float var5, float var6, boolean var7) {
      return this.startCameraShake(SoundPlayer.SimpleEmitter(var1, var2), var3, var4, var5, var6, var7);
   }

   public Point2D.Float getCurrentCameraShake() {
      Point2D.Float var1 = new Point2D.Float();

      Point2D.Float var4;
      for(Iterator var2 = this.cameraShakes.iterator(); var2.hasNext(); var1 = new Point2D.Float(var1.x + var4.x, var1.y + var4.y)) {
         CameraShake var3 = (CameraShake)var2.next();
         var4 = var3.getCurrentShake(this.worldEntity.getTime(), this.getPlayer());
      }

      return var1;
   }

   public void startCharacterSaveTimer() {
      if (this.worldEntity != null) {
         this.characterSaveTime = this.worldEntity.getTime() + (long)Math.max(1, characterAutoSaveIntervalInSec) * 1000L;
      }
   }

   public void tickAutoSave() {
      if (this.worldSettings == null || this.worldSettings.allowOutsideCharacters || !this.loading.isDone()) {
         if (this.characterSaveTime == 0L) {
            this.startCharacterSaveTimer();
         }

         if (this.characterSaveTime > 0L && characterAutoSaveIntervalInSec > 0 && this.characterSaveTime <= this.worldEntity.getTime()) {
            CharacterSave.saveCharacter(this, backupEachCharacterAutoSave > 0 && this.characterSavesCounter % backupEachCharacterAutoSave == 0);
            ++this.characterSavesCounter;
            this.startCharacterSaveTimer();
         }

      }
   }

   public PermissionLevel getPermissionLevel() {
      return this.permissionLevel;
   }

   public void permissionUpdate(PacketPermissionUpdate var1) {
      this.permissionLevel = var1.permissionLevel;
   }

   public TickManager tickManager() {
      return this.tickManager;
   }

   public void submitPingPacket(PacketPing var1) {
      if (var1.responseKey != -1) {
         this.network.sendPacket(new PacketPing(var1.responseKey));
      }

      this.waitAttempts = 0;
   }

   private PacketManager newPacketManager() {
      return new PacketManager() {
         public void processInstantly(NetworkPacket var1) {
            var1.getTypePacket().processClient(var1, Client.this);
         }
      };
   }

   public void reloadMap() {
      GlobalData.setCurrentState(new MainMenu(this));
   }

   public Container getContainer() {
      return this.openContainer != null ? this.openContainer : this.inventoryContainer;
   }

   private void openContainer(Container var1, boolean var2, boolean var3) {
      if (this.openContainer != null) {
         this.openContainer.onClose();
      }

      this.openContainer = var1;
      PlayerMob var4 = this.getPlayer();
      this.shouldCloseInventoryOnContainerClose = false;
      if (var2) {
         this.shouldCloseInventoryOnContainerClose = !var4.isInventoryExtended();
         var4.setInventoryExtended(true);
      } else if (var3) {
         var4.setInventoryExtended(false);
      }

   }

   public void openContainerForm(ContainerComponent<?> var1) {
      this.openContainer(var1.getContainer(), var1.shouldOpenInventory(), var1.shouldCloseInventory());
      this.setFocusForm(var1);
      this.openContainer.init();
      GlobalData.updateCraftable();
   }

   public void closeContainer(boolean var1) {
      if (this.openContainer != null) {
         if (var1) {
            this.network.sendPacket(new PacketCloseContainer());
         }

         if (this.shouldCloseInventoryOnContainerClose && this.getPlayer().getDraggingItem() == null) {
            this.getPlayer().setInventoryExtended(false);
         }

         this.openContainer.onClose();
         this.openContainer = null;
         this.resetFocusForm();
      }

   }

   public boolean hasOpenContainer() {
      return this.openContainer != null;
   }

   public void syncOpenContainer(int var1) {
      if (var1 == -1) {
         if (this.openContainer != null) {
            this.closeContainer(false);
         }
      } else if (this.openContainer == null) {
         this.network.sendPacket(new PacketCloseContainer());
      } else if (this.openContainer.uniqueSeed != var1) {
         this.closeContainer(true);
      }

   }

   public Container getInventoryContainer() {
      return this.inventoryContainer;
   }

   public void initInventoryContainer() {
      this.inventoryContainer = new Container(this.getClient(), 0);
   }

   public void saveAndClose(String var1, PacketDisconnect.Code var2) {
      this.instantDisconnect(var1);
      if (this.getLocalServer() != null) {
         this.getLocalServer().stop(var2);
      }

   }
}
