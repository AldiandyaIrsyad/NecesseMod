package necesse.inventory.item.armorItem.ravenlords;

import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.ChestArmorItem;

public class RavenlordsChestplateArmorItem extends ChestArmorItem {
   public RavenlordsChestplateArmorItem() {
      super(29, 1400, Item.Rarity.EPIC, "ravenlordschestplate", "ravenlordsarms");
   }

   public ArmorModifiers getArmorModifiers(InventoryItem var1, Mob var2) {
      return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.SPEED, 0.3F), new ModifierValue(BuffModifiers.CRIT_DAMAGE, 0.2F)});
   }
}
