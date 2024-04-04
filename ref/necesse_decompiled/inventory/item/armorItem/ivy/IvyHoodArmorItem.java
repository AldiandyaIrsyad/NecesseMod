package necesse.inventory.item.armorItem.ivy;

import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;

public class IvyHoodArmorItem extends SetHelmetArmorItem {
   public IvyHoodArmorItem() {
      super(11, DamageTypeRegistry.RANGED, 800, Item.Rarity.UNCOMMON, "ivyhood", "ivychestplate", "ivyboots", "ivyhoodsetbonus");
   }

   public ArmorModifiers getArmorModifiers(InventoryItem var1, Mob var2) {
      return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.RANGED_ATTACK_SPEED, 0.1F), new ModifierValue(BuffModifiers.PROJECTILE_VELOCITY, 0.2F)});
   }
}
