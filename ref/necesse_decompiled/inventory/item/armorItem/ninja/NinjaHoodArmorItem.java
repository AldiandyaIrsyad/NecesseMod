package necesse.inventory.item.armorItem.ninja;

import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.gameDamageType.DamageType;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;

public class NinjaHoodArmorItem extends SetHelmetArmorItem {
   public NinjaHoodArmorItem() {
      super(15, (DamageType)null, 1200, Item.Rarity.RARE, "ninjahood", "ninjarobe", "ninjashoes", "ninjasetbonus");
   }

   public ArmorModifiers getArmorModifiers(InventoryItem var1, Mob var2) {
      return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.CRIT_DAMAGE, 0.25F)});
   }
}
