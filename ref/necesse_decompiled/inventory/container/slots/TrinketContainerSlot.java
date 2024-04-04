package necesse.inventory.container.slots;

import necesse.engine.localization.Localization;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

public class TrinketContainerSlot extends ContainerSlot {
   public TrinketContainerSlot(Inventory var1, int var2) {
      super(var1, var2);
   }

   public String getItemInvalidError(InventoryItem var1) {
      if (var1 == null) {
         return null;
      } else if (var1.item.isTrinketItem()) {
         TrinketItem var2 = (TrinketItem)var1.item;
         String var3 = var2.getInvalidInSlotError(this.getContainer(), this, var1);
         if (var3 != null) {
            return var3;
         } else {
            if (var2.isAbilityTrinket(var1)) {
               ContainerSlot var4 = this.getContainer().getSlot(this.getContainer().CLIENT_TRINKET_ABILITY_SLOT);
               if (var4.isClear()) {
                  return Localization.translate("itemtooltip", "useabilityslot");
               }
            }

            return null;
         }
      } else {
         return "";
      }
   }

   public int getItemStackLimit(InventoryItem var1) {
      return 1;
   }
}
