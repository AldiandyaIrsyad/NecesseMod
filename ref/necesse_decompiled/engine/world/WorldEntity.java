package necesse.engine.world;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import necesse.engine.GameLog;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModLoader;
import necesse.engine.modLoader.ModSaveInfo;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketChangeWorldTime;
import necesse.engine.network.packet.PacketQuestUpdate;
import necesse.engine.network.packet.PacketWorldData;
import necesse.engine.network.packet.PacketWorldEvent;
import necesse.engine.network.server.Server;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.quest.Quest;
import necesse.engine.quest.QuestManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.WorldDataRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.engine.save.levelData.QuestSave;
import necesse.engine.save.levelData.WorldEventSave;
import necesse.engine.team.TeamManager;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.InvalidLevelIdentifierException;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.worldData.WorldData;
import necesse.engine.world.worldEvent.WorldEvent;
import necesse.entity.mobs.Mob;
import necesse.level.maps.Level;
import necesse.level.maps.presets.ElderHousePreset;
import necesse.level.maps.presets.Preset;

public class WorldEntity implements GameClock {
   public static float sleepingModifier = 20.0F;
   private long uniqueID;
   private long worldTime;
   private float worldTimeMod;
   private long time;
   private long localTime;
   private float timeMod;
   private float timeBuffer;
   private float worldTimeBuffer;
   private boolean lastPreventSleep;
   private boolean preventSleep;
   private boolean lastIsSleeping;
   private boolean isSleeping;
   private float addedSleepingTime;
   private long gameTicks;
   private long frameTicks;
   public final PlayerStats worldStats;
   public final QuestManager quests;
   public final TeamManager teams;
   public LevelIdentifier spawnLevelIdentifier;
   public Point spawnTile;
   public ArrayList<ModSaveInfo> lastMods;
   public ArrayList<WorldEvent> events;
   private HashMap<String, WorldData> data;
   public Client client;
   public World serverWorld;
   private float[] dayMods;
   private int[] dayShares;
   private int dayTotal;
   public static int DEFAULT_DAY_TOTAL = 960;
   public static float[] DEFAULT_DAY_MODS = new float[]{0.4F, 0.1F, 0.4F, 0.1F};

   private WorldEntity(Server var1) {
      this.worldStats = new PlayerStats(false, PlayerStats.Mode.READ_AND_WRITE);
      this.lastMods = null;
      this.events = new ArrayList();
      this.data = new HashMap();
      this.quests = new QuestManager(var1);
      this.teams = new TeamManager(var1);
      this.worldTime = 0L;
      this.worldTimeMod = 1.0F;
      this.time = 0L;
      this.localTime = 0L;
      this.timeMod = 1.0F;
      this.resetUniqueID();
      this.calculateAmbientLightValues();
      this.spawnTile = new Point(150, 150);
   }

   private WorldEntity(World var1, int var2, Point var3) {
      this(var1.server);
      this.serverWorld = var1;
      if (var3 == null) {
         Point var4 = WorldGenerator.getStartingIsland(var2);
         this.spawnLevelIdentifier = new LevelIdentifier(var4.x, var4.y, 0);
      } else {
         this.spawnLevelIdentifier = new LevelIdentifier(var3.x, var3.y, 0);
      }

      this.spawnTile = new Point(150, 150);
      this.calculateAmbientLightValues();
   }

