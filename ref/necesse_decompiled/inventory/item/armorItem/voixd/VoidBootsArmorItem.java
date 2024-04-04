package necesse.inventory.item.armorItem.voixd;

import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.BootsArmorItem;
import necesse.inventory.item.upgradeUtils.FloatUpgradeValue;

public class VoidBootsArmorItem extends BootsArmorItem {
   public FloatUpgradeValue speed = (new FloatUpgradeValue()).setBaseValue(0.15F).setUpgradedValue(1.0F, 0.25F);

   public VoidBootsArmorItem() {
      super(8, 700, Item.Rarity.COMMON, "voidboots");
   }

   public ArmorModifiers getArmorModifiers(InventoryItem var1, Mob var2) {
      return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.SPEED, this.speed.getValue(this.getUpgradeTier(var1)))});
   }
}
