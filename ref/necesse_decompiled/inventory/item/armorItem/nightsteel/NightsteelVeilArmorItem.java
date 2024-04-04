package necesse.inventory.item.armorItem.nightsteel;

import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;
import necesse.inventory.item.upgradeUtils.FloatUpgradeValue;

public class NightsteelVeilArmorItem extends SetHelmetArmorItem {
   public FloatUpgradeValue manaRegen = (new FloatUpgradeValue()).setBaseValue(1.5F).setUpgradedValue(1.0F, 1.5F);

   public NightsteelVeilArmorItem() {
      super(21, DamageTypeRegistry.MAGIC, 1400, Item.Rarity.EPIC, "nightsteelveil", "nightsteelchestplate", "nightsteelboots", "nightsteelveilsetbonus");
      this.hairDrawOptions = ArmorItem.HairDrawMode.OVER_HAIR;
   }

   public ArmorModifiers getArmorModifiers(InventoryItem var1, Mob var2) {
      return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.MAGIC_CRIT_CHANCE, 0.1F), new ModifierValue(BuffModifiers.MAGIC_CRIT_DAMAGE, 0.1F), new ModifierValue(BuffModifiers.COMBAT_MANA_REGEN, this.manaRegen.getValue(this.getUpgradeTier(var1)))});
   }
}
