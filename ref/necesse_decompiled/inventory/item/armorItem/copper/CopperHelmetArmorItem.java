package necesse.inventory.item.armorItem.copper;

import necesse.engine.registries.DamageTypeRegistry;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;

public class CopperHelmetArmorItem extends SetHelmetArmorItem {
   public CopperHelmetArmorItem() {
      super(4, DamageTypeRegistry.MELEE, 200, "copperhelmet", "copperchestplate", "copperboots", "coppersetbonus");
   }
}
