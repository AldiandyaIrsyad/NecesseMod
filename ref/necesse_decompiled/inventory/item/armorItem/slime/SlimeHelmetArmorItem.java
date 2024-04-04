package necesse.inventory.item.armorItem.slime;

import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;

public class SlimeHelmetArmorItem extends SetHelmetArmorItem {
   public SlimeHelmetArmorItem() {
      super(28, DamageTypeRegistry.MELEE, 1300, Item.Rarity.EPIC, "slimehelmet", "slimechestplate", "slimeboots", "slimehelmetsetbonus");
   }

   public ArmorModifiers getArmorModifiers(InventoryItem var1, Mob var2) {
      return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.MELEE_CRIT_CHANCE, 0.15F), new ModifierValue(BuffModifiers.MELEE_CRIT_DAMAGE, 0.15F)});
   }
}
