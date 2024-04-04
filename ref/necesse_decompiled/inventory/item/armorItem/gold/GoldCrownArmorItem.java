package necesse.inventory.item.armorItem.gold;

import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;

public class GoldCrownArmorItem extends SetHelmetArmorItem {
   public GoldCrownArmorItem() {
      super(2, DamageTypeRegistry.SUMMON, 400, Item.Rarity.COMMON, "goldcrown", "goldchestplate", "goldboots", "goldcrownsetbonus");
   }

   public ArmorModifiers getArmorModifiers(InventoryItem var1, Mob var2) {
      return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_SUMMONS, 1)});
   }
}
