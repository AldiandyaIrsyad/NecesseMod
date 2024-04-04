package necesse.inventory.item;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import necesse.engine.GlobalData;
import necesse.engine.Screen;
import necesse.engine.control.ControllerInput;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.server.ItemDropperHandler;
import necesse.engine.registries.GlobalIngredientRegistry;
import necesse.engine.registries.IDData;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.RegistryClosedException;
import necesse.engine.util.ComparableSequence;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.IntersectionPoint;
import necesse.engine.world.GameClock;
import necesse.engine.world.WorldSettings;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.ItemCooldown;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.entity.pickup.PickupEntity;
import necesse.gfx.GameColor;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.forms.presets.sidebar.SidebarForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryAddConsumer;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryItemsRemoved;
import necesse.inventory.PlaceableItemInterface;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerActionResult;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.item.armorItem.ChestArmorItem;
import necesse.inventory.item.arrowItem.ArrowItem;
import necesse.inventory.item.baitItem.BaitItem;
import necesse.inventory.item.bulletItem.BulletItem;
import necesse.inventory.item.matItem.MatItem;
import necesse.inventory.item.mountItem.MountItem;
import necesse.inventory.item.placeableItem.consumableItem.food.FoodConsumableItem;
import necesse.inventory.item.questItem.QuestItem;
import necesse.inventory.item.toolItem.ToolItem;
import necesse.inventory.item.trinketItem.TrinketItem;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;
import necesse.inventory.recipe.GlobalIngredient;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.IngredientCounter;
import necesse.inventory.recipe.Recipes;
import necesse.inventory.recipe.Tech;
import necesse.level.maps.CollisionPoint;
import necesse.level.maps.Level;
import necesse.level.maps.TilePosition;
import necesse.level.maps.light.GameLight;

public class Item {
   public static float GLOBAL_SPOIL_TIME_MODIFIER = 1.0F;
   public static final FontOptions tipFontOptions = (new FontOptions(12)).color(220, 220, 220).outline();
   public final IDData idData = new IDData();
   private final boolean isPlaceable;
   private final boolean isTickItem;
   protected boolean showAttackAllDirections;
   public final Type type;
   protected boolean isPotion;
   protected boolean dropsAsMatDeathPenalty = false;
   protected long dropDecayTimeMillis;
   protected int spoilDurationSeconds;
   protected int incinerationTimeMillis = 3000;
   protected Rarity rarity;
   protected int stackSize;
   private ArrayList<Integer> globalIngredients;
   private HashMap<ItemCategoryManager, String[]> itemCategoryTree;
   protected GameTexture itemTexture;
   protected GameTexture holdTexture;
   protected GameTexture attackTexture;
   protected int worldDrawSize;
   protected int attackXOffset;
   protected int attackYOffset;
   protected float hungerUsage;
   /** @deprecated */
   @Deprecated
   protected int animSpeed;
   /** @deprecated */
   @Deprecated
   protected int itemCooldown;
   /** @deprecated */
   @Deprecated
   protected int cooldown;
   protected IntUpgradeValue attackAnimTime;
   protected IntUpgradeValue attackCooldownTime;
   protected IntUpgradeValue itemCooldownTime;
   protected int animAttacks;
   protected boolean changeDir;
   protected ArrayList<String> keyWords;

   private static Type findType(Item var0) {
      Type[] var1 = Item.Type.values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Type var4 = var1[var3];
         if (var4.subClass.isInstance(var0)) {
            return var4;
         }
      }

