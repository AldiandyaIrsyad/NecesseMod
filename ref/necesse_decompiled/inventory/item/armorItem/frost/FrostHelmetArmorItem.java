package necesse.inventory.item.armorItem.frost;

import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;

public class FrostHelmetArmorItem extends SetHelmetArmorItem {
   public FrostHelmetArmorItem() {
      super(7, DamageTypeRegistry.MELEE, 500, Item.Rarity.COMMON, "frosthelmet", "frostchestplate", "frostboots", "frostsetbonus");
   }

   public ArmorModifiers getArmorModifiers(InventoryItem var1, Mob var2) {
      return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.MELEE_DAMAGE, 0.1F)});
   }
}
