package necesse.engine.network.server;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import necesse.engine.GameAuth;
import necesse.engine.GameDeathPenalty;
import necesse.engine.GameLog;
import necesse.engine.Settings;
import necesse.engine.achievements.AchievementManager;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.networkInfo.NetworkInfo;
import necesse.engine.network.packet.PacketAddDeathLocation;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.network.packet.PacketClientStats;
import necesse.engine.network.packet.PacketClientStatsUpdate;
import necesse.engine.network.packet.PacketCloseContainer;
import necesse.engine.network.packet.PacketDisconnect;
import necesse.engine.network.packet.PacketLevelData;
import necesse.engine.network.packet.PacketLevelEvent;
import necesse.engine.network.packet.PacketNeedRequestSelf;
import necesse.engine.network.packet.PacketNeedSpawnedPacket;
import necesse.engine.network.packet.PacketNetworkUpdate;
import necesse.engine.network.packet.PacketPermissionUpdate;
import necesse.engine.network.packet.PacketPing;
import necesse.engine.network.packet.PacketPlayerAppearance;
import necesse.engine.network.packet.PacketPlayerDie;
import necesse.engine.network.packet.PacketPlayerLatency;
import necesse.engine.network.packet.PacketPlayerLevelChange;
import necesse.engine.network.packet.PacketPlayerMovement;
import necesse.engine.network.packet.PacketPlayerRespawn;
import necesse.engine.network.packet.PacketPlayerStatsUpdate;
import necesse.engine.network.packet.PacketPlayerSync;
import necesse.engine.network.packet.PacketRequestClientStats;
import necesse.engine.network.packet.PacketSelectedCharacter;
import necesse.engine.network.packet.PacketShowDPS;
import necesse.engine.network.packet.PacketSpawnPlayer;
import necesse.engine.network.packet.PacketSummonFocus;
import necesse.engine.network.packet.PacketUniqueFloatText;
import necesse.engine.network.packet.PacketWorldData;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.quest.Quest;
import necesse.engine.save.CharacterSave;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.team.PlayerTeam;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.DPSTracker;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.HashMapSet;
import necesse.engine.util.InvalidLevelIdentifierException;
import necesse.engine.util.LevelDeathLocation;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.TeleportResult;
import necesse.engine.util.WorldDeathLocation;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.MapDiscoverEvent;
import necesse.entity.manager.EntityManager;
import necesse.entity.manager.MobSpawnArea;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobWorldPosition;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.objectEntity.PortalObjectEntity;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.HumanLook;
import necesse.inventory.InventoryAddConsumer;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.Container;
import necesse.inventory.container.travel.TravelContainer;
import necesse.inventory.item.toolItem.ToolDamageItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.LootTable;
import necesse.level.gameObject.RespawnObject;
import necesse.level.maps.DiscoveredMap;
import necesse.level.maps.DiscoveredMapManager;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.FishingLootTable;
import necesse.level.maps.biomes.FishingSpot;
import necesse.level.maps.biomes.MobSpawnTable;
import necesse.level.maps.levelData.settlementData.settler.Settler;
import necesse.level.maps.regionSystem.Region;
import necesse.level.maps.regionSystem.RegionPosition;

public class ServerClient extends NetworkClient {
   public static float mobSpawnRate = TickManager.getTickDelta(0.7F);
   public static float mobSpawnRatePartyMemberModifier = 0.2F;
   public static float critterSpawnRate = TickManager.getTickDelta(0.7F);
   public static float settlerSpawnRate = TickManager.getTickDelta(1.0F);
   public static int MS_TO_AFK = 30000;
   private final long sessionID;
   public NetworkInfo networkInfo;
   private int characterUniqueID;
   private PermissionLevel permissionLevel;
   private LevelIdentifier levelIdentifier;
   public int latency;
   public int timeConnected;
   private long sessionTime;
   private long msTimeTicker;
   public long pvpSetCooldown;
   public int networkUpdateTimer;
   public int pingTimer;
   public long lastReceivedPacketTime;
   private long lastResetConnectionTime;
   private int pingKickBuffer;
   private ExpectedPing expectedPing;
   private final Object pingLock = new Object();
   public long respawnTime;
   private float nextMobSpawn;
   private float nextCritterSpawn;
   private float nextSettlerSpawn;
   public long lastActionTime;
   private boolean isAFK;
   private boolean hasRequestedSelf;
   private long lastRequestSelfPacketSentSystemTime;
   private long spawnedCheckTimer;
   private long lastSpawnPacketRequestSystemTime;
   private boolean needAppearance;
   private boolean submittedCharacter;
   private LinkedList<WorldDeathLocation> deathLocations = new LinkedList();
   private HashMapSet<LevelIdentifier, Point> loadedRegions;
   private HashSet<String> knownIslands;
   private HashSet<String> visitedIslands;
   public LevelIdentifier spawnLevelIdentifier;
   public Point spawnTile;
   public LevelIdentifier levelIdentifierFallback;
   public Point tilePosFallback;
   public HashSet<Integer> teamInvites = new HashSet();
   public HashSet<Long> joinRequests = new HashSet();
   public HashMap<Integer, ServerClient> questInvites = new HashMap();
   private AchievementManager achievements;
   private PlayerStats totalStats;
   private PlayerStats characterStats;
   public PlayerStats newStats;
   private int lastDistanceRan;
   private int lastDistanceRidden;
   public HashSet<Quest> quests = new HashSet();
   private ArrayList<MobFollower> followers = new ArrayList();
   public FollowerTargetCooldown followerTargetCooldowns = new FollowerTargetCooldown(this);
   public Mob summonFocus;
   public final AdventureParty adventureParty = new AdventureParty(this);
   public DiscoveredMapManager mapManager = new DiscoveredMapManager();
   public Point lastDiscoverPoint;
   public DPSTracker trainingDummyDPSTracker = new DPSTracker();
   public ArrayList<MobWorldPosition> homePortals = new ArrayList();
   private boolean sentConnectingMessage;
   private boolean sentJoinedMessage;
   private Server server;
   private Container inventoryContainer;
   private Container openContainer;
   private long packetsOutTotal;
   private long packetsOutBytes;
   private long packetsInTotal;
   private long packetsInBytes;

   public ServerClient(Server var1, long var2, NetworkInfo var4, int var5, long var6, LoadData var8) {
      super(var5, var6);
      this.server = var1;
      this.sessionID = var2;
      this.networkInfo = var4;
      this.characterStats = new PlayerStats(false, PlayerStats.Mode.READ_ONLY);
      this.totalStats = null;
      this.newStats = new PlayerStats(false, PlayerStats.Mode.WRITE_ONLY);
      this.makeServerClient();
      if (Settings.serverOwnerAuth != -1L && Settings.serverOwnerAuth == var6) {
         this.permissionLevel = PermissionLevel.OWNER;
      } else {
         this.permissionLevel = PermissionLevel.USER;
      }

      this.reset();
      this.lastReceivedPacketTime = System.currentTimeMillis();
      this.knownIslands = new HashSet();
      this.visitedIslands = new HashSet();
      this.pvpEnabled = false;
      this.needAppearance = false;
      this.achievements = null;
      if (var8 != null) {
         this.applySave(var8);
      } else {
         this.characterUniqueID = CharacterSave.getNewUniqueCharacterID((Predicate)null);
      }

      this.refreshAFKTimer();
      if (var6 == GameAuth.getAuthentication()) {
         this.permissionLevel = PermissionLevel.OWNER;
      }

   }

   public long getSessionID() {
      return this.sessionID;
   }

   public int getCharacterUniqueID() {
      return this.characterUniqueID;
   }

