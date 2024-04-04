package necesse.inventory.item.armorItem.glacial;

import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;

public class GlacialHelmetArmorItem extends SetHelmetArmorItem {
   public GlacialHelmetArmorItem() {
      super(20, DamageTypeRegistry.MELEE, 1200, Item.Rarity.UNCOMMON, "glacialhelmet", "glacialchestplate", "glacialboots", "glacialhelmetsetbonus");
   }

   public ArmorModifiers getArmorModifiers(InventoryItem var1, Mob var2) {
      return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.MELEE_ATTACK_SPEED, 0.1F), new ModifierValue(BuffModifiers.CRIT_CHANCE, 0.1F)});
   }
}
