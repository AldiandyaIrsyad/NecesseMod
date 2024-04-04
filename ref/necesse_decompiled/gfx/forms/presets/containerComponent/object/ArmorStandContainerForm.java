package necesse.gfx.forms.presets.containerComponent.object;

import necesse.engine.network.client.Client;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.containerSlot.FormContainerArmorStandSlot;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.inventory.container.object.OEInventoryContainer;
import necesse.inventory.item.armorItem.ArmorItem;

public class ArmorStandContainerForm extends OEInventoryContainerForm<OEInventoryContainer> {
   public ArmorStandContainerForm(Client var1, OEInventoryContainer var2) {
      super(var1, var2);
   }

   protected void addSlots(FormFlow var1) {
      this.slots = new FormContainerSlot[((OEInventoryContainer)this.container).INVENTORY_END - ((OEInventoryContainer)this.container).INVENTORY_START + 1];
      if (this.slots.length != 3) {
         super.addSlots(var1);
      } else {
         int var2 = var1.next(40);
         this.slots[0] = (FormContainerSlot)this.inventoryForm.addComponent(new FormContainerArmorStandSlot(this.client, this.container, ((OEInventoryContainer)this.container).INVENTORY_START, 4, var2, ArmorItem.ArmorType.HEAD));
         this.slots[1] = (FormContainerSlot)this.inventoryForm.addComponent(new FormContainerArmorStandSlot(this.client, this.container, ((OEInventoryContainer)this.container).INVENTORY_START + 1, 44, var2, ArmorItem.ArmorType.CHEST));
         this.slots[2] = (FormContainerSlot)this.inventoryForm.addComponent(new FormContainerArmorStandSlot(this.client, this.container, ((OEInventoryContainer)this.container).INVENTORY_START + 2, 84, var2, ArmorItem.ArmorType.FEET));
      }

   }
}