   public SaveData getSave() {
      SaveData var1 = new SaveData("PLAYER");
      var1.addSafeString("name", this.getName());
      var1.addInt("characterUniqueID", this.characterUniqueID);
      var1.addInt("permissions", this.permissionLevel.getLevel());
      var1.addBoolean("needAppearance", this.needAppearance);
      var1.addInt("team", this.getTeamID());
      var1.addBoolean("pvp", this.pvpEnabled);
      var1.addBoolean("isDead", this.isDead());
      SaveData var2 = new SaveData("MOB");
      this.playerMob.addSaveData(var2);
      var1.addSaveData(var2);
      var1.addUnsafeString("level", this.levelIdentifier.stringID);
      if (this.levelIdentifierFallback != null) {
         var1.addUnsafeString("levelFallback", this.levelIdentifierFallback.stringID);
         if (this.tilePosFallback != null) {
            var1.addPoint("tilePosFallback", this.tilePosFallback);
         }
      }

      if (this.levelIdentifier.isIslandPosition()) {
         var1.addPoint("island", new Point(this.levelIdentifier.getIslandX(), this.levelIdentifier.getIslandY()));
         var1.addInt("dimension", this.levelIdentifier.getIslandDimension());
      }

      var1.addStringHashSet("knownIslands", this.knownIslands);
      var1.addStringHashSet("visitedIslands", this.visitedIslands);
      var1.addUnsafeString("spawnLevel", this.spawnLevelIdentifier.stringID);
      if (this.spawnLevelIdentifier.isIslandPosition()) {
         var1.addPoint("spawnIsland", new Point(this.spawnLevelIdentifier.getIslandX(), this.spawnLevelIdentifier.getIslandY()));
         var1.addInt("spawnDimension", this.spawnLevelIdentifier.getIslandDimension());
      }

      var1.addPoint("spawnTile", this.spawnTile);
      SaveData var3 = new SaveData("adventureParty");
      this.adventureParty.addSaveData(var3);
      var1.addSaveData(var3);
      SaveData var4;
      Iterator var5;
      if (!this.homePortals.isEmpty()) {
         var4 = new SaveData("homePortals");
         var5 = this.homePortals.iterator();

         while(var5.hasNext()) {
            MobWorldPosition var6 = (MobWorldPosition)var5.next();
            var4.addSaveData(var6.getSaveData("portal"));
         }

         var1.addSaveData(var4);
      }

      var1.addIntArray("quests", this.quests.stream().filter(Objects::nonNull).mapToInt(Quest::getUniqueID).toArray());
      var4 = new SaveData("DEATHS");
      var5 = this.getDeathLocations().iterator();

      SaveData var7;
      while(var5.hasNext()) {
         WorldDeathLocation var9 = (WorldDeathLocation)var5.next();
         var7 = new SaveData("death");
         var9.addSaveData(var7);
         var4.addSaveData(var7);
      }

      var1.addSaveData(var4);
      this.mapManager.clearRemovedLevelIdentifiers(this.server.world);
      SaveData var8 = new SaveData("MAPS");
      this.mapManager.addSaveData(var8);
      var1.addSaveData(var8);
      SaveData var10 = new SaveData("STATS");
      this.characterStats.addSaveData(var10);
      var1.addSaveData(var10);
      var7 = new SaveData("NEWSTATS");
      this.newStats.addSaveData(var7);
      if (!var7.isEmpty()) {
         var1.addSaveData(var7);
      }

      return var1;
   }

   public static String loadClientName(LoadData var0) {
      boolean var1 = var0.getBoolean("needAppearance", true, false);
      return var1 ? "N/A" : var0.getUnsafeString("name", "N/A");
   }

   public static HumanLook loadClientLook(LoadData var0) {
      HumanLook var1 = new HumanLook();
      if (var0.hasLoadDataByName("MOB")) {
         LoadData var2 = var0.getFirstLoadDataByName("MOB");
         if (var2.hasLoadDataByName("LOOK")) {
            LoadData var3 = var2.getFirstLoadDataByName("LOOK");
            var1.applyLoadData(var3);
         } else {
            GameLog.warn.println("Could not load client look: Doesn't have MOB.LOOK component");
         }
      } else {
         GameLog.warn.println("Could not load client look: Doesn't have MOB component");
      }

      return var1;
   }

   public static PlayerStats loadClientStats(LoadData var0) {
      LoadData var1 = var0.getFirstLoadDataByName("STATS");
      PlayerStats var2 = new PlayerStats(false, PlayerStats.Mode.READ_ONLY);
      if (var1 != null) {
         var2.applyLoadData(var1);
      }

      return var2;
   }

   public void applySave(LoadData var1) {
      this.playerMob = new PlayerMob(this.authentication, this);

      try {
         LoadData var2 = var1.getFirstLoadDataByName("STATS");
         if (var2 != null) {
            this.characterStats.applyLoadData(var2);
         } else {
            GameLog.warn.println("Could not load " + this.getName() + " server stats");
         }

         LoadData var3 = var1.getFirstLoadDataByName("NEWSTATS");
         if (var3 != null) {
            this.newStats.applyLoadData(var3);
         }

         this.permissionLevel = PermissionLevel.getLevel(var1.getInt("permissions", PermissionLevel.USER.getLevel()));
         this.knownIslands = var1.getStringHashSet("knownIslands", new HashSet());
         this.visitedIslands = var1.getStringHashSet("visitedIslands", new HashSet());
         LevelIdentifier var4 = this.server.world.worldEntity.spawnLevelIdentifier;

         Point var6;
         try {
            LevelIdentifier var5 = null;
            String var19 = var1.getUnsafeString("level", (String)null, false);
            if (var19 != null) {
               var5 = new LevelIdentifier(var19);
               if (!this.server.world.levelExists(var5)) {
                  var5 = null;
                  String var21 = var1.getUnsafeString("levelFallback", (String)null, false);
                  if (var21 != null) {
                     this.levelIdentifierFallback = new LevelIdentifier(var21);
                     if (this.server.world.levelExists(this.levelIdentifierFallback)) {
                        this.tilePosFallback = var1.getPoint("tilePosFallback", (Point)null, false);
                     }
                  }
               }
            }

            if (var5 == null) {
               var5 = this.levelIdentifierFallback;
            }

            if (var5 == null) {
               var5 = var4;
            }

            this.setLevelIdentifier(var5);
         } catch (InvalidLevelIdentifierException var17) {
            var6 = var1.getPoint("island", (Point)null, false);
            int var7 = var1.getInt("dimension", 0, false);
            if (var6 != null) {
               this.setLevelIdentifier(new LevelIdentifier(var6.x, var6.y, var7));
            } else {
               GameLog.warn.println("Could not load player spawn level");
               this.setLevelIdentifier(var4);
            }
         }

         this.playerMob.setLevel(this.server.world.getLevel(this));
         this.needAppearance = var1.getBoolean("needAppearance", true);
         this.isDead = var1.getBoolean("isDead", false);
         this.setTeamID(this.server.world.getTeams().getPlayerTeamID(this.authentication));
         this.pvpEnabled = var1.getBoolean("pvp", false);
         this.playerMob.playerName = var1.getSafeString("name", "N/A");
         if (this.playerMob.getDisplayName().equals("N/A")) {
            this.needAppearance = true;
         }

         this.characterUniqueID = var1.getInt("characterUniqueID", this.characterUniqueID, false);
         if (this.characterUniqueID == 0) {
            this.characterUniqueID = CharacterSave.getNewUniqueCharacterID((Predicate)null);
         }

         LoadData var22 = var1.getFirstLoadDataByName("MOB");
         if (var22 != null) {
            this.playerMob.applyLoadData(var22);
         } else {
            GameLog.warn.println("Could not load player mob data");
         }

         if (this.tilePosFallback != null) {
            var6 = this.getPlayerPosFromTile(this.getLevel(), this.tilePosFallback.x, this.tilePosFallback.y);
            this.playerMob.setPos((float)var6.x, (float)var6.y, true);
         }

         try {
            this.spawnLevelIdentifier = new LevelIdentifier(var1.getUnsafeString("spawnLevel", (String)null, false));
         } catch (InvalidLevelIdentifierException var16) {
            Point var23 = var1.getPoint("spawnIsland", (Point)null, false);
            int var8 = var1.getInt("spawnDimension", 0, false);
            if (var23 != null) {
               this.spawnLevelIdentifier = new LevelIdentifier(var23.x, var23.y, var8);
            } else {
               this.spawnLevelIdentifier = var4;
            }
         }

         this.spawnTile = var1.getPoint("spawnTile", this.server.world.worldEntity.spawnTile);
         LoadData var20 = var1.getFirstLoadDataByName("adventureParty");
         if (var20 != null) {
            try {
               this.adventureParty.applyLoadData(var20);
            } catch (Exception var15) {
               System.err.println("Failed to load adventure party data for " + this.getName());
               var15.printStackTrace();
            }
         }

         this.homePortals.clear();
         LoadData var24 = var1.getFirstLoadDataByName("homePortals");
         LoadData var9;
         if (var24 != null) {
            Iterator var25 = var24.getLoadDataByName("portal").iterator();

            while(var25.hasNext()) {
               var9 = (LoadData)var25.next();
               this.homePortals.add(new MobWorldPosition(var9));
            }
         }

         int[] var26 = var1.getIntArray("quests", new int[0]);
         int[] var27 = var26;
         int var10 = var26.length;

         int var12;
         for(int var11 = 0; var11 < var10; ++var11) {
            var12 = var27[var11];
            Quest var13 = this.server.world.getQuests().getQuest(var12);
            if (var13 != null) {
               this.quests.add(var13);
            }
         }

         var9 = var1.getFirstLoadDataByName("DEATHS");
         LoadData var30;
         if (var9 != null) {
            Iterator var28 = var9.getLoadDataByName("death").iterator();

            while(var28.hasNext()) {
               var30 = (LoadData)var28.next();

               try {
                  this.deathLocations.add(new WorldDeathLocation(var30));
               } catch (Exception var14) {
                  System.err.println("Failed to load death location for " + this.getName());
                  var14.printStackTrace();
               }
            }

            this.deathLocations.sort(Comparator.comparingInt((var0) -> {
               return var0.deathTime;
            }));
         }

         LoadData var29 = var1.getFirstLoadDataByName("MAPS");
         if (var29 != null) {
            this.mapManager.applySaveData(var29);
         }

         var30 = this.server.world.loadPlayerMap(this);
         if (var30 != null) {
            var12 = this.mapManager.migrateSaveData(var30);
            if (var12 > 0) {
               System.out.println("Migrated " + this.getName() + " map data for " + var12 + " levels");
            }
         }
      } catch (Exception var18) {
         var18.printStackTrace();
      }

      if (Settings.serverOwnerAuth != -1L && Settings.serverOwnerAuth == this.authentication) {
         this.permissionLevel = PermissionLevel.OWNER;
      }

      if (!this.needAppearance && this.playerMob.playerName.equals(Settings.serverOwnerName)) {
         this.permissionLevel = PermissionLevel.OWNER;
      }

      this.playerMob.setTeam(this.getTeamID());
      this.playerMob.setUniqueID(this.slot);
      if (!this.playerMob.isInitialized()) {
         this.playerMob.init();
      }

      this.updateInventoryContainer();
      this.server.usedNames.put(this.authentication, this.getName());
   }

