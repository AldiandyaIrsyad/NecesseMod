package necesse.inventory.item.armorItem.spider;

import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;

public class SpiderHelmetArmorItem extends SetHelmetArmorItem {
   public SpiderHelmetArmorItem() {
      super(4, DamageTypeRegistry.SUMMON, 400, Item.Rarity.UNCOMMON, "spiderhelmet", "spiderchestplate", "spiderboots", "spidersetbonus");
   }

   public ArmorModifiers getArmorModifiers(InventoryItem var1, Mob var2) {
      return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_SUMMONS, 1)});
   }
}
