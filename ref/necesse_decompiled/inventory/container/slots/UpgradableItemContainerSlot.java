package necesse.inventory.container.slots;

import necesse.engine.localization.Localization;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.upgradeUtils.UpgradableItem;

public class UpgradableItemContainerSlot extends ContainerSlot {
   public UpgradableItemContainerSlot(Inventory var1, int var2) {
      super(var1, var2);
   }

   public String getItemInvalidError(InventoryItem var1) {
      if (var1 == null) {
         return null;
      } else {
         return var1.item instanceof UpgradableItem ? ((UpgradableItem)var1.item).getCanBeUpgradedError(var1) : Localization.translate("ui", "itemnotupgradable");
      }
   }
}