   public void onUnloading() {
      if (this.playerMob.isRiding()) {
         this.playerMob.dismount();
      }

   }

   public void setupSyncUpdate(PacketWriter var1, ServerClient var2) {
      var1.putNextBoolean(this.hasSpawned);
      var1.putNextBoolean(this.isDead);
      if (this.isDead) {
         var1.putNextInt(this.getRespawnTimeRemaining());
      }

      var1.putNextBoolean(this.pvpEnabled);
      var1.putNextInt(this.getTeamID());
      var1.putNextBoolean(this.isSamePlace(var2));
      var1.putNextBoolean(this.summonFocus != null);
      if (this.summonFocus != null) {
         var1.putNextInt(this.summonFocus.getUniqueID());
      }

      if (var2 == this) {
         var1.putNextBoolean(this.openContainer != null);
         if (this.openContainer != null) {
            var1.putNextInt(this.openContainer.uniqueSeed);
         }
      }

   }

   public void tickMovement(float var1) {
      if (this.playerMob != null) {
         if (this.hasSpawned) {
            this.playerMob.setLevel(this.server.world.getLevel(this));
            if (!this.isDead()) {
               synchronized(this.playerMob.getLevel().entityManager.lock) {
                  this.playerMob.tickMovement(var1);
                  this.playerMob.getLevel().entityManager.players.updateRegion(this.playerMob);
               }
            } else {
               this.playerMob.updateRegion((GameLinkedList)null);
            }
         } else {
            this.playerMob.updateRegion((GameLinkedList)null);
         }
      }

   }

   public void tick() {
      if (this.spawnedCheckTimer != 0L && this.spawnedCheckTimer < System.currentTimeMillis()) {
         if (!this.hasSpawned()) {
            GameLog.warn.println("Kicking player " + this.getName() + " because they did not submit a spawn packet");
            this.server.disconnectClient(this, PacketDisconnect.Code.STATE_DESYNC);
            return;
         }

         this.spawnedCheckTimer = 0L;
      }

      if (!this.needTotalStats() && this.newStats.isImportantDirty()) {
         this.combineNewStats();
      }

      long var1 = this.server.world.getTime();
      this.trainingDummyDPSTracker.tick(var1);
      Level var3 = this.server.world.getLevel(this);
      this.msTimeTicker += 50L;
      if (this.msTimeTicker > 1000L) {
         this.msTimeTicker -= 1000L;
         this.newStats.time_played.increment(1);
         ++this.networkUpdateTimer;
         if (this.networkUpdateTimer % 5 == 0) {
            this.followerTargetCooldowns.cleanCache();
            this.sendPacket(new PacketNetworkUpdate(this));
            if (this.needTotalStats()) {
               this.sendPacket(new PacketRequestClientStats());
            } else if (this.newStats.isDirty()) {
               this.combineNewStats();
            }
         }

         if (this.networkUpdateTimer % 10 == 0) {
            this.server.streamClients().forEach((var1x) -> {
               var1x.sendPacket(new PacketPlayerSync(this, var1x));
            });
         }

         if (this.trainingDummyDPSTracker.isLastHitBeforeReset(var1)) {
            float var4 = (float)this.trainingDummyDPSTracker.getDPS(var1);
            this.sendPacket(new PacketShowDPS(this.playerMob.getUniqueID(), var4));
         }

         if (this.playerMob != null) {
            int var8 = (int)this.playerMob.getDistanceRan() - this.lastDistanceRan;
            if (var8 > 0) {
               this.lastDistanceRan = (int)this.playerMob.getDistanceRan();
               this.newStats.distance_ran.increment(var8);
               if (var8 > 1000) {
                  GameLog.warn.println(this.getName() + " ran more than 1000 units the last second? (" + var8 + ")");
               }
            }

            int var5 = (int)this.playerMob.getDistanceRidden() - this.lastDistanceRidden;
            if (var5 > 0) {
               this.lastDistanceRidden = (int)this.playerMob.getDistanceRidden();
               this.newStats.distance_ridden.increment(var5);
               if (var5 > 1000) {
                  GameLog.warn.println(this.getName() + " rode more than 1000 units the last second? (" + var5 + ")");
               }
            }
         }

         ++this.sessionTime;
      }

      this.adventureParty.serverTick();
      if (this.hasSpawned && this.playerMob != null) {
         Iterator var9 = this.quests.iterator();

         while(var9.hasNext()) {
            Quest var10 = (Quest)var9.next();
            if (var10.isRemoved()) {
               var9.remove();
            } else {
               var10.tick(this);
            }
         }

         if (this.playerMob.getAttackHandler() != null) {
            this.refreshAFKTimer();
         }

         if (!this.isDead()) {
            if (this.openContainer != null && !this.openContainer.isValid(this)) {
               this.closeContainer(true);
            }

            this.getContainer().tick();
            this.playerMob.setLevel(this.server.world.getLevel(this));
            synchronized(this.playerMob.getLevel().entityManager.lock) {
               this.playerMob.serverTick();
            }

            this.nextMobSpawn += this.getMobSpawnRate(var3);

            while(this.nextMobSpawn >= 1.0F) {
               --this.nextMobSpawn;
               if (!var3.entityManager.tickMobSpawning(this.server, this)) {
                  this.nextMobSpawn += 0.5F;
               }
            }

            this.nextCritterSpawn += this.getCritterSpawnRate(var3);

            while(this.nextCritterSpawn >= 1.0F) {
               --this.nextCritterSpawn;
               if (!var3.entityManager.tickCritterSpawning(this.server, this)) {
                  this.nextCritterSpawn += 0.5F;
               }
            }

            this.nextSettlerSpawn += settlerSpawnRate;

            while(this.nextSettlerSpawn >= 1.0F) {
               --this.nextSettlerSpawn;
               Settler.tickServerClientSpawn(this.server, this);
            }
         }

         this.tickFollowers();
         boolean var11 = this.getTimeSinceLastAction() >= (long)MS_TO_AFK;
         if (this.isAFK != var11) {
            this.isAFK = var11;
            if (this.isAFK) {
               System.out.println(this.getName() + " is now AFK");
            } else {
               System.out.println(this.getName() + " is no longer AFK");
            }
         }
      }

      if (this.summonFocus != null && (this.summonFocus.removed() || !this.summonFocus.canBeTargeted(this.playerMob, this))) {
         this.clearSummonFocus();
      }

   }

   public void clearSummonFocus() {
      if (this.summonFocus != null) {
         this.summonFocus = null;
         this.server.network.sendToAllClients(new PacketSummonFocus(this.slot, (Mob)null));
      }

   }

   private void combineNewStats() {
      this.characterStats.combineDirty(this.newStats);
      if (this.getServer().world.settings.achievementsEnabled() && !this.needTotalStats()) {
         this.totalStats.combineDirty(this.newStats);
      }

      this.server.world.worldEntity.worldStats.combineDirty(this.newStats);
      Level var1 = this.getLevel();
      if (var1 != null) {
         var1.levelStats.combineDirty(this.newStats);
      }

      this.newStats.resetCombine();
      this.newStats.cleanAll();
      if (this.characterStats.isDirty()) {
         this.sendPacket(new PacketPlayerStatsUpdate(this.characterStats));
         this.characterStats.cleanAll();
      }

      if (!this.needTotalStats() && this.totalStats.isDirty()) {
         this.sendPacket(new PacketClientStatsUpdate(this.totalStats));
         this.achievements.runStatsUpdate(this);
         this.totalStats.cleanAll();
      }

   }

   private boolean hasAxe() {
      return this.playerMob.getInv().streamSlots(false, true, true).anyMatch((var1) -> {
         if (!var1.isSlotClear(this.playerMob.getInv())) {
            InventoryItem var2 = var1.getItem(this.playerMob.getInv());
            if (var2.item instanceof ToolDamageItem) {
               ToolType var3 = ((ToolDamageItem)var2.item).getToolType();
               return var3 == ToolType.ALL || var3 == ToolType.AXE;
            }
         }

         return false;
      });
   }

