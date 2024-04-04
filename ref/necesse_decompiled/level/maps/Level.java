package necesse.level.maps;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Stream;
import necesse.engine.DisposableExecutorService;
import necesse.engine.GameState;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.WorldSettingsGetter;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.modLoader.ModSaveInfo;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketChangeObject;
import necesse.engine.network.packet.PacketChangeTile;
import necesse.engine.network.packet.PacketChangeWire;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.IDData;
import necesse.engine.registries.LevelLayerRegistry;
import necesse.engine.registries.LevelRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.save.LevelSave;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.tickManager.Performance;
import necesse.engine.tickManager.PerformanceTimerManager;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.engine.util.IntersectionPoint;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.RayLinkedList;
import necesse.engine.world.World;
import necesse.engine.world.WorldEntity;
import necesse.engine.world.WorldEntityGameClock;
import necesse.engine.world.WorldGenerator;
import necesse.engine.world.WorldSettings;
import necesse.entity.Entity;
import necesse.entity.manager.EntityManager;
import necesse.entity.manager.MobDeathListenerEntityComponent;
import necesse.entity.manager.ObjectDestroyedListenerEntityComponent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelDrawUtils;
import necesse.gfx.drawables.WallShadowVariables;
import necesse.gfx.gameSound.GameSound;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.gameObject.GameObject;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.trial.TrialRoomLevel;
import necesse.level.maps.hudManager.HudManager;
import necesse.level.maps.layers.LevelLayer;
import necesse.level.maps.layers.LogicLevelLayer;
import necesse.level.maps.layers.ObjectLevelLayer;
import necesse.level.maps.layers.ObjectRotationLevelLayer;
import necesse.level.maps.layers.RainingLevelLayer;
import necesse.level.maps.layers.SettlementLevelLayer;
import necesse.level.maps.layers.TileLevelLayer;
import necesse.level.maps.levelBuffManager.LevelBuffManager;
import necesse.level.maps.levelData.LevelData;
import necesse.level.maps.levelData.LevelDataManager;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.light.LightManager;
import necesse.level.maps.liquidManager.LiquidManager;
import necesse.level.maps.regionSystem.RegionManager;
import necesse.level.maps.splattingManager.SplattingManager;
import necesse.level.maps.wireManager.WireManager;

public class Level implements WorldEntityGameClock, GameState, WorldSettingsGetter {
   public final IDData idData = new IDData();
   public PerformanceTimerManager debugLoadingPerformance;
   public static int EXECUTOR_POOL_SIZE = 5;
   public ArrayList<ModSaveInfo> lastMods = null;
   public final HashSet<LevelIdentifier> childLevels = new HashSet();
   private LevelIdentifier identifier;
   public final boolean isIncursionLevel;
   public final boolean isTrialRoom;
   private boolean loadingComplete;
   public final int width;
   public final int height;
   public boolean isCave;
   public boolean isProtected = false;
   public final LevelLayer[] layers;
   protected final TileLevelLayer tileLayer;
   protected final ObjectLevelLayer objectLayer;
   protected final ObjectRotationLevelLayer objectRotationLayer;
   public final LogicLevelLayer logicLayer;
   public final RainingLevelLayer rainingLayer;
   public final SettlementLevelLayer settlementLayer;
   public final EntityManager entityManager;
   public final WireManager wireManager;
   public final LevelDataManager levelDataManager;
   private final HashMap<Integer, Long> grassWeave;
   public final LightManager lightManager;
   public final LevelBuffManager buffManager;
   public final GNDItemMap gndData;
   public final SplattingManager splattingManager;
   public final RegionManager regionManager;
   public final LiquidManager liquidManager;
   public final HudManager hudManager;
   public final LevelDrawUtils drawUtils;
   private boolean isDisposed;
   private DisposableExecutorService executor;
   private final Object executorLock = new Object();
   public Biome biome;
   public int unloadLevelBuffer;
   protected boolean lastPreventSleep;
   protected boolean preventSleep;
   public int presentPlayers;
   public int presentAdventurePartyMembers;
   public long lastWorldTime;
   public int tileTickX;
   public int tileTickY;
   private int tileTicks;
   private WorldEntity worldEntity;
   private Server server;
   private Client client;
   public final PlayerStats levelStats;
   public static final Point[] adjacentGetters = new Point[]{new Point(-1, -1), new Point(0, -1), new Point(1, -1), new Point(-1, 0), new Point(1, 0), new Point(-1, 1), new Point(0, 1), new Point(1, 1)};
   protected static WorldSettings defaultWorldSettings = new WorldSettings((World)null);

   public final String getStringID() {
      return this.idData.getStringID();
   }

   public final int getID() {
      return this.idData.getID();
   }

