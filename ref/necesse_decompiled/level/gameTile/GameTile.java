package necesse.level.gameTile;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.GameEvents;
import necesse.engine.Screen;
import necesse.engine.events.loot.TileLootTableDropsEvent;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.IDData;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.RegistryClosedException;
import necesse.engine.registries.TileRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.TileDamageType;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.LevelTileDrawOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.gfx.gameTexture.MergeFunction;
import necesse.gfx.gameTexture.SharedGameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.placeableItem.tileItem.TileItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.LevelTile;
import necesse.level.maps.TilePosition;
import necesse.level.maps.biomes.MobSpawnTable;
import necesse.level.maps.layers.SimulatePriorityList;
import necesse.level.maps.levelBuffManager.LevelModifiers;
import necesse.level.maps.levelData.jobs.LevelJob;
import necesse.level.maps.light.GameLight;

public class GameTile {
   public static SharedGameTexture tileTextures;
   public static GameTextureSection tileBlankTexture;
   public static GameTextureSection tileShoreTexture;
   private static GameTextureSection tileErrorTexture;
   public static GameTexture generatedTileTexture;
   public final IDData idData = new IDData();
   public final boolean terrainSplatting;
   public final boolean isLiquid;
   public String[] itemCategoryTree = new String[]{"tiles"};
   public HashSet<String> itemGlobalIngredients = new HashSet();
   public boolean isFloor;
   public boolean smartMinePriority;
   public int lightLevel;
   public float lightHue;
   public float lightSat;
   public Color mapColor = new Color(200, 50, 50);
   private GameMessage displayName;
   public boolean canBeMined;
   public int toolTier = 0;
   public int stackSize = 100;
   public int tileHealth;
   public boolean shouldReturnOnDeletedLevels;
   public HashSet<String> roomProperties = new HashSet();

   public static void setupTileTextures() {
      tileTextures = new SharedGameTexture("tilesShared");
      tileBlankTexture = tileTextures.addBlankQuad(32, 32);
      tileShoreTexture = tileTextures.addTexture(GameTexture.fromFile("tiles/shoremask"));
      tileErrorTexture = tileTextures.addTexture(GameResources.error);
   }

   public static void generateTileTextures() {
      generatedTileTexture = tileTextures.generate();
      tileTextures.close();
   }

   public final String getStringID() {
      return this.idData.getStringID();
   }

   public final int getID() {
      return this.idData.getID();
   }

   public GameTile(boolean var1) {
      if (TileRegistry.instance.isClosed()) {
         throw new RegistryClosedException("Cannot construct GameTile objects when tile registry is closed, since they are a static registered objects. Use TileRegistry.getTile(...) to get tiles.");
      } else {
         this.terrainSplatting = this instanceof TerrainSplatterTile;
         this.isLiquid = this instanceof LiquidTile;
         this.isFloor = var1;
         this.smartMinePriority = var1;
         this.tileHealth = var1 ? 50 : 100;
         if (this.isLiquid) {
            this.setItemCategory("tiles", "liquids");
         } else if (var1) {
            this.setItemCategory("tiles", "floors");
         } else if (this.terrainSplatting) {
            this.setItemCategory("tiles", "terrain");
         }

      }
   }

   public void onTileRegistryClosed() {
   }

   public LootTable getLootTable(Level var1, int var2, int var3) {
      return ItemRegistry.itemExists(this.getStringID()) ? new LootTable(new LootItemInterface[]{(new LootItem(this.getStringID())).preventLootMultiplier()}) : new LootTable();
   }

   public void addDrawables(LevelTileDrawOptions var1, List<LevelSortedDrawable> var2, Level var3, int var4, int var5, GameCamera var6, TickManager var7) {
      int var8 = var6.getTileDrawX(var4);
      int var9 = var6.getTileDrawY(var5);
      var1.add(tileErrorTexture).pos(var8, var9);
   }

   public void addBridgeDrawables(LevelTileDrawOptions var1, List<LevelSortedDrawable> var2, Level var3, int var4, int var5, GameCamera var6, TickManager var7) {
   }

   public void addLightDrawables(LevelTileDrawOptions var1, List<LevelSortedDrawable> var2, Level var3, int var4, int var5, GameCamera var6, TickManager var7) {
      var1.addLight(var7, var3, var4, var5, var6);
   }

   public void drawPreview(Level var1, int var2, int var3, float var4, PlayerMob var5, GameCamera var6) {
   }

   public GameMessage getNewLocalization() {
      return new LocalMessage("tile", this.getStringID());
   }

   public void updateLocalDisplayName() {
      this.displayName = this.getNewLocalization();
   }

   public GameMessage getLocalization() {
      return this.displayName;
   }

