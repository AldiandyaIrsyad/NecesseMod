package necesse.inventory.item.armorItem.ivy;

import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;

public class IvyHelmetArmorItem extends SetHelmetArmorItem {
   public IvyHelmetArmorItem() {
      super(16, DamageTypeRegistry.MELEE, 800, Item.Rarity.UNCOMMON, "ivyhelmet", "ivychestplate", "ivyboots", "ivyhelmetsetbonus");
   }

   public ArmorModifiers getArmorModifiers(InventoryItem var1, Mob var2) {
      return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.MELEE_ATTACK_SPEED, 0.1F), new ModifierValue(BuffModifiers.CRIT_CHANCE, 0.1F)});
   }
}