   public void initServer(boolean var1) {
      Level var2 = this.serverWorld.getLevel(this.spawnLevelIdentifier);
      if (var2 != null) {
         this.spawnTile = new Point(var2.width / 2, var2.height / 2);
         var2.setObject(this.spawnTile.x, this.spawnTile.y, 0);
         GameRandom var3 = new GameRandom(var2.getSeed());
         ArrayList var5;
         int var7;
         int var8;
         Point var13;
         int var14;
         if (var1) {
            Object var4 = new ElderHousePreset(var3);
            if (var3.nextBoolean()) {
               var4 = ((Preset)var4).tryMirrorX();
            }

            var5 = new ArrayList();
            Rectangle var6 = new Rectangle(-4, -4, 8, 8);

            for(var7 = -((Preset)var4).width - 4; var7 < ((Preset)var4).width / 2 + 4; ++var7) {
               for(var8 = -((Preset)var4).height - 4; var8 < ((Preset)var4).height / 2 + 4; ++var8) {
                  Rectangle var9 = new Rectangle(var7, var8, ((Preset)var4).width, ((Preset)var4).height);
                  if (!var6.intersects(var9)) {
                     var5.add(new Point(var7, var8));
                  }
               }
            }

            var13 = (Point)var5.get(var3.nextInt(var5.size()));
            var8 = this.spawnTile.x + var13.x;
            var14 = this.spawnTile.y + var13.y;
            Rectangle var10 = new Rectangle(var8 * 32, var14 * 32, ((Preset)var4).width * 32, ((Preset)var4).height * 32);
            var2.entityManager.mobs.streamInRegionsShape(var10, 1).filter((var1x) -> {
               return var10.intersects(var1x.getCollision());
            }).forEach(Mob::remove);
            ((Preset)var4).applyToLevel(var2, var8, var14);
         } else {
            Mob var11 = MobRegistry.getMob("elderhuman", var2);
            var5 = new ArrayList();
            byte var12 = 8;

            for(var7 = this.spawnTile.x - var12; var7 < this.spawnTile.x + var12; ++var7) {
               for(var8 = this.spawnTile.y - var12; var8 < this.spawnTile.y + var12; ++var8) {
                  var14 = var7 * 32 + 16;
                  int var15 = var8 * 32 + 16;
                  if (!var11.collidesWith(var2, var14, var15)) {
                     var5.add(new Point(var14, var15));
                  }
               }
            }

            var13 = new Point(this.spawnTile.x * 32 + 16, this.spawnTile.y * 32 + 16);
            if (var5.size() > 0) {
               var13 = (Point)var5.get(var3.nextInt(var5.size()));
            }

            var2.entityManager.addMob(var11, (float)var13.x, (float)var13.y);
         }
      }

   }

   private WorldEntity(Client var1) {
      this((Server)null);
      this.client = var1;
      this.calculateAmbientLightValues();
   }

   public SaveData getSave() {
      SaveData var1 = new SaveData("WORLD");
      var1.addLong("uniqueID", this.uniqueID);
      var1.addSafeString("gameVersion", "0.24.2");
      var1.addLong("worldTime", this.worldTime);
      var1.addFloat("worldTimeMod", this.worldTimeMod);
      var1.addLong("time", this.time);
      var1.addFloat("timeMod", this.timeMod);
      var1.addUnsafeString("spawnLevel", this.spawnLevelIdentifier.stringID);
      if (this.spawnLevelIdentifier.isIslandPosition()) {
         var1.addPoint("spawnIsland", new Point(this.spawnLevelIdentifier.getIslandX(), this.spawnLevelIdentifier.getIslandY()));
      }

      var1.addPoint("spawnTile", this.spawnTile);
      SaveData var2 = new SaveData("MODS");
      Iterator var3 = ModLoader.getEnabledMods().iterator();

      while(var3.hasNext()) {
         LoadedMod var4 = (LoadedMod)var3.next();
         SaveData var5 = (new ModSaveInfo(var4)).getSaveData();
         var2.addSaveData(var5);
      }

      var1.addSaveData(var2);
      SaveData var10 = new SaveData("EVENTS");
      Iterator var11 = this.events.iterator();

      while(var11.hasNext()) {
         WorldEvent var13 = (WorldEvent)var11.next();
         if (var13.shouldSave) {
            var10.addSaveData(WorldEventSave.getSave(var13));
         }
      }

      var1.addSaveData(var10);
      SaveData var12 = new SaveData("WORLDDATA");
      HashMap var14 = this.getWorldData();
      Iterator var6 = var14.keySet().iterator();

      SaveData var8;
      while(var6.hasNext()) {
         String var7 = (String)var6.next();
         var8 = new SaveData(var7);
         ((WorldData)var14.get(var7)).addSaveData(var8);
         var12.addSaveData(var8);
      }

      var1.addSaveData(var12);
      SaveData var15 = new SaveData("TEAMS");
      this.teams.addSaveData(var15);
      var1.addSaveData(var15);
      SaveData var16 = new SaveData("QUESTS");
      Iterator var17 = this.quests.getQuests().iterator();

      while(var17.hasNext()) {
         Quest var9 = (Quest)var17.next();
         if (!var9.isRemoved()) {
            var16.addSaveData(QuestSave.getSave(var9));
         }
      }

      var1.addSaveData(var16);
      var8 = new SaveData("STATS");
      this.worldStats.addSaveData(var8);
      var1.addSaveData(var8);
      return var1;
   }

