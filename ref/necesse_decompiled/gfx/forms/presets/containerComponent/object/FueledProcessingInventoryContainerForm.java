package necesse.gfx.forms.presets.containerComponent.object;

import java.awt.Rectangle;
import java.util.Objects;
import java.util.function.Supplier;
import necesse.engine.Settings;
import necesse.engine.network.client.Client;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.FueledProcessingInventoryObjectEntity;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.containerSlot.FormContainerProcessingRecipeSlot;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.position.FormRelativePosition;
import necesse.gfx.forms.presets.containerComponent.ContainerFormList;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.container.customAction.BooleanCustomAction;
import necesse.inventory.container.object.FueledProcessingOEInventoryContainer;

public class FueledProcessingInventoryContainerForm extends ContainerFormList<FueledProcessingOEInventoryContainer> {
   protected OEInventoryContainerForm<FueledProcessingOEInventoryContainer> containerForm;
   protected FuelContainerForm fuelForm;

   public FueledProcessingInventoryContainerForm(Client var1, FueledProcessingOEInventoryContainer var2) {
      super(var1, var2);
      final int var3 = var2.fueledProcessingObjectEntity.fuelSlots;
      final int var4 = var2.fueledProcessingObjectEntity.inputSlots;
      this.containerForm = (OEInventoryContainerForm)this.addComponent(new OEInventoryContainerForm<FueledProcessingOEInventoryContainer>(var1, var2) {
         protected void addSlots(FormFlow var1) {
            this.slots = new FormContainerSlot[((FueledProcessingOEInventoryContainer)this.container).INVENTORY_END - ((FueledProcessingOEInventoryContainer)this.container).INVENTORY_START + 1 - var3];
            ProcessingHelp var2 = ((FueledProcessingOEInventoryContainer)this.container).fueledProcessingObjectEntity.getProcessingHelp();
            int var3x = var1.next();
            byte var4x = 40;

            for(int var5 = 0; var5 < this.slots.length; ++var5) {
               int var6 = var5 + ((FueledProcessingOEInventoryContainer)this.container).INVENTORY_START + var3;
               int var7;
               int var8;
               int var9;
               if (var5 < var4) {
                  var9 = var4 * 40;
                  var7 = this.inventoryForm.getWidth() / 2 - var9 - var4x / 2 + var5 % 4 * 40;
                  var8 = var5 / 4 * 40;
               } else {
                  var9 = var5 - var4;
                  var7 = this.inventoryForm.getWidth() / 2 + var4x / 2 + var9 % 4 * 40;
                  var8 = var9 / 4 * 40;
               }

               this.slots[var5] = (FormContainerSlot)this.inventoryForm.addComponent(new FormContainerProcessingRecipeSlot(this.client, this.container, var6, var7, var3x + var8, var2));
               if (var1.next() < var3x + var8 + 40) {
                  var1.next(var3x + var8 + 40 - var1.next());
               }
            }

         }
      });
      this.containerForm.inventoryForm.addComponent(new FormProcessingProgressArrow(this.containerForm.inventoryForm.getWidth() / 2 - 16, 30 + (this.containerForm.inventoryForm.getHeight() - 30) / 2 - 16, var2.fueledProcessingObjectEntity.getProcessingHelp()));
      int var10006 = var2.INVENTORY_START;
      int var10007 = var2.INVENTORY_START + var3 - 1;
      GameTexture var10008 = Settings.UI.inventoryslot_icon_fuel;
      boolean var10009 = var2.fueledProcessingObjectEntity.fuelAlwaysOn;
      BooleanCustomAction var10010 = var2.fueledProcessingObjectEntity.shouldBeAbleToChangeKeepFuelRunning() ? var2.setKeepRunning : null;
      FueledProcessingInventoryObjectEntity var10011 = var2.fueledProcessingObjectEntity;
      Objects.requireNonNull(var10011);
      Supplier var5 = var10011::shouldKeepFuelRunning;
      FueledProcessingInventoryObjectEntity var10012 = var2.fueledProcessingObjectEntity;
      Objects.requireNonNull(var10012);
      this.fuelForm = (FuelContainerForm)this.addComponent(new FuelContainerForm(var1, var2, var10006, var10007, var10008, var10009, var10010, var5, var10012::getFuelProgress));
      this.fuelForm.setPosition(new FormRelativePosition(this.containerForm.inventoryForm, -this.fuelForm.getWidth() - Settings.UI.formSpacing, -Math.max(0, this.fuelForm.getHeight() - this.containerForm.inventoryForm.getHeight())));
   }

   public boolean shouldOpenInventory() {
      return true;
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      this.fuelForm.setHidden(!this.containerForm.isCurrent(this.containerForm.inventoryForm));
      super.draw(var1, var2, var3);
   }
}
