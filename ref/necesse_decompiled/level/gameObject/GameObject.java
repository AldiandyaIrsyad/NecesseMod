package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.GameEvents;
import necesse.engine.Screen;
import necesse.engine.events.loot.ObjectLootTableDropsEvent;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.packet.PacketHitObject;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.IDData;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.RegistryClosedException;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.TileDamageResult;
import necesse.entity.TileDamageType;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.objectItem.ObjectItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.layers.SimulatePriorityList;
import necesse.level.maps.levelBuffManager.LevelModifiers;
import necesse.level.maps.levelData.jobs.LevelJob;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.regionSystem.SemiRegion;

public class GameObject {
   public final IDData idData;
   private GameRandom tileRandom;
   protected Rectangle collision;
   protected Rectangle hoverHitbox;
   protected int hoverHitboxSortY;
   public Color mapColor;
   public Color debrisColor;
   public boolean displayMapTooltip;
   public int lightLevel;
   public float lightHue;
   public float lightSat;
   public final boolean isDoor;
   public boolean isWall;
   public boolean isRock;
   public boolean isFence;
   public boolean isSwitch;
   public boolean isSwitched;
   public boolean isGrass;
   public boolean isSeed;
   public boolean isFlowerpot;
   public boolean isTree;
   public boolean isOre;
   public boolean isSolid;
   public boolean canPlaceOnLiquid;
   public boolean canPlaceOnShore;
   public boolean isLightTransparent;
   public boolean showsWire;
   public boolean attackThrough;
   public boolean overridesInLiquid;
   public boolean isIncursionExtractionObject;
   private GameMessage displayName;
   public int stackSize;
   public String[] itemCategoryTree;
   public HashSet<String> itemGlobalIngredients;
   public Item.Rarity rarity;
   public ToolType toolType;
   public int objectHealth;
   public int toolTier;
   public boolean drawDamage;
   public boolean canPlaceOnProtectedLevels;
   public boolean shouldReturnOnDeletedLevels;
   public HashSet<String> roomProperties;
   public HashSet<String> replaceCategories;
   public HashSet<String> canReplaceCategories;
   public boolean replaceRotations;
   protected SemiRegion.RegionType regionType;

   public final String getStringID() {
      return this.idData.getStringID();
   }

   public final int getID() {
      return this.idData.getID();
   }

   public GameObject() {
      this(new Rectangle());
   }

   public GameObject(Rectangle var1) {
      this.idData = new IDData();
      this.tileRandom = new GameRandom();
      this.hoverHitbox = new Rectangle(32, 32);
      this.hoverHitboxSortY = 16;
      this.displayMapTooltip = false;
      this.displayName = new StaticMessage("Unknown");
      this.stackSize = 100;
      this.itemCategoryTree = new String[]{"objects"};
      this.itemGlobalIngredients = new HashSet();
      this.rarity = Item.Rarity.NORMAL;
      this.toolType = ToolType.PICKAXE;
      this.objectHealth = 100;
      this.toolTier = 0;
      this.drawDamage = true;
      this.canPlaceOnProtectedLevels = false;
      this.shouldReturnOnDeletedLevels = false;
      this.roomProperties = new HashSet();
      this.replaceCategories = new HashSet();
      this.canReplaceCategories = new HashSet();
      this.replaceRotations = true;
      if (ObjectRegistry.instance.isClosed()) {
         throw new RegistryClosedException("Cannot construct GameObject objects when object registry is closed, since they are a static registered objects. Use ObjectRegistry.getObject(...) to get objects.");
      } else {
         this.collision = var1;
         this.isDoor = this instanceof DoorObject;
         if (var1.width != 0 && var1.height != 0) {
            this.isSolid = true;
         }

         this.mapColor = new Color(127, 127, 127);
         this.regionType = this.isSolid ? SemiRegion.RegionType.SOLID : SemiRegion.RegionType.OPEN;
      }
   }

   public void onObjectRegistryClosed() {
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
   }

   public GameMessage getNewLocalization() {
      return new LocalMessage("object", this.getMultiTile(0).getMasterObject().getStringID());
   }

   public void updateLocalDisplayName() {
      this.displayName = this.getNewLocalization();
   }

   public final GameMessage getLocalization() {
      return this.displayName;
   }