   public void applyLoadData(LoadData var1, boolean var2) {
      this.uniqueID = var1.getLong("uniqueID", this.uniqueID, false);
      this.worldTime = var1.getLong("worldTime", this.worldTime);
      this.worldTimeMod = var1.getFloat("worldTimeMod", this.worldTimeMod);
      this.time = var1.getLong("time", this.time);
      this.timeMod = var1.getFloat("timeMod", this.timeMod);

      try {
         this.spawnLevelIdentifier = new LevelIdentifier(var1.getUnsafeString("spawnLevel", (String)null, false));
      } catch (InvalidLevelIdentifierException var14) {
         Point var4 = var1.getPoint("spawnIsland", new Point(0, 0));
         this.spawnLevelIdentifier = new LevelIdentifier(var4.x, var4.y, 0);
      }

      if (var1.getFirstLoadDataByName("spawnPoint") != null) {
         Point var3 = var1.getPoint("spawnPoint", new Point(4800, 4800));
         this.spawnTile = new Point(var3.x / 32, var3.y / 32);
      } else {
         this.spawnTile = var1.getPoint("spawnTile", this.spawnTile);
      }

      LoadData var18 = var1.getFirstLoadDataByName("MODS");
      LoadData var5;
      if (var18 != null) {
         this.lastMods = new ArrayList();
         Iterator var19 = var18.getLoadData().iterator();

         while(var19.hasNext()) {
            var5 = (LoadData)var19.next();

            try {
               this.lastMods.add(new ModSaveInfo(var5));
            } catch (LoadDataException var13) {
               GameLog.warn.print("Could not load mod info: " + var13.getMessage());
            }
         }
      }

      LoadData var20 = var1.getFirstLoadDataByName("STATS");
      if (var20 != null) {
         this.worldStats.applyLoadData(var20);
      } else if (!var2) {
         GameLog.warn.println("Could not load world stats");
      }

      this.calculateAmbientLightValues();
      if (!var2) {
         Iterator var6;
         LoadData var7;
         List var21;
         try {
            var21 = var1.getFirstLoadDataByName("EVENTS").getLoadData();
            var6 = var21.iterator();

            while(var6.hasNext()) {
               var7 = (LoadData)var6.next();

               try {
                  WorldEvent var8 = WorldEventSave.loadSave(var7);
                  if (var8 != null) {
                     this.addWorldEvent(var8);
                  }
               } catch (Exception var12) {
                  var12.printStackTrace();
               }
            }
         } catch (Exception var17) {
            GameLog.warn.println("Could not complete loading of world events");
         }

         try {
            var21 = var1.getFirstLoadDataByName("WORLDDATA").getLoadData();
            var6 = var21.iterator();

            while(var6.hasNext()) {
               var7 = (LoadData)var6.next();

               try {
                  WorldData var22 = WorldDataRegistry.loadWorldData(this, var7);
                  if (var22 != null) {
                     this.addWorldData(var7.getName(), var22);
                  }
               } catch (Exception var11) {
                  var11.printStackTrace();
               }
            }
         } catch (Exception var16) {
            GameLog.warn.println("Could not complete loading of world data");
         }

         try {
            var5 = var1.getFirstLoadDataByName("TEAMS");
            if (var5 != null) {
               this.teams.applySaveData(var5);
            }
         } catch (Exception var10) {
            GameLog.warn.println("Could not complete loading of teams");
         }

         try {
            var21 = var1.getFirstLoadDataByName("QUESTS").getLoadData();
            var6 = var21.iterator();

            while(var6.hasNext()) {
               var7 = (LoadData)var6.next();

               try {
                  Quest var23 = QuestSave.loadSave(var7);
                  if (var23 != null) {
                     this.quests.addQuest(var23, false);
                  }
               } catch (Exception var9) {
                  var9.printStackTrace();
               }
            }
         } catch (Exception var15) {
            GameLog.warn.println("Could not complete loading of quests");
         }
      }

   }

