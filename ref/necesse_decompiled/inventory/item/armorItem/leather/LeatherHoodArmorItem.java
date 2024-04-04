package necesse.inventory.item.armorItem.leather;

import necesse.engine.registries.DamageTypeRegistry;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;

public class LeatherHoodArmorItem extends SetHelmetArmorItem {
   public LeatherHoodArmorItem() {
      super(3, DamageTypeRegistry.RANGED, 150, Item.Rarity.NORMAL, "leatherhood", "leathershirt", "leatherboots", "leathersetbonus");
   }
}
