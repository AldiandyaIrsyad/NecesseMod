package necesse.gfx.forms.presets.containerComponent.object;

import java.awt.Rectangle;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormCustomDraw;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.components.localComponents.FormLocalCheckBox;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.position.FormRelativePosition;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.container.object.FueledCraftingStationContainer;

public class FueledCraftingStationContainerForm<T extends FueledCraftingStationContainer> extends CraftingStationContainerForm<T> {
   protected Form fuelForm;
   private FormLocalCheckBox keepRunningCheckbox;

   public FueledCraftingStationContainerForm(Client var1, final T var2) {
      super(var1, var2);
      byte var3 = 2;
      int var4 = var2.INVENTORY_END - var2.INVENTORY_START + 1;
      int var5 = (var4 + var3 - 1) / var3;
      this.fuelForm = new Form(120, var5 * 40 + 60);
      if (!var2.objectEntity.alwaysOn) {
         this.keepRunningCheckbox = (FormLocalCheckBox)this.fuelForm.addComponent(new FormLocalCheckBox("ui", "fuelkeeprunning", 5, this.fuelForm.getHeight() - 10, var2.objectEntity.keepRunning, this.fuelForm.getWidth() - 10));
         this.keepRunningCheckbox.onClicked((var1x) -> {
            var2.setKeepRunning.runAndSend(((FormCheckBox)var1x.from).checked);
         });
         Rectangle var6 = this.keepRunningCheckbox.getBoundingBox();
         this.keepRunningCheckbox.setPosition(this.fuelForm.getWidth() / 2 - var6.width / 2, this.keepRunningCheckbox.getY());
         this.fuelForm.setHeight(this.fuelForm.getHeight() + var6.height);
      }

      this.fuelForm.setPosition(new FormRelativePosition(this.craftingForm, -this.fuelForm.getWidth() - Settings.UI.formSpacing, 0));
      this.fuelForm.addComponent(new FormLocalLabel("ui", "fuel", new FontOptions(16), 0, this.fuelForm.getWidth() / 2, 5));
      this.fuelForm.addComponent(new FormCustomDraw(this.fuelForm.getWidth() / 2 - 40, 26, 80, Settings.UI.progressbar_small_empty.getHeight()) {
         public void draw(TickManager var1, PlayerMob var2x, Rectangle var3) {
            FormComponent.drawWidthComponent(new GameSprite(Settings.UI.progressbar_small_empty, 0, 0, Settings.UI.progressbar_small_empty.getHeight()), new GameSprite(Settings.UI.progressbar_small_empty, 1, 0, Settings.UI.progressbar_small_empty.getHeight()), this.getX(), this.getY(), this.width);
            float var4 = 0.0F;
            if (var2.objectEntity.isFueled()) {
               var4 = var2.objectEntity.getFuelProgressLeft();
               int var5 = (int)(var4 * (float)this.width);
               FormComponent.drawWidthComponent(new GameSprite(Settings.UI.progressbar_small_full, 0, 0, Settings.UI.progressbar_small_full.getHeight()), new GameSprite(Settings.UI.progressbar_small_full, 1, 0, Settings.UI.progressbar_small_full.getHeight()), this.getX(), this.getY(), var5);
            }

            if (this.isHovering()) {
               Screen.addTooltip(new StringTooltips((int)(var4 * 100.0F) + "%"), TooltipLocation.FORM_FOCUS);
            }

         }
      });

      for(int var12 = var2.INVENTORY_START; var12 <= var2.INVENTORY_END; ++var12) {
         int var7 = var12 - var2.INVENTORY_START;
         int var8 = var7 % var3;
         int var9 = var7 / var3;
         int var10 = Math.min(var4 - var9 * var3, var3);
         int var11 = var10 * 20;
         ((FormContainerSlot)this.fuelForm.addComponent(new FormContainerSlot(var1, var2, var12, this.fuelForm.getWidth() / 2 + var8 * 40 - var11, var9 * 40 + 40))).setDecal(Settings.UI.inventoryslot_icon_fuel);
      }

   }

   protected void init() {
      super.init();
      this.getManager().addComponent(this.fuelForm);
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      this.fuelForm.setHidden(!this.isCurrent(this.craftingForm));
      this.craftList.usableError = !((FueledCraftingStationContainer)this.container).objectEntity.isFueled() && !((FueledCraftingStationContainer)this.container).objectEntity.canFuel() ? new LocalMessage("ui", "needfuel") : null;
      if (this.keepRunningCheckbox != null && this.keepRunningCheckbox.checked != ((FueledCraftingStationContainer)this.container).objectEntity.keepRunning) {
         this.keepRunningCheckbox.checked = ((FueledCraftingStationContainer)this.container).objectEntity.keepRunning;
      }

      super.draw(var1, var2, var3);
   }

   public void dispose() {
      super.dispose();
      if (this.fuelForm != null) {
         this.getManager().removeComponent(this.fuelForm);
         this.fuelForm = null;
      }

   }
}
