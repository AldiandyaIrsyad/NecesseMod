package necesse.inventory.item.armorItem.gold;

import necesse.engine.registries.DamageTypeRegistry;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;

public class GoldHelmetArmorItem extends SetHelmetArmorItem {
   public GoldHelmetArmorItem() {
      super(6, DamageTypeRegistry.MELEE, 300, "goldhelmet", "goldchestplate", "goldboots", "goldsetbonus");
   }
}
