package necesse.gfx.forms.presets.containerComponent.object;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import necesse.engine.Settings;
import necesse.engine.network.client.Client;
import necesse.gfx.fairType.FairItemGlyph;
import necesse.gfx.fairType.FairSpacerGlyph;
import necesse.gfx.fairType.FairType;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormFairTypeLabel;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.containerComponent.ContainerFormSwitcher;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryUpdateListener;
import necesse.inventory.container.object.SalvageStationContainer;

public class SalvageStationContainerForm<T extends SalvageStationContainer> extends ContainerFormSwitcher<T> {
   public Form mainForm = (Form)this.addComponent(new Form(408, 120));
   public int rewardsStartY;
   public Collection<FormComponent> rewardComponents;
   public FormLocalTextButton salvageButton;
   private InventoryUpdateListener inventoryUpdateListener;

   public SalvageStationContainerForm(Client var1, T var2) {
      super(var1, var2);
      FormFlow var3 = new FormFlow(5);
      this.mainForm.addComponent((FormLocalLabel)var3.nextY(new FormLocalLabel(var2.salvageEntity.getInventoryName(), new FontOptions(20), 0, this.mainForm.getWidth() / 2, 0, this.mainForm.getWidth() - 20), 5));
      this.mainForm.addComponent((FormLocalLabel)var3.nextY(new FormLocalLabel("ui", "salvageplaceitems", new FontOptions(16), 0, this.mainForm.getWidth() / 2, 0, this.mainForm.getWidth() - 20), 10));
      byte var4 = 40;
      byte var5 = 4;
      int var6 = this.mainForm.getWidth() - var5 * 2;
      int var7 = Math.max(var6 / var4, 1);
      int var8 = var2.salvageEntity.inventory.getSize();
      int var9 = 1 + var8 / var7;
      if (var8 % var7 == 0) {
         --var9;
      }

      int var10;
      for(var10 = 0; var10 < var9; ++var10) {
         int var11 = var3.next(40);
         int var12 = Math.min(var8 - var10 * var7, var7);
         int var13 = this.mainForm.getWidth() / 2 - var4 * var12 / 2;

         for(int var14 = 0; var14 < var12; ++var14) {
            int var15 = var10 * var7 + var14;
            FormContainerSlot var16 = (new FormContainerSlot(var1, var2, var2.SALVAGE_INVENTORY_START + var15, var13 + var14 * var4, var11)).setDecal(Settings.UI.inventoryslot_icon_trinket);
            this.mainForm.addComponent(var16);
         }
      }

      var3.next(10);
      this.mainForm.addComponent((FormLocalLabel)var3.nextY(new FormLocalLabel("ui", "salvageresults", new FontOptions(16), 0, this.mainForm.getWidth() / 2, 0, this.mainForm.getWidth() - 20), 2));
      this.rewardsStartY = var3.next();
      var10 = Math.min(this.mainForm.getWidth() - 8, 300);
      this.salvageButton = (FormLocalTextButton)this.mainForm.addComponent(new FormLocalTextButton("ui", "salvagebutton", this.mainForm.getWidth() / 2 - var10 / 2, 0, var10, FormInputSize.SIZE_24, ButtonColor.BASE));
      this.salvageButton.onClicked((var1x) -> {
         var2.salvageButton.runAndSend();
      });
      this.updateRewards();
      this.makeCurrent(this.mainForm);
   }

   protected void init() {
      super.init();
      this.inventoryUpdateListener = ((SalvageStationContainer)this.container).salvageEntity.inventory.addSlotUpdateListener(new InventoryUpdateListener() {
         public void onSlotUpdate(int var1) {
            SalvageStationContainerForm.this.updateRewards();
         }

         public boolean isDisposed() {
            return SalvageStationContainerForm.this.isDisposed();
         }
      });
   }

   public void updateRewards() {
      if (this.rewardComponents != null) {
         Collection var10000 = this.rewardComponents;
         Form var10001 = this.mainForm;
         Objects.requireNonNull(var10001);
         var10000.forEach(var10001::removeComponent);
      }

      this.rewardComponents = new ArrayList();
      FormFlow var1 = new FormFlow(this.rewardsStartY);
      ArrayList var2 = ((SalvageStationContainer)this.container).getCurrentSalvageRewards(false);
      if (var2 == null || var2.isEmpty()) {
         var2 = new ArrayList(Collections.singleton(new InventoryItem("upgradeshard", 0)));
      }

      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         InventoryItem var4 = (InventoryItem)var3.next();
         FontOptions var5 = new FontOptions(16);
         FairType var6 = new FairType();
         var6.append(new FairItemGlyph(var5.getSize(), var4));
         var6.append(new FairSpacerGlyph(5.0F, 2.0F));
         var6.append(var5, var4.getAmount() + " " + var4.getItemDisplayName());
         FormFairTypeLabel var7 = new FormFairTypeLabel("", this.mainForm.getWidth() / 2, 0);
         var7.setTextAlign(FairType.TextAlign.CENTER);
         var7.setMaxWidth(this.mainForm.getWidth() - 20);
         var7.setCustomFairType(var6);
         this.rewardComponents.add((FormComponent)var1.nextY((FormFairTypeLabel)this.mainForm.addComponent(var7), 2));
      }

      var1.next(10);
      var1.nextY(this.salvageButton, 4);
      this.mainForm.setHeight(var1.next());
      this.onWindowResized();
   }

   public void onWindowResized() {
      super.onWindowResized();
      ContainerComponent.setPosFocus(this.mainForm);
   }

   public boolean shouldOpenInventory() {
      return true;
   }

   public void dispose() {
      super.dispose();
      if (this.inventoryUpdateListener != null) {
         this.inventoryUpdateListener.dispose();
      }

   }
}