   public final String getDisplayName() {
      return this.displayName.translate();
   }

   public GameObject setItemCategory(String... var1) {
      this.itemCategoryTree = var1;
      return this;
   }

   public GameObject addGlobalIngredient(String... var1) {
      this.itemGlobalIngredients.addAll(Arrays.asList(var1));
      return this;
   }

   protected Rectangle getCollision(Level var1, int var2, int var3, int var4) {
      return new Rectangle(var2 * 32 + this.collision.x, var3 * 32 + this.collision.y, this.collision.width, this.collision.height);
   }

   public List<Rectangle> getCollisions(Level var1, int var2, int var3, int var4) {
      LinkedList var5 = new LinkedList();
      var5.add(this.getCollision(var1, var2, var3, var4));
      return var5;
   }

   protected ObjectHoverHitbox getHoverHitbox(Level var1, int var2, int var3) {
      return new ObjectHoverHitbox(var2, var3, this.hoverHitbox.x, this.hoverHitbox.y, this.hoverHitbox.width, this.hoverHitbox.height, this.hoverHitboxSortY);
   }

   public List<ObjectHoverHitbox> getHoverHitboxes(Level var1, int var2, int var3) {
      LinkedList var4 = new LinkedList();
      var4.add(this.getHoverHitbox(var1, var2, var3));
      return var4;
   }

   public List<Rectangle> getProjectileCollisions(Level var1, int var2, int var3, int var4) {
      return this.getCollisions(var1, var2, var3, var4);
   }

   public List<Rectangle> getAttackThroughCollisions(Level var1, int var2, int var3) {
      LinkedList var4 = new LinkedList();
      var4.add(new Rectangle(var2 * 32, var3 * 32, 32, 32));
      return var4;
   }

   public int getLightLevel(Level var1, int var2, int var3) {
      return this.lightLevel;
   }

   public GameLight getLight(Level var1, int var2, int var3) {
      return var1.lightManager.newLight(this.lightHue, this.lightSat, (float)this.getLightLevel(var1, var2, var3));
   }

   public LootTable getLootTable(Level var1, int var2, int var3) {
      return ItemRegistry.itemExists(this.getStringID()) && ItemRegistry.isObtainable(ItemRegistry.getItemID(this.getStringID())) ? new LootTable(new LootItemInterface[]{(new LootItem(this.getStringID())).preventLootMultiplier()}) : new LootTable();
   }

   public String canPlace(Level var1, int var2, int var3, int var4) {
      if (var1.getObjectID(var2, var3) == this.getID()) {
         return "sameobject";
      } else if (var1.getObjectID(var2, var3) != 0 && !var1.getObject(var2, var3).isGrass) {
         return "occupied";
      } else if (!this.canPlaceOnLiquid && var1.isLiquidTile(var2, var3)) {
         return "liquid";
      } else if (!this.canPlaceOnShore && var1.isShore(var2, var3)) {
         return "shore";
      } else {
         MultiTile var5 = this.getMultiTile(var4);
         return var5.isMaster ? (String)var5.streamOtherObjects(var2, var3).map((var2x) -> {
            return ((GameObject)var2x.value).canPlace(var1, var2x.tileX, var2x.tileY, var4);
         }).reduce((Object)null, (var0, var1x) -> {
            return var0 != null ? var0 : var1x;
         }) : null;
      }
   }

   public void attemptPlace(Level var1, int var2, int var3, PlayerMob var4, String var5) {
   }

   public boolean checkPlaceCollision(Level var1, int var2, int var3, int var4, boolean var5) {
      Iterator var6 = this.getCollisions(var1, var2, var3, var4).iterator();

      Rectangle var7;
      do {
         if (!var6.hasNext()) {
            MultiTile var8 = this.getMultiTile(var4);
            if (var8.isMaster) {
               return var8.streamOtherObjects(var2, var3).anyMatch((var3x) -> {
                  return ((GameObject)var3x.value).checkPlaceCollision(var1, var3x.tileX, var3x.tileY, var4, var5);
               });
            }

            return false;
         }

         var7 = (Rectangle)var6.next();
      } while(!var1.entityCollides(var7, var5));

      return true;
   }

