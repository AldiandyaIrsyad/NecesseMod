package necesse.inventory.item.armorItem.nightsteel;

import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;

public class NightsteelCircletArmorItem extends SetHelmetArmorItem {
   public NightsteelCircletArmorItem() {
      super(16, DamageTypeRegistry.SUMMON, 1400, Item.Rarity.EPIC, "nightsteelcirclet", "nightsteelchestplate", "nightsteelboots", "nightsteelcircletsetbonus");
   }

   public ArmorModifiers getArmorModifiers(InventoryItem var1, Mob var2) {
      return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.SUMMON_CRIT_CHANCE, 0.1F), new ModifierValue(BuffModifiers.SUMMON_CRIT_DAMAGE, 0.1F), new ModifierValue(BuffModifiers.MAX_SUMMONS, 1)});
   }
}