   public void applyWorldPacket(PacketWorldData var1) {
      this.worldTime = var1.worldTime;
      this.time = var1.time;
      this.isSleeping = var1.isSleeping;
   }

   private WorldSettings getWorldSettings() {
      if (this.client != null) {
         return this.client.worldSettings;
      } else {
         return this.serverWorld != null ? this.serverWorld.settings : null;
      }
   }

   public long getUniqueID() {
      return this.uniqueID;
   }

   public void resetUniqueID() {
      this.uniqueID = GameRandom.globalRandom.nextLong();
   }

   public long getTime() {
      return this.time;
   }

   public long getLocalTime() {
      return this.localTime;
   }

   public long getWorldTime() {
      return this.worldTime;
   }

   public long getGameTicks() {
      return this.gameTicks;
   }

   public long getFrameTicks() {
      return this.frameTicks;
   }

   public void clientFrameTick(TickManager var1) {
      ++this.frameTicks;
      this.tickTime(var1);
   }

   public void clientTick() {
      this.lastPreventSleep = this.preventSleep;
      this.preventSleep = false;
      ++this.gameTicks;

      for(int var1 = 0; var1 < this.events.size(); ++var1) {
         WorldEvent var2 = (WorldEvent)this.events.get(var1);
         if (var2.isOver()) {
            this.events.remove(var1);
            --var1;
         } else {
            var2.clientTick();
         }
      }

      Iterator var3 = this.data.values().iterator();

      while(var3.hasNext()) {
         WorldData var4 = (WorldData)var3.next();
         var4.tick();
      }

   }

   public void serverFrameTick(TickManager var1) {
      ++this.frameTicks;
      this.tickTime(var1);
   }

   public void serverTick() {
      this.lastPreventSleep = this.preventSleep;
      this.preventSleep = false;
      boolean var1 = this.isSleeping();
      this.lastIsSleeping = this.isSleeping;
      this.isSleeping = false;
      ++this.gameTicks;

      int var2;
      for(var2 = 0; var2 < this.events.size(); ++var2) {
         WorldEvent var3 = (WorldEvent)this.events.get(var2);
         if (var3.isOver()) {
            this.events.remove(var2);
            --var2;
         } else {
            var3.serverTick();
         }
      }

      Iterator var5 = this.data.values().iterator();

      while(var5.hasNext()) {
         WorldData var6 = (WorldData)var5.next();
         var6.tick();
      }

      if (this.serverWorld != null) {
         if (this.quests.isDirty()) {
            LinkedList var7 = new LinkedList();
            Iterator var8 = this.quests.getQuests().iterator();

            while(var8.hasNext()) {
               Quest var4 = (Quest)var8.next();
               if (var4.isRemoved()) {
                  var7.add(var4);
               } else if (var4.isDirty()) {
                  this.serverWorld.server.network.sendPacket(new PacketQuestUpdate(var4), (Predicate)((var1x) -> {
                     return var4.isActiveFor(var1x.authentication);
                  }));
                  var4.clean();
               }
            }

            QuestManager var10001 = this.quests;
            Objects.requireNonNull(var10001);
            var7.forEach(var10001::removeQuest);
            this.quests.cleanAll();
         }

         var2 = (int)Math.max(5000.0F, sleepingModifier * 1000.0F);
         if (this.addedSleepingTime >= (float)var2) {
            this.serverWorld.simulateWorldTime((long)var2, true);
            this.addedSleepingTime -= (float)var2;
         }
      }

      if (this.serverWorld != null && this.isSleeping() != var1) {
         this.serverWorld.server.network.sendToAllClients(new PacketChangeWorldTime(this.serverWorld.server));
      }

   }

