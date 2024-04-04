package necesse.inventory.item.toolItem.miscToolItem;

import necesse.engine.localization.Localization;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.toolItem.ToolDamageItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.CustomWildFlowerObject;
import necesse.level.gameObject.FruitBushObject;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.SeedObject;
import necesse.level.maps.Level;

public class SickleToolItem extends ToolDamageItem {
   public SickleToolItem() {
      super(100);
      this.setItemCategory(new String[]{"equipment", "tools"});
      this.toolType = ToolType.PICKAXE;
      this.rarity = Item.Rarity.COMMON;
      this.attackAnimTime.setBaseValue(300);
      this.animAttacks = 3;
      this.toolDps.setBaseValue(100);
      this.addedRange = 1;
      this.attackDamage.setBaseValue(10.0F);
      this.knockback.setBaseValue(50);
      this.attackRange.setBaseValue(50);
      this.width = 10.0F;
      this.attackXOffset = 6;
      this.attackYOffset = 6;
   }

   public boolean isEnchantable(InventoryItem var1) {
      return false;
   }

   public boolean canSmartMineTile(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5) {
      GameObject var6 = var1.getObject(var2, var3);
      if (var6 instanceof SeedObject) {
         return ((SeedObject)var6).isLastStage();
      } else if (var6 instanceof CustomWildFlowerObject) {
         return true;
      } else if (var6 instanceof FruitBushObject) {
         return ((FruitBushObject)var6).getFruitStage(var1, var2, var3) > 0;
      } else {
         return var6.isGrass;
      }
   }

   public boolean canDamageTile(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5) {
      GameObject var6 = var1.getObject(var2, var3);
      if (var6 instanceof SeedObject) {
         return ((SeedObject)var6).isLastStage();
      } else if (var6 instanceof CustomWildFlowerObject) {
         return true;
      } else {
         return var6 instanceof FruitBushObject ? true : var6.isGrass;
      }
   }

   protected void runTileDamage(Level var1, int var2, int var3, int var4, int var5, PlayerMob var6, InventoryItem var7, int var8) {
      GameObject var9 = var1.getObject(var4, var5);
      if (var9 instanceof FruitBushObject) {
         if (var1.isServer()) {
            ((FruitBushObject)var9).harvest(var1, var4, var5, var6);
         }
      } else {
         super.runTileDamage(var1, var2, var3, var4, var5, var6, var7, var8);
      }

   }

   protected void addToolTooltips(ListGameTooltips var1) {
      var1.add(Localization.translate("itemtooltip", "sickletip"));
   }

   public void addStatTooltips(ItemStatTipList var1, InventoryItem var2, InventoryItem var3, Mob var4, boolean var5) {
   }

   public String getCanBeUpgradedError(InventoryItem var1) {
      return Localization.translate("ui", "itemnotupgradable");
   }
}