   public String getDisplayName() {
      return this.displayName.translate();
   }

   public GameTile setItemCategory(String... var1) {
      this.itemCategoryTree = var1;
      return this;
   }

   public GameTile addGlobalIngredient(String... var1) {
      this.itemGlobalIngredients.addAll(Arrays.asList(var1));
      return this;
   }

   public Color getMapColor(Level var1, int var2, int var3) {
      return this.mapColor;
   }

   public Color getDebrisColor(Level var1, int var2, int var3) {
      return this.getMapColor(var1, var2, var3);
   }

   public final void loadTileTextures() {
      this.loadTextures();
      if (this.terrainSplatting) {
         ((TerrainSplatterTile)this).generateSplattingTextures();
      }

   }

   public GameTexture generateItemTexture() {
      GameTexture var1 = GameTexture.fromFile("tiles/itemmask", true);
      GameTexture var2 = new GameTexture(GameResources.error);
      var2.merge(var1, 0, 0, MergeFunction.MULTIPLY);
      var2.makeFinal();
      return var2;
   }

   protected void loadTextures() {
   }

   protected long getTileSeed(int var1, int var2, int var3) {
      return (long)((var1 * 1289969 + var2 * 888161) * GameRandom.prime(Math.abs(var3)));
   }

   protected long getTileSeed(int var1, int var2) {
      return this.getTileSeed(var1, var2, 0);
   }

   public TileItem getTileItem() {
      return (TileItem)ItemRegistry.getItem(this.getStringID());
   }

   public TileItem generateNewTileItem() {
      return new TileItem(this);
   }

   public boolean canBePlacedOn(Level var1, int var2, int var3, GameTile var4) {
      return !this.isLiquid && !this.isFloor;
   }

   public String canPlace(Level var1, int var2, int var3) {
      GameTile var4 = var1.getTile(var2, var3);
      if (var4 == this) {
         return "sametile";
      } else {
         return !var4.canBePlacedOn(var1, var2, var3, this) ? "wrongtile" : null;
      }
   }

   public boolean isValid(Level var1, int var2, int var3) {
      return true;
   }

   public void checkAround(Level var1, int var2, int var3) {
      for(int var4 = -1; var4 <= 1; ++var4) {
         for(int var5 = -1; var5 <= 1; ++var5) {
            LevelTile var6 = var1.getLevelTile(var2 + var4, var3 + var5);
            if (!var6.isValid()) {
               var1.entityManager.doDamageOverride(var2 + var4, var3 + var5, var6.tile.tileHealth, TileDamageType.Tile);
            }
         }
      }

   }

   public void attemptPlace(Level var1, int var2, int var3, PlayerMob var4, String var5) {
   }

   public void placeTile(Level var1, int var2, int var3) {
      var1.setTile(var2, var3, this.getID());
   }

   public void playPlaceSound(int var1, int var2) {
      if (this.isLiquid) {
         Screen.playSound(GameResources.watersplash, SoundEffect.effect((float)(var1 * 32 + 16), (float)(var2 * 32 + 16)));
      } else {
         Screen.playSound(GameResources.tap, SoundEffect.effect((float)(var1 * 32 + 16), (float)(var2 * 32 + 16)));
      }

   }

   public boolean canReplace(Level var1, int var2, int var3) {
      if (var1.getTileID(var2, var3) == this.getID()) {
         return false;
      } else {
         return !this.isLiquid && !var1.getTile(var2, var3).isLiquid;
      }
   }

   public ArrayList<InventoryItem> getDroppedItems(Level var1, int var2, int var3) {
      return this.getLootTable(var1, var2, var3).getNewList(GameRandom.globalRandom, (Float)var1.buffManager.getModifier(LevelModifiers.LOOT));
   }

   public void tick(Mob var1, Level var2, int var3, int var4) {
   }

   public void addSimulateLogic(Level var1, int var2, int var3, long var4, SimulatePriorityList var6, boolean var7) {
   }

   public void tick(Level var1, int var2, int var3) {
   }

   public void tickEffect(Level var1, int var2, int var3) {
   }

   public List<LevelJob> getLevelJobs(Level var1, int var2, int var3) {
      return Collections.emptyList();
   }

   public void tickValid(Level var1, int var2, int var3, boolean var4) {
   }

   public int getDestroyedTile() {
      return TileRegistry.dirtID;
   }

   public int getLightLevel() {
      return this.lightLevel;
   }

   public GameLight getLight(Level var1) {
      return var1.lightManager.newLight(this.lightHue, this.lightSat, (float)this.getLightLevel());
   }

   public float getItemSinkingRate(float var1) {
      return 0.0F;
   }