   public boolean isValid(Level var1, int var2, int var3) {
      if (this.toolType == ToolType.UNBREAKABLE) {
         return true;
      } else {
         return (this.canPlaceOnLiquid || !var1.isLiquidTile(var2, var3)) && (this.canPlaceOnShore || !var1.isShore(var2, var3)) ? this.getMultiTile(var1.getObjectRotation(var2, var3)).streamOtherIDs(var2, var3).allMatch((var1x) -> {
            return var1.getObjectID(var1x.tileX, var1x.tileY) == (Integer)var1x.value;
         }) : false;
      }
   }

   public void checkAround(Level var1, int var2, int var3) {
      for(int var4 = -1; var4 <= 1; ++var4) {
         for(int var5 = -1; var5 <= 1; ++var5) {
            LevelObject var6 = var1.getLevelObject(var2 + var4, var3 + var5);
            if (var6.object.getID() != 0 && !var6.isValid()) {
               var1.entityManager.doDamageOverride(var2 + var4, var3 + var5, var6.object.objectHealth, TileDamageType.Object);
            }
         }
      }

   }

   public boolean shouldSnapSmartMining(Level var1, int var2, int var3) {
      return this.isSolid(var1, var2, var3);
   }

   public boolean shouldSnapControllerMining(Level var1, int var2, int var3) {
      return true;
   }

   public boolean isSolid(Level var1, int var2, int var3) {
      return this.isSolid;
   }

   public boolean isLightTransparent(Level var1, int var2, int var3) {
      return this.isLightTransparent;
   }

   public int getLightLevelMod(Level var1, int var2, int var3) {
      return this.isSolid(var1, var2, var3) && !this.isLightTransparent(var1, var2, var3) ? 40 : 10;
   }

   public GameObject setDebrisColor(Color var1) {
      this.debrisColor = var1;
      return this;
   }

   public Color getDebrisColor(Level var1, int var2, int var3) {
      if (this.debrisColor != null) {
         return this.debrisColor;
      } else {
         Color var4 = this.getMapColor(var1, var2, var3);
         return var4 != null ? var4 : new Color(127, 127, 127);
      }
   }

   public Color getMapColor(Level var1, int var2, int var3) {
      return this.mapColor;
   }

   public boolean canInteract(Level var1, int var2, int var3, PlayerMob var4) {
      return false;
   }

   public String getInteractTip(Level var1, int var2, int var3, PlayerMob var4, boolean var5) {
      return null;
   }

   public void onMouseHover(Level var1, int var2, int var3, GameCamera var4, PlayerMob var5, boolean var6) {
      ObjectEntity var7 = var1.entityManager.getObjectEntity(var2, var3);
      if (var7 != null) {
         var7.onMouseHover(var5, var6);
      }

   }

   public int getInteractDistance() {
      return 100;
   }

   public boolean inInteractRange(Level var1, int var2, int var3, PlayerMob var4) {
      Point var5 = this.getMultiTile(var1.getObjectRotation(var2, var3)).getCenterLevelPos(var2, var3);
      return var4.getDistance((float)var5.x, (float)var5.y) < (float)this.getInteractDistance();
   }

   public void interact(Level var1, int var2, int var3, PlayerMob var4) {
   }

   public ObjectEntity getNewObjectEntity(Level var1, int var2, int var3) {
      return null;
   }

   public ObjectEntity getCurrentObjectEntity(Level var1, int var2, int var3) {
      return var1.entityManager.getObjectEntity(var2, var3);
   }

   public <T extends ObjectEntity> T getCurrentObjectEntity(Level var1, int var2, int var3, Class<T> var4) {
      return var1.entityManager.getObjectEntity(var2, var3, var4);
   }

   public final ObjectItem getObjectItem() {
      return (ObjectItem)ItemRegistry.getItem(this.getStringID());
   }

   public Item generateNewObjectItem() {
      return new ObjectItem(this);
   }

   public void placeObject(Level var1, int var2, int var3, int var4) {
      var1.setObject(var2, var3, this.getID());
      var1.setObjectRotation(var2, var3, (byte)var4);
      if (var1.isServer()) {
         ObjectEntity var5 = this.getNewObjectEntity(var1, var2, var3);
         if (var5 != null) {
            var1.entityManager.objectEntities.add(var5);
         }
      }

      MultiTile var6 = this.getMultiTile(var4);
      if (var6.isMaster) {
         var6.streamOtherObjects(var2, var3).forEach((var2x) -> {
            ((GameObject)var2x.value).placeObject(var1, var2x.tileX, var2x.tileY, var4);
         });
      }

   }

