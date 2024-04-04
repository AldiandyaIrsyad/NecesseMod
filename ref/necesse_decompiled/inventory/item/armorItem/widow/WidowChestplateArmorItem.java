package necesse.inventory.item.armorItem.widow;

import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.ChestArmorItem;

public class WidowChestplateArmorItem extends ChestArmorItem {
   public WidowChestplateArmorItem() {
      super(25, 1250, Item.Rarity.UNCOMMON, "widowchest", "widowarms");
   }

   public ArmorModifiers getArmorModifiers(InventoryItem var1, Mob var2) {
      return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.MAGIC_DAMAGE, 0.1F)});
   }
}