   public float getItemMaxSinking() {
      return 0.0F;
   }

   public boolean inLiquid(Level var1, int var2, int var3, int var4, int var5) {
      return this.isLiquid;
   }

   public int getMobSinkingAmount(Mob var1) {
      return var1.inLiquid() ? 10 : 0;
   }

   public ListGameTooltips getItemTooltips(InventoryItem var1, PlayerMob var2) {
      ListGameTooltips var3 = new ListGameTooltips();
      var3.add(Localization.translate("itemtooltip", "tiletip"));
      return var3;
   }

   public double spreadToDirtChance() {
      return 0.0;
   }

   public double getPathCost(Level var1, int var2, int var3, Mob var4) {
      return 0.0;
   }

   public ModifierValue<Float> getSpeedModifier(Mob var1) {
      return new ModifierValue(BuffModifiers.SPEED, 0.0F);
   }

   public ModifierValue<Float> getSlowModifier(Mob var1) {
      return new ModifierValue(BuffModifiers.SLOW, 0.0F);
   }

   public ModifierValue<Float> getFrictionModifier(Mob var1) {
      return new ModifierValue(BuffModifiers.FRICTION, 0.0F);
   }

   public void onDestroyed(Level var1, int var2, int var3, ServerClient var4, ArrayList<ItemPickupEntity> var5) {
      if (var5 != null) {
         TileLootTableDropsEvent var6;
         GameEvents.triggerEvent(var6 = new TileLootTableDropsEvent(new LevelTile(var1, var2, var3), new Point(var2 * 32 + 16, var3 * 32 + 16), this.getDroppedItems(var1, var2, var3)));
         if (var6.dropPos != null && var6.drops != null) {
            Iterator var7 = var6.drops.iterator();

            while(var7.hasNext()) {
               InventoryItem var8 = (InventoryItem)var7.next();
               ItemPickupEntity var9 = var8.getPickupEntity(var1, (float)var6.dropPos.x, (float)var6.dropPos.y);
               var1.entityManager.pickups.add(var9);
               var5.add(var9);
            }
         }
      }

      if (var4 != null) {
         var4.newStats.tiles_mined.increment(1);
      }

      if (!var1.isServer()) {
         this.spawnDestroyedParticles(var1, var2, var3);
      }

      var1.setTile(var2, var3, this.getDestroyedTile());
   }

   public boolean shouldReturnOnDeletedLevels(Level var1, int var2, int var3) {
      return this.shouldReturnOnDeletedLevels;
   }

   public boolean onDamaged(Level var1, int var2, int var3, int var4, ServerClient var5, boolean var6, int var7, int var8) {
      if (var6 && !var1.isServer()) {
         this.spawnDebrisParticles(var1, var2, var3, var4 > 0, var7, var8);
         this.playDamageSound(var1, var2, var3, var4 > 0);
      }

      return true;
   }

   public void spawnDestroyedParticles(Level var1, int var2, int var3) {
      Color var4 = this.getDebrisColor(var1, var2, var3);
      if (var4 != null) {
         for(int var5 = 0; var5 < 5; ++var5) {
            float var6 = (float)(var2 * 32) + GameRandom.globalRandom.getFloatOffset(16.0F, 5.0F);
            float var7 = (float)(var3 * 32) + GameRandom.globalRandom.getFloatOffset(24.0F, 5.0F);
            float var8 = GameRandom.globalRandom.getFloatBetween(5.0F, 10.0F);
            float var9 = GameRandom.globalRandom.getFloatBetween(0.0F, 80.0F);
            final float var10 = GameRandom.globalRandom.getFloatBetween(-5.0F, 0.0F);
            float var11 = GameRandom.globalRandom.getFloatBetween(10.0F, 20.0F);
            boolean var12 = GameRandom.globalRandom.nextBoolean();
            boolean var13 = GameRandom.globalRandom.nextBoolean();
            float var14 = GameRandom.globalRandom.getFloatBetween(0.5F, 2.0F);
            float var15 = GameRandom.globalRandom.floatGaussian() * 30.0F;
            float var16 = GameRandom.globalRandom.floatGaussian() * 20.0F;
            int var17 = GameRandom.globalRandom.getIntBetween(1500, 2500);
            int var18 = GameRandom.globalRandom.getIntBetween(500, 1500);
            int var19 = var17 + var18;
            final ParticleOption.HeightMover var20 = new ParticleOption.HeightMover(var8, var9, var11, 2.0F, var10, 0.0F);
            AtomicReference var21 = new AtomicReference(0.0F);
            var1.entityManager.addParticle(var6, var7, Particle.GType.COSMETIC).sprite(GameResources.debrisParticles.sprite(GameRandom.globalRandom.nextInt(6), 0, 20)).color(this.getDebrisColor(var1, var2, var3)).fadesAlphaTime(0, var18).sizeFadesInAndOut(10, 15, 0, 0).height((ParticleOption.HeightGetter)var20).onMoveTick((var3x, var4x, var5x, var6x) -> {
               if (var20.currentHeight > var10) {
                  var21.set((Float)var21.get() + var3x);
               }

            }).modify((var2x, var3x, var4x, var5x) -> {
               float var6 = (Float)var21.get() * var14;
               var2x.rotate(var6);
            }).moves(new ParticleOption.FrictionMover(var15, var16, 2.0F) {
               public void tick(Point2D.Float var1, float var2, int var3, int var4, float var5) {
                  if (var20.currentHeight > var10) {
                     super.tick(var1, var2, var3, var4, var5);
                  }

               }
            }).modify((var2x, var3x, var4x, var5x) -> {
               var2x.mirror(var12, var13);
            }).lifeTime(var19);
         }

      }
   }

