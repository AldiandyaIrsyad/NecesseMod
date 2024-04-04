package necesse.inventory.item.armorItem.spiderite;

import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.ChestArmorItem;

public class SpideriteChestplateArmorItem extends ChestArmorItem {
   public SpideriteChestplateArmorItem() {
      super(29, 1400, Item.Rarity.EPIC, "spideritechestplate", "spideritearms");
   }

   public ArmorModifiers getArmorModifiers(InventoryItem var1, Mob var2) {
      return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.ATTACK_SPEED, 0.1F), new ModifierValue(BuffModifiers.STAMINA_REGEN, 0.3F)});
   }
}
