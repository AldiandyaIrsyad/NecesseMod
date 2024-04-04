package necesse.gfx.forms.presets.containerComponent.object;

import java.awt.Rectangle;
import java.util.Objects;
import java.util.function.Supplier;
import necesse.engine.Settings;
import necesse.engine.network.client.Client;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.FueledRefrigeratorObjectEntity;
import necesse.gfx.forms.position.FormRelativePosition;
import necesse.gfx.forms.presets.containerComponent.ContainerFormList;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.container.customAction.BooleanCustomAction;
import necesse.inventory.container.object.FueledRefrigeratorInventoryContainer;

public class FueledRefrigeratorInventoryContainerForm<T extends FueledRefrigeratorInventoryContainer> extends ContainerFormList<T> {
   protected OEInventoryContainerForm<T> inventoryContainerForm;
   protected FuelContainerForm fuelForm;

   public FueledRefrigeratorInventoryContainerForm(Client var1, T var2) {
      super(var1, var2);
      this.inventoryContainerForm = (OEInventoryContainerForm)this.addComponent(new OEInventoryContainerForm(var1, var2));
      int var10006 = var2.FUEL_START;
      int var10007 = var2.FUEL_END;
      GameTexture var10008 = Settings.UI.inventoryslot_icon_cooling_box_fuel;
      Supplier var10011 = () -> {
         return true;
      };
      FueledRefrigeratorObjectEntity var10012 = var2.refrigeratorObjectEntity;
      Objects.requireNonNull(var10012);
      this.fuelForm = (FuelContainerForm)this.addComponent(new FuelContainerForm(var1, var2, var10006, var10007, var10008, true, (BooleanCustomAction)null, var10011, var10012::getFuelProgress));
      this.fuelForm.setPosition(new FormRelativePosition(this.inventoryContainerForm.inventoryForm, -this.fuelForm.getWidth() - Settings.UI.formSpacing, -Math.max(0, this.fuelForm.getHeight() - this.inventoryContainerForm.inventoryForm.getHeight())));
   }

   public boolean shouldOpenInventory() {
      return true;
   }

   public void onWindowResized() {
      super.onWindowResized();
      this.inventoryContainerForm.onWindowResized();
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      this.fuelForm.setHidden(!this.inventoryContainerForm.isCurrent(this.inventoryContainerForm.inventoryForm));
      super.draw(var1, var2, var3);
   }
}
