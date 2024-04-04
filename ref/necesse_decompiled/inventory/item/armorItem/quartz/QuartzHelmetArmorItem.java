package necesse.inventory.item.armorItem.quartz;

import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;

public class QuartzHelmetArmorItem extends SetHelmetArmorItem {
   public QuartzHelmetArmorItem() {
      super(10, DamageTypeRegistry.MELEE, 900, Item.Rarity.UNCOMMON, "quartzhelmet", "quartzchestplate", "quartzboots", "quartzhelmetsetbonus");
   }

   public ArmorModifiers getArmorModifiers(InventoryItem var1, Mob var2) {
      return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.ATTACK_SPEED, 0.1F), new ModifierValue(BuffModifiers.CRIT_CHANCE, 0.05F)});
   }
}
