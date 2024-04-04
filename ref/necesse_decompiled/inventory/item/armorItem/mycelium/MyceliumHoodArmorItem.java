package necesse.inventory.item.armorItem.mycelium;

import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;

public class MyceliumHoodArmorItem extends SetHelmetArmorItem {
   public MyceliumHoodArmorItem() {
      super(20, DamageTypeRegistry.RANGED, 1250, Item.Rarity.UNCOMMON, "myceliumhood", "myceliumchestplate", "myceliumboots", "myceliumhoodsetbonus");
   }

   public ArmorModifiers getArmorModifiers(InventoryItem var1, Mob var2) {
      return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.RANGED_ATTACK_SPEED, 0.05F), new ModifierValue(BuffModifiers.CRIT_CHANCE, 0.05F)});
   }
}