   private void tickTime(TickManager var1) {
      this.worldTimeBuffer += var1.getFullDelta() * this.worldTimeMod;
      if (this.lastIsSleeping) {
         float var2 = var1.getFullDelta() * sleepingModifier;
         this.addedSleepingTime += var2;
         this.worldTimeBuffer += var2;
      }

      long var6 = (long)this.worldTimeBuffer;
      this.worldTimeBuffer -= (float)var6;
      this.worldTime += var6;
      this.timeBuffer += var1.getFullDelta() * this.timeMod;
      long var4 = (long)this.timeBuffer;
      this.timeBuffer -= (float)var4;
      this.time += var4;
      this.localTime += var4;
   }

   public void addWorldTime(long var1) {
      this.worldTime += var1;
   }

   public void setWorldTime(long var1) {
      this.worldTime = var1;
   }

   public boolean isSleeping() {
      return this.isSleeping || this.lastIsSleeping;
   }

   public void keepSleeping() {
      if (!this.isSleeping) {
         this.isSleeping = true;
         if (this.serverWorld != null) {
            this.serverWorld.server.network.sendToAllClients(new PacketChangeWorldTime(this.serverWorld.server));
         }
      }

   }

   public void preventSleep() {
      this.preventSleep = true;
   }

   public boolean isSleepPrevented() {
      return this.lastPreventSleep || this.preventSleep;
   }

   public boolean isWithinWorldBorder(int var1, int var2) {
      if (Settings.worldBorderSize >= 0) {
         LevelIdentifier var3 = this.spawnLevelIdentifier;
         if (var3.isIslandPosition()) {
            return GameMath.squareDistance((float)var1, (float)var2, (float)var3.getIslandX(), (float)var3.getIslandY()) <= (float)Settings.worldBorderSize;
         }
      }

      return true;
   }

   public void applyChangeWorldTimePacket(PacketChangeWorldTime var1) {
      this.worldTime = var1.worldTime;
      this.isSleeping = var1.isSleeping;
   }

   public int getDay() {
      return (int)((double)this.worldTime / 1000.0 / (double)this.getDayTimeMax());
   }

   public float getDayTime() {
      return (float)((double)this.worldTime / 1000.0 % (double)this.getDayTimeMax());
   }

   public int getDayTimeInt() {
      return (int)this.getDayTime();
   }

   public float getDayTimePercent() {
      long var1 = this.worldTime % (long)(this.getDayTimeMax() * 1000);
      return (float)var1 / (float)this.getDayTimeMax() / 1000.0F;
   }

   public float getDayTimeHourFloat() {
      return (this.getDayTimePercent() * 24.0F + this.getHourOffset()) % 24.0F;
   }

   public float hourToDayTime(float var1) {
      var1 -= this.getHourOffset();
      if (var1 < 0.0F) {
         var1 = 24.0F + var1 % 24.0F;
      }

      float var2 = var1 % 24.0F / 24.0F;
      return (float)this.getDayTimeMax() * var2;
   }

   protected float getHourOffset() {
      WorldSettings var1 = this.getWorldSettings();
      float var2 = 1.0F;
      float var3 = 1.0F;
      if (var1 != null) {
         var2 = GameMath.limit(var1.dayTimeMod, 0.0F, 10.0F);
         var3 = GameMath.limit(var1.nightTimeMod, 0.0F, 10.0F);
      }

      return 8.0F / ((var2 + var3) / 2.0F);
   }

   private static float defaultHourToDayTime(float var0) {
      var0 -= 8.0F;
      if (var0 < 0.0F) {
         var0 = 24.0F + var0 % 24.0F;
      }

      float var1 = var0 % 24.0F / 24.0F;
      return (float)DEFAULT_DAY_TOTAL * var1;
   }

   public int getDayTimeHour() {
      return (int)this.getDayTimeHourFloat();
   }

   public int getDayTimeMinute() {
      float var1 = this.getDayTimeHourFloat();
      return (int)((var1 - (float)((int)var1)) * 60.0F);
   }

   public String getDayTimeReadable() {
      int var1 = this.getDayTimeHour();
      int var2 = this.getDayTimeMinute();
      return (var1 < 10 ? "0" + var1 : var1) + ":" + (var2 < 10 ? "0" + var2 : var2);
   }

