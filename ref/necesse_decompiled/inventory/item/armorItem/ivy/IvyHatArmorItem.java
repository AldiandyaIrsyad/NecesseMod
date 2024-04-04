package necesse.inventory.item.armorItem.ivy;

import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;
import necesse.inventory.item.upgradeUtils.FloatUpgradeValue;

public class IvyHatArmorItem extends SetHelmetArmorItem {
   public FloatUpgradeValue manaRegen = (new FloatUpgradeValue()).setBaseValue(1.0F).setUpgradedValue(1.0F, 1.5F);

   public IvyHatArmorItem() {
      super(10, DamageTypeRegistry.MAGIC, 800, Item.Rarity.UNCOMMON, "ivyhat", "ivychestplate", "ivyboots", "ivyhatsetbonus");
   }

   public ArmorModifiers getArmorModifiers(InventoryItem var1, Mob var2) {
      return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.MAGIC_ATTACK_SPEED, 0.15F), new ModifierValue(BuffModifiers.COMBAT_MANA_REGEN, this.manaRegen.getValue(this.getUpgradeTier(var1)))});
   }
}