   public void die(int var1) {
      if (!this.isDead()) {
         this.isDead = true;
         this.newStats.deaths.increment(1);
         this.closeContainer(false);
         if (this.server.world.settings.deathPenalty == GameDeathPenalty.DROP_MATS) {
            ItemDropperHandler var2 = (var1x, var2x, var3) -> {
               ItemPickupEntity var4 = var1x.getPickupEntity(this.playerMob.getLevel(), this.playerMob.x, this.playerMob.y).setPlayerDeathAuth(this, var2x, var3);
               this.playerMob.getLevel().entityManager.pickups.add(var4);
            };
            this.playerMob.getInv().streamSlots(false, true, true).forEach((var2x) -> {
               if (!var2x.isSlotClear(this.playerMob.getInv())) {
                  boolean var3 = var2x.isItemLocked(this.playerMob.getInv());
                  InventoryItem var4 = var2x.getItem(this.playerMob.getInv());
                  if (var4.item.dropAsMatDeathPenalty(var2x, var3, var4, var2)) {
                     var2x.setItem(this.playerMob.getInv(), (InventoryItem)null);
                  }
               }

            });
         } else if (this.server.world.settings.deathPenalty == GameDeathPenalty.DROP_MAIN_INVENTORY) {
            this.playerMob.getInv().dropMainInventory();
         } else if (this.server.world.settings.deathPenalty == GameDeathPenalty.DROP_FULL_INVENTORY || this.server.world.settings.deathPenalty == GameDeathPenalty.HARDCORE) {
            this.playerMob.getInv().dropInventory();
         }

         if (!this.hasAxe()) {
            this.playerMob.getInv().addItem(new InventoryItem("woodaxe", 1), true, "respawnitem", (InventoryAddConsumer)null);
         }

         this.respawnTime = this.server.world.worldEntity.getTime() + (long)var1;
         this.deathLocations.addLast(new WorldDeathLocation(this.characterStats, this.levelIdentifier, this.playerMob.getX(), this.playerMob.getY()));
         this.sendPacket(new PacketAddDeathLocation(new LevelDeathLocation(0, this.playerMob.getX(), this.playerMob.getY())));
         this.clearFollowers();
         this.server.network.sendToAllClients(new PacketPlayerDie(this.slot, var1));
      }

   }

   public boolean removeDeathLocation(LevelIdentifier var1, int var2, int var3) {
      return this.deathLocations.removeIf((var3x) -> {
         return var3x.levelIdentifier.equals(var1) && var3x.x == var2 && var3x.y == var3;
      });
   }

   public boolean removeDeathLocations(int var1, int var2) {
      return this.deathLocations.removeIf((var2x) -> {
         return var2x.levelIdentifier.isIslandPosition() && var2x.levelIdentifier.getIslandX() == var1 && var2x.levelIdentifier.getIslandY() == var2;
      });
   }

   public float getCritterSpawnRate(Level var1) {
      return critterSpawnRate * this.getSpawnRateModifier(var1, Mob.CRITTER_SPAWN_AREA, 0.75F);
   }

   public float getCritterSpawnCap(Level var1) {
      return EntityManager.getSpawnCap(var1.presentPlayers, 20.0F, -10.0F) * this.server.world.settings.difficulty.enemySpawnCapModifier * var1.entityManager.getSpawnCapMod() * (Float)this.playerMob.buffManager.getModifier(BuffModifiers.MOB_SPAWN_CAP);
   }

   public MobSpawnTable getCritterSpawnTable(Level var1) {
      MobSpawnTable var2 = (new MobSpawnTable()).include(var1.biome.getCritterSpawnTable(var1));
      Iterator var3 = this.quests.iterator();

      while(var3.hasNext()) {
         Quest var4 = (Quest)var3.next();
         MobSpawnTable var5 = var4.getExtraCritterSpawnTable(this, var1);
         if (var5 != null) {
            var2.include(var5);
         }
      }

      return var2;
   }

   public int getOtherNearbyPlayers(Level var1, int var2) {
      return var1.presentPlayers > 1 ? (int)var1.entityManager.players.streamArea((float)this.playerMob.getX(), (float)this.playerMob.getY(), var2).filter((var1x) -> {
         return var1x != this.playerMob;
      }).filter((var2x) -> {
         return var2x.getDistance(this.playerMob) <= (float)var2;
      }).count() : 0;
   }

   public int getOtherNearbyPlayers(Level var1, MobSpawnArea var2) {
      return this.getOtherNearbyPlayers(var1, (var2.minSpawnDistance + var2.maxSpawnDistance) / 2);
   }

   public float getSpawnRateModifier(Level var1, MobSpawnArea var2, float var3) {
      int var4 = this.getOtherNearbyPlayers(var1, var2);
      return var4 <= 0 ? 1.0F : (float)Math.pow((double)(1.0F / (float)var4), (double)var3);
   }

   public float getMobSpawnRate(Level var1) {
      return mobSpawnRate * (1.0F + (float)this.adventureParty.getSize() * mobSpawnRatePartyMemberModifier) * this.server.world.settings.difficulty.enemySpawnRateModifier * var1.entityManager.getSpawnRate() * (Float)this.playerMob.buffManager.getModifier(BuffModifiers.MOB_SPAWN_RATE) * this.getSpawnRateModifier(var1, Mob.MOB_SPAWN_AREA, 0.75F);
   }

   public float getMobSpawnCap(Level var1) {
      return EntityManager.getSpawnCap(var1.presentPlayers, 25.0F, 5.0F) * this.server.world.settings.difficulty.enemySpawnCapModifier * var1.entityManager.getSpawnCapMod() * (Float)this.playerMob.buffManager.getModifier(BuffModifiers.MOB_SPAWN_CAP);
   }

   public MobSpawnTable getMobSpawnTable(Level var1) {
      MobSpawnTable var2 = (new MobSpawnTable()).include(var1.biome.getMobSpawnTable(var1));
      Iterator var3 = this.quests.iterator();

      while(var3.hasNext()) {
         Quest var4 = (Quest)var3.next();
         MobSpawnTable var5 = var4.getExtraMobSpawnTable(this, var1);
         if (var5 != null) {
            var2.include(var5);
         }
      }

      return var2;
   }

   public FishingLootTable getFishingLoot(FishingSpot var1) {
      FishingLootTable var2 = new FishingLootTable(var1.tile.level.biome.getFishingLootTable(var1));
      Iterator var3 = this.quests.iterator();

      while(var3.hasNext()) {
         Quest var4 = (Quest)var3.next();
         FishingLootTable var5 = var4.getExtraFishingLoot(this, var1);
         if (var5 != null) {
            var2.addAll(var5);
         }
      }

      return var2;
   }

   protected void cleanDeathLocations() {
      while(true) {
         if (!this.deathLocations.isEmpty()) {
            WorldDeathLocation var1 = (WorldDeathLocation)this.deathLocations.getFirst();
            if (var1.getSecondsSince(this.characterStats) > 7200) {
               this.deathLocations.removeFirst();
               continue;
            }
         }

         while(this.deathLocations.size() > 5) {
            this.deathLocations.removeFirst();
         }

         return;
      }
   }

   public Iterable<WorldDeathLocation> getDeathLocations() {
      this.cleanDeathLocations();
      return this.deathLocations;
   }

   public Stream<WorldDeathLocation> streamDeathLocations() {
      this.cleanDeathLocations();
      return this.deathLocations.stream();
   }

   public void addLoadedRegion(Region var1, boolean var2) {
      this.addLoadedRegion(var1.level, var1.regionX, var1.regionY, var2);
   }

   public void addLoadedRegion(RegionPosition var1, boolean var2) {
      this.addLoadedRegion(var1.level, var1.regionX, var1.regionY, var2);
   }

   public void addLoadedRegion(Level var1, int var2, int var3, boolean var4) {
      if (!this.hasRegionLoaded(var1, var2, var3) || var4) {
         Region var5 = var1.regionManager.getRegion(var2, var3);
         if (var5 != null) {
            this.loadedRegions.add(var1.getIdentifier(), new Point(var2, var3));
            var1.entityManager.onServerClientLoadedRegion(var5, this);
         }
      }

   }

   public boolean removeLoadedRegion(RegionPosition var1) {
      return this.removeLoadedRegion(var1.level, var1.regionX, var1.regionY);
   }

   public boolean removeLoadedRegion(Level var1, int var2, int var3) {
      return this.loadedRegions.remove(var1.getIdentifier(), new Point(var2, var3));
   }

   public void removeLoadedRegions(Level var1) {
      this.loadedRegions.clear(var1.getIdentifier());
   }

   public boolean hasRegionLoaded(RegionPosition var1) {
      return this.hasRegionLoaded(var1.level, var1.regionX, var1.regionY);
   }

   public boolean hasRegionLoaded(Level var1, int var2, int var3) {
      return this.hasRegionLoaded(var1.getIdentifier(), var2, var3);
   }

   public boolean hasRegionLoaded(LevelIdentifier var1, int var2, int var3) {
      return this.loadedRegions.contains(var1, new Point(var2, var3));
   }

   public int getLoadedRegionsCount(LevelIdentifier var1) {
      return this.loadedRegions.getSize(var1);
   }

