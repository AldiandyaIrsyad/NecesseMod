package necesse.inventory.item.armorItem.tungsten;

import necesse.engine.registries.DamageTypeRegistry;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;

public class TungstenHelmetArmorItem extends SetHelmetArmorItem {
   public TungstenHelmetArmorItem() {
      super(19, DamageTypeRegistry.MELEE, 1000, Item.Rarity.UNCOMMON, "tungstenhelmet", "tungstenchestplate", "tungstenboots", "tungstensetbonus");
   }
}