      return Item.Type.MISC;
   }

   public final int getID() {
      return this.idData.getID();
   }

   public String getStringID() {
      return this.idData.getStringID();
   }

   public Item(int var1) {
      this.rarity = Item.Rarity.NORMAL;
      this.globalIngredients = new ArrayList();
      this.itemCategoryTree = new HashMap();
      this.itemCategoryTree.put(ItemCategory.masterManager, new String[]{"misc"});
      this.worldDrawSize = 24;
      this.attackXOffset = 8;
      this.attackYOffset = 16;
      this.hungerUsage = 0.0F;
      this.animSpeed = -1;
      this.itemCooldown = -1;
      this.cooldown = -1;
      this.attackAnimTime = new IntUpgradeValue(true, 200, 0.0F);
      this.attackCooldownTime = new IntUpgradeValue(true, 0, 0.0F);
      this.itemCooldownTime = new IntUpgradeValue(true, 0, 0.0F);
      this.animAttacks = 1;
      this.changeDir = false;
      this.keyWords = new ArrayList();
      if (ItemRegistry.instance.isClosed()) {
         throw new RegistryClosedException("Cannot construct Item objects when item registry is closed, since they are a static registered objects. Use ItemRegistry.getItem(...) to get items.");
      } else {
         this.isPlaceable = this instanceof PlaceableItemInterface;
         this.isTickItem = this instanceof TickItem;
         this.type = findType(this);
         this.stackSize = var1;
         this.init();
      }
   }

   protected ListGameTooltips getBaseTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = new ListGameTooltips();
      var4.addAll(this.getDisplayNameTooltips(var1, var2, var3));
      var4.addAll(this.getDebugTooltips(var1, var2, var3));
      var4.addAll(this.getCraftingMatTooltips(var1, var2, var3));
      return var4;
   }

   protected ListGameTooltips getDisplayNameTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = new ListGameTooltips();
      var4.add((Object)(new StringTooltips(this.getDisplayName(var1), this.getRarityColor(var1))));
      return var4;
   }

   protected ListGameTooltips getDebugTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = new ListGameTooltips();
      if (GlobalData.debugActive()) {
         var4.add(this.getStringID() + " (" + this.getID() + ")");
         var4.add("Value: " + this.getBrokerValue(var1));
         var4.add("Category: " + GameUtils.join(ItemCategory.masterManager.getItemsCategory(this).getStringIDTree(false), "."));
         LoadedMod var5 = ItemRegistry.getItemMod(this.getID());
         if (var5 != null) {
            var4.add("Mod: " + var5.name);
         }
      }

      return var4;
   }

   protected ListGameTooltips getCraftingMatTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = new ListGameTooltips();
      HashSet var5 = Recipes.getCraftingMatTechs(this.getID());
      LinkedList var6 = new LinkedList();
      Iterator var7 = var5.iterator();

      while(var7.hasNext()) {
         Tech var8 = (Tech)var7.next();
         if (var8.craftingMatTip != null && var6.stream().noneMatch((var1x) -> {
            return var1x.isSame(var8.craftingMatTip);
         })) {
            var6.add(var8.craftingMatTip);
         }
      }

      var7 = this.getGlobalIngredients().iterator();

      while(var7.hasNext()) {
         int var10 = (Integer)var7.next();
         GlobalIngredient var9 = GlobalIngredientRegistry.getGlobalIngredient(var10);
         if (var9.craftingMatTip != null && var6.stream().noneMatch((var1x) -> {
            return var1x.isSame(var9.craftingMatTip);
         })) {
            var6.add(var9.craftingMatTip);
         }
      }

      var7 = var6.iterator();

      while(var7.hasNext()) {
         GameMessage var11 = (GameMessage)var7.next();
         var4.add(var11.translate());
      }

      return var4;
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      return this.getBaseTooltips(var1, var2, var3);
   }

   /** @deprecated */
   @Deprecated
   public void init() {
   }

   public void onItemRegistryClosed() {
      if (this.animSpeed != -1 && this.attackAnimTime.isEmpty()) {
         this.attackAnimTime.setBaseValue(this.animSpeed);
      }

      if (this.cooldown != -1 && this.attackCooldownTime.isEmpty()) {
         this.attackCooldownTime.setBaseValue(this.cooldown);
      }

      if (this.itemCooldown != -1 && this.itemCooldownTime.isEmpty()) {
         this.itemCooldownTime.setBaseValue(this.itemCooldown);
      }

   }

   public GameMessage getNewLocalization() {
      return new LocalMessage("item", this.getStringID());
   }

   public GameMessage getLocalization(InventoryItem var1) {
      GNDItemMap var2 = var1.getGndData();
      if (var2.hasKey("name")) {
         GNDItem var3 = var2.getItem("name");
         if (!GNDItem.isDefault(var3)) {
            String var4 = var3.toString();
            if (!var4.isEmpty()) {
               return new StaticMessage(var4);
            }
         }
      }

      return ItemRegistry.getLocalization(this.getID());
   }

   public final String getDisplayName(InventoryItem var1) {
      return this.getLocalization(var1).translate();
   }

   public void draw(InventoryItem var1, PlayerMob var2, int var3, int var4, boolean var5) {
      this.drawIcon(var1, var2, var3, var4, 32);
      if (var5 && var2 != null) {
         float var6 = this.getItemCooldownPercent(var1, var2);
         if (var6 > 0.0F) {
            byte var7 = 34;
            int var8 = GameMath.limit((int)(var6 * (float)var7), 1, var7);
            Screen.initQuadDraw(var7, var8).color(0.0F, 0.0F, 0.0F, 0.5F).draw(var3 - 1, var4 + Math.abs(var8 - var7) - 1);
         }
      }

   }

   public float getItemCooldownPercent(InventoryItem var1, PlayerMob var2) {
      ItemCooldown var3 = var2.getItemCooldown(this);
      return var3 != null ? var3.getPercentRemaining(var2.getWorldEntity().getTime()) : 0.0F;
   }

   public void drawIcon(InventoryItem var1, PlayerMob var2, int var3, int var4, int var5) {
      Color var6 = this.getDrawColor(var1, var2);
      this.getItemSprite(var1, var2).initDraw().color(var6).size(var5).draw(var3, var4);
   }

   public int getWorldDrawSize(InventoryItem var1, PlayerMob var2) {
      return this.worldDrawSize;
   }

   public DrawOptions getWorldDrawOptions(InventoryItem var1, PlayerMob var2, int var3, int var4, GameLight var5, float var6) {
      return this.getWorldDrawOptions(var1, var2, var3, var4, var5, var6, this.getWorldDrawSize(var1, var2));
   }

   public DrawOptions getWorldDrawOptions(InventoryItem var1, PlayerMob var2, int var3, int var4, GameLight var5, float var6, int var7) {
      return this.getWorldDrawOptions(var1, var2, this.getWorldItemSprite(var1, var2), var3, var4, var5, var6, var7);
   }

   protected final DrawOptions getWorldDrawOptions(InventoryItem var1, PlayerMob var2, GameSprite var3, int var4, int var5, GameLight var6, float var7, int var8) {
      Color var9 = this.getDrawColor(var1, var2);
      TextureDrawOptionsEnd var10 = var3.initDrawSection(0, var3.spriteWidth, 0, var3.spriteHeight - (int)(var7 * (float)var3.spriteHeight)).colorLight(var9, var6).size(var8);
      int var11 = (int)(var7 * (float)var10.getHeight());
      int var12 = var10.getWidth();
      int var13 = var10.getHeight();
      var10 = var10.size(var10.getWidth(), var10.getHeight() - var11);
      return var10.pos(var4 - var12 / 2, var5 - var13 + var11, true);
   }

   public DrawOptions getWorldShadowDrawOptions(InventoryItem var1, PlayerMob var2, int var3, int var4, GameLight var5, float var6) {
      return this.getWorldShadowDrawOptions(var1, var2, var3, var4, var5, var6, this.getWorldDrawSize(var1, var2));
   }

   public DrawOptions getWorldShadowDrawOptions(InventoryItem var1, PlayerMob var2, int var3, int var4, GameLight var5, float var6, int var7) {
      GameSprite var8 = this.getWorldShadowSprite(var1, var2);
      float var9 = (float)Math.max(var8.width, var8.height) / 24.0F;
      float var10 = 1.0F;
      if (var6 >= 0.75F) {
         var10 = Math.abs((var6 - 0.75F) * 4.0F - 1.0F);
      }

      return var8.initDraw().size((int)((float)var7 * var9 * var10)).light(var5).posMiddle(var3, var4, true);
   }

   public GameSprite getWorldShadowSprite(InventoryItem var1, PlayerMob var2) {
      return new GameSprite(GameResources.item_shadow);
   }

   public GameSprite getWorldItemSprite(InventoryItem var1, PlayerMob var2) {
      return this.getItemSprite(var1, var2);
   }

   public GameSprite getItemSprite(InventoryItem var1, PlayerMob var2) {
      return new GameSprite(this.itemTexture, 32);
   }

   public Color getDrawColor(InventoryItem var1, PlayerMob var2) {
      return new Color(255, 255, 255);
   }

   public final DrawOptions getAttackDrawOptions(InventoryItem var1, Level var2, PlayerMob var3, GameTexture var4, InventoryItem var5, float var6, int var7, int var8, GameLight var9) {
      return this.getAttackDrawOptions(var1, var2, var3, var3.dir, var3.attackDir.x, var3.attackDir.y, (Boolean)var3.buffManager.getModifier(BuffModifiers.INVISIBILITY) ? null : var4, var5, var6, var7, var8, var9);
   }

   public final DrawOptions getAttackDrawOptions(InventoryItem var1, Level var2, PlayerMob var3, int var4, float var5, float var6, GameTexture var7, InventoryItem var8, float var9, int var10, int var11, GameLight var12) {
      return this.getAttackDrawOptions(var1, var2, var3, var4, var5, var6, var9, var7 == null ? null : new GameSprite(var7, 0, 8, 32), var8, var10, var11, var12);
   }

   public DrawOptions getAttackDrawOptions(InventoryItem var1, Level var2, PlayerMob var3, int var4, float var5, float var6, float var7, GameSprite var8, InventoryItem var9, int var10, int var11, GameLight var12) {
      ItemAttackDrawOptions var13 = this.setupAttackDrawOptions(var1, var2, var3, var4, var5, var6, var7, var8, var9, var12);
      this.setDrawAttackRotation(var1, var13, var5, var6, var7);
      return var13.pos(var10, var11);
   }

   public ItemAttackDrawOptions setupAttackDrawOptions(InventoryItem var1, PlayerMob var2, int var3, float var4, float var5, float var6, GameSprite var7, GameSprite var8, Color var9, Color var10, GameLight var11) {
      ItemAttackDrawOptions var12 = ItemAttackDrawOptions.start(var3);
      this.setupItemSpriteAttackDrawOptions(var12, var1, var2, var3, var4, var5, var6, var10, var11);
      var12.light(var11);
      if (var7 != null) {
         var12.armSprite(var7);
      }

      if (var8 != null) {
         var12.armorSprite(var8);
         var12.armorColor(var9);
      }

      if (!this.animDrawBehindHand()) {
         var12.itemAfterHand();
      }

      return var12;
   }

   public ItemAttackDrawOptions setupItemSpriteAttackDrawOptions(ItemAttackDrawOptions var1, InventoryItem var2, PlayerMob var3, int var4, float var5, float var6, float var7, Color var8, GameLight var9) {
      ItemAttackDrawOptions.AttackItemSprite var10 = var1.itemSprite(this.getAttackSprite(var2, var3));
      var10.itemRotatePoint(this.attackXOffset, this.attackYOffset);
      if (var8 != null) {
         var10.itemColor(var8);
      }

      return var10.itemEnd();
   }

   public GameSprite getAttackSprite(InventoryItem var1, PlayerMob var2) {
      return this.attackTexture != null ? new GameSprite(this.attackTexture) : new GameSprite(this.getItemSprite(var1, var2), 24);
   }

   public ItemAttackDrawOptions setupAttackDrawOptions(InventoryItem var1, Level var2, PlayerMob var3, int var4, float var5, float var6, float var7, GameSprite var8, InventoryItem var9, GameLight var10) {
      GameSprite var11 = null;
      Color var12 = null;
      if (var9 != null && var9.item.isArmorItem() && var9.item instanceof ChestArmorItem) {
         var11 = ((ChestArmorItem)var9.item).getAttackArmSprite(var9, var2, var3);
         var12 = var9.item.getDrawColor(var9, var3);
      }

      return this.setupAttackDrawOptions(var1, var3, var4, var5, var6, var7, var8, var11, var12, this.getDrawColor(var1, var3), var10);
   }

   public void setDrawAttackRotation(InventoryItem var1, ItemAttackDrawOptions var2, float var3, float var4, float var5) {
      var2.pointRotation(var3, var4);
   }

   public boolean holdsItem(InventoryItem var1, PlayerMob var2) {
      return this.holdTexture != null;
   }

   public DrawOptions getHoldItemDrawOptions(InventoryItem var1, PlayerMob var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, boolean var10, GameLight var11, float var12, GameTexture var13) {
      byte var14 = 0;
      byte var15 = 0;
      TextureDrawOptionsEnd var16;
      if (this.holdTexture.getHeight() / 128 == 4) {
         var7 *= 2;
         var8 *= 2;
         var14 = -32;
         var15 = -32;
         var16 = this.holdTexture.initDraw().sprite(var3, var4, 128);
         if (var13 != null) {
            var16.addShaderState(GameResources.edgeMaskShader.addMaskOffset(var14, var15));
         }
      } else {
         var16 = this.holdTexture.initDraw().sprite(var3, var4, 64);
      }

      var16 = var16.light(var11).alpha(var12).size(var7, var8).mirror(var9, var10).addShaderTextureFit(var13, 1);
      return var16.pos(var5 + var14, var6 + var15);
   }

   public boolean holdItemInFrontOfArms(InventoryItem var1, PlayerMob var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, boolean var10, GameLight var11, float var12, GameTexture var13) {
      return false;
   }

   protected void loadItemTextures() {
      this.itemTexture = GameTexture.fromFile("items/" + this.getStringID());
   }

   protected void loadHoldTextures() {
      try {
         this.holdTexture = GameTexture.fromFileRaw("player/holditems/" + this.getStringID());
      } catch (FileNotFoundException var2) {
         this.holdTexture = null;
      }

   }

   protected void loadAttackTexture() {
      try {
         this.attackTexture = GameTexture.fromFileRaw("player/weapons/" + this.getStringID());
      } catch (FileNotFoundException var2) {
         this.attackTexture = null;
      }

   }

   public void loadTextures() {
      this.loadItemTextures();
      this.loadHoldTextures();
      this.loadAttackTexture();
   }

   public float getBrokerValue(InventoryItem var1) {
      return ItemRegistry.getBrokerValue(this.getID());
   }

   public int getStackSize() {
      return this.stackSize;
   }

   public boolean isToolItem() {
      return this.type == Item.Type.TOOL;
   }

   public boolean isPlaceable() {
      return this.isPlaceable;
   }

   public boolean isTickItem() {
      return this.isTickItem;
   }

   public PlaceableItemInterface getPlaceable() {
      return this.isPlaceable() ? (PlaceableItemInterface)this : null;
   }

   public boolean isArmorItem() {
      return this.type == Item.Type.ARMOR;
   }

   public boolean isMountItem() {
      return this.type == Item.Type.MOUNT;
   }

   public boolean isTrinketItem() {
      return this.type == Item.Type.TRINKET;
   }

   public boolean isFoodItem() {
      return this.type == Item.Type.FOOD;
   }

   public boolean isEnchantable(InventoryItem var1) {
      return false;
   }

   public String getIsEnchantableError(InventoryItem var1) {
      return null;
   }

   public int getSpriteRes() {
      return 32;
   }

   public boolean getConstantUse(InventoryItem var1) {
      return true;
   }

   public boolean animDrawBehindHand() {
      return false;
   }

   public float zoomAmount() {
      return 0.0F;
   }

   public Rarity getRarity(InventoryItem var1) {
      return this.rarity;
   }

   public GameColor getRarityColor(InventoryItem var1) {
      Rarity var2 = this.getRarity(var1);
      return var2 == null ? GameColor.NO_COLOR : var2.color;
   }

   public float getHungerUsage(InventoryItem var1, PlayerMob var2) {
      return this.hungerUsage;
   }

   public int getUpgradeLevel(InventoryItem var1) {
      return var1.getGndData().getInt("upgradeLevel");
   }

   public float getUpgradeTier(InventoryItem var1) {
      return (float)this.getUpgradeLevel(var1) / 100.0F;
   }

   public void setUpgradeLevel(InventoryItem var1, int var2) {
      var1.getGndData().setInt("upgradeLevel", var2);
   }

   public void setUpgradeTier(InventoryItem var1, float var2) {
      this.setUpgradeLevel(var1, (int)(var2 * 100.0F));
   }

   public int getFlatAttackAnimTime(InventoryItem var1) {
      GNDItemMap var2 = var1.getGndData();
      return var2.hasKey("attackAnimTime") ? var2.getInt("attackAnimTime") : this.attackAnimTime.getValue(this.getUpgradeTier(var1));
   }

   public int getFlatAttackCooldownTime(InventoryItem var1) {
      GNDItemMap var2 = var1.getGndData();
      return var2.hasKey("attackCooldownTime") ? var2.getInt("attackCooldownTime") : this.attackCooldownTime.getValue(this.getUpgradeTier(var1));
   }

   public int getFlatItemCooldownTime(InventoryItem var1) {
      GNDItemMap var2 = var1.getGndData();
      return var2.hasKey("itemCooldownTime") ? var2.getInt("itemCooldownTime") : this.itemCooldownTime.getValue(this.getUpgradeTier(var1));
   }

   public int getAttackAnimTime(InventoryItem var1, Mob var2) {
      return Math.round((float)this.getFlatAttackAnimTime(var1) * (1.0F / this.getAttackSpeedModifier(var1, var2)));
   }

   public int getAttackCooldownTime(InventoryItem var1, Mob var2) {
      return Math.round((float)this.getFlatAttackCooldownTime(var1) * (1.0F / this.getAttackSpeedModifier(var1, var2)));
   }

   public int getItemCooldownTime(InventoryItem var1, Mob var2) {
      return this.getFlatItemCooldownTime(var1);
   }

   public float getAttackSpeedModifier(InventoryItem var1, Mob var2) {
      return 1.0F;
   }

   /** @deprecated */
   @Deprecated
   public int getAnimSpeed(InventoryItem var1, Mob var2) {
      return this.getAttackAnimTime(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   public int getCooldown(InventoryItem var1, Mob var2) {
      return this.getAttackCooldownTime(var1, var2);
   }

   public double toAttacksPerSecond(int var1) {
      double var2 = 1.0 / ((double)var1 / 1000.0);
      return GameMath.toDecimals(var2, 1);
   }

   public int getAnimAttacks(InventoryItem var1) {
      return Math.max(1, this.animAttacks);
   }

   public boolean changesDir() {
      return this.changeDir;
   }

   public void tickPickupEntity(ItemPickupEntity var1) {
      if (var1.isClient()) {
         this.refreshLight(var1.getLevel(), var1.x, var1.y, var1.item);
      }

   }

   public void refreshLight(Level var1, float var2, float var3, InventoryItem var4) {
   }

   public float getSinkingRate(ItemPickupEntity var1, float var2) {
      return !var1.getLevel().inLiquid(var1.getX(), var1.getY()) ? 0.0F : var1.getLevel().getTile(var1.getX() / 32, var1.getY() / 32).getItemSinkingRate(var2);
   }

   public float getMaxSinking(ItemPickupEntity var1) {
      return !var1.getLevel().inLiquid(var1.getX(), var1.getY()) ? 0.0F : var1.getLevel().getTile(var1.getX() / 32, var1.getY() / 32).getItemMaxSinking();
   }

   public int getIncinerationRate() {
      return this.incinerationTimeMillis;
   }

   public boolean showWires() {
      return false;
   }

   public SidebarForm getSidebar(InventoryItem var1) {
      return null;
   }

   public boolean isGlobalIngredient(int var1) {
      return this.globalIngredients.contains(var1);
   }

   public boolean isGlobalIngredient(String var1) {
      return this.isGlobalIngredient(GlobalIngredientRegistry.getGlobalIngredientID(var1));
   }

   public boolean isGlobalIngredient(GlobalIngredient var1) {
      return this.isGlobalIngredient(var1.getID());
   }

   public Item addGlobalIngredient(String... var1) {
      String[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String var5 = var2[var4];
         this.addGlobalIngredient(GlobalIngredientRegistry.getGlobalIngredient(var5));
      }

      return this;
   }

   private void addGlobalIngredient(GlobalIngredient... var1) {
      GlobalIngredient[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         GlobalIngredient var5 = var2[var4];
         if (this.globalIngredients.contains(var5.getID())) {
            return;
         }

         this.globalIngredients.add(var5.getID());
         if (this.idData.isSet()) {
            var5.registerItemID(this.getID());
         }
      }

   }

   public ArrayList<Integer> getGlobalIngredients() {
      return this.globalIngredients;
   }

   public Item setItemCategory(ItemCategoryManager var1, String... var2) {
      if (this.idData.isSet()) {
         var1.setItemCategory(this, var2);
      } else {
         this.itemCategoryTree.put(var1, var2);
      }

      return this;
   }

   public Item setItemCategory(String... var1) {
      return this.setItemCategory(ItemCategory.masterManager, var1);
   }

   public void registerItemCategory() {
      if (this.itemCategoryTree != null && this.idData.isSet()) {
         Iterator var1 = this.itemCategoryTree.entrySet().iterator();

         while(var1.hasNext()) {
            Map.Entry var2 = (Map.Entry)var1.next();
            String[] var3 = (String[])var2.getValue();
            if (var3 != null) {
               ((ItemCategoryManager)var2.getKey()).setItemCategory(this, var3);
            }
         }

         this.itemCategoryTree = null;
      }

   }

   public void setupAttackContentPacket(PacketWriter var1, Level var2, int var3, int var4, PlayerMob var5, InventoryItem var6) {
   }

   public String canAttack(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5) {
      return null;
   }

   public void onServerCanAttackFailed(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, String var6, boolean var7) {
   }

   public Point getControllerAttackLevelPos(Level var1, float var2, float var3, PlayerMob var4, InventoryItem var5) {
      return new Point((int)(var4.x + var2 * 100.0F), (int)(var4.y + var3 * 100.0F));
   }

   public void drawControllerAimPos(GameCamera var1, Level var2, PlayerMob var3, InventoryItem var4) {
      float var5 = ControllerInput.getAimX();
      float var6 = ControllerInput.getAimY();
      if (var5 != 0.0F || var6 != 0.0F) {
         Point var7 = this.getControllerAttackLevelPos(var2, var5, var6, var3, var4);
         int var8 = var1.getDrawX(var7.x);
         int var9 = var1.getDrawY(var7.y);
         int var10 = var1.getDrawX(var3.x);
         int var11 = var1.getDrawY(var3.y);
         this.drawControllerAimInsideWindow(var2.getWorldEntity().getLocalTime(), var8, var9, var10, var11);
      }

   }

   public void drawControllerAimInsideWindow(long var1, int var3, int var4, int var5, int var6) {
      byte var7 = 10;
      boolean var8 = Screen.getSceneWidth() != Screen.getHudWidth() || Screen.getSceneHeight() != Screen.getHudHeight();
      float var9;
      float var10;
      if (var8) {
         var9 = (float)Screen.getSceneWidth() / (float)Screen.getHudWidth();
         var10 = (float)Screen.getSceneHeight() / (float)Screen.getHudHeight();
      } else {
         var9 = 1.0F;
         var10 = 1.0F;
      }

      List var11 = (List)GlobalData.getCurrentState().streamHudHitboxes().map((var4x) -> {
         if (var8) {
            int var5 = (int)((float)var4x.x * var9);
            int var6 = (int)((float)var4x.width * var9);
            int var7x = (int)((float)var4x.y * var10);
            int var8x = (int)((float)var4x.height * var10);
            return new Rectangle(var5 - var7, var7x - var7, var6 + var7 * 2, var8x + var7 * 2);
         } else {
            return new Rectangle(var4x.x - var7, var4x.y - var7, var4x.width + var7 * 2, var4x.height + var7 * 2);
         }
      }).filter((var0) -> {
         return !var0.isEmpty();
      }).collect(Collectors.toList());
      byte var12 = 20;
      Rectangle var13 = new Rectangle(var12, var12, Screen.getSceneWidth() - var12 * 2, Screen.getSceneHeight() - var12 * 2 - 20);
      boolean var14 = false;
      if (!var13.contains(var3, var4)) {
         Line2D.Float var15 = new Line2D.Float((float)var5, (float)var6, (float)var3, (float)var4);
         IntersectionPoint var16 = CollisionPoint.getClosestCollision((List)Collections.singletonList(var13), var15, true);
         if (var16 != null) {
            var14 = true;
            var3 = (int)var16.x;
            var4 = (int)var16.y;
         }
      }

      Iterator var19 = var11.iterator();

      while(var19.hasNext()) {
         Rectangle var21 = (Rectangle)var19.next();
         if (var21.contains(var3, var4)) {
            Line2D.Float var17 = new Line2D.Float((float)var5, (float)var6, (float)var3, (float)var4);
            IntersectionPoint var18 = CollisionPoint.getClosestCollision((List)var11, var17, false);
            if (var18 != null) {
               var14 = true;
               var3 = (int)var18.x;
               var4 = (int)var18.y;
               break;
            }
         }
      }

      if (var14) {
         Point2D.Float var20 = GameMath.normalize((float)(var3 - var5), (float)(var4 - var6));
         float var22 = GameMath.getAngle(var20);
         GameResources.aimArrow.initDraw().color(new Color(250, 50, 50)).rotate(var22 + 90.0F).posMiddle(var3, var4).draw();
      } else {
         GameResources.aim.initDraw().color(new Color(250, 50, 50)).rotate((float)var1 / 5.0F).posMiddle(var3, var4).draw();
      }

   }

   public static int getRandomAttackSeed(GameRandom var0) {
      return var0.nextInt(65535);
   }

   public InventoryItem onAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      return var6;
   }

   public boolean shouldRunOnAttackedBuffEvent(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PlayerInventorySlot var6, int var7, int var8, PacketReader var9) {
      return true;
   }

   public void showAttack(Level var1, int var2, int var3, AttackAnimMob var4, int var5, InventoryItem var6, int var7, PacketReader var8) {
   }

   public boolean showAttackAllDirections(AttackAnimMob var1, InventoryItem var2) {
      return this.showAttackAllDirections;
   }

   public float getFinalAttackMovementMod(InventoryItem var1, PlayerMob var2) {
      float var3 = this.getAttackMovementMod(var1);
      float var4 = 1.0F - var3;
      return var3 + var4 * Math.abs((Float)var2.buffManager.getModifier(BuffModifiers.ATTACK_MOVEMENT_MOD) - 1.0F);
   }

   public float getAttackMovementMod(InventoryItem var1) {
      return 1.0F;
   }

   public void tickHolding(InventoryItem var1, PlayerMob var2) {
      if (var2.isClient()) {
         this.refreshLight(var2.getLevel(), var2.x, var2.y, var1);
      }

   }

   public ItemUsed useHealthPotion(Level var1, PlayerMob var2, InventoryItem var3) {
      return new ItemUsed(false, var3);
   }

   public ItemUsed useManaPotion(Level var1, PlayerMob var2, InventoryItem var3) {
      return new ItemUsed(false, var3);
   }

   public ItemUsed eatFood(Level var1, PlayerMob var2, InventoryItem var3) {
      return new ItemUsed(false, var3);
   }

   public ItemUsed useBuffPotion(Level var1, PlayerMob var2, InventoryItem var3) {
      return new ItemUsed(false, var3);
   }

   public String getInventoryRightClickControlTip(Container var1, InventoryItem var2, int var3, ContainerSlot var4) {
      return null;
   }

   public Supplier<ContainerActionResult> getInventoryRightClickAction(Container var1, InventoryItem var2, int var3, ContainerSlot var4) {
      return null;
   }

   public boolean isPotion() {
      return this.isPotion;
   }

   public boolean onMouseHoverMob(InventoryItem var1, GameCamera var2, PlayerMob var3, Mob var4, boolean var5) {
      return false;
   }

   public boolean onMouseHoverPickup(InventoryItem var1, GameCamera var2, PlayerMob var3, PickupEntity var4, boolean var5) {
      return false;
   }

   public void onMouseHoverTile(InventoryItem var1, GameCamera var2, PlayerMob var3, int var4, int var5, TilePosition var6, boolean var7) {
   }

   public int compareTo(InventoryItem var1, InventoryItem var2) {
      ItemCategory var3 = ItemCategory.masterManager.getItemsCategory(var1.item);
      ItemCategory var4 = ItemCategory.masterManager.getItemsCategory(var2.item);
      int var5 = var3.compareTo(var4);
      if (var5 == 0) {
         if (var1.item.getID() != var2.item.getID()) {
            String var6 = ItemRegistry.getDisplayName(var1.item.getID());
            String var7 = ItemRegistry.getDisplayName(var2.item.getID());
            if (var6 == null && var7 == null) {
               return 0;
            }

            if (var6 == null) {
               return 1;
            }

            if (var7 == null) {
               return -1;
            }

            var5 = var6.compareTo(var7);
         } else {
            var5 = this.compareToSameItem(var1, var2);
         }

         return var5;
      } else {
         return var5;
      }
   }

   public int compareToSameItem(InventoryItem var1, InventoryItem var2) {
      return var1.item.getDisplayName(var1).compareTo(var2.item.getDisplayName(var2));
   }

   public ItemPickupEntity getPickupEntity(Level var1, InventoryItem var2, float var3, float var4, float var5, float var6) {
      return new ItemPickupEntity(var1, var2, var3, var4, var5, var6);
   }

   public boolean isSameItem(Level var1, InventoryItem var2, InventoryItem var3, String var4) {
      return this == var3.item;
   }

   public boolean isSameGNDData(Level var1, InventoryItem var2, InventoryItem var3, String var4) {
      return true;
   }

   public boolean canCombineItem(Level var1, PlayerMob var2, InventoryItem var3, InventoryItem var4, String var5) {
      return var4 == null ? false : this.isSameItem(var1, var3, var4, var5);
   }

   public boolean ignoreCombineStackLimit(Level var1, PlayerMob var2, InventoryItem var3, InventoryItem var4, String var5) {
      return false;
   }

   /** @deprecated */
   @Deprecated
   public boolean onCombine(Level var1, PlayerMob var2, InventoryItem var3, InventoryItem var4, int var5, int var6, boolean var7, String var8) {
      return this.onCombine(var1, var2, (Inventory)null, -1, var3, var4, var5, var6, var7, var8, (InventoryAddConsumer)null);
   }

   public boolean onCombine(Level var1, PlayerMob var2, Inventory var3, int var4, InventoryItem var5, InventoryItem var6, int var7, int var8, boolean var9, String var10, InventoryAddConsumer var11) {
      var8 = Math.min(var8, var7 - var5.getAmount());
      if (var8 <= 0) {
         return false;
      } else {
         int var12 = var5.getAmount();
         long var13 = 0L;
         long var15 = 0L;
         WorldSettings var17;
         if (this.shouldSpoilTick(var5) && var1 != null) {
            var17 = var1.getWorldSettings();
            if (var17 == null || var17.survivalMode) {
               var13 = this.getAndUpdateCurrentSpoilTime(var5, var1, 1.0F);
            }
         }

         if (var6.item.shouldSpoilTick(var6) && var1 != null) {
            var17 = var1.getWorldSettings();
            if (var17 == null || var17.survivalMode) {
               var15 = var6.item.getAndUpdateCurrentSpoilTime(var6, var1, 1.0F);
            }
         }

         var5.setAmount(var5.getAmount() + var8);
         if (var11 != null) {
            var11.add(var3, var4, var8);
         }

         if (var9) {
            var5.setNew(var5.isNew() || var6.isNew());
         }

         var6.setAmount(var6.getAmount() - var8);
         if (var15 != var13) {
            int var22 = var5.getAmount();
            if (var13 != 0L && var15 != 0L) {
               double var18 = (double)var12 / (double)var22;
               long var20 = GameMath.lerp(var18, var15, var13);
               this.setSpoilTime(var5, var20);
            } else if (var15 != 0L) {
               this.setSpoilTime(var5, var15);
            }
         }

         return true;
      }
   }

   public ComparableSequence<Integer> getInventoryPriority(Level var1, PlayerMob var2, Inventory var3, int var4, InventoryItem var5, String var6) {
      return new ComparableSequence(var4);
   }

   public ComparableSequence<Integer> getInventoryAddPriority(Level var1, PlayerMob var2, Inventory var3, int var4, InventoryItem var5, InventoryItem var6, String var7) {
      return new ComparableSequence(var4);
   }

   public int getInventoryAmount(Level var1, PlayerMob var2, InventoryItem var3, Item var4, String var5) {
      return var4 == this ? var3.getAmount() : 0;
   }

   public int getInventoryAmount(Level var1, PlayerMob var2, InventoryItem var3, Type var4, String var5) {
      return var4 == this.type ? var3.getAmount() : 0;
   }

   public void countIngredientAmount(Level var1, PlayerMob var2, Inventory var3, int var4, InventoryItem var5, IngredientCounter var6) {
      var6.handle(var3, var4, var5);
   }

   public Item getInventoryFirstItem(Level var1, PlayerMob var2, InventoryItem var3, Item[] var4, String var5) {
      Item[] var6 = var4;
      int var7 = var4.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         Item var9 = var6[var8];
         if (var9 == this) {
            return this;
         }
      }

      return null;
   }

   public Item getInventoryFirstItem(Level var1, PlayerMob var2, InventoryItem var3, Type var4, String var5) {
      return var4 == this.type ? this : null;
   }

   public boolean inventoryAddItem(Level var1, PlayerMob var2, InventoryItem var3, InventoryItem var4, String var5, boolean var6, int var7, boolean var8) {
      return this.inventoryAddItem(var1, var2, (Inventory)null, -1, var3, var4, var5, var6, var7, var8, (InventoryAddConsumer)null);
   }

   public boolean inventoryAddItem(Level var1, PlayerMob var2, Inventory var3, int var4, InventoryItem var5, InventoryItem var6, String var7, boolean var8, int var9, boolean var10, InventoryAddConsumer var11) {
      return var8 && this.canCombineItem(var1, var2, var5, var6, var7) ? this.onCombine(var1, var2, var3, var4, var5, var6, this.getStackSize(), var6.getAmount(), var10, var7, var11) : false;
   }

   public int inventoryCanAddItem(Level var1, PlayerMob var2, InventoryItem var3, InventoryItem var4, String var5, boolean var6, int var7) {
      return var6 && this.canCombineItem(var1, var2, var3, var4, var5) ? Math.max(0, var7 - var3.getAmount()) : 0;
   }

   public int removeInventoryAmount(Level var1, PlayerMob var2, InventoryItem var3, Item var4, int var5, String var6) {
      if (var4 == this) {
         int var7 = Math.min(var3.getAmount(), var5);
         var3.setAmount(var3.getAmount() - var7);
         return var7;
      } else {
         return 0;
      }
   }

   public int removeInventoryAmount(Level var1, PlayerMob var2, InventoryItem var3, Type var4, int var5, String var6) {
      if (var4 == this.type) {
         int var7 = Math.min(var3.getAmount(), var5);
         var3.setAmount(var3.getAmount() - var7);
         return var7;
      } else {
         return 0;
      }
   }

   public int removeInventoryAmount(Level var1, PlayerMob var2, final InventoryItem var3, Inventory var4, int var5, Ingredient var6, int var7, Collection<InventoryItemsRemoved> var8) {
      if (var6.matchesItem(this)) {
         final int var9 = Math.min(var3.getAmount(), var7);
         var3.setAmount(var3.getAmount() - var9);
         if (var9 > 0 && var8 != null) {
            var8.add(new InventoryItemsRemoved(var4, var5, var3, var9) {
               public void revert() {
                  var3.setAmount(var3.getAmount() + var9);
                  if (this.inventory != null && this.inventorySlot != -1) {
                     this.inventory.setItem(this.inventorySlot, var3);
                  }

               }
            });
         }

         return var9;
      } else {
         return 0;
      }
   }

   public boolean dropAsMatDeathPenalty(PlayerInventorySlot var1, boolean var2, InventoryItem var3, ItemDropperHandler var4) {
      if (this.dropsAsMatDeathPenalty) {
         var4.dropItem(var3, var1, var2);
         return true;
      } else {
         return false;
      }
   }

   public long getDropDecayTime(InventoryItem var1) {
      return this.dropDecayTimeMillis;
   }

   public Item spoilDuration(int var1) {
      this.spoilDurationSeconds = var1 * 60;
      return this;
   }

   public int getStartSpoilSeconds(InventoryItem var1) {
      return this.spoilDurationSeconds;
   }

   public boolean shouldSpoilTick(InventoryItem var1) {
      return this.getStartSpoilSeconds(var1) > 0;
   }

   public long getAndUpdateCurrentSpoilTime(InventoryItem var1, GameClock var2, float var3) {
      if (var1 != null) {
         var3 = Math.max(0.0F, var3 * GLOBAL_SPOIL_TIME_MODIFIER);
         float var4 = this.getCurrentSpoilRateModifier(var1);
         long var5 = var1.getGndData().getLong("spoilTime");
         if (var5 == 0L) {
            if (var3 <= 0.0F) {
               var5 = (long)(-this.getStartSpoilSeconds(var1)) * 1000L;
            } else {
               var5 = var2.getWorldTime() + (long)((double)((long)this.getStartSpoilSeconds(var1) * 1000L) * (1.0 / (double)var3));
            }
         } else if (var4 != var3) {
            long var7;
            if (var4 <= 0.0F) {
               if (var5 > 0L) {
                  var7 = Math.max(0L, var5 - var2.getWorldTime());
               } else {
                  var7 = -var5;
               }

               var5 = var2.getWorldTime() + (long)((double)var7 * (1.0 / (double)var3));
            } else if (var3 <= 0.0F) {
               var7 = var5 - var2.getWorldTime();
               var5 = (long)(-((double)var7 * (double)var4));
            } else {
               var7 = Math.max(0L, var5 - var2.getWorldTime());
               long var9 = (long)((double)var7 * (double)var4);
               long var11 = (long)((double)var9 * (1.0 / (double)var3));
               var5 = var2.getWorldTime() + var11;
            }
         }

         var1.getGndData().setFloat("spoilModifier", var3);
         this.setSpoilTime(var1, var5);
         return var5;
      } else {
         return 0L;
      }
   }

   public long getCurrentSpoilTime(InventoryItem var1) {
      long var2 = 0L;
      if (var1 != null) {
         var2 = var1.getGndData().getLong("spoilTime");
         if (this.getCurrentSpoilRateModifier(var1) > 0.0F) {
            var2 = Math.max(0L, var2);
         }
      }

      return var2;
   }

   public float getCurrentSpoilRateModifier(InventoryItem var1) {
      return var1 == null ? 0.0F : Math.max(0.0F, var1.getGndData().getFloat("spoilModifier"));
   }

   public void setSpoilTime(InventoryItem var1, long var2) {
      var1.getGndData().setLong("spoilTime", var2);
   }

   public long tickSpoilTime(InventoryItem var1, GameClock var2, float var3, Consumer<InventoryItem> var4) {
      long var5 = this.getAndUpdateCurrentSpoilTime(var1, var2, var3);
      if (var5 > 0L && var2 != null) {
         long var7 = var5 - var2.getWorldTime();
         if (var7 <= 0L && var4 != null) {
            InventoryItem var9 = this.getSpoiledItem(var1);
            var4.accept(var9);
            return 0L;
         }
      }

      return var5;
   }

   public InventoryItem getSpoiledItem(InventoryItem var1) {
      return new InventoryItem("spoiledfood", var1.getAmount());
   }

   public InventoryItem getDefaultItem(PlayerMob var1, int var2) {
      return new InventoryItem(this, var2);
   }

   public void addDefaultItems(List<InventoryItem> var1, PlayerMob var2) {
      var1.add(this.getDefaultItem(var2, 1));
   }

   public InventoryItem getDefaultLootItem(GameRandom var1, int var2) {
      return new InventoryItem(this, var2);
   }

   public boolean matchesSearch(InventoryItem var1, PlayerMob var2, String var3) {
      if (var3 != null && var3.length() != 0) {
         var3 = var3.toLowerCase();
         if (this.getStringID().toLowerCase().contains(var3)) {
            return true;
         } else if (var1.getItemDisplayName().toLowerCase().contains(var3)) {
            return true;
         } else {
            for(ItemCategory var4 = ItemCategory.getItemsCategory(this); var4 != null; var4 = var4.parent) {
               if (var4.stringID.toLowerCase().contains(var3)) {
                  return true;
               }

               if (var4.displayName.translate().toLowerCase().contains(var3)) {
                  return true;
               }
            }

            Iterator var5 = this.getGlobalIngredients().iterator();

            GlobalIngredient var7;
            do {
               if (!var5.hasNext()) {
                  var5 = this.keyWords.iterator();

                  String var8;
                  do {
                     if (!var5.hasNext()) {
                        return false;
                     }

                     var8 = (String)var5.next();
                  } while(!var8.toLowerCase().contains(var3));

                  return true;
               }

               Integer var6 = (Integer)var5.next();
               var7 = GlobalIngredientRegistry.getGlobalIngredient(var6);
               if (var7.getStringID().toLowerCase().contains(var3)) {
                  return true;
               }
            } while(!var7.displayName.translate().toLowerCase().contains(var3));

            return true;
         }
      } else {
         return true;
      }
   }

   public static enum Type {
      MAT(MatItem.class),
      TOOL(ToolItem.class),
      ARMOR(ArmorItem.class),
      TRINKET(TrinketItem.class),
      MOUNT(MountItem.class),
      ARROW(ArrowItem.class),
      BULLET(BulletItem.class),
      BAIT(BaitItem.class),
      FOOD(FoodConsumableItem.class),
      QUEST(QuestItem.class),
      MISC(Item.class);

      final Class<? extends Item> subClass;

      private Type(Class var3) {
         this.subClass = var3;
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{MAT, TOOL, ARMOR, TRINKET, MOUNT, ARROW, BULLET, BAIT, FOOD, QUEST, MISC};
      }
   }

   public static enum Rarity {
      NORMAL(GameColor.ITEM_NORMAL, 350, 40),
      COMMON(GameColor.ITEM_COMMON, 250, 300),
      UNCOMMON(GameColor.ITEM_UNCOMMON, 230, 280),
      RARE(GameColor.ITEM_RARE, 300, 350),
      EPIC(GameColor.ITEM_EPIC, 260, 310),
      LEGENDARY(GameColor.ITEM_LEGENDARY, 280, 330),
      QUEST(GameColor.ITEM_QUEST, 330, 20),
      UNIQUE(GameColor.ITEM_UNIQUE, 220, 280);

      public final GameColor color;
      public final int outlineMinHue;
      public final int outlineMaxHue;

      private Rarity(GameColor var3, int var4, int var5) {
         this.color = var3;
         this.outlineMinHue = var4;
         this.outlineMaxHue = var5;
      }

      public Rarity getNext(Rarity var1) {
         int var2 = var1 == null ? values().length - 1 : var1.ordinal();
         if (this.ordinal() > var2) {
            return this;
         } else {
            int var3 = this.ordinal() + 1;
            return var3 >= var2 ? values()[var2] : values()[var3];
         }
      }

      // $FF: synthetic method
      private static Rarity[] $values() {
         return new Rarity[]{NORMAL, COMMON, UNCOMMON, RARE, EPIC, LEGENDARY, QUEST, UNIQUE};
      }
   }
}