   public void tickMapDiscovery() {
      this.mapManager.tickDiscovery(this);
   }

   public void tickTimeConnected() {
      this.timeConnected += 50;
      if (this.timeConnected % 10000 == 0) {
         this.sendPacket(new PacketWorldData(this.server.world.worldEntity));
      }

      this.pingTimer += 50;
      long var1 = Settings.maxClientLatencySeconds > 4 ? 2000L : Math.max((long)Settings.maxClientLatencySeconds * 1000L / 2L, 500L);
      if ((long)this.pingTimer > var1) {
         this.pingTimer = 0;
         synchronized(this.pingLock) {
            this.expectedPing = new ExpectedPing(GameRandom.globalRandom.nextInt(), System.currentTimeMillis(), this.expectedPing);
            this.sendPacket(new PacketPing(this.expectedPing.responseKey));
         }
      }

      long var3 = System.currentTimeMillis() - this.lastReceivedPacketTime;
      if (var3 >= 5000L) {
         long var5 = System.currentTimeMillis() - this.lastResetConnectionTime;
         if (var5 >= 5000L) {
            if (this.networkInfo != null) {
               this.networkInfo.resetConnection();
            }

            this.lastResetConnectionTime = System.currentTimeMillis();
         }
      }

      if (Settings.maxClientLatencySeconds > 0) {
         if ((long)this.latency <= (long)Settings.maxClientLatencySeconds * 1000L && var3 <= (long)Settings.maxClientLatencySeconds * 1000L) {
            if (this.pingKickBuffer > 0) {
               --this.pingKickBuffer;
            }
         } else {
            ++this.pingKickBuffer;
            if (this.pingKickBuffer > 100) {
               System.out.println("Ping threshold for \"" + this.getName() + "\" reached, resulting in kick.");
               this.server.disconnectClient(this.slot, PacketDisconnect.Code.CLIENT_NOT_RESPONDING);
            }
         }
      }

   }

   public void submitPingPacket(PacketPing var1) {
      if (var1.responseKey == -1) {
         this.sendPacket(new PacketPing(-1));
      } else {
         synchronized(this.pingLock) {
            ExpectedPing var3 = null;
            ExpectedPing var4 = this.expectedPing;

            for(int var5 = 0; var4 != null; ++var5) {
               if (var4.responseKey == var1.responseKey) {
                  if (var3 != null) {
                     var3.next = null;
                  } else {
                     this.expectedPing = null;
                  }

                  this.latency = (int)(System.currentTimeMillis() - var4.timeSent);
                  this.pingTimer = 0;
                  this.server.network.sendToAllClients(new PacketPlayerLatency(this.slot, this.latency));
                  break;
               }

               var3 = var4;
               var4 = var4.next;
            }

         }
      }
   }

   public String getName() {
      return this.playerMob == null ? "AUTH:" + this.authentication : this.playerMob.getDisplayName();
   }

   public void reset() {
      this.loadedRegions = new HashMapSet();
      this.hasSpawned = false;
      this.lastSpawnPacketRequestSystemTime = 0L;
      this.expectedPing = null;
      this.hasRequestedSelf = false;
      this.lastRequestSelfPacketSentSystemTime = 0L;
      this.closeContainer(true);
   }

   public boolean checkHasRequestedSelf() {
      if (!this.hasRequestedSelf) {
         long var1 = System.currentTimeMillis() - this.lastRequestSelfPacketSentSystemTime;
         if (var1 >= 1000L) {
            this.lastRequestSelfPacketSentSystemTime = System.currentTimeMillis();
            this.sendPacket(new PacketNeedRequestSelf());
         }
      }

      return this.hasRequestedSelf;
   }

   public void requestSelf() {
      this.hasRequestedSelf = true;
   }

   public int getRespawnTimeRemaining() {
      return (int)Math.max(0L, this.respawnTime - this.server.world.worldEntity.getTime());
   }

   public void respawn() {
      if (this.isDead() && this.getRespawnTimeRemaining() <= 200) {
         if (this.server.world.settings.deathPenalty != GameDeathPenalty.HARDCORE) {
            this.validateSpawnPoint(true);
            Point var1 = null;
            Level var2 = this.server.world.getLevel(this.spawnLevelIdentifier);
            if (!this.isDefaultSpawnPoint()) {
               Point var3 = RespawnObject.calculateSpawnOffset(var2, this.spawnTile.x, this.spawnTile.y, this);
               var1 = new Point(this.spawnTile.x * 32 + var3.x, this.spawnTile.y * 32 + var3.y);
            } else {
               var1 = this.getPlayerPosFromTile(var2, this.spawnTile.x, this.spawnTile.y);
            }

            this.playerMob.restore();
            this.hasSpawned = false;
            this.isDead = false;
            if (!this.levelIdentifier.equals(this.spawnLevelIdentifier)) {
               this.reset();
            }

            this.setLevelIdentifier(this.spawnLevelIdentifier);
            this.playerMob.setPos((float)var1.x, (float)var1.y, true);
            this.playerMob.dx = 0.0F;
            this.playerMob.dy = 0.0F;
            this.playerMob.setHealth(Math.max(this.playerMob.getMaxHealth() / 2, 1));
            this.playerMob.setMana((float)Math.max(this.playerMob.getMaxMana(), 1));
            this.playerMob.hungerLevel = Math.max(0.5F, this.playerMob.hungerLevel);
            this.server.network.sendToAllClients(new PacketPlayerRespawn(this));
         }
      }
   }

   public boolean validateSpawnPoint(boolean var1) {
      if (!this.isDefaultSpawnPoint()) {
         Level var2 = this.server.world.getLevel(this.spawnLevelIdentifier);
         Point var3 = RespawnObject.calculateSpawnOffset(var2, this.spawnTile.x, this.spawnTile.y, this);
         if (var3 == null) {
            this.resetSpawnPoint(this.server);
            if (var1) {
               this.sendChatMessage((GameMessage)(new LocalMessage("misc", "spawninvalid")));
            }

            return false;
         }
      }

      return true;
   }

   public boolean isDefaultSpawnPoint() {
      return this.server.world.worldEntity.spawnLevelIdentifier.equals(this.spawnLevelIdentifier) && this.server.world.worldEntity.spawnTile.equals(this.spawnTile);
   }

   public void resetSpawnPoint(Server var1) {
      this.spawnLevelIdentifier = var1.world.worldEntity.spawnLevelIdentifier;
      this.spawnTile = var1.world.worldEntity.spawnTile;
   }

   public void checkSpawned() {
      if (!this.hasSpawned()) {
         long var1 = System.currentTimeMillis() - this.lastSpawnPacketRequestSystemTime;
         if (var1 >= 1000L) {
            this.lastSpawnPacketRequestSystemTime = System.currentTimeMillis();
            this.sendPacket(new PacketNeedSpawnedPacket());
         }

         if (this.spawnedCheckTimer == 0L) {
            GameLog.warn.println(this.getName() + " has not submitted spawn packet, giving them 5 seconds to do so.");
            this.spawnedCheckTimer = System.currentTimeMillis() + 5000L;
         }
      }

   }

   public boolean pvpEnabled() {
      return this.server.world.settings.forcedPvP || this.pvpEnabled;
   }

   public void addDiscoveredIsland(int var1, int var2) {
      if (!this.knownIslands.contains(var1 + "x" + var2)) {
         this.knownIslands.add(var1 + "x" + var2);
      }
   }

   public boolean combineFromDiscovered(HashSet<String> var1) {
      return this.knownIslands.addAll(var1);
   }

   public boolean combineToDiscovered(HashSet<String> var1) {
      return var1.addAll(this.knownIslands);
   }

   public boolean hasDiscoveredIsland(int var1, int var2) {
      return this.knownIslands.contains(var1 + "x" + var2);
   }

   public List<String> getDiscoveredIslands() {
      return new ArrayList(this.knownIslands);
   }

   public int getTotalDiscoveredIslands() {
      return this.knownIslands.size();
   }

   public void addVisitedIsland(int var1, int var2) {
      if (!this.visitedIslands.contains(var1 + "x" + var2)) {
         Biome var3 = this.server.levelCache.getBiome(var1, var2);
         this.newStats.biomes_visited.addBiomeVisited(var3);
         this.visitedIslands.add(var1 + "x" + var2);
      }
   }

   public boolean hasVisitedIsland(int var1, int var2) {
      return this.visitedIslands.contains(var1 + "x" + var2);
   }

   public List<String> getVisitedIsland() {
      return new ArrayList(this.visitedIslands);
   }

   public int getTotalVisitedIsland() {
      return this.visitedIslands.size();
   }

   public long getSessionTime() {
      return this.sessionTime;
   }

   public void refreshAFKTimer() {
      this.lastActionTime = this.server.world.worldEntity.getTime();
   }

   public long getTimeSinceLastAction() {
      return this.server.world.worldEntity.getTime() - this.lastActionTime;
   }

   public boolean isAFK() {
      return this.isAFK;
   }

