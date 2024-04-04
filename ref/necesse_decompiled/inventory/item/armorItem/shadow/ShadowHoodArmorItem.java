package necesse.inventory.item.armorItem.shadow;

import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;

public class ShadowHoodArmorItem extends SetHelmetArmorItem {
   public ShadowHoodArmorItem() {
      super(16, DamageTypeRegistry.RANGED, 1100, Item.Rarity.UNCOMMON, "shadowhood", "shadowmantle", "shadowboots", "shadowhoodsetbonus");
   }

   public ArmorModifiers getArmorModifiers(InventoryItem var1, Mob var2) {
      return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.RANGED_ATTACK_SPEED, 0.05F)});
   }
}
