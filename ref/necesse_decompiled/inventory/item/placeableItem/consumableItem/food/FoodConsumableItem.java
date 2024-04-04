package necesse.inventory.item.placeableItem.consumableItem.food;

import java.awt.Color;
import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.modifiers.ModifierTooltip;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketMobBuff;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.ComparableSequence;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.mobs.buffs.staticBuffs.FoodBuff;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.MergeFunction;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.ItemUsed;
import necesse.inventory.item.placeableItem.consumableItem.AdventurePartyConsumableItem;
import necesse.inventory.item.placeableItem.consumableItem.ConsumableItem;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.settler.FoodQuality;

public class FoodConsumableItem extends ConsumableItem implements AdventurePartyConsumableItem {
   public FoodQuality quality;
   public int nutrition;
   public boolean isDebuff;
   protected String cropTextureName;
   public int duration;
   public boolean drinkSound;
   public ModifierValue<?>[] modifiers;
   public GameTexture buffTexture;

   public FoodConsumableItem(int var1, Item.Rarity var2, FoodQuality var3, int var4, int var5, boolean var6, ModifierValue<?>... var7) {
      super(var1, true);
      this.duration = var5;
      this.rarity = var2;
      this.quality = var3;
      this.nutrition = var4;
      this.drinkSound = var6;
      this.modifiers = var7;
      if (var3 != null) {
         this.setItemCategory(var3.masterCategoryTree);
      } else {
         this.setItemCategory(new String[]{"consumable", "food"});
      }

      this.keyWords.add("food");
      this.dropDecayTimeMillis = 1800000L;
      this.dropsAsMatDeathPenalty = false;
   }

   public FoodConsumableItem(int var1, Item.Rarity var2, FoodQuality var3, int var4, int var5, ModifierValue<?>... var6) {
      this(var1, var2, var3, var4, var5, false, var6);
   }

   public void registerItemCategory() {
      if (this.quality != null) {
         this.setItemCategory(this.quality.masterCategoryTree);
      } else {
         this.setItemCategory(new String[]{"consumable", "food"});
      }

      super.registerItemCategory();
      if (this.quality != null) {
         ItemCategory.foodQualityManager.setItemCategory(this, (ItemCategory)this.quality.foodCategory);
      }

   }

   public boolean getConstantUse(InventoryItem var1) {
      return true;
   }

   public FoodConsumableItem cropTexture(String var1) {
      this.cropTextureName = var1;
      return this;
   }

   public FoodConsumableItem debuff() {
      this.isDebuff = true;
      return this;
   }

   public void loadTextures() {
      super.loadTextures();
      GameTexture var1 = GameTexture.fromFile("buffs/mask", true);
      GameTexture var2 = GameTexture.fromFile(this.isDebuff ? "buffs/negative" : "buffs/positive", true);
      this.buffTexture = new GameTexture("buffs/food " + this.getStringID(), var2.getWidth(), var2.getHeight());
      int var3 = (this.buffTexture.getWidth() - this.itemTexture.getWidth()) / 2;
      int var4 = (this.buffTexture.getHeight() - this.itemTexture.getHeight()) / 2;

      for(int var5 = 0; var5 < this.buffTexture.getWidth(); ++var5) {
         for(int var6 = 0; var6 < this.buffTexture.getHeight(); ++var6) {
            Color var7 = MergeFunction.GLBLEND.merge(this.itemTexture.getColor(var5 + var3, var6 + var4), var1.getColor(var5, var6));
            this.buffTexture.setPixel(var5, var6, MergeFunction.NORMAL.merge(var2.getColor(var5, var6), var7));
         }
      }

      this.buffTexture.makeFinal();
      this.itemTexture.makeFinal();
   }

   protected void loadItemTextures() {
      if (this.cropTextureName != null) {
         this.itemTexture = new GameTexture(GameTexture.fromFile("objects/" + this.cropTextureName), 0, 0, 32);
      } else {
         this.itemTexture = GameTexture.fromFile("items/" + this.getStringID(), true);
      }

   }

   public void setDrawAttackRotation(InventoryItem var1, ItemAttackDrawOptions var2, float var3, float var4, float var5) {
      var2.swingRotationInv(var5);
   }

   public boolean shouldSendToOtherClients(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, String var6, PacketReader var7) {
      return var6 == null;
   }