   public boolean needAppearance() {
      return this.needAppearance;
   }

   public boolean hasSubmittedCharacter() {
      return this.submittedCharacter;
   }

   public void setFallbackLevel(Level var1, int var2, int var3) {
      this.levelIdentifierFallback = var1.getIdentifier();
      this.tilePosFallback = new Point(var2, var3);
   }

   public void setLevelIdentifier(LevelIdentifier var1) {
      this.levelIdentifier = var1;
      if (this.levelIdentifier.isIslandPosition()) {
         this.addDiscoveredIsland(this.levelIdentifier.getIslandX(), this.levelIdentifier.getIslandY());
         this.addVisitedIsland(this.levelIdentifier.getIslandX(), this.levelIdentifier.getIslandY());
      }

   }

   public void changeToFallbackLevel(LevelIdentifier var1) {
      if (this.levelIdentifierFallback == null || this.tilePosFallback == null || var1 != null && this.levelIdentifierFallback.equals(var1)) {
         this.validateSpawnPoint(true);
         if (this.spawnLevelIdentifier == null || var1 != null && this.spawnLevelIdentifier.equals(var1)) {
            this.changeLevel(this.server.world.worldEntity.spawnLevelIdentifier, (var1x) -> {
               return this.getPlayerPosFromTile(var1x, this.server.world.worldEntity.spawnTile.x, this.server.world.worldEntity.spawnTile.y);
            }, true);
         } else {
            this.changeLevel(this.spawnLevelIdentifier, (var1x) -> {
               return this.getPlayerPosFromTile(var1x, this.spawnTile.x, this.spawnTile.y);
            }, true);
         }
      } else {
         this.changeLevel(this.levelIdentifierFallback, (var1x) -> {
            return this.getPlayerPosFromTile(var1x, this.tilePosFallback.x, this.tilePosFallback.y);
         }, true);
      }

   }

   public Point getPlayerPosFromTile(Level var1, int var2, int var3) {
      Point var4 = this.playerMob.getPathMoveOffset();
      if (!this.playerMob.collidesWith(var1, var2 * 32 + var4.x, var3 * 32 + var4.y) && !this.playerMob.collidesWithAnyMob(var1, var2 * 32 + var4.x, var3 * 32 + var4.y)) {
         return new Point(var2 * 32 + var4.x, var3 * 32 + var4.y);
      } else {
         Point var5 = PortalObjectEntity.getTeleportDestinationAroundObject(var1, this.playerMob, var2, var3, true);
         return var5 != null ? var5 : new Point(var2 * 32 + var4.x, var3 * 32 + var4.y);
      }
   }

   public void changeIsland(int var1, int var2, int var3) {
      int var4 = this.playerMob.getX();
      int var5 = this.playerMob.getY();
      LevelIdentifier var6 = this.levelIdentifier;
      this.changeIsland(var1, var2, var3, (var5x) -> {
         int var6x = GameMath.limit(var4, 160, (var5x.width - 5) * 32);
         int var7 = GameMath.limit(var5, 160, (var5x.height - 5) * 32);
         if (var6.isIslandPosition()) {
            if (var6.getIslandX() < var1) {
               var6x = 160;
            } else if (var6.getIslandX() > var1) {
               var6x = (var5x.width - 5) * 32;
            }

            if (var6.getIslandY() < var2) {
               var7 = 160;
            } else if (var6.getIslandY() > var2) {
               var7 = (var5x.height - 5) * 32;
            }
         }

         return new Point(var6x, var7);
      }, true);
   }

   /** @deprecated */
   @Deprecated
   public void changeIsland(LevelIdentifier var1) {
      this.changeLevel(var1);
   }

   public void changeLevel(LevelIdentifier var1) {
      int var2 = this.playerMob.getX();
      int var3 = this.playerMob.getY();
      LevelIdentifier var4 = this.levelIdentifier;
      this.changeLevel(var1, (var4x) -> {
         int var5 = GameMath.limit(var2, 160, (var4x.width - 5) * 32);
         int var6 = GameMath.limit(var3, 160, (var4x.height - 5) * 32);
         if (var4.isIslandPosition() && var1.isIslandPosition()) {
            if (var4.getIslandX() < var1.getIslandX()) {
               var5 = 160;
            } else if (var4.getIslandX() > var1.getIslandX()) {
               var5 = (var4x.width - 5) * 32;
            }

            if (var4.getIslandY() < var1.getIslandY()) {
               var6 = 160;
            } else if (var4.getIslandY() > var1.getIslandY()) {
               var6 = (var4x.height - 5) * 32;
            }
         }

         return new Point(var5, var6);
      }, true);
   }

   public void changeIsland(int var1, int var2, int var3, Function<Level, Point> var4, boolean var5) {
      this.changeLevel(new LevelIdentifier(var1, var2, var3), var4, var5);
   }

   /** @deprecated */
   @Deprecated
   public void changeIsland(LevelIdentifier var1, Function<Level, Point> var2, boolean var3) {
      this.changeLevelCheck(var1, (var1x) -> {
         return new TeleportResult(true, var2 == null ? null : (Point)var2.apply(var1x));
      }, var3);
   }

   public void changeIslandCheck(int var1, int var2, int var3, Function<Level, TeleportResult> var4, boolean var5) {
      this.changeLevelCheck(new LevelIdentifier(var1, var2, var3), var4, var5);
   }

   /** @deprecated */
   @Deprecated
   public void changeIslandCheck(LevelIdentifier var1, Function<Level, TeleportResult> var2, boolean var3) {
      this.changeLevelCheck(var1, var2, var3);
   }

   public void changeLevel(LevelIdentifier var1, Function<LevelIdentifier, Level> var2, Function<Level, Point> var3, boolean var4) {
      this.changeLevelCheck(var1, var2, (var1x) -> {
         return new TeleportResult(true, var3 == null ? null : (Point)var3.apply(var1x));
      }, var4);
   }

   public void changeLevel(LevelIdentifier var1, Function<Level, Point> var2, boolean var3) {
      this.changeLevelCheck(var1, (var1x) -> {
         return new TeleportResult(true, var2 == null ? null : (Point)var2.apply(var1x));
      }, var3);
   }

   public void changeLevelCheck(LevelIdentifier var1, Function<Level, TeleportResult> var2, boolean var3) {
      this.changeLevelCheck(var1, (Function)null, var2, var3);
   }

   public void changeLevelCheck(LevelIdentifier var1, Function<LevelIdentifier, Level> var2, Function<Level, TeleportResult> var3, boolean var4) {
      if (this.isSamePlace(var1) && var3 != null) {
         TeleportResult var5 = (TeleportResult)var3.apply(this.getLevel());
         if (var5.isValid && var5.targetPosition != null) {
            if (var5.newDestination == null || var5.newDestination.equals(var1)) {
               this.playerMob.setPos((float)var5.targetPosition.x, (float)var5.targetPosition.y, true);
               this.server.network.sendToClientsAt(new PacketPlayerMovement(this, true), (ServerClient)this);
               return;
            }

            var1 = var5.newDestination;
            var3 = null;
         }
      }

      this.combineNewStats();
      System.out.println("Changed " + this.getName() + " level to " + var1);
      boolean var19 = var3 == null;
      Point var6 = null;
      Level var7 = this.getLevel();
      LevelIdentifier var9 = null;
      boolean var10 = this.server.world.levelManager.isLoaded(var1);
      if (!var10) {
         this.server.network.sendToAllClients(new PacketPlayerLevelChange(this.slot, var1, var4));
      }

      Level var8 = this.server.world.getLevel(var1, var2 == null ? null : () -> {
         return (Level)var2.apply(var1);
      });
      if (var3 != null) {
         TeleportResult var12 = (TeleportResult)var3.apply(var8);
         var19 = var12.isValid;
         var9 = var12.newDestination;
         var6 = var12.targetPosition;
      }

      if (!var19) {
         if (!var10) {
            this.server.network.sendToAllClients(new PacketPlayerLevelChange(this.slot, var7.getIdentifier(), var4));
         }

      } else {
         if (var9 != null && !var9.equals(var1)) {
            var1 = var9;
            var8 = this.server.world.getLevel(var9, var2 == null ? null : () -> {
               return (Level)var2.apply(var9);
            });
            if (!var10) {
               this.server.network.sendToAllClients(new PacketPlayerLevelChange(this.slot, var9, var4));
            }
         }

         if (var10) {
            this.server.network.sendToAllClients(new PacketPlayerLevelChange(this.slot, var1, var4));
         }

         this.mapManager.ensureMapSize(var8);
         this.setLevelIdentifier(var1);
         int var16;
         int var17;
         if (this.levelIdentifier.isIslandPosition()) {
            int var20 = Math.max(0, (Integer)this.playerMob.buffManager.getModifier(BuffModifiers.BIOME_VIEW_DISTANCE));

            for(int var13 = -var20; var13 <= var20; ++var13) {
               for(int var14 = -var20; var14 <= var20; ++var14) {
                  int var15 = TravelContainer.dist(0, 0, var13, var14);
                  if (var20 >= var15) {
                     var16 = this.levelIdentifier.getIslandX() + var13;
                     var17 = this.levelIdentifier.getIslandY() + var14;
                     this.addDiscoveredIsland(var16, var17);
                  }
               }
            }

            this.addVisitedIsland(this.levelIdentifier.getIslandX(), this.levelIdentifier.getIslandY());
         }

         this.playerMob.moveX = 0.0F;
         this.playerMob.moveY = 0.0F;
         this.reset();
         if (var6 != null) {
            this.playerMob.setPos((float)var6.x, (float)var6.y, true);
            this.server.network.sendToAllClients(new PacketPlayerMovement(this, true));
         }

         Mob var21 = this.playerMob.getMount();
         if (var4 && var21 != null) {
            var7.entityManager.changeMobLevel(var21, var8, var6 != null ? var6.x : var21.getX(), var6 != null ? var6.y : var21.getY(), true);
         } else {
            this.playerMob.dismount();
         }

         this.saveClient();
         this.playerMob.boomerangs.clear();
         this.playerMob.toolHits.clear();
         this.playerMob.setLevel(var8);
         this.playerMob.setUniqueID(this.slot);
         LevelIdentifier var22 = var8.getIdentifier();
         if (var22.isIslandPosition() && var22.getIslandDimension() == -2 && this.achievementsLoaded()) {
            this.achievements().GETTING_HOT.markCompleted(this);
         }

         RegionPosition var23 = var8.regionManager.getRegionPosByTile(this.playerMob.getTileX(), this.playerMob.getTileY());
         ArrayList var24 = new ArrayList(9);

         for(var16 = -1; var16 <= 1; ++var16) {
            for(var17 = -1; var17 <= 1; ++var17) {
               Region var18 = var8.regionManager.getRegion(var23.regionX + var16, var23.regionY + var17);
               if (var18 != null) {
                  var24.add(var18);
               }
            }
         }

         Iterator var25 = var24.iterator();

         while(var25.hasNext()) {
            Region var26 = (Region)var25.next();
            this.addLoadedRegion(var26, true);
         }

         this.sendPacket(new PacketLevelData(var8, this, var24));
      }
   }

