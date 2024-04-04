package necesse.gfx.forms.presets.containerComponent.object;

import java.awt.Rectangle;
import java.util.Objects;
import java.util.function.Supplier;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.network.client.Client;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.FueledIncineratorObjectEntity;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormCustomDraw;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.position.FormRelativePosition;
import necesse.gfx.forms.presets.containerComponent.ContainerFormList;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.container.customAction.BooleanCustomAction;
import necesse.inventory.container.object.FueledIncineratorInventoryContainer;

public class FueledIncineratorInventoryContainerForm<T extends FueledIncineratorInventoryContainer> extends ContainerFormList<T> {
   protected OEInventoryContainerForm<T> inventoryContainerForm;
   protected FuelContainerForm fuelForm;

   public FueledIncineratorInventoryContainerForm(Client var1, T var2) {
      super(var1, var2);
      this.inventoryContainerForm = (OEInventoryContainerForm)this.addComponent(new OEInventoryContainerForm<T>(var1, var2) {
         protected void addSlots(FormFlow var1) {
            super.addSlots(var1);
            var1.next(8);
            this.inventoryForm.addComponent((<undefinedtype>)var1.nextY(new FormCustomDraw(6, 0, this.inventoryForm.getWidth() - 12, Settings.UI.progressbar_small_empty.getHeight()) {
               public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
                  FormComponent.drawWidthComponent(new GameSprite(Settings.UI.progressbar_small_empty, 0, 0, Settings.UI.progressbar_small_empty.getHeight()), new GameSprite(Settings.UI.progressbar_small_empty, 1, 0, Settings.UI.progressbar_small_empty.getHeight()), this.getX(), this.getY(), this.width);
                  float var4 = ((FueledIncineratorInventoryContainer)container).incineratorObjectEntity.getProcessingProgress();
                  int var5 = (int)(var4 * (float)this.width);
                  FormComponent.drawWidthComponent(new GameSprite(Settings.UI.progressbar_small_full, 0, 0, Settings.UI.progressbar_small_full.getHeight()), new GameSprite(Settings.UI.progressbar_small_full, 1, 0, Settings.UI.progressbar_small_full.getHeight()), this.getX(), this.getY(), var5);
                  if (this.isHovering()) {
                     Screen.addTooltip(new StringTooltips((int)(var4 * 100.0F) + "%"), TooltipLocation.FORM_FOCUS);
                  }

               }
            }, 8));
         }
      });
      int var10006 = var2.FUEL_START;
      int var10007 = var2.FUEL_END;
      GameTexture var10008 = Settings.UI.inventoryslot_icon_fuel;
      Supplier var10011 = () -> {
         return true;
      };
      FueledIncineratorObjectEntity var10012 = var2.incineratorObjectEntity;
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
