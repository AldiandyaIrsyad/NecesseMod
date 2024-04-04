package necesse.inventory.item.armorItem.frost;

import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;
import necesse.inventory.item.upgradeUtils.FloatUpgradeValue;

public class FrostHatArmorItem extends SetHelmetArmorItem {
   public FloatUpgradeValue manaRegen = (new FloatUpgradeValue()).setBaseValue(0.5F).setUpgradedValue(1.0F, 1.5F);

   public FrostHatArmorItem() {
      super(5, DamageTypeRegistry.MAGIC, 500, Item.Rarity.COMMON, "frosthat", "frostchestplate", "frostboots", "frosthatsetbonus");
   }

   public ArmorModifiers getArmorModifiers(InventoryItem var1, Mob var2) {
      return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.MAGIC_DAMAGE, 0.1F), new ModifierValue(BuffModifiers.COMBAT_MANA_REGEN, this.manaRegen.getValue(this.getUpgradeTier(var1)))});
   }
}