   public LevelIdentifier getLevelIdentifier() {
      return this.levelIdentifier;
   }

   public void saveClient() {
      this.server.world.savePlayer(this);
   }

   public PlayerTeam getPlayerTeam() {
      if (this.getTeamID() == -1) {
         return null;
      } else {
         PlayerTeam var1 = this.server.world.getTeams().getTeam(this.getTeamID());
         return var1 != null && var1.isMember(this.authentication) ? var1 : null;
      }
   }

   public boolean isTileKnown(int var1, int var2) {
      DiscoveredMap var3 = this.mapManager.getDiscovery(this.getLevelIdentifier());
      return var3 == null ? false : var3.isTileKnown(var1, var2);
   }

   public void discoverEntireMap() {
      DiscoveredMap var1 = this.mapManager.getDiscovery(this.getLevelIdentifier());
      if (var1 != null) {
         var1.discoverEntireMap();
      }

      this.sendPacket(new PacketLevelEvent(new MapDiscoverEvent()));
   }

   public void tickFollowers() {
      HashMap var1 = new HashMap();
      Iterator var2 = this.followers.iterator();

      while(var2.hasNext()) {
         MobFollower var3 = (MobFollower)var2.next();
         float var4 = (Float)var1.getOrDefault(var3.summonType, 0.0F);
         var1.put(var3.summonType, var4 + var3.spaceTaken);
      }

      LinkedList var7 = new LinkedList();
      Iterator var8 = this.followers.iterator();

      while(true) {
         while(var8.hasNext()) {
            MobFollower var10 = (MobFollower)var8.next();
            float var5 = (Float)var1.getOrDefault(var10.summonType, 0.0F);
            if (var10.mob.removed()) {
               var1.put(var10.summonType, var5 - var10.spaceTaken);
               var7.add(var10);
            } else {
               if (var10.maxSpace != null) {
                  int var6 = (Integer)var10.maxSpace.apply(this.playerMob);
                  if (var5 > (float)var6) {
                     var1.put(var10.summonType, var5 - var10.spaceTaken);
                     var7.add(var10);
                     continue;
                  }
               }

               if (var10.buffType != null && !this.playerMob.buffManager.hasBuff(var10.buffType)) {
                  var1.put(var10.summonType, var5 - var10.spaceTaken);
                  var7.add(var10);
               } else if (var10.updateMob != null) {
                  var10.updateMob.accept(this, var10.mob);
               }
            }
         }

         HashSet var9 = new HashSet();
         Iterator var11 = var7.iterator();

         while(var11.hasNext()) {
            MobFollower var12 = (MobFollower)var11.next();
            this.removeFollower(var12.mob, true, false);
            if (var12.buffType != null) {
               var9.add(var12.buffType);
            }
         }

         var11 = var9.iterator();

         while(var11.hasNext()) {
            String var13 = (String)var11.next();
            this.updateFollowerBuff(var13);
         }

         return;
      }
   }

   public Stream<MobFollower> streamFollowers() {
      return this.followers.stream();
   }

   public void clearFollowers() {
      ArrayList var1 = new ArrayList(this.followers);
      HashSet var2 = new HashSet();
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         MobFollower var4 = (MobFollower)var3.next();
         this.removeFollower(var4.mob, true, false);
         if (var4.buffType != null) {
            var2.add(var4.buffType);
         }
      }

      var3 = var2.iterator();