   public TimeOfDay getTimeOfDay() {
      int var1 = this.getDayTimeInt();
      if ((float)var1 < (float)this.dayShares[0] / (DEFAULT_DAY_MODS[0] * (float)DEFAULT_DAY_TOTAL / defaultHourToDayTime(12.0F))) {
         return WorldEntity.TimeOfDay.MORNING;
      } else if ((float)var1 < (float)this.dayShares[0] / (DEFAULT_DAY_MODS[0] * (float)DEFAULT_DAY_TOTAL / defaultHourToDayTime(14.0F))) {
         return WorldEntity.TimeOfDay.NOON;
      } else if ((float)var1 < (float)this.dayShares[1] / ((DEFAULT_DAY_MODS[0] + DEFAULT_DAY_MODS[1]) * (float)DEFAULT_DAY_TOTAL / defaultHourToDayTime(18.0F))) {
         return WorldEntity.TimeOfDay.AFTERNOON;
      } else if ((float)var1 < (float)this.dayShares[1] / ((DEFAULT_DAY_MODS[0] + DEFAULT_DAY_MODS[1]) * (float)DEFAULT_DAY_TOTAL / defaultHourToDayTime(19.25F))) {
         return WorldEntity.TimeOfDay.EVENING;
      } else {
         return (float)var1 < (float)this.dayShares[3] / ((float)DEFAULT_DAY_TOTAL / defaultHourToDayTime(6.5F)) ? WorldEntity.TimeOfDay.NIGHT : WorldEntity.TimeOfDay.MORNING;
      }
   }

   public boolean isNight() {
      return this.getTimeOfDay() == WorldEntity.TimeOfDay.NIGHT;
   }

   public void calculateAmbientLightValues() {
      float var1 = 1.0F;
      float var2 = 1.0F;
      WorldSettings var3 = this.getWorldSettings();
      if (var3 != null) {
         var1 = GameMath.limit(var3.dayTimeMod, 0.0F, 10.0F);
         var2 = GameMath.limit(var3.nightTimeMod, 0.0F, 10.0F);
         var1 = (float)((int)(var1 * 10.0F)) / 10.0F;
         var2 = (float)((int)(var2 * 10.0F)) / 10.0F;
         if (var1 == 0.0F && var2 == 0.0F) {
            var1 = 1.0F;
         }
      }

      this.dayMods = new float[]{DEFAULT_DAY_MODS[0] * var1, DEFAULT_DAY_MODS[1] * (var1 + var2) / 2.0F, DEFAULT_DAY_MODS[2] * var2, DEFAULT_DAY_MODS[3] * (var1 + var2) / 2.0F};
      float var4 = this.dayMods[0] + this.dayMods[1] + this.dayMods[2] + this.dayMods[3];
      this.dayTotal = (int)((float)DEFAULT_DAY_TOTAL * var4);
      this.dayShares = new int[4];
      int var5 = 0;

      for(int var6 = 0; var6 < 4; ++var6) {
         var5 += (int)((float)DEFAULT_DAY_TOTAL * this.dayMods[var6]);
         this.dayShares[var6] = var5;
      }

   }

   public int getDayTimeMax() {
      return this.dayTotal;
   }

   public float getSunProgress() {
      float var1 = this.getDayTime();
      float var2 = 0.0F;
      int var3 = this.dayTotal - this.getNightDuration();
      if (var1 <= (float)this.dayShares[1]) {
         var2 = (float)this.getDayToNightDuration() + var1;
      } else {
         if (var1 <= (float)this.dayShares[2]) {
            return -1.0F;
         }

         if (var1 <= (float)this.dayShares[3]) {
            var2 = var1 - (float)this.dayShares[2];
         }
      }

      return var2 / (float)var3;
   }

   public float getMoonProgress() {
      float var1 = this.getDayTime();
      float var2 = 0.0F;
      int var3 = this.dayTotal - this.getDayDuration();
      if (var1 <= (float)this.dayShares[0]) {
         return -1.0F;
      } else {
         if (var1 <= (float)this.dayShares[3]) {
            var2 = var1 - (float)this.dayShares[0];
         }

         return var2 / (float)var3;
      }
   }