   public void onOtherPlayerPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      this.playConsumeSound(var1, var4, var5);
   }

   public String canConsume(Level var1, PlayerMob var2, InventoryItem var3) {
      boolean var4 = var1.getWorldSettings() == null || var1.getWorldSettings().playerHunger();
      if (var2.hungerLevel < 1.0F && var4) {
         return null;
      } else {
         ActiveBuff var5 = var2.buffManager.getBuff((Buff)BuffRegistry.FOOD_BUFF);
         if (var5 != null) {
            FoodConsumableItem var6 = FoodBuff.getFoodItem(var5);
            if (var6 == this) {
               return "alreadyconsumed";
            }
         }

         return null;
      }
   }

   public boolean consume(Level var1, PlayerMob var2, InventoryItem var3) {
      boolean var4 = var1.getWorldSettings() == null || var1.getWorldSettings().playerHunger();
      if (this.isDebuff && var2.hungerLevel >= 1.0F && var4) {
         return false;
      } else if (var2.useFoodItem(this, true)) {
         if (this.singleUse) {
            var3.setAmount(var3.getAmount() - 1);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean canAndShouldPartyConsume(Level var1, HumanMob var2, ServerClient var3, InventoryItem var4, String var5) {
      return var2.hungerLevel <= 0.3F;
   }

   public InventoryItem onPartyConsume(Level var1, HumanMob var2, ServerClient var3, InventoryItem var4, String var5) {
      var2.useFoodItem(this, true);
      InventoryItem var6 = var4.copy();
      if (this.singleUse) {
         var4.setAmount(var4.getAmount() - 1);
      }

      return var6;
   }

   public void giveFoodBuff(Mob var1) {
      FoodBuff var2 = this.isDebuff ? BuffRegistry.FOOD_DEBUFF : BuffRegistry.FOOD_BUFF;
      ActiveBuff var3 = new ActiveBuff(var2, var1, (float)this.duration, (Attacker)null);
      var2.setFoodItem(var3, this);
      var1.buffManager.addBuff(var3, false, true);
      if (var1.isServer()) {
         var1.getLevel().getServer().network.sendToClientsAt(new PacketMobBuff(var1.getUniqueID(), var3), (Level)var1.getLevel());
      }

   }

   public void playConsumeSound(Level var1, PlayerMob var2, InventoryItem var3) {
      Screen.playSound(this.drinkSound ? GameResources.drink : GameResources.eat, SoundEffect.effect(var2));
   }

   public InventoryItem onPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      if (this.consume(var1, var4, var5) && !var1.isServer()) {
         this.playConsumeSound(var1, var4, var5);
      }

      return var5;
   }

   public String canPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      return this.canConsume(var1, var4, var5);
   }

   public ComparableSequence<Integer> getInventoryPriority(Level var1, PlayerMob var2, Inventory var3, int var4, InventoryItem var5, String var6) {
      ComparableSequence var7 = super.getInventoryPriority(var1, var2, var3, var4, var5, var6);
      return !var6.equals("usebuffpotion") && !var6.equals("usehealthpotion") && !var6.equals("eatfood") ? var7 : var7.beforeBy((Comparable)(this.isDebuff ? 100 : -this.getRarity(var5).ordinal()));
   }

   public ItemUsed eatFood(Level var1, PlayerMob var2, InventoryItem var3) {
      boolean var4 = var1.getWorldSettings() == null || var1.getWorldSettings().playerHunger();
      if (var2.hungerLevel >= 1.0F && var4) {
         return new ItemUsed(false, var3);
      } else if (!var4 && var2.buffManager.hasBuff((Buff)BuffRegistry.FOOD_BUFF)) {
         return new ItemUsed(false, var3);
      } else {
         String var5 = this.canPlace(var1, 0, 0, var2, var3, (PacketReader)null);
         return var5 == null ? new ItemUsed(true, this.onPlace(var1, 0, 0, var2, var3, (PacketReader)null)) : new ItemUsed(false, this.onAttemptPlace(var1, 0, 0, var2, var3, (PacketReader)null, var5));
      }
   }

   public ItemUsed useBuffPotion(Level var1, PlayerMob var2, InventoryItem var3) {
      if (this.isDebuff) {
         return new ItemUsed(false, var3);
      } else {
         ActiveBuff var4 = var2.buffManager.getBuff((Buff)BuffRegistry.FOOD_BUFF);
         if (var4 != null) {
            return new ItemUsed(false, var3);
         } else {
            String var5 = this.canPlace(var1, 0, 0, var2, var3, (PacketReader)null);
            return var5 == null ? new ItemUsed(true, this.onPlace(var1, 0, 0, var2, var3, (PacketReader)null)) : new ItemUsed(false, this.onAttemptPlace(var1, 0, 0, var2, var3, (PacketReader)null, var5));
         }
      }
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "consumetip"));
      if (var2 == null || var2.getWorldSettings() == null || var2.getWorldSettings().playerHunger()) {
         var4.add(Localization.translate("itemtooltip", "nutritiontip", "value", (Object)this.nutrition));
      }

      if (this.quality != null) {
         var4.add(Localization.translate("itemtooltip", "foodqualitytip", "quality", this.quality.displayName.translate()));
      }

      var4.add(getBuffDurationMessage(this.duration));
      ModifierValue[] var5 = this.modifiers;
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         ModifierValue var8 = var5[var7];
         ModifierTooltip var9 = var8.getTooltip();
         var4.add((Object)var9.toTooltip(true));
      }

      return var4;
   }
}
