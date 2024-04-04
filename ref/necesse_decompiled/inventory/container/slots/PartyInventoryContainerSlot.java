package necesse.inventory.container.slots;

import necesse.engine.localization.Localization;
import necesse.engine.network.server.AdventureParty;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.placeableItem.consumableItem.AdventurePartyConsumableItem;

public class PartyInventoryContainerSlot extends ContainerSlot {
   public AdventureParty party;

   public PartyInventoryContainerSlot(Inventory var1, int var2, AdventureParty var3) {
      super(var1, var2);
      this.party = var3;
   }

   public String getItemInvalidError(InventoryItem var1) {
      if (var1 == null) {
         return null;
      } else if (this.party != null && this.party.isEmpty()) {
         return Localization.translate("ui", "adventurepartycantaddempty");
      } else {
         return var1.item instanceof AdventurePartyConsumableItem && ((AdventurePartyConsumableItem)var1.item).canAddToPartyInventory(var1, this.getContainer().client, this.getInventory(), this.getInventorySlot()) ? null : "";
      }
   }
}
