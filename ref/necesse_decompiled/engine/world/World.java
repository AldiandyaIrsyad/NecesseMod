package necesse.engine.world;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.attribute.FileTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Supplier;
import necesse.engine.GameLog;
import necesse.engine.GlobalData;
import necesse.engine.network.networkInfo.NetworkInfo;
import necesse.engine.network.packet.PacketChangeWorldTime;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.quest.QuestManager;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.save.LevelSave;
import necesse.engine.save.LoadData;
import necesse.engine.save.WorldEntitySave;
import necesse.engine.team.TeamManager;
import necesse.engine.tickManager.Performance;
import necesse.engine.tickManager.PerformanceTimerManager;
import necesse.engine.tickManager.PerformanceTimerUtils;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameUtils;
import necesse.engine.util.LevelIdentifier;
import necesse.gfx.HumanLook;
import necesse.level.maps.Level;
import necesse.level.maps.TemporaryDummyLevel;
import necesse.level.maps.biomes.Biome;

public class World {
   public final String displayName;
   public final File filePath;
   public WorldFileSystem fileSystem;
   public final LevelManager levelManager;
   public final Server server;
   public final WorldSettings settings;
   public WorldEntity worldEntity;

   private World(File var1) throws IOException, FileSystemClosedException {
      this.filePath = var1;
      if (this.filePath != null && this.filePath.getName().length() != 0) {
         this.displayName = getWorldDisplayName(this.filePath.getName());
         this.server = null;
         this.reloadFileSystem();
         this.settings = new WorldSettings(this);
         this.settings.loadSettings(false);
         this.levelManager = new LevelManager(this);
         if (this.fileSystem.worldEntityFileExists()) {
            this.worldEntity = this.loadWorldEntity(true);
         }

      } else {
         throw new IllegalArgumentException("Server name cannot be null or empty");
      }
   }

   public World(Server var1) throws IOException, FileSystemClosedException {
      this.filePath = var1.settings.worldFilePath;
      if (this.filePath != null && this.filePath.getName().length() != 0) {
         this.displayName = getWorldDisplayName(this.filePath.getName());
         this.server = var1;
         this.reloadFileSystem();
         this.settings = new WorldSettings(this);
         this.levelManager = new LevelManager(this);
      } else {
         throw new IllegalArgumentException("Server name cannot be null or empty");
      }
   }

   public static World getSaveDataWorld(File var0) throws IOException, FileSystemClosedException {
      return new World(var0);
   }

   private static String encodedPath(String var0) {
      return var0.replace("%", "%25").replace("^", "%5E").replace("\u00b4", "%C2%B4").replace("`", "%60").replace("#", "%23").replace("!", "%21").replace("{", "%7B").replace("}", "%7D").replace("[", "%5B").replace("]", "%5D").replace(" ", "%20").replace("\t", "%09").replace("\u3000", "%E3%80%80");
   }

   public static WorldFileSystem getFileSystem(File var0, boolean var1) throws IOException, FileSystemClosedException {
      String var2 = var0.getAbsolutePath();
      String var3 = encodedPath(var2);
      return new WorldFileSystem(var2, var3, var1, false);
   }

   public static String getWorldDisplayName(String var0) {
      return GameUtils.removeFileExtension(var0);
   }

   public static File getExistingWorldFilePath(String var0) {
      String[] var1 = loadWorldsFromPaths();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         String var4 = var1[var3];
         File var5 = new File(var4 + var0 + "/");
         if (var5.exists() && var5.isDirectory()) {
            return var5;
         }

         File var6 = new File(var4 + var0 + ".zip");
         if (var6.exists() && !var6.isDirectory()) {
            return var6;
         }
      }