   public void playPlaceSound(int var1, int var2) {
      Screen.playSound(GameResources.tap, SoundEffect.effect((float)(var1 * 32 + 16), (float)(var2 * 32 + 16)));
   }

   public boolean canReplace(Level var1, int var2, int var3, int var4) {
      GameObject var5 = var1.getObject(var2, var3);
      if (var5.getID() == this.getID()) {
         if (!this.replaceRotations) {
            return false;
         }

         if (var1.getObjectRotation(var2, var3) == var4) {
            return false;
         }
      }

      if (var5.getID() == 0) {
         return true;
      } else {
         Iterator var6 = this.canReplaceCategories.iterator();

         String var7;
         do {
            if (!var6.hasNext()) {
               return false;
            }

            var7 = (String)var6.next();
         } while(!var5.replaceCategories.contains(var7));

         return true;
      }
   }

   public MultiTile getMultiTile(int var1) {
      return new MultiTile(0, 0, 1, 1, var1, true, new int[]{this.getID()});
   }

   public final MultiTile getMultiTile(Level var1, int var2, int var3) {
      return this.getMultiTile(var1.getObjectRotation(var2, var3));
   }

   public final boolean isMultiTileMaster() {
      return this.getMultiTile(0).isMaster;
   }

   public void loadTextures() {
   }

   public GameTexture generateItemTexture() {
      return GameTexture.fromFile("items/" + this.getStringID());
   }

   public void tick(Mob var1, Level var2, int var3, int var4) {
   }

   public void addSimulateLogic(Level var1, int var2, int var3, long var4, SimulatePriorityList var6, boolean var7) {
   }

   public void tick(Level var1, int var2, int var3) {
   }

   public void tickEffect(Level var1, int var2, int var3) {
   }

   public boolean overridesInLiquid(Level var1, int var2, int var3, int var4, int var5) {
      return this.overridesInLiquid;
   }

   public boolean isWireActive(Level var1, int var2, int var3, int var4) {
      return false;
   }

   public List<LevelJob> getLevelJobs(Level var1, int var2, int var3) {
      return Collections.emptyList();
   }

   public ListGameTooltips getItemTooltips(InventoryItem var1, PlayerMob var2) {
      ListGameTooltips var3 = new ListGameTooltips();
      var3.add(Localization.translate("itemtooltip", "placetip"));
      return var3;
   }

   public void onWireUpdate(Level var1, int var2, int var3, int var4, boolean var5) {
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
      ObjectLootTableDropsEvent var6 = null;
      if (var5 != null) {
         ArrayList var7 = this.getDroppedItems(var1, var2, var3);
         GameEvents.triggerEvent(var6 = new ObjectLootTableDropsEvent(new LevelObject(var1, var2, var3), new Point(var2 * 32 + 16, var3 * 32 + 16), var7));
         if (var6.dropPos != null && var6.drops != null) {
            Iterator var8 = var6.drops.iterator();

            while(var8.hasNext()) {
               InventoryItem var9 = (InventoryItem)var8.next();
               ItemPickupEntity var10 = var9.getPickupEntity(var1, (float)var6.dropPos.x, (float)var6.dropPos.y);
               var1.entityManager.pickups.add(var10);
               var5.add(var10);
            }
         }
      }

      if (var4 != null) {
         var4.newStats.objects_mined.increment(1);
      }

      if (!var1.isServer()) {
         this.spawnDestroyedParticles(var1, var2, var3);
      }

      ObjectEntity var11 = var1.entityManager.getObjectEntity(var2, var3);
      var1.setObject(var2, var3, 0);
      if (var11 != null) {
         var11.onObjectDestroyed(this, var4, var5);
         var11.remove();
      }

   }

   public boolean shouldReturnOnDeletedLevels(Level var1, int var2, int var3) {
      return this.shouldReturnOnDeletedLevels;
   }

