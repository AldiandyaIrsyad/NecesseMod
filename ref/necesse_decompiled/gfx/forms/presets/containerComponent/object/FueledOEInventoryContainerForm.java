package necesse.gfx.forms.presets.containerComponent.object;

import java.awt.Rectangle;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.network.client.Client;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormCustomDraw;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.components.localComponents.FormLocalCheckBox;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.presets.containerComponent.ContainerFormSwitcher;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementObjectStatusFormManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.container.object.FueledOEInventoryContainer;

public class FueledOEInventoryContainerForm<T extends FueledOEInventoryContainer> extends ContainerFormSwitcher<T> {
   private Form inventoryForm = (Form)this.addComponent(new Form("inventoryForm", 120, 100));
   private FormLocalCheckBox keepRunningCheckbox;
   public SettlementObjectStatusFormManager settlementObjectFormManager;

   public FueledOEInventoryContainerForm(Client var1, final T var2) {
      super(var1, var2);
      byte var3 = 2;
      int var4 = var2.INVENTORY_END - var2.INVENTORY_START + 1;
      int var5 = (var4 + var3 - 1) / var3;
      this.inventoryForm.setHeight(var5 * 40 + 70);
      FormLocalLabel var6 = (FormLocalLabel)this.inventoryForm.addComponent(new FormLocalLabel("ui", "fuel", new FontOptions(20), -1, 4, 6));
      Rectangle var7 = var6.getBoundingBox();
      if (var7.width > this.inventoryForm.getWidth() - 8 - 28) {
         this.inventoryForm.setWidth(var7.width + 8 + 28);
      } else {
         var6.setX((this.inventoryForm.getWidth() - 8 - 28 - var7.width) / 2);
      }

      if (!var2.objectEntity.alwaysOn) {
         this.keepRunningCheckbox = (FormLocalCheckBox)this.inventoryForm.addComponent(new FormLocalCheckBox("ui", "fuelkeeprunning", 5, this.inventoryForm.getHeight() - 10, var2.objectEntity.keepRunning, this.inventoryForm.getWidth() - 10));
         this.keepRunningCheckbox.onClicked((var1x) -> {
            var2.setKeepRunning.runAndSend(((FormCheckBox)var1x.from).checked);
         });
         Rectangle var8 = this.keepRunningCheckbox.getBoundingBox();
         this.keepRunningCheckbox.setPosition(this.inventoryForm.getWidth() / 2 - var8.width / 2, this.keepRunningCheckbox.getY());
         this.inventoryForm.setHeight(this.inventoryForm.getHeight() + var8.height);
      }

      this.inventoryForm.addComponent(new FormCustomDraw(this.inventoryForm.getWidth() / 2 - 40, 36, 80, Settings.UI.progressbar_small_empty.getHeight()) {
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

      for(int var14 = var2.INVENTORY_START; var14 <= var2.INVENTORY_END; ++var14) {
         int var9 = var14 - var2.INVENTORY_START;
         int var10 = var9 % var3;
         int var11 = var9 / var3;
         int var12 = Math.min(var4 - var11 * var3, var3);
         int var13 = var12 * 20;
         ((FormContainerSlot)this.inventoryForm.addComponent(new FormContainerSlot(var1, var2, var14, this.inventoryForm.getWidth() / 2 + var10 * 40 - var13, var11 * 40 + 50))).setDecal(Settings.UI.inventoryslot_icon_fuel);
      }

      this.settlementObjectFormManager = var2.settlementObjectManager.getFormManager(this, this.inventoryForm, var1);
      this.settlementObjectFormManager.addConfigButtonRow(this.inventoryForm, new FormFlow(this.inventoryForm.getWidth() - 4), 4, -1);
      this.makeCurrent(this.inventoryForm);
   }

   public boolean shouldOpenInventory() {
      return true;
   }

   public void onWindowResized() {
      super.onWindowResized();
      ContainerComponent.setPosFocus(this.inventoryForm);
      this.settlementObjectFormManager.onWindowResized();
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      this.settlementObjectFormManager.updateButtons();
      if (this.keepRunningCheckbox != null && this.keepRunningCheckbox.checked != ((FueledOEInventoryContainer)this.container).objectEntity.keepRunning) {
         this.keepRunningCheckbox.checked = ((FueledOEInventoryContainer)this.container).objectEntity.keepRunning;
      }

      super.draw(var1, var2, var3);
   }
}
