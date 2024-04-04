package necesse.gfx.forms.presets.containerComponent.item;

import necesse.engine.Settings;
import necesse.engine.network.client.Client;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.container.item.ItemInventoryContainer;

public class PortableMusicPlayerContainerForm extends ItemInventoryContainerForm<ItemInventoryContainer> {
   public PortableMusicPlayerContainerForm(Client var1, ItemInventoryContainer var2) {
      super(var1, var2);
   }

   protected void addSlots(FormFlow var1) {
      this.slots = new FormContainerSlot[((ItemInventoryContainer)this.container).INVENTORY_END - ((ItemInventoryContainer)this.container).INVENTORY_START + 1];
      if (this.slots.length != 1) {
         super.addSlots(var1);
      } else {
         int var2 = var1.next(40);
         FormLocalLabel var3 = (FormLocalLabel)this.addComponent(new FormLocalLabel("ui", "insertvinyl", new FontOptions(16), -1, 5, var2 + 10));
         FormContainerSlot var4 = (FormContainerSlot)this.addComponent(new FormContainerSlot(this.client, this.container, ((ItemInventoryContainer)this.container).INVENTORY_START, var3.getBoundingBox().width + 10, var2));
         var4.setDecal(Settings.UI.inventoryslot_icon_vinyl);
         this.slots[0] = var4;
      }

   }
}
