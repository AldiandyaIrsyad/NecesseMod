package necesse.inventory.item.armorItem.ravenlords;

import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.gameDamageType.DamageType;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;

public class RavenlordsHeaddressArmorItem extends SetHelmetArmorItem {
   public RavenlordsHeaddressArmorItem() {
      super(23, (DamageType)null, 1400, Item.Rarity.EPIC, "ravenlordsheaddress", "ravenlordschestplate", "ravenlordsboots", "ravenlordsheaddresssetbonus");
   }

   public ArmorModifiers getArmorModifiers(InventoryItem var1, Mob var2) {
      return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.SPEED, 0.35F), new ModifierValue(BuffModifiers.CRIT_CHANCE, 0.1F)});
   }
}
