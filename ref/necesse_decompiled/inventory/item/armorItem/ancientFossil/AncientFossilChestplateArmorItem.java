package necesse.inventory.item.armorItem.ancientFossil;

import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.ChestArmorItem;

public class AncientFossilChestplateArmorItem extends ChestArmorItem {
   public AncientFossilChestplateArmorItem() {
      super(18, 1300, Item.Rarity.UNCOMMON, "ancientfossilchest", "ancientfossilarms");
   }

   public ArmorModifiers getArmorModifiers(InventoryItem var1, Mob var2) {
      return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.ALL_DAMAGE, 0.1F)});
   }
}
