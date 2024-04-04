package necesse.gfx.forms.presets.containerComponent.object;

import necesse.engine.network.client.Client;
import necesse.entity.objectEntity.ProcessingInventoryObjectEntity;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.containerSlot.FormContainerProcessingRecipeSlot;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.inventory.container.object.OEInventoryContainer;

public class ProcessingInventoryContainerForm extends OEInventoryContainerForm<OEInventoryContainer> {
   private ProcessingInventoryObjectEntity processingObjectEntity;

   public ProcessingInventoryContainerForm(Client var1, OEInventoryContainer var2) {
      super(var1, var2);
      this.processingObjectEntity = (ProcessingInventoryObjectEntity)var2.objectEntity;
      this.inventoryForm.addComponent(new FormProcessingProgressArrow(this.inventoryForm.getWidth() / 2 - 16, 30 + (this.inventoryForm.getHeight() - 30) / 2 - 16, this.processingObjectEntity.getProcessingHelp()));
   }

   protected void addSlots(FormFlow var1) {
      this.slots = new FormContainerSlot[((OEInventoryContainer)this.container).INVENTORY_END - ((OEInventoryContainer)this.container).INVENTORY_START + 1];
      ProcessingInventoryObjectEntity var2 = (ProcessingInventoryObjectEntity)((OEInventoryContainer)this.container).objectEntity;
      int var3 = var2.inputSlots;
      ProcessingHelp var4 = var2.getProcessingHelp();
      int var5 = var1.next();
      byte var6 = 40;

      for(int var7 = 0; var7 < this.slots.length; ++var7) {
         int var8 = var7 + ((OEInventoryContainer)this.container).INVENTORY_START;
         int var9;
         int var10;
         int var11;
         if (var7 < var3) {
            var11 = var3 * 40;
            var9 = this.inventoryForm.getWidth() / 2 - var11 - var6 / 2 + var7 % 4 * 40;
            var10 = var7 / 4 * 40;
         } else {
            var11 = var7 - var3;
            var9 = this.inventoryForm.getWidth() / 2 + var6 / 2 + var11 % 4 * 40;
            var10 = var11 / 4 * 40;
         }

         this.slots[var7] = (FormContainerSlot)this.inventoryForm.addComponent(new FormContainerProcessingRecipeSlot(this.client, this.container, var8, var9, var5 + var10, var4));
         if (var1.next() < var5 + var10 + 40) {
            var1.next(var5 + var10 + 40 - var1.next());
         }
      }

   }
}