   public void spawnDebrisParticles(Level var1, int var2, int var3, boolean var4, int var5, int var6) {
      if (var4) {
         Color var7 = this.getDebrisColor(var1, var2, var3);
         if (var7 != null) {
            for(int var8 = 0; var8 < 3; ++var8) {
               float var9 = GameRandom.globalRandom.getFloatBetween(5.0F, 10.0F);
               float var10 = GameRandom.globalRandom.getFloatBetween(0.0F, 80.0F);
               final float var11 = GameRandom.globalRandom.getFloatBetween(-5.0F, 0.0F);
               float var12 = GameRandom.globalRandom.getFloatBetween(10.0F, 20.0F);
               boolean var13 = GameRandom.globalRandom.nextBoolean();
               boolean var14 = GameRandom.globalRandom.nextBoolean();
               float var15 = GameRandom.globalRandom.getFloatBetween(0.5F, 2.0F);
               float var16 = GameRandom.globalRandom.floatGaussian() * 30.0F;
               float var17 = GameRandom.globalRandom.floatGaussian() * 20.0F;
               int var18 = GameRandom.globalRandom.getIntBetween(500, 1500);
               int var19 = GameRandom.globalRandom.getIntBetween(500, 1500);
               int var20 = var18 + var19;
               final ParticleOption.HeightMover var21 = new ParticleOption.HeightMover(var9, var10, var12, 2.0F, var11, 0.0F);
               AtomicReference var22 = new AtomicReference(0.0F);
               var1.entityManager.addParticle((float)var5, (float)var6 + var9, Particle.GType.COSMETIC).sprite(GameResources.debrisParticles.sprite(GameRandom.globalRandom.nextInt(6), 0, 20)).color(this.getDebrisColor(var1, var2, var3)).fadesAlphaTime(0, var19).sizeFadesInAndOut(10, 15, 0, 0).height((ParticleOption.HeightGetter)var21).onMoveTick((var3x, var4x, var5x, var6x) -> {
                  if (var21.currentHeight > var11) {
                     var22.set((Float)var22.get() + var3x);
                  }

               }).modify((var2x, var3x, var4x, var5x) -> {
                  float var6 = (Float)var22.get() * var15;
                  var2x.rotate(var6);
               }).moves(new ParticleOption.FrictionMover(var16, var17, 2.0F) {
                  public void tick(Point2D.Float var1, float var2, int var3, int var4, float var5) {
                     if (var21.currentHeight > var11) {
                        super.tick(var1, var2, var3, var4, var5);
                     }

                  }
               }).modify((var2x, var3x, var4x, var5x) -> {
                  var2x.mirror(var13, var14);
               }).lifeTime(var20);
            }

         }
      }
   }

   public void doExplosionDamage(Level var1, int var2, int var3, int var4, int var5, ServerClient var6) {
      var1.entityManager.doDamage(var2, var3, var4, TileDamageType.Tile, var5, var6);
   }

   public void playDamageSound(Level var1, int var2, int var3, boolean var4) {
      Screen.playSound(GameResources.tap, SoundEffect.effect((float)(var2 * 32 + 16), (float)(var3 * 32 + 16)).pitch(var4 ? 1.0F : 2.0F));
   }

   public GameTooltips getMapTooltips(Level var1, int var2, int var3) {
      return null;
   }

   public int getLiquidBobbing(Level var1, int var2, int var3) {
      return 0;
   }

   public MobSpawnTable getMobSpawnTable(TilePosition var1, MobSpawnTable var2) {
      return var2;
   }

   public int getMobSpawnPositionTickets(Level var1, int var2, int var3) {
      return 100;
   }
}
