package necesse.inventory.item.armorItem.ancientFossil;

import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.gameDamageType.DamageType;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;

public class AncientFossilHelmetArmorItem extends SetHelmetArmorItem {
   public AncientFossilHelmetArmorItem() {
      super(16, (DamageType)null, 1300, Item.Rarity.UNCOMMON, "ancientfossilhelmet", "ancientfossilchestplate", "ancientfossilboots", "ancientfossilhelmetsetbonus");
   }

   public ArmorModifiers getArmorModifiers(InventoryItem var1, Mob var2) {
      return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.ATTACK_SPEED, 0.15F)});
   }
}