   public ArrayList<InventoryItem> getDroppedItems(Level var1, int var2, int var3) {
      ArrayList var4 = this.getLootTable(var1, var2, var3).getNewList(GameRandom.globalRandom, (Float)var1.buffManager.getModifier(LevelModifiers.LOOT));
      ObjectEntity var5 = var1.entityManager.getObjectEntity(var2, var3);
      if (var5 != null) {
         var4.addAll(var5.getDroppedItems());
      }

      return var4;
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

   public void playDamageSound(Level var1, int var2, int var3, boolean var4) {
      Screen.playSound(GameResources.tap, SoundEffect.effect((float)(var2 * 32 + 16), (float)(var3 * 32 + 16)).pitch(var4 ? 1.0F : 2.0F));
   }

   public boolean pathCollidesIfOpen(Level var1, int var2, int var3, CollisionFilter var4, Rectangle var5) {
      return false;
   }

   public boolean pathCollidesIfBreakDown(Level var1, int var2, int var3, CollisionFilter var4, Rectangle var5) {
      return false;
   }

   public void onPathOpened(Level var1, int var2, int var3) {
      var1.entityManager.doDamage(var2, var3, this.objectHealth, (TileDamageType)TileDamageType.Object, this.toolTier, (ServerClient)null, true, var2 * 32 + 16, var3 * 32 + 16);
   }

   public void onPathBreakDown(Level var1, int var2, int var3, int var4, int var5, int var6) {
      var1.entityManager.doDamage(var2, var3, this.objectHealth, (TileDamageType)TileDamageType.Object, this.toolTier, (ServerClient)null, true, var5, var6);
   }

   public double getPathCost(Level var1, int var2, int var3) {
      return 0.0;
   }

   public double getBreakDownPathCost(Level var1, int var2, int var3) {
      return (double)((float)this.objectHealth * 2.5F);
   }

   public void doExplosionDamage(Level var1, int var2, int var3, int var4, int var5, ServerClient var6) {
      var1.entityManager.doDamage(var2, var3, var4, TileDamageType.Object, var5, var6);
   }

   public void attackThrough(Level var1, int var2, int var3, GameDamage var4, Attacker var5) {
      ServerClient var6 = null;
      Mob var7 = var5 == null ? null : var5.getAttackOwner();
      if (var7 != null && var7.isPlayer) {
         PlayerMob var8 = (PlayerMob)var7;
         if (var8.isServerClient()) {
            var6 = var8.getServerClient();
         }
      }

      TileDamageResult var9 = var1.entityManager.doDamage(var2, var3, this.objectHealth, TileDamageType.Object, this.toolTier, var6);
      if (var9 != null && var9.addedDamage > 0) {
         var1.getServer().network.sendToClientsWithTile(new PacketHitObject(var1, var2, var3, this, var4), var1, var2, var3);
      }

   }

   public void attackThrough(Level var1, int var2, int var3, GameDamage var4) {
   }

   protected long getTileSeed(int var1, int var2, int var3) {
      return (long)((var1 * 1289969 + var2 * 888161) * GameRandom.prime(Math.abs(var3)));
   }

   protected long getTileSeed(int var1, int var2) {
      return this.getTileSeed(var1, var2, 0);
   }

   public int getPlaceRotation(Level var1, int var2, int var3, PlayerMob var4, int var5) {
      return var5;
   }

   public Point getPlaceOffset(Level var1, int var2, int var3, PlayerMob var4, int var5) {
      MultiTile var6 = this.getMultiTile(var5);
      return new Point(-var6.getCenterXOffset() * 32 / 2, -var6.getCenterYOffset() * 32 / 2);
   }

   public GameTooltips getMapTooltips(Level var1, int var2, int var3) {
      MultiTile var4 = this.getMultiTile(var1.getObjectRotation(var2, var3));
      if (var4.isMaster) {
         ObjectEntity var5 = var1.entityManager.getObjectEntity(var2, var3);
         if (this.displayMapTooltip) {
            if (var5 != null) {
               GameTooltips var6 = var5.getMapTooltips();
               if (var6 != null) {
                  return var6;
               }
            }

            return new StringTooltips(this.getDisplayName());
         } else {
            return null;
         }
      } else {
         return (GameTooltips)var4.getMasterLevelObject(var1, var2, var3).map(LevelObject::getMapTooltips).orElseGet(StringTooltips::new);
      }
   }

   public SemiRegion.RegionType getRegionType() {
      return this.regionType;
   }

   public boolean stopsTerrainSplatting() {
      return false;
   }

   public boolean drawsFullTile() {
      return false;
   }
}
