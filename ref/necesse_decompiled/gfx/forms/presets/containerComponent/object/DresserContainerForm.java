package necesse.gfx.forms.presets.containerComponent.object;

import necesse.engine.network.client.Client;
import necesse.entity.objectEntity.DresserObjectEntity;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.containerSlot.FormContainerArmorStandSlot;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.inventory.container.object.OEInventoryContainer;
import necesse.inventory.item.armorItem.ArmorItem;

public class DresserContainerForm extends OEInventoryContainerForm<OEInventoryContainer> {
   public DresserContainerForm(Client var1, OEInventoryContainer var2) {
      super(var1, var2);
   }

   protected void addSlots(FormFlow var1) {
      this.slots = new FormContainerSlot[((OEInventoryContainer)this.container).INVENTORY_END - ((OEInventoryContainer)this.container).INVENTORY_START + 1];
      int var2 = var1.next();

      for(int var3 = 0; var3 < this.slots.length; ++var3) {
         int var4 = var3 + ((OEInventoryContainer)this.container).INVENTORY_START;
         int var5 = var3 % 10;
         if (var5 == 0) {
            var2 = var1.next(40);
         }

         ArmorItem.ArmorType var6 = DresserObjectEntity.getArmorType(var3);
         if (var6 != null) {
            this.slots[var3] = (FormContainerSlot)this.inventoryForm.addComponent(new FormContainerArmorStandSlot(this.client, this.container, var4, 4 + var5 * 40, var2, var6));
         } else {
            this.slots[var3] = (FormContainerSlot)this.inventoryForm.addComponent(new FormContainerSlot(this.client, this.container, var4, 4 + var5 * 40, var2));
         }
      }

   }
}
