package necesse.inventory.item.armorItem.iron;

import necesse.engine.registries.DamageTypeRegistry;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;

public class IronHelmetArmorItem extends SetHelmetArmorItem {
   public IronHelmetArmorItem() {
      super(5, DamageTypeRegistry.MELEE, 250, "ironhelmet", "ironchestplate", "ironboots", "ironsetbonus");
   }
}