      while(var3.hasNext()) {
         String var5 = (String)var3.next();
         this.updateFollowerBuff(var5);
      }

   }

   public void removeFollower(Mob var1, boolean var2) {
      this.removeFollower(var1, var2, true);
   }

   public void removeFollower(Mob var1, boolean var2, boolean var3) {
      var1.setFollowing((ServerClient)null, var2);
      MobFollower var4 = (MobFollower)this.followers.stream().filter((var1x) -> {
         return var1x.mob == var1;
      }).findFirst().orElse((Object)null);
      if (var4 != null) {
         this.followers.remove(var4);
         if (var3) {
            this.updateFollowerBuff(var4.buffType);
         }
      }

   }

   public void addFollower(String var1, Mob var2, FollowPosition var3, String var4, float var5, int var6, BiConsumer<ServerClient, Mob> var7, boolean var8) {
      this.addFollower(var1, var2, var3, var4, var5, (var1x) -> {
         return var6;
      }, var7, var8);
   }

   public void addFollower(String var1, Mob var2, FollowPosition var3, String var4, float var5, Function<PlayerMob, Integer> var6, BiConsumer<ServerClient, Mob> var7, boolean var8) {
      var2.setFollowing(this, var8);
      this.followers.add(new MobFollower(var1, var2, var3, var4, var5, var6, var7));
      this.updateFollowerBuff(var4);
   }

   public void updateFollowerBuff(String var1) {
      if (var1 != null) {
         double var2 = this.followers.stream().filter((var1x) -> {
            return var1x.buffType != null && var1x.buffType.equals(var1);
         }).mapToDouble((var0) -> {
            return (double)var0.spaceTaken;
         }).sum();
         var2 = Math.ceil(var2);
         if (var2 <= 0.0) {
            this.playerMob.buffManager.removeBuff(var1, true);
         } else {
            this.playerMob.buffManager.removeBuff(var1, false);
            ActiveBuff var4 = new ActiveBuff(var1, this.playerMob, 0, (Attacker)null);

            for(long var5 = 1L; (double)var5 < var2; ++var5) {
               var4.addStack(0, (Attacker)null);
            }

            this.playerMob.buffManager.addBuff(var4, true);
         }

      }
   }

   public float getFollowerCount(String var1) {
      return (float)this.followers.stream().filter((var1x) -> {
         return var1x.summonType.equals(var1);
      }).mapToDouble((var0) -> {
         return (double)var0.spaceTaken;
      }).sum();
   }

   public boolean isFollower(Mob var1) {
      return this.followers.stream().anyMatch((var1x) -> {
         return var1x.mob == var1;
      });
   }

   public FollowerPosition getFollowerPos(Mob var1, Mob var2, FollowerPosition var3) {
      MobFollower var4 = (MobFollower)this.followers.stream().filter((var1x) -> {
         return var1x.mob == var1;
      }).findFirst().orElse((Object)null);
      if (var4 != null) {
         int var5 = 0;
         int var6 = 0;
         Iterator var7 = this.followers.iterator();

         while(var7.hasNext()) {
            MobFollower var8 = (MobFollower)var7.next();
            if (var8.mob == var1) {
               var5 = var6;
            }

            if (var8.position == var4.position) {
               ++var6;
            }
         }

         return var4.position.getRelativePos(var2, var3, var5, var6);
      } else {
         return null;
      }
   }

   public int getSameFollowers(Mob var1) {
      MobFollower var2 = (MobFollower)this.followers.stream().filter((var1x) -> {
         return var1x.mob == var1;
      }).findFirst().orElse((Object)null);
      return var2 != null ? (int)this.followers.stream().filter((var1x) -> {
         return var1x.position == var2.position;
      }).count() : 0;
   }

   public void sendConnectingMessage() {
      if (!this.sentConnectingMessage) {
         this.server.network.sendToAllClients(new PacketChatMessage(new LocalMessage("misc", "playerconnecting", "player", this.getName())));
         this.sentConnectingMessage = true;
      }
   }

   public void sendJoinedMessage() {
      if (!this.sentJoinedMessage) {
         this.server.network.sendToAllClients(new PacketChatMessage(new LocalMessage("misc", "playerjoined", "player", this.getName())));
         this.sentJoinedMessage = true;
         if (this.server.isHosted() && this.server.streamClients().allMatch((var1x) -> {
            return var1x == this || var1x.networkInfo == null;
         })) {
            ServerClient var1 = this.server.getLocalServerClient();
            if (var1 != null && var1 != this) {
               PlayerTeam var2 = var1.getPlayerTeam();
               if (var2 == null || var2.getMemberCount() <= 1) {
                  var1.sendChatMessage((GameMessage)(new LocalMessage("misc", "teamcooptip")));
               }
            }
         }

      }
   }

   public void sendChatMessage(String var1) {
      this.sendPacket(new PacketChatMessage(var1));
   }

   public void sendChatMessage(GameMessage var1) {
      this.sendPacket(new PacketChatMessage(var1));
   }

   public void sendUniqueFloatText(int var1, int var2, GameMessage var3, String var4, int var5) {
      this.sendPacket(new PacketUniqueFloatText(var1, var2, var3, var4, var5));
   }

   public void sendPacket(Packet var1) {
      this.server.network.sendPacket(var1, this);
   }

   public void submitSpawnPacket(PacketSpawnPlayer var1) {
      if (this.needAppearance()) {
         this.server.disconnectClient(this, PacketDisconnect.Code.MISSING_APPEARANCE);
         System.out.println("Disconnected client " + this.authentication + " for missing appearance.");
      } else if (this.playerMob != null) {
         this.refreshAFKTimer();
         if (!this.hasSpawned) {
            this.playerMob.refreshSpawnTime();
         }

         this.hasSpawned = true;
         this.spawnedCheckTimer = 0L;
         Level var2 = this.server.world.getLevel(this);
         synchronized(var2.entityManager.lock) {
            var2.entityManager.getLevelEvents().stream().filter(LevelEvent::isNetworkImportant).forEach((var1x) -> {
               this.sendPacket(new PacketLevelEvent(var1x));
            });
         }

         this.sendJoinedMessage();
         this.server.network.sendToAllClients(new PacketSpawnPlayer(this));
      }
   }

   public void applyLoadedCharacterPacket(PacketSelectedCharacter var1) {
      this.characterUniqueID = var1.characterUniqueID;
      if (var1.networkData != null) {
         var1.networkData.applyToPlayer(this.playerMob);
         if (!var1.networkData.applyToStats(this.characterStats)) {
            this.characterStats = new PlayerStats(false, PlayerStats.Mode.READ_ONLY);
         }
      }

      this.server.usedNames.put(this.authentication, this.getName());
      this.server.world.savePlayer(this);
      this.server.network.sendToAllClients(new PacketPlayerAppearance(this));
      this.needAppearance = false;
      this.submittedCharacter = true;
   }

   public void applyAppearancePacket(PacketPlayerAppearance var1) {
      this.characterUniqueID = var1.characterUniqueID;
      this.playerMob.applyAppearancePacket(var1);
      this.server.usedNames.put(this.authentication, this.getName());
      this.server.world.savePlayer(this);
      this.server.network.sendToAllClients(var1);
      this.needAppearance = false;
      this.submittedCharacter = true;
   }

   public boolean needTotalStats() {
      return this.totalStats == null;
   }

   public void applyClientStatsPacket(PacketClientStats var1) {
      if (this.needTotalStats()) {
         this.totalStats = var1.stats;
         this.achievements = var1.achievements;
         this.totalStats.cleanAll();
         if (this.playerMob.getInv().trinkets.getSize() > 4) {
            this.achievements().MAGICAL_DROP.markCompleted(this);
         }

      }
   }

   public PlayerStats characterStats() {
      return this.characterStats;
   }

   public void resetStats() {
      this.characterStats = new PlayerStats(false, PlayerStats.Mode.READ_ONLY);
      if (!this.needTotalStats()) {
         this.totalStats = new PlayerStats(false, PlayerStats.Mode.READ_AND_WRITE);
      }

      this.newStats = new PlayerStats(false, PlayerStats.Mode.WRITE_ONLY);
   }

   public void markObtainItem(String var1) {
      this.newStats.items_obtained.addItem(var1);
      if (this.achievementsLoaded() && !this.achievements().GET_PET.isCompleted()) {
         boolean var2 = AchievementManager.GET_PET_ITEMS.contains(var1);
         if (var2) {
            this.achievements().GET_PET.markCompleted(this);
         }
      }

   }

   public boolean achievementsLoaded() {
      return this.achievements != null;
   }

   public AchievementManager achievements() {
      return this.achievements;
   }

   public PermissionLevel getPermissionLevel() {
      return this.permissionLevel;
   }

   public boolean setPermissionLevel(PermissionLevel var1, boolean var2) {
      if (this.permissionLevel == var1) {
         return false;
      } else {
         this.permissionLevel = var1;
         if (var2) {
            LocalMessage var3 = new LocalMessage("misc", "permchange");
            var3.addReplacement("perm", var1.name);
            this.sendChatMessage((GameMessage)var3);
         }

         this.sendPacket(new PacketPermissionUpdate(var1));
         return true;
      }
   }

   public void updateInventoryContainer() {
      this.inventoryContainer = new Container(this, 0);
   }

   public Container getContainer() {
      return this.openContainer != null ? this.openContainer : this.inventoryContainer;
   }

   public void openContainer(Container var1) {
      if (this.openContainer != null) {
         this.openContainer.onClose();
      }

      this.openContainer = var1;
      this.openContainer.init();
   }

   public void closeContainer(boolean var1) {
      if (this.openContainer != null) {
         if (var1) {
            this.sendPacket(new PacketCloseContainer());
         }

         this.openContainer.onClose();
         this.openContainer = null;
      }

   }

   public void addQuestDrops(List<InventoryItem> var1, Mob var2, GameRandom var3) {
      Iterator var4 = this.quests.iterator();

      while(var4.hasNext()) {
         Quest var5 = (Quest)var4.next();
         LootTable var6 = var5.getExtraMobDrops(this, var2);
         if (var6 != null) {
            var6.addItems(var1, var3, 1.0F, var2, this);
         }
      }

   }

   public Server getServer() {
      return this.server;
   }

   public Level getLevel() {
      return this.getServer().world.getLevel(this);
   }

   public void submitOutPacket(NetworkPacket var1) {
      this.packetsOutBytes += (long)var1.getByteSize();
      ++this.packetsOutTotal;
   }

   public long getPacketsOutTotal() {
      return this.packetsOutTotal;
   }

   public long getPacketsOutBytes() {
      return this.packetsOutBytes;
   }

   public void submitInPacket(NetworkPacket var1) {
      this.packetsInBytes += (long)var1.getByteSize();
      ++this.packetsInTotal;
      this.lastReceivedPacketTime = System.currentTimeMillis();
   }

   public long getPacketsInTotal() {
      return this.packetsInTotal;
   }

   public long getPacketsInBytes() {
      return this.packetsInBytes;
   }

   public void dispose() {
      super.dispose();
      if (this.networkInfo != null) {
         this.networkInfo.closeConnection();
      }

      this.closeContainer(false);
      if (this.playerMob != null) {
         this.playerMob.remove();
         this.playerMob.dispose();
      }

   }

   public static ServerClient getNewPlayerClient(Server var0, long var1, NetworkInfo var3, int var4, long var5) {
      ServerClient var7 = new ServerClient(var0, var1, var3, var4, var5, (LoadData)null);
      var7.spawnLevelIdentifier = var0.world.worldEntity.spawnLevelIdentifier;
      var7.spawnTile = var0.world.worldEntity.spawnTile;
      var7.setLevelIdentifier(var7.spawnLevelIdentifier);
      var7.playerMob = new PlayerMob(var5, var7);
      var7.playerMob.getInv().giveStarterItems();
      var7.playerMob.setPos((float)(var7.spawnTile.x * 32 + 16), (float)(var7.spawnTile.y * 32 + 16), true);
      var7.playerMob.setUniqueID(var4);
      var7.needAppearance = true;
      var7.setTeamID(-1);
      var7.updateInventoryContainer();
      var7.playerMob.setLevel(var0.world.getLevel(var7.levelIdentifier));
      var7.playerMob.init();
      return var7;
   }

   private static class ExpectedPing {
      public final int responseKey;
      public final long timeSent;
      public ExpectedPing next;

      public ExpectedPing(int var1, long var2, ExpectedPing var4) {
         this.responseKey = var1;
         this.timeSent = var2;
         this.next = var4;
      }
   }
}