   public Level(LevelIdentifier var1, int var2, int var3, WorldEntity var4) {
      this.levelStats = new PlayerStats(false, PlayerStats.Mode.READ_AND_WRITE);
      this.identifier = var1;
      this.width = var2;
      this.height = var3;
      this.isIncursionLevel = this instanceof IncursionLevel;
      this.isTrialRoom = this instanceof TrialRoomLevel;
      this.setWorldEntity(var4);
      LevelRegistry.instance.applyIDData(this.getClass(), this.idData);
      this.layers = LevelLayerRegistry.getNewLayersArray(this);
      this.tileLayer = (TileLevelLayer)this.getLayer(LevelLayerRegistry.TILE_LAYER, TileLevelLayer.class);
      this.objectLayer = (ObjectLevelLayer)this.getLayer(LevelLayerRegistry.OBJECT_LAYER, ObjectLevelLayer.class);
      this.objectRotationLayer = (ObjectRotationLevelLayer)this.getLayer(LevelLayerRegistry.OBJECT_ROTATIONS_LAYER, ObjectRotationLevelLayer.class);
      this.logicLayer = (LogicLevelLayer)this.getLayer(LevelLayerRegistry.LOGIC_LAYER, LogicLevelLayer.class);
      this.rainingLayer = (RainingLevelLayer)this.getLayer(LevelLayerRegistry.RAINING_LAYER, RainingLevelLayer.class);
      this.settlementLayer = (SettlementLevelLayer)this.getLayer(LevelLayerRegistry.SETTLEMENT_LAYER, SettlementLevelLayer.class);
      this.regionManager = this.constructRegionManager();
      this.wireManager = this.constructWireManager();
      this.levelDataManager = this.constructLevelDataManager();
      if (var1.isIslandPosition()) {
         this.biome = WorldGenerator.getBiome(var1.getIslandX(), var1.getIslandY());
      } else {
         this.biome = BiomeRegistry.UNKNOWN;
      }

      this.lightManager = this.constructLightManager();
      this.splattingManager = this.constructSplattingManager();
      this.liquidManager = this.constructLiquidManager();
      this.entityManager = this.constructEntityManager();
      this.buffManager = this.constructLevelBuffManager();
      this.gndData = new GNDItemMap();
      this.hudManager = this.constructHudManager();
      this.drawUtils = this.constructLevelDrawUtils();
      this.grassWeave = new HashMap();
      this.tileTicks = var2 * var3 / 20;
      LevelLayer[] var5 = this.layers;
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         LevelLayer var8 = var5[var7];
         var8.init();
      }

   }

   public RegionManager constructRegionManager() {
      return new RegionManager(this);
   }

   public WireManager constructWireManager() {
      return new WireManager(this);
   }

   public LevelDataManager constructLevelDataManager() {
      return new LevelDataManager(this);
   }

   public LightManager constructLightManager() {
      return new LightManager(this);
   }

   public SplattingManager constructSplattingManager() {
      return new SplattingManager(this);
   }

   public LiquidManager constructLiquidManager() {
      return new LiquidManager(this);
   }

   public EntityManager constructEntityManager() {
      return new EntityManager(this);
   }

   public LevelBuffManager constructLevelBuffManager() {
      return new LevelBuffManager(this);
   }

   public HudManager constructHudManager() {
      return new HudManager(this);
   }

   public LevelDrawUtils constructLevelDrawUtils() {
      return new LevelDrawUtils(this);
   }

   public void writeLevelDataPacket(PacketWriter var1) {
      var1.putNextInt(this.biome.getID());
      var1.putNextBoolean(this.isCave);
      var1.putNextBoolean(this.isProtected);
      LevelLayer[] var2 = this.layers;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         LevelLayer var5 = var2[var4];
         var5.writeLevelDataPacket(var1);
      }

      this.gndData.writePacket(var1);
   }

   public void readLevelDataPacket(PacketReader var1) {
      this.biome = BiomeRegistry.getBiome(var1.getNextInt());
      this.isCave = var1.getNextBoolean();
      this.isProtected = var1.getNextBoolean();
      LevelLayer[] var2 = this.layers;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         LevelLayer var5 = var2[var4];
         var5.readLevelDataPacket(var1);
      }

      this.gndData.readPacket(var1);
   }

   public boolean shouldSave() {
      return true;
   }

   public void addSaveData(SaveData var1) {
      LevelSave.addLevelBasics(this, var1);
   }

   public void applyLoadData(LoadData var1) {
      LevelSave.applyLevelBasics(this, var1);
   }

   public void onLoadingComplete() {
      this.loadingComplete = true;
      LevelLayer[] var1 = this.layers;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         LevelLayer var4 = var1[var3];
         var4.onLoadingComplete();
      }

      this.lightManager.onLoadingComplete();
      this.splattingManager.updateSplatting(0, 0, this.width - 1, this.height - 1);
      this.regionManager.calculateRegions();
      this.liquidManager.calculateLevel();
      this.buffManager.forceUpdateBuffs();
      this.entityManager.onLoadingComplete();
   }

   public boolean isLoadingComplete() {
      return this.loadingComplete;
   }

   public void onUnloading() {
      if (this.isServer()) {
         this.getServer().streamClients().forEach((var1) -> {
            var1.removeLoadedRegions(this);
         });
      }

      this.entityManager.onUnloading();
   }

   public void simulateSinceLastWorldTime(boolean var1) {
      if (this.worldEntity != null && this.lastWorldTime != 0L) {
         long var2 = this.worldEntity.getWorldTime();
         long var4 = var2 - this.lastWorldTime;
         this.simulateWorldTime(var4, var1);
      }

   }

   public void simulateWorldTime(long var1, boolean var3) {
      if (var1 > 0L) {
         LevelLayer[] var4 = this.layers;
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            LevelLayer var7 = var4[var6];
            var7.simulateWorld(var1, var3);
         }

      }
   }

   public int getRegionID(int var1, int var2) {
      return this.regionManager.getRegionID(var1, var2);
   }

   public int getRoomID(int var1, int var2) {
      return this.regionManager.getRoomID(var1, var2);
   }

   public boolean isOutsideRoomID(int var1, int var2) {
      return this.regionManager.isOutsideRoomID(var1, var2);
   }

   public boolean isOutside(int var1, int var2) {
      return this.regionManager.isOutside(var1, var2);
   }

   public int getRoomSize(int var1) {
      return this.regionManager.getRoomSize(var1);
   }

   public int getRoomSize(int var1, int var2) {
      return this.regionManager.getRoomSize(var1, var2);
   }

   public long getSeed() {
      return this.identifier.isIslandPosition() ? WorldGenerator.getSeed(this.identifier.getIslandX(), this.identifier.getIslandY()) : (long)this.identifier.hashCode();
   }

   public void draw(GameCamera var1, PlayerMob var2, TickManager var3, boolean var4) {
      if (this.lightManager != null && !this.lightManager.isDisposed()) {
         this.lightManager.ensureSetting(Settings.lights);
         this.drawUtils.draw(var1, var2, var3, var4);
      }
   }

   public void drawHud(GameCamera var1, PlayerMob var2, TickManager var3) {
      this.drawUtils.drawLastHudDrawables(var1, var2, var3);
   }

   public void tickEffect(GameCamera var1, PlayerMob var2) {
      LevelDrawUtils.DrawArea var3 = new LevelDrawUtils.DrawArea(this, var1);
      float var4 = this.rainingLayer.getRainAlpha();
      double var5 = -1.0;

      for(int var7 = var3.startX; var7 < var3.endX; ++var7) {
         for(int var8 = var3.startY; var8 < var3.endY; ++var8) {
            LevelLayer[] var9 = this.layers;
            int var10 = var9.length;

            for(int var11 = 0; var11 < var10; ++var11) {
               LevelLayer var12 = var9[var11];
               var12.tickTileEffect(var1, var2, var7, var8);
            }

            if (var4 > 0.0F && this.regionManager.isOutside(var7, var8)) {
               if (var2 != null) {
                  double var14 = var2.getPositionPoint().distance((double)(var7 * 32 + 16), (double)(var8 * 32 + 16));
                  if (var5 == -1.0 || var14 < var5) {
                     var5 = var14;
                  }
               }

               this.biome.tickRainEffect(var1, this, var7, var8, var4);
            }
         }
      }

      if (var5 >= 0.0) {
         GameSound var13 = this.biome.getRainSound(this);
         if (var13 != null) {
            Screen.setWeatherSound(var13, 0.0F, 0.0F, (float)var5, var4);
         }
      }

   }

   public void clientTick() {
      if (this.worldEntity != null) {
         this.lastWorldTime = this.worldEntity.getWorldTime();
      }

      this.lastPreventSleep = this.preventSleep;
      this.preventSleep = false;
      this.lightManager.ensureSetting(Settings.lights);
      this.lightManager.updateAmbientLight();
      this.wireManager.clientTick();
      this.buffManager.clientTick();
      TickManager var10000 = this.tickManager();
      EntityManager var10002 = this.entityManager;
      Objects.requireNonNull(var10002);
      Performance.record(var10000, "entities", (Runnable)(var10002::clientTick));
      this.tickGrassWeave();
      this.biome.clientTick(this);
      this.levelDataManager.tick();
      this.hudManager.tick();
      Performance.record(this.tickManager(), "levelLayers", (Runnable)(() -> {
         LevelLayer[] var1 = this.layers;
         int var2 = var1.length;

         int var3;
         for(var3 = 0; var3 < var2; ++var3) {
            LevelLayer var4 = var1[var3];
            var4.clientTick();
         }

         for(int var6 = 0; var6 < this.tileTicks; ++var6) {
            LevelLayer[] var7 = this.layers;
            var3 = var7.length;

            for(int var8 = 0; var8 < var3; ++var8) {
               LevelLayer var5 = var7[var8];
               var5.tickTile(this.tileTickX, this.tileTickY);
            }

            ++this.tileTickX;
            if (this.tileTickX >= this.width) {
               this.tileTickX = 0;
               ++this.tileTickY;
               if (this.tileTickY >= this.height) {
                  this.tileTickY = 0;
               }
            }
         }

      }));
      this.presentPlayers = 0;
      if (this.getClient() != null) {
         for(int var1 = 0; var1 < this.getClient().getSlots(); ++var1) {
            ClientClient var2 = this.getClient().getClient(var1);
            if (var2 != null && var2.isSamePlace(this) && var2.hasSpawned() && !var2.isDead()) {
               ++this.presentPlayers;
            }
         }
      }

   }

   public void serverTick() {
      if (this.worldEntity != null) {
         this.lastWorldTime = this.worldEntity.getWorldTime();
      }

      this.lastPreventSleep = this.preventSleep;
      this.preventSleep = false;
      this.lightManager.updateAmbientLight();
      this.wireManager.serverTick();
      this.buffManager.serverTick();
      TickManager var10000 = this.tickManager();
      EntityManager var10002 = this.entityManager;
      Objects.requireNonNull(var10002);
      Performance.record(var10000, "entities", (Runnable)(var10002::serverTick));
      ++this.unloadLevelBuffer;
      this.biome.serverTick(this);
      this.levelDataManager.tick();
      Performance.record(this.tickManager(), "levelLayers", (Runnable)(() -> {
         LevelLayer[] var1 = this.layers;
         int var2 = var1.length;

         int var3;
         for(var3 = 0; var3 < var2; ++var3) {
            LevelLayer var4 = var1[var3];
            var4.serverTick();
         }

         for(int var6 = 0; var6 < this.tileTicks; ++var6) {
            LevelLayer[] var7 = this.layers;
            var3 = var7.length;

            for(int var8 = 0; var8 < var3; ++var8) {
               LevelLayer var5 = var7[var8];
               var5.tickTile(this.tileTickX, this.tileTickY);
            }

            ++this.tileTickX;
            if (this.tileTickX >= this.width) {
               this.tileTickX = 0;
               ++this.tileTickY;
               if (this.tileTickY >= this.height) {
                  this.tileTickY = 0;
               }
            }
         }

      }));
      this.presentPlayers = 0;
      this.presentAdventurePartyMembers = 0;
      if (this.isServer()) {
         for(int var1 = 0; var1 < this.getServer().getSlots(); ++var1) {
            ServerClient var2 = this.getServer().getClient(var1);
            if (var2 != null && var2.isSamePlace(this)) {
               this.unloadLevelBuffer = 0;
               if (var2.hasSpawned() && !var2.isDead()) {
                  ++this.presentPlayers;
                  this.presentAdventurePartyMembers += var2.adventureParty.getSize();
               }
            }
         }
      }

   }

   public void frameTick(TickManager var1) {
      LevelLayer[] var2 = this.layers;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         LevelLayer var5 = var2[var4];
         var5.frameTick(var1);
      }

      this.entityManager.frameTick(var1);
   }

   public GameMessage getSetSpawnError(int var1, int var2, ServerClient var3) {
      return null;
   }

   public void preventSleep() {
      this.preventSleep = true;
   }

   public boolean isSleepPrevented() {
      return this.lastPreventSleep || this.preventSleep;
   }

   public GameLight getLightLevel(int var1, int var2) {
      return this.lightManager.getLightLevel(var1, var2);
   }

   public GameLight getLightLevelWall(int var1, int var2) {
      return this.lightManager.getLightLevelWall(var1, var2);
   }

   public GameLight getLightLevel(Entity var1) {
      return this.getLightLevel(var1.getX() / 32, var1.getY() / 32);
   }

   public Stream<WallShadowVariables> getWallShadows() {
      float var1 = this.getWorldEntity().getAmbientLightFloat();
      if (var1 <= 0.0F) {
         return Stream.empty();
      } else {
         float var2 = this.getWorldEntity().getSunProgress();
         return var2 < 0.0F ? Stream.empty() : Stream.of(WallShadowVariables.fromProgress(var1, var2, 32.0F, 320.0F));
      }
   }

   public <T extends LevelLayer> T getLayer(int var1, Class<T> var2) {
      return (LevelLayer)var2.cast(this.layers[var1]);
   }

   public GameTile getTile(int var1, int var2) {
      return this.tileLayer.getTile(var1, var2);
   }

   public LevelTile getLevelTile(int var1, int var2) {
      return new LevelTile(this, var1, var2);
   }

   public int getTileID(int var1, int var2) {
      return this.tileLayer.getTileID(var1, var2);
   }

   public GameMessage getTileName(int var1, int var2) {
      return this.getTile(var1, var2).getLocalization();
   }

   public GameMessage getObjectName(int var1, int var2) {
      return this.getObject(var1, var2).getLocalization();
   }

   public void setTile(int var1, int var2, int var3) {
      this.tileLayer.setTile(var1, var2, var3);
   }

   public GameObject getObject(int var1, int var2) {
      return this.objectLayer.getObject(var1, var2);
   }

   public LevelObject getLevelObject(int var1, int var2) {
      return new LevelObject(this, var1, var2);
   }

   public int getObjectID(int var1, int var2) {
      return this.objectLayer.getObjectID(var1, var2);
   }

   public byte getObjectRotation(int var1, int var2) {
      return this.objectRotationLayer.getObjectRotation(var1, var2);
   }

   public void setObjectRotation(int var1, int var2, int var3) {
      this.objectRotationLayer.setObjectRotation(var1, var2, var3);
   }

   public <T> T[] getRelative(int var1, int var2, Point[] var3, BiFunction<Integer, Integer, T> var4, IntFunction<T[]> var5) {
      Object[] var6 = (Object[])var5.apply(var3.length);

      for(int var7 = 0; var7 < var3.length; ++var7) {
         var6[var7] = var4.apply(var1 + var3[var7].x, var2 + var3[var7].y);
      }

      return var6;
   }

   public boolean getRelativeAnd(int var1, int var2, Point[] var3, BiPredicate<Integer, Integer> var4) {
      Point[] var5 = var3;
      int var6 = var3.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         Point var8 = var5[var7];
         if (!var4.test(var1 + var8.x, var2 + var8.y)) {
            return false;
         }
      }

      return true;
   }

   public Integer[] getAdjacentTilesInt(int var1, int var2) {
      return (Integer[])this.getRelative(var1, var2, adjacentGetters, this::getTileID, (var0) -> {
         return new Integer[var0];
      });
   }

   public GameTile[] getAdjacentTiles(int var1, int var2) {
      return (GameTile[])this.getRelative(var1, var2, adjacentGetters, this::getTile, (var0) -> {
         return new GameTile[var0];
      });
   }

   public LevelTile[] getAdjacentLevelTiles(int var1, int var2) {
      return (LevelTile[])this.getRelative(var1, var2, adjacentGetters, this::getLevelTile, (var0) -> {
         return new LevelTile[var0];
      });
   }

   public GameObject[] getAdjacentObjects(int var1, int var2) {
      return (GameObject[])this.getRelative(var1, var2, adjacentGetters, this::getObject, (var0) -> {
         return new GameObject[var0];
      });
   }

   public LevelObject[] getAdjacentLevelObjects(int var1, int var2) {
      return (LevelObject[])this.getRelative(var1, var2, adjacentGetters, this::getLevelObject, (var0) -> {
         return new LevelObject[var0];
      });
   }

   public Integer[] getAdjacentObjectsInt(int var1, int var2) {
      return (Integer[])this.getRelative(var1, var2, adjacentGetters, this::getObjectID, (var0) -> {
         return new Integer[var0];
      });
   }

   public boolean isShore(int var1, int var2) {
      return this.liquidManager.isShore(var1, var2);
   }

   public boolean isProtected(int var1, int var2) {
      return this.isProtected;
   }

   public long grassWeave(int var1, int var2) {
      synchronized(this.grassWeave) {
         return (Long)this.grassWeave.getOrDefault(var1 + var2 * this.width, 0L) - this.getWorldEntity().getLocalTime();
      }
   }

   public boolean isGrassWeaving(int var1, int var2) {
      int var3 = var1 + var2 * this.width;
      synchronized(this.grassWeave) {
         return this.grassWeave.containsKey(var3);
      }
   }

   public void makeGrassWeave(int var1, int var2, int var3, boolean var4) {
      if (!this.isServer()) {
         long var5 = this.getWorldEntity().getLocalTime() + (long)var3;
         int var7 = var1 + var2 * this.width;
         synchronized(this.grassWeave) {
            if (var4 || !this.grassWeave.containsKey(var7)) {
               this.grassWeave.put(var7, var5);
            }

         }
      }
   }

   public void forceGrassWeave(int var1, int var2, int var3) {
      if (!this.isServer()) {
         long var4 = this.getWorldEntity().getLocalTime() + (long)var3;
         int var6 = var1 + var2 * this.width;
         synchronized(this.grassWeave) {
            this.grassWeave.put(var6, var4);
         }
      }
   }

   public void tickGrassWeave() {
      long var1 = this.getWorldEntity().getLocalTime();
      ArrayList var3 = new ArrayList();
      this.grassWeave.forEach((var3x, var4) -> {
         if (var4 - var1 < 0L) {
            var3.add(var3x);
         }

      });
      synchronized(this.grassWeave) {
         HashMap var10001 = this.grassWeave;
         Objects.requireNonNull(var10001);
         var3.forEach(var10001::remove);
      }
   }

   public ArrayList<LevelObjectHit> getCollisions(Shape var1, CollisionFilter var2) {
      ArrayList var3 = new ArrayList();
      if (var2 == null) {
         return var3;
      } else {
         this.streamShapeBounds(var1).forEach((var3x) -> {
            var2.addCollisions(var3, var1, var3x);
         });
         Rectangle var4 = new Rectangle(0, 0, this.width * 32, this.height * 32);
         if (!var4.contains(var1.getBounds())) {
            var3.add(new LevelObjectHit(var4, this));
         }

         return var3;
      }
   }

   public boolean collides(Shape var1, CollisionFilter var2) {
      return var2 == null ? false : this.collides(var1, (var2x) -> {
         return var2.check(var1, var2x);
      });
   }

   public boolean collides(Line2D var1, CollisionFilter var2) {
      if (var2 == null) {
         return false;
      } else {
         double var3 = var1.getP1().distance(var1.getP2());
         RayLinkedList var5 = GameUtils.castRay(this, var1.getX1(), var1.getY1(), var1.getX2() - var1.getX1(), var1.getY2() - var1.getY1(), var3, 0, var2);
         return var5.totalDist < var3 && var3 > 0.0;
      }
   }

   public boolean collides(Line2D var1, float var2, float var3, CollisionFilter var4) {
      if (var4 == null) {
         return false;
      } else if (this.collides(var1, var4)) {
         return true;
      } else {
         double var5 = var1.getP1().distance(var1.getP2());
         Point2D.Double var7 = GameMath.normalize(var1.getX2() - var1.getX1(), var1.getY2() - var1.getY1());
         boolean var8 = false;
         float var9 = var2 / 2.0F;

         for(float var10 = var3; var10 <= var9; var10 += var3) {
            Point2D.Double var11 = GameMath.getPerpendicularPoint(var1.getP1(), var10, var7);
            RayLinkedList var12 = GameUtils.castRay(this, var11.x, var11.y, var7.x, var7.y, var5, 0, var4);
            if (var12.totalDist < var5 && var5 > 0.0) {
               return true;
            }

            Point2D.Double var13 = GameMath.getPerpendicularPoint(var1.getP1(), -var10, var7);
            RayLinkedList var14 = GameUtils.castRay(this, var13.x, var13.y, var7.x, var7.y, var5, 0, var4);
            if (var14.totalDist < var5 && var5 > 0.0) {
               return true;
            }

            if (var10 == var9) {
               var8 = true;
            }
         }

         if (var8) {
            Point2D.Double var15 = GameMath.getPerpendicularPoint(var1.getP1(), var9, var7);
            RayLinkedList var16 = GameUtils.castRay(this, var15.x, var15.y, var7.x, var7.y, var5, 0, var4);
            if (var16.totalDist < var5 && var5 > 0.0) {
               return true;
            }

            Point2D.Double var17 = GameMath.getPerpendicularPoint(var1.getP1(), -var9, var7);
            RayLinkedList var18 = GameUtils.castRay(this, var17.x, var17.y, var7.x, var7.y, var5, 0, var4);
            if (var18.totalDist < var5 && var5 > 0.0) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean collides(Shape var1, Function<TilePosition, Boolean> var2) {
      Stream var10000 = this.streamShapeBounds(var1);
      Objects.requireNonNull(var2);
      if (var10000.anyMatch(var2::apply)) {
         return true;
      } else {
         Rectangle var3 = new Rectangle(0, 0, this.width * 32, this.height * 32);
         return !var3.contains(var1.getBounds());
      }
   }

   protected Stream<TilePosition> streamShapeBounds(Shape var1) {
      return (new LevelTilesSpliterator(this, var1, 0)).stream();
   }

   public IntersectionPoint<LevelObjectHit> getCollisionPoint(List<LevelObjectHit> var1, Line2D var2, boolean var3) {
      return CollisionPoint.getClosestCollision(var1, var2, var3);
   }

   public boolean isSolidTile(int var1, int var2) {
      return this.getObject(var1, var2).isSolid(this, var1, var2);
   }

   public boolean clientsCollides(Rectangle var1) {
      return GameUtils.streamNetworkClients(this).filter((var0) -> {
         return var0.playerMob != null && var0.hasSpawned() && !var0.playerMob.isFlying();
      }).anyMatch((var1x) -> {
         return var1.intersects(var1x.playerMob.getCollision());
      });
   }

   public boolean entityCollides(Rectangle var1, boolean var2) {
      boolean var3 = false;
      if (var2) {
         var3 = this.clientsCollides(var1);
      }

      var3 = var3 || this.entityManager.mobs.streamInRegionsShape(var1, 1).filter((var0) -> {
         return !var0.removed() && var0.canLevelInteract() && !var0.isFlying();
      }).anyMatch((var1x) -> {
         return var1.intersects(var1x.getCollision());
      });
      return var3;
   }

   public boolean isLiquidTile(int var1, int var2) {
      return this.getTile(var1, var2).isLiquid;
   }

   public boolean inLiquid(int var1, int var2) {
      int var3 = var1 / 32;
      int var4 = var2 / 32;
      return this.getTile(var3, var4).inLiquid(this, var3, var4, var1, var2) && !this.getObject(var3, var4).overridesInLiquid(this, var3, var4, var1, var2);
   }

   public boolean inLiquid(Point var1) {
      return this.inLiquid(var1.x, var1.y);
   }

   public void setObject(int var1, int var2, int var3) {
      this.objectLayer.setObject(var1, var2, var3);
   }

   public void setObject(int var1, int var2, int var3, int var4) {
      this.setObject(var1, var2, var3);
      this.setObjectRotation(var1, var2, var4);
   }

   public void replaceObjectEntity(int var1, int var2) {
      GameObject var3 = ObjectRegistry.getObject(this.getObjectID(var1, var2));
      ObjectEntity var4 = var3.getNewObjectEntity(this, var1, var2);
      if (var4 != null) {
         this.entityManager.objectEntities.add(var4);
         if (this.isClient() && var4.shouldRequestPacket()) {
            this.getClient().loading.objectEntities.addObjectEntityRequest(var1, var2);
         }
      } else {
         this.entityManager.removeObjectEntity(var1, var2);
      }

   }

   public void sendObjectChangePacket(Server var1, int var2, int var3, int var4, int var5) {
      this.setObject(var2, var3, var4, var5);
      if (var1 != null) {
         var1.network.sendToClientsWithTile(new PacketChangeObject(this, var2, var3, var4, var5), this, var2, var3);
      }

   }

   public void sendObjectChangePacket(Server var1, int var2, int var3, int var4) {
      this.sendObjectChangePacket(var1, var2, var3, var4, this.getObjectRotation(var2, var3));
   }

   public void sendTileChangePacket(Server var1, int var2, int var3, int var4) {
      this.setTile(var2, var3, var4);
      if (var1 != null) {
         var1.network.sendToClientsWithTile(new PacketChangeTile(this, var2, var3, var4), this, var2, var3);
      }

   }

   public void sendObjectUpdatePacket(int var1, int var2) {
      if (this.isServer()) {
         this.getServer().network.sendToClientsWithTile(new PacketChangeObject(this, var1, var2, this.getObjectID(var1, var2), this.getObjectRotation(var1, var2)), this, var1, var2);
      }

   }

   public void sendTileUpdatePacket(int var1, int var2) {
      if (this.isServer()) {
         this.getServer().network.sendToClientsWithTile(new PacketChangeTile(this, var1, var2, this.getTileID(var1, var2)), this, var1, var2);
      }

   }

   public void sendWireChangePacket(Server var1, int var2, int var3, byte var4) {
      this.wireManager.setWireData(var2, var3, var4, true);
      var1.network.sendToClientsWithTile(new PacketChangeWire(this, var2, var3, var4), this, var2, var3);
   }

   public void sendWireUpdatePacket(int var1, int var2) {
      if (this.isServer()) {
         this.getServer().network.sendToClientsWithTile(new PacketChangeWire(this, var1, var2, this.wireManager.getWireData(var1, var2)), this, var1, var2);
      }

   }

   public LevelData getLevelData(String var1) {
      return this.levelDataManager.getLevelData(var1);
   }

   public void addLevelData(String var1, LevelData var2) {
      this.levelDataManager.addLevelData(var1, var2);
   }

   public boolean shouldLimitCameraWithinBounds(PlayerMob var1) {
      return Settings.limitCameraToLevelBounds;
   }

   public Stream<ModifierValue<?>> getDefaultLevelModifiers() {
      return this.entityManager.getLevelModifiers();
   }

   public Stream<ModifierValue<?>> getMobModifiers(Mob var1) {
      return this.entityManager.getMobModifiers(var1);
   }

   public LootTable getCrateLootTable() {
      return LootTablePresets.basicCrate;
   }

   public void onMobDied(Mob var1, Attacker var2, HashSet<Attacker> var3) {
      this.entityManager.componentManager.streamAll(MobDeathListenerEntityComponent.class).forEach((var3x) -> {
         var3x.onLevelMobDied(var1, var2, var3);
      });
   }

   public LootTable getExtraMobDrops(Mob var1) {
      return this.biome.getExtraMobDrops(var1);
   }

   public LootTable getExtraPrivateMobDrops(Mob var1, ServerClient var2) {
      return this.biome.getExtraPrivateMobDrops(var1, var2);
   }

   public void onObjectDestroyed(GameObject var1, int var2, int var3, ServerClient var4, ArrayList<ItemPickupEntity> var5) {
      this.entityManager.componentManager.streamAll(ObjectDestroyedListenerEntityComponent.class).forEach((var5x) -> {
         var5x.onObjectDestroyed(var1, var2, var3, var4, var5);
      });
   }

   public void addReturnedItems(ArrayList<InventoryItem> var1) {
      for(int var2 = 0; var2 < this.width; ++var2) {
         for(int var3 = 0; var3 < this.height; ++var3) {
            GameObject var4 = this.getObject(var2, var3);
            if (var4.shouldReturnOnDeletedLevels(this, var2, var3)) {
               Iterator var5 = var4.getDroppedItems(this, var2, var3).iterator();

               while(var5.hasNext()) {
                  InventoryItem var6 = (InventoryItem)var5.next();
                  var6.combineOrAddToList(this, (PlayerMob)null, var1, "add");
               }
            }

            GameTile var8 = this.getTile(var2, var3);
            if (var8.shouldReturnOnDeletedLevels(this, var2, var3)) {
               Iterator var9 = var8.getDroppedItems(this, var2, var3).iterator();

               while(var9.hasNext()) {
                  InventoryItem var7 = (InventoryItem)var9.next();
                  var7.combineOrAddToList(this, (PlayerMob)null, var1, "add");
               }
            }
         }
      }

   }

   public boolean isSamePlace(Level var1) {
      return this.identifier.equals(var1.identifier);
   }

   public boolean isIslandPosition() {
      return this.identifier.isIslandPosition();
   }

   public int getIslandX() {
      return this.identifier.getIslandX();
   }

   public int getIslandY() {
      return this.identifier.getIslandY();
   }

   public int getIslandDimension() {
      return this.identifier.getIslandDimension();
   }

   public LevelIdentifier getIdentifier() {
      return this.identifier;
   }

   public int getIdentifierHashCode() {
      return this.identifier.hashCode();
   }

   public void overwriteIdentifier(LevelIdentifier var1) {
      this.identifier = var1;
   }

   public void makeClientLevel(Client var1) {
      this.server = null;
      this.client = var1;
   }

   public void makeServerLevel(Server var1) {
      this.client = null;
      this.server = var1;
      this.lightManager.setting = Settings.LightSetting.White;
   }

   /** @deprecated */
   @Deprecated
   public boolean isClientLevel() {
      return this.isClient();
   }

   public boolean isClient() {
      return this.client != null;
   }

   public Client getClient() {
      return this.client;
   }

   /** @deprecated */
   @Deprecated
   public boolean isServerLevel() {
      return this.isServer();
   }

   public boolean isServer() {
      return this.server != null;
   }

   public Server getServer() {
      return this.server;
   }

   public void setWorldEntity(WorldEntity var1) {
      this.worldEntity = var1;
   }

   public WorldEntity getWorldEntity() {
      return this.worldEntity;
   }

   public TickManager tickManager() {
      if (this.getClient() != null) {
         return this.getClient().tickManager();
      } else {
         return this.getServer() != null ? this.getServer().tickManager() : Screen.tickManager;
      }
   }

   public WorldSettings getWorldSettings() {
      if (this.getClient() != null) {
         return this.getClient().worldSettings;
      } else {
         return this.getServer() != null ? this.getServer().world.settings : defaultWorldSettings;
      }
   }

   public GameMessage getLocationMessage() {
      return this.biome.getLocalization();
   }

   public DisposableExecutorService executor() {
      synchronized(this.executorLock) {
         if (this.executor == null) {
            AtomicInteger var2 = new AtomicInteger();
            ThreadPoolExecutor var3 = new ThreadPoolExecutor(0, EXECUTOR_POOL_SIZE, 1L, TimeUnit.MINUTES, new LinkedBlockingDeque(), (var2x) -> {
               return new Thread((ThreadGroup)null, var2x, "level-" + this.getHostString() + "-" + this.getIdentifier() + "-executor-" + var2.addAndGet(1));
            });
            var3.allowCoreThreadTimeOut(true);
            var3.setCorePoolSize(EXECUTOR_POOL_SIZE);
            this.executor = new DisposableExecutorService(var3);
         }

         return this.executor;
      }
   }

   public void dispose() {
      this.isDisposed = true;
      this.entityManager.dispose();
      this.lightManager.dispose();
      this.drawUtils.dispose();
      synchronized(this.executorLock) {
         if (this.executor != null) {
            this.executor.dispose();
         }

      }
   }

   public boolean isDisposed() {
      return this.isDisposed;
   }

   public String toString() {
      return this.getClass().getSimpleName() + "@" + Integer.toHexString(this.hashCode()) + "{" + this.getHostString() + ", " + this.identifier.toString() + "}";
   }
}
