package necesse.inventory.item.armorItem.demonic;

import necesse.engine.registries.DamageTypeRegistry;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;

public class DemonicHelmetArmorItem extends SetHelmetArmorItem {
   public DemonicHelmetArmorItem() {
      super(11, DamageTypeRegistry.MELEE, 600, Item.Rarity.COMMON, "demonichelmet", "demonicchestplate", "demonicboots", "demonicsetbonus");
   }
}
