package necesse.gfx.forms.presets.containerComponent.object;

import necesse.engine.network.client.Client;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.lists.FormContainerInventoryList;
import necesse.inventory.container.object.OEInventoryContainer;

public class OEInventoryListContainerForm<T extends OEInventoryContainer> extends OEInventoryContainerForm<T> {
   public OEInventoryListContainerForm(Client var1, T var2) {
      super(var1, var2);
   }

   protected void addSlots(FormFlow var1) {
      short var2 = 170;
      this.inventoryForm.addComponent(new FormContainerInventoryList(0, var1.next(var2), this.inventoryForm.getWidth(), var2, this.client, ((OEInventoryContainer)this.container).INVENTORY_START, ((OEInventoryContainer)this.container).INVENTORY_END));
   }
}