   public float getMoonLightFloat() {
      float var1 = this.getDayTime();
      float var2 = 0.0F;
      if (var1 <= (float)this.dayShares[0]) {
         var2 = 0.0F;
      } else if (var1 <= (float)this.dayShares[1]) {
         var2 = (var1 - (float)this.dayShares[0]) / ((float)DEFAULT_DAY_TOTAL * this.dayMods[1]);
      } else if (var1 <= (float)this.dayShares[2]) {
         var2 = 1.0F;
      } else if (var1 <= (float)this.dayShares[3]) {
         var2 = Math.abs((var1 - (float)this.dayShares[2]) / ((float)DEFAULT_DAY_TOTAL * this.dayMods[3]) - 1.0F);
      }

      return var2;
   }

   public float getAmbientLightFloat() {
      float var1 = this.getDayTime();
      float var2 = 0.0F;
      if (var1 <= (float)this.dayShares[0]) {
         var2 = 1.0F;
      } else if (var1 <= (float)this.dayShares[1]) {
         var2 = Math.abs((var1 - (float)this.dayShares[0]) / ((float)DEFAULT_DAY_TOTAL * this.dayMods[1]) - 1.0F);
      } else if (var1 <= (float)this.dayShares[2]) {
         var2 = 0.0F;
      } else if (var1 <= (float)this.dayShares[3]) {
         var2 = (var1 - (float)this.dayShares[2]) / ((float)DEFAULT_DAY_TOTAL * this.dayMods[3]);
      }

      return var2;
   }

   public float getAmbientLight() {
      return this.getAmbientLightFloat() * 150.0F;
   }

   public int getDayDuration() {
      return this.dayShares[0];
   }

   public int getDayToNightDuration() {
      return this.dayShares[1] - this.dayShares[0];
   }

   public int getNightDuration() {
      return this.dayShares[2] - this.dayShares[1];
   }

   public int getNightToDayDuration() {
      return this.dayShares[3] - this.dayShares[2];
   }

   public void addWorldEvent(WorldEvent var1) {
      var1.world = this;
      if (this.serverWorld != null) {
         this.serverWorld.server.network.sendToAllClients(new PacketWorldEvent(var1));
      }

      var1.init();
      this.events.add(var1);
   }

   public WorldData getWorldData(String var1) {
      return (WorldData)this.data.get(var1);
   }

   public void addWorldData(String var1, WorldData var2) {
      if (!var1.matches("[a-zA-Z0-9]+")) {
         throw new IllegalArgumentException("Key \"" + var1 + "\" contains illegal characters");
      } else {
         var2.setWorldEntity(this);
         this.data.put(var1, var2);
      }
   }

   public HashMap<String, WorldData> getWorldData() {
      return this.data;
   }

   public static WorldEntity getClientWorldEntity(Client var0) {
      return new WorldEntity(var0);
   }

   public static WorldEntity getPlainWorldEntity(World var0) {
      WorldEntity var1 = new WorldEntity(var0.server);
      var1.serverWorld = var0;
      return var1;
   }

   public static WorldEntity getServerWorldEntity(World var0, int var1, Point var2) {
      return new WorldEntity(var0, var1, var2);
   }

   public static WorldEntity getDebugWorldEntity() {
      return new WorldEntity((Server)null);
   }

   public static enum TimeOfDay {
      MORNING(new LocalMessage("ui", "timemorning")),
      NOON(new LocalMessage("ui", "timenoon")),
      AFTERNOON(new LocalMessage("ui", "timeafternoon")),
      EVENING(new LocalMessage("ui", "timeevening")),
      NIGHT(new LocalMessage("ui", "timenight"));

      public GameMessage displayName;

      private TimeOfDay(GameMessage var3) {
         this.displayName = var3;
      }

      // $FF: synthetic method
      private static TimeOfDay[] $values() {
         return new TimeOfDay[]{MORNING, NOON, AFTERNOON, EVENING, NIGHT};
      }
   }
}