      return null;
   }

   public static boolean isWorldADirectory(File var0) {
      if (!var0.exists()) {
         return false;
      } else {
         return var0.getName().endsWith(".zip") ? var0.isDirectory() : true;
      }
   }

   public static boolean worldExists(File var0) {
      if (!var0.exists()) {
         return false;
      } else {
         return var0.isFile() && var0.getName().endsWith(".zip") ? true : var0.isDirectory();
      }
   }

   public static File worldExists(String var0) {
      String[] var1 = loadWorldsFromPaths();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         String var4 = var1[var3];
         File var5 = new File(var4 + var0);
         if (worldExists(var5)) {
            return var5;
         }
      }

      return null;
   }

   public static File worldExistsWithName(String var0) {
      if (var0.endsWith(".zip")) {
         var0 = var0.substring(0, var0.length() - 4);
      }

      File var1 = worldExists(var0);
      return var1 != null ? var1 : worldExists(var0 + ".zip");
   }

   public static void copyWorld(File var0, File var1, boolean var2) throws IOException {
      if (!var0.exists()) {
         throw new FileNotFoundException();
      } else {
         try {
            GameUtils.copyFileOrFolderReplaceExisting(var0, var1);
         } catch (IOException var7) {
            deleteWorld(var1);
            throw var7;
         }

         if (var0.isFile() && var0.getName().endsWith(".zip") && var2) {
            try {
               WorldFileSystem var3 = getFileSystem(var1, true);

               try {
                  var3.fixArchiveFirstFolder(getWorldDisplayName(var1.getName()));
               } catch (Throwable var8) {
                  if (var3 != null) {
                     try {
                        var3.close();
                     } catch (Throwable var6) {
                        var8.addSuppressed(var6);
                     }
                  }

                  throw var8;
               }

               if (var3 != null) {
                  var3.close();
               }
            } catch (IOException var9) {
               deleteWorld(var1);
               throw var9;
            } catch (FileSystemClosedException var10) {
               deleteWorld(var1);
               throw new IOException(var10);
            }
         }

      }
   }

   public static void moveWorld(File var0, File var1) throws IOException {
      if (!var0.exists()) {
         throw new FileNotFoundException();
      } else {
         GameUtils.moveFileOrFolderReplaceExisting(var0, var1);
         if (var0.isFile() && var0.getName().endsWith(".zip")) {
            try {
               WorldFileSystem var2 = getFileSystem(var1, true);

               try {
                  var2.fixArchiveFirstFolder(getWorldDisplayName(var1.getName()));
               } catch (Throwable var6) {
                  if (var2 != null) {
                     try {
                        var2.close();
                     } catch (Throwable var5) {
                        var6.addSuppressed(var5);
                     }
                  }

                  throw var6;
               }

               if (var2 != null) {
                  var2.close();
               }
            } catch (FileSystemClosedException var7) {
               throw new IOException(var7);
            }
         }

      }
   }

   public static boolean deleteWorld(File var0) {
      return !var0.exists() ? false : GameUtils.deleteFileOrFolder(var0);
   }

   public void init() {
      this.settings.loadSettings();
      if (this.fileSystem.worldEntityFileExists()) {
         this.worldEntity = this.loadWorldEntity();
      } else {
         System.out.println("Creating save with name: " + this.filePath.getName());
      }

      if (this.worldEntity == null) {
         System.out.println("Could not find world file, creating new one: " + this.filePath.getName());
         this.worldEntity = WorldEntity.getServerWorldEntity(this, this.server.settings.spawnSeed, this.server.settings.spawnIsland);
         this.worldEntity.initServer(this.server.settings.spawnGuide);
      }

      this.saveWorldEntity();
   }

   public void serverTick() {
      this.levelManager.serverTick();
      this.worldEntity.serverTick();
   }

   public void frameTick(TickManager var1) {
      this.worldEntity.serverFrameTick(var1);
      this.levelManager.frameTick(var1);
   }

   public Level getLevel(ServerClient var1) {
      return this.getLevel(var1.getLevelIdentifier());
   }

   public Level getLevel(LevelIdentifier var1) {
      return this.getLevel(var1, (Supplier)null);
   }

   public Level getLevel(LevelIdentifier var1, Supplier<Level> var2) {
      Object var3 = this.levelManager.getLevel(var1);
      if (var3 == null || var3 instanceof TemporaryDummyLevel && var2 != null) {
         if (var3 == null) {
            this.levelManager.loadLevel(var1);
         }

         var3 = this.levelManager.getLevel(var1);
         if (var3 == null || var3 instanceof TemporaryDummyLevel && var2 != null) {
            if (var2 != null) {
               var3 = (Level)var2.get();
               if (var3 != null) {
                  ((Level)var3).overwriteIdentifier(var1);
                  ((Level)var3).makeServerLevel(this.server);
                  ((Level)var3).entityManager.refreshSetLevel();
                  this.levelManager.overwriteLevel((Level)var3);
                  this.levelManager.loadLevel(var1);
               }
            }

            if (var3 == null) {
               if (var1.isIslandPosition()) {
                  var3 = this.getGeneratedLevel(var1.getIslandX(), var1.getIslandY(), var1.getIslandDimension());
                  ((Level)var3).overwriteIdentifier(var1);
                  ((Level)var3).makeServerLevel(this.server);
                  this.levelManager.overwriteLevel((Level)var3);
               } else {
                  var3 = new TemporaryDummyLevel(var1, this.worldEntity);
                  ((Level)var3).overwriteIdentifier(var1);
                  ((Level)var3).makeServerLevel(this.server);
                  ((Level)var3).biome = BiomeRegistry.UNKNOWN;
                  this.levelManager.overwriteLevel((Level)var3);
               }
            }
         }

         this.server.levelCache.updateLevel((Level)var3);
      }

      return (Level)var3;
   }

   public void generateNewLevel(int var1, int var2, int var3) {
      this.levelManager.overwriteLevel(this.getGeneratedLevel(var1, var2, var3));
   }

   private Level getGeneratedLevel(int var1, int var2, int var3) {
      Level var4 = WorldGenerator.generateNewLevel(var1, var2, var3, this.server);
      var4.makeServerLevel(this.server);
      var4.setWorldEntity(this.server.world.worldEntity);
      var4.overwriteIdentifier(new LevelIdentifier(var1, var2, var3));
      var4.entityManager.refreshSetLevel();
      return var4;
   }

   public Biome getBiome(int var1, int var2) {
      LevelIdentifier var3 = new LevelIdentifier(var1, var2, 0);
      if (this.server.world.levelManager.isLoaded(var3)) {
         return this.server.world.levelManager.getLevel(var3).biome;
      } else if (this.server.world.levelExists(var3)) {
         LoadData var4 = this.server.world.loadLevelScript(var3);
         Biome var5 = LevelSave.getLevelSaveBiome(var4);
         return var5 == null ? BiomeRegistry.UNKNOWN : var5;
      } else {
         return WorldGenerator.getBiome(var1, var2);
      }
   }

   public long getUniqueID() {
      return this.worldEntity.getUniqueID();
   }

   public long getTime() {
      return this.worldEntity.getTime();
   }

   public long getLocalTime() {
      return this.worldEntity.getLocalTime();
   }

   public long getWorldTime() {
      return this.worldEntity.getWorldTime();
   }

   public void addWorldTime(long var1) {
      this.worldEntity.addWorldTime(var1);
      this.server.network.sendToAllClients(new PacketChangeWorldTime(this.server));
   }

   public long getTimeToNextTimeOfDay(int var1) {
      long var2 = (long)(this.worldEntity.getDayTimeMax() - this.worldEntity.getDayTimeInt() + var1) * 1000L;
      if (this.worldEntity.getWorldTime() + var2 - (long)this.worldEntity.getDayTimeMax() * 1000L > this.worldEntity.getWorldTime()) {
         var2 -= (long)this.worldEntity.getDayTimeMax() * 1000L;
      }

      return var2;
   }

   public void setDawn() {
      this.addWorldTime(this.getTimeToNextTimeOfDay(this.worldEntity.getDayDuration() + this.worldEntity.getDayToNightDuration() + this.worldEntity.getNightDuration()));
   }

   public void setMorning() {
      long var1 = (long)(this.worldEntity.getDayTimeMax() - this.worldEntity.getDayTimeInt()) * 1000L;
      this.addWorldTime(var1);
   }

   public void setMidday() {
      this.addWorldTime(this.getTimeToNextTimeOfDay(this.worldEntity.getDayDuration() / 2));
   }

   public void setDusk() {
      this.addWorldTime(this.getTimeToNextTimeOfDay(this.worldEntity.getDayDuration()));
   }

   public void setNight() {
      this.addWorldTime(this.getTimeToNextTimeOfDay(this.worldEntity.getDayDuration() + this.worldEntity.getDayToNightDuration()));
   }

   public void setMidnight() {
      this.addWorldTime(this.getTimeToNextTimeOfDay(this.worldEntity.getDayDuration() + this.worldEntity.getDayToNightDuration() + this.worldEntity.getNightDuration() / 2));
   }

   public WorldEntity loadWorldEntity() {
      return this.loadWorldEntity(false);
   }

   public FileTime getLastModified() {
      try {
         return this.fileSystem.getLastModified();
      } catch (IOException var2) {
         return FileTime.fromMillis(0L);
      }
   }

   public WorldEntity loadWorldEntity(boolean var1) {
      WorldFile var2 = this.fileSystem.getWorldEntityFile();
      if (!var2.exists()) {
         System.err.println("Could not find WorldEntity file for " + this.filePath.getName());
         return null;
      } else {
         WorldEntity var3 = WorldEntitySave.loadSave(new LoadData(var2), var1, this);
         if (var3 == null) {
            GameLog.warn.println("World file is corrupt for " + this.filePath.getName());
            return null;
         } else {
            return var3;
         }
      }
   }

   public void simulateWorldTime(long var1, boolean var3) {
      Iterator var4 = this.levelManager.getLoadedLevels().iterator();

      while(var4.hasNext()) {
         Level var5 = (Level)var4.next();
         var5.simulateWorldTime(var1, var3);
      }

   }

   public Level loadLevel(WorldFile var1) {
      LoadData var2 = this.loadLevelScript(var1);
      if (var2 == null) {
         System.out.println("Could not find Level: " + var1 + " file for " + this.filePath.getName());
         return null;
      } else {
         Level var3 = LevelSave.loadSave(var2, this.server);

         Object var5;
         try {
            if (var3 != null) {
               Performance.recordConstant(LevelSave.debugLoadingPerformance, "finalize", () -> {
                  PerformanceTimerManager var10000 = LevelSave.debugLoadingPerformance;
                  Objects.requireNonNull(var3);
                  Performance.recordConstant(var10000, "repair", var3::onLoadingComplete);
                  Performance.recordConstant(LevelSave.debugLoadingPerformance, "simulate", () -> {
                     var3.simulateSinceLastWorldTime(false);
                  });
               });
               Level var11 = var3;
               return var11;
            }

            GameLog.warn.println("Level file " + var1 + " is corrupt for " + this.filePath.getName());
            String var4 = "corruptlevels/" + GameUtils.removeFileExtension(var1.getFileName().toString()) + " - Corrupt" + ".dat";
            GameLog.warn.println("Backing up corrupt level file to " + var4);

            try {
               var1.copyTo(this.fileSystem.file(var4));
            } catch (IOException var9) {
               System.err.println("Error copying corrupt file");
               var9.printStackTrace();
            }

            var5 = null;
         } finally {
            if (LevelSave.debugLoadingPerformance != null) {
               PerformanceTimerUtils.printPerformanceTimer(LevelSave.debugLoadingPerformance.getCurrentRootPerformanceTimer());
            }

            LevelSave.debugLoadingPerformance = null;
         }

         return (Level)var5;
      }
   }

   public Level loadLevel(LevelIdentifier var1) {
      Level var2 = this.loadLevel(this.fileSystem.getLevelFile(var1));
      if (var2 != null) {
         var2.overwriteIdentifier(var1);
      }

      return var2;
   }

   public LoadData loadLevelScript(WorldFile var1) {
      return var1.exists() && !var1.isDirectory() ? new LoadData(var1) : null;
   }

   public LoadData loadLevelScript(LevelIdentifier var1) {
      return this.loadLevelScript(this.fileSystem.getLevelFile(var1));
   }

   public LoadData loadClientScript(WorldFile var1) {
      if (var1.exists() && !var1.isDirectory()) {
         return new LoadData(var1);
      } else {
         System.out.println("Could not find Player: " + var1 + " in directory");
         return null;
      }
   }

   public LoadData loadClientScript(long var1) {
      return this.loadClientScript(this.fileSystem.getPlayerFile(var1));
   }

   public ServerClient loadClient(WorldFile var1, long var2, NetworkInfo var4, int var5, long var6) {
      LoadData var8 = this.loadClientScript(var1);
      if (var8 == null) {
         GameLog.warn.println("Player file is corrupt for " + var1);
         return null;
      } else {
         return new ServerClient(this.server, var2, var4, var5, var6, var8);
      }
   }

   public ServerClient loadClient(long var1, long var3, NetworkInfo var5, int var6) {
      return this.loadClient(this.fileSystem.getPlayerFile(var3), var1, var5, var6, var3);
   }

   public String loadClientName(WorldFile var1) {
      LoadData var2 = this.loadClientScript(var1);
      return var2 == null ? "N/A" : ServerClient.loadClientName(var2);
   }

   public String loadClientName(long var1) {
      return this.loadClientName(this.fileSystem.getPlayerFile(var1));
   }

   public HumanLook loadClientLook(WorldFile var1) {
      LoadData var2 = this.loadClientScript(var1);
      return var2 == null ? new HumanLook() : ServerClient.loadClientLook(var2);
   }

   public HumanLook loadClientLook(long var1) {
      return this.loadClientLook(this.fileSystem.getPlayerFile(var1));
   }

   public PlayerStats loadClientStats(WorldFile var1) {
      LoadData var2 = this.loadClientScript(var1);
      return var2 == null ? new PlayerStats(false, PlayerStats.Mode.READ_ONLY) : ServerClient.loadClientStats(var2);
   }

   public PlayerStats loadClientStats(long var1) {
      return this.loadClientStats(this.fileSystem.getPlayerFile(var1));
   }

   public boolean hasClient(long var1) {
      return this.fileSystem.playerFileExists(var1);
   }

   public void saveWorldEntity() {
      WorldFile var1 = this.fileSystem.getWorldEntityFile();
      WorldEntitySave.getSave(this.worldEntity).saveScript(var1);
   }

   public void saveLevel(LevelIdentifier var1) {
      Level var2 = this.getLevel(var1);
      if (var2 != null) {
         this.saveLevel(var2);
      }

   }

   public void saveLevel(Level var1) {
      if (var1.shouldSave()) {
         LevelSave.getSave(var1).saveScript(this.fileSystem.getLevelFile(var1));
      }
   }

   public void savePlayer(ServerClient var1) {
      var1.getSave().saveScript(this.fileSystem.getPlayerFile(var1));
   }

   public LoadData loadPlayerMap(ServerClient var1) {
      WorldFile var2 = this.fileSystem.getMapPlayerFile(var1);
      return var2.exists() && !var2.isDirectory() ? new LoadData(var2) : null;
   }

   public boolean levelExists(LevelIdentifier var1) {
      return this.levelManager.isLoaded(var1) || this.fileSystem.levelFileExists(var1);
   }

   public QuestManager getQuests() {
      return this.worldEntity.quests;
   }

   public TeamManager getTeams() {
      return this.worldEntity.teams;
   }

   public HashMap<Long, String> getUsedPlayerNames() {
      HashMap var1 = new HashMap();
      Iterator var2 = this.fileSystem.getPlayerFiles().iterator();

      while(var2.hasNext()) {
         WorldFile var3 = (WorldFile)var2.next();

         try {
            String var4 = GameUtils.removeFileExtension(var3.getFileName().toString());
            long var5 = Long.parseLong(var4);
            String var7 = this.loadClientName(var3);
            if (var7 != null && var7.length() > 0) {
               var1.put(var5, var7);
            }
         } catch (Exception var8) {
            GameLog.warn.println("Found invalid player file name: " + var3);
         }
      }

      return var1;
   }

   public void dispose() {
      this.levelManager.dispose();

      try {
         this.closeFileSystem();
      } catch (IOException var2) {
         System.err.println("Error closing world file system");
         var2.printStackTrace();
      }

   }

   public void reloadFileSystem() throws IOException, FileSystemClosedException {
      if (this.fileSystem != null && this.fileSystem.isOpen()) {
         this.fileSystem.close();
      }

      this.fileSystem = getFileSystem(this.filePath, true);
   }

   public void closeFileSystem() throws IOException {
      if (this.fileSystem != null && this.fileSystem.isOpen()) {
         this.fileSystem.close();
      }

   }

   public static String getSavesPath() {
      return GlobalData.appDataPath() + "saves/";
   }

   public static String getWorldsPath() {
      return getSavesPath() + "worlds/";
   }

   public static String[] loadWorldsFromPaths() {
      return new String[]{getWorldsPath(), getSavesPath()};
   }
}
