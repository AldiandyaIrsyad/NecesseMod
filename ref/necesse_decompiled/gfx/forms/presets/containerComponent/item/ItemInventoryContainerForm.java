package necesse.gfx.forms.presets.containerComponent.item;

import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.Control;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameColor;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabelEdit;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.presets.containerComponent.ContainerForm;
import necesse.gfx.forms.presets.containerComponent.object.OEInventoryContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.item.ItemInventoryContainer;
import necesse.inventory.item.miscItem.InternalInventoryItemInterface;

public class ItemInventoryContainerForm<T extends ItemInventoryContainer> extends ContainerForm<T> {
   public FormLabelEdit label;
   public FormContentIconButton edit;
   public LocalMessage renameTip;
   public FormContainerSlot[] slots;

   public ItemInventoryContainerForm(Client var1, T var2) {
      super(var1, 408, 100, var2);
      InventoryItem var3 = var2.getInventoryItem();
      InternalInventoryItemInterface var4 = var2.inventoryItem;
      FontOptions var5 = new FontOptions(20);
      this.label = (FormLabelEdit)this.addComponent(new FormLabelEdit("", var5, Settings.UI.activeTextColor, 5, 5, this.getWidth() - 10, 50), -1000);
      this.label.onMouseChangedTyping((var1x) -> {
         this.runEditUpdate();
      });
      this.label.onSubmit((var1x) -> {
         this.runEditUpdate();
      });
      this.label.allowCaretSetTyping = var4.canChangePouchName();
      this.label.allowItemAppend = true;
      this.label.setParsers(OEInventoryContainerForm.getParsers(var5));
      this.label.setText(var3 == null ? "NULL" : var3.getItemDisplayName());
      FormFlow var6 = new FormFlow(this.getWidth() - 4);
      this.renameTip = new LocalMessage("ui", "renamebutton");
      if (var4.canChangePouchName()) {
         this.edit = (FormContentIconButton)this.addComponent(new FormContentIconButton(var6.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.container_rename, new GameMessage[]{this.renameTip}));
         this.edit.onClicked((var1x) -> {
            this.label.setTyping(!this.label.isTyping());
            this.runEditUpdate();
         });
      }

      FormContentIconButton var7;
      FormContentIconButton var8;
      if (var4.canQuickStackInventory()) {
         var7 = (FormContentIconButton)this.addComponent(new FormContentIconButton(var6.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.inventory_quickstack_out, new GameMessage[0]) {
            public GameTooltips getTooltips(PlayerMob var1) {
               StringTooltips var2 = new StringTooltips(Localization.translate("ui", "inventoryquickstack"));
               if (!Screen.isKeyDown(340) && !Screen.isKeyDown(344)) {
                  var2.add(Localization.translate("ui", "shiftmoreinfo"), GameColor.LIGHT_GRAY);
               } else {
                  var2.add(Localization.translate("ui", "inventoryquickstackinfo", "key", TypeParsers.getInputParseString(Control.INV_LOCK) + "+" + TypeParsers.getInputParseString(-100)), (GameColor)GameColor.LIGHT_GRAY, 400);
               }

               return var2;
            }
         });
         var7.onClicked((var1x) -> {
            var2.quickStackButton.runAndSend();
         });
         var7.setCooldown(500);
         var8 = (FormContentIconButton)this.addComponent(new FormContentIconButton(var6.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.container_loot_all, new GameMessage[0]) {
            public GameTooltips getTooltips(PlayerMob var1) {
               StringTooltips var2 = new StringTooltips(Localization.translate("ui", "inventorytransferall"));
               if (!Screen.isKeyDown(340) && !Screen.isKeyDown(344)) {
                  var2.add(Localization.translate("ui", "shiftmoreinfo"), GameColor.LIGHT_GRAY);
               } else {
                  var2.add(Localization.translate("ui", "inventorytransferallinfo", "key", TypeParsers.getInputParseString(Control.INV_LOCK) + "+" + TypeParsers.getInputParseString(-100)), (GameColor)GameColor.LIGHT_GRAY, 400);
               }

               return var2;
            }
         });
         var8.mirrorY();
         var8.onClicked((var1x) -> {
            var2.transferAll.runAndSend();
         });
         var8.setCooldown(500);
      }

      if (var4.canRestockInventory()) {
         var7 = (FormContentIconButton)this.addComponent(new FormContentIconButton(var6.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.inventory_quickstack_in, new GameMessage[]{new LocalMessage("ui", "inventoryrestock")}));
         var7.onClicked((var1x) -> {
            var2.restockButton.runAndSend();
         });
         var7.setCooldown(500);
      }

      var7 = (FormContentIconButton)this.addComponent(new FormContentIconButton(var6.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.container_loot_all, new GameMessage[]{new LocalMessage("ui", "inventorylootall")}));
      var7.onClicked((var1x) -> {
         var2.lootButton.runAndSend();
      });
      var7.setCooldown(500);
      if (var4.canSortInventory()) {
         var8 = (FormContentIconButton)this.addComponent(new FormContentIconButton(var6.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.inventory_sort, new GameMessage[]{new LocalMessage("ui", "inventorysort")}));
         var8.onClicked((var1x) -> {
            var2.sortButton.runAndSend();
         });
         var8.setCooldown(500);
      }

      this.label.setWidth(var6.next() - 10);
      FormFlow var9 = new FormFlow(34);
      this.addSlots(var9);
      var9.next(4);
      this.setHeight(var9.next());
   }

   protected void runEditUpdate() {
      InternalInventoryItemInterface var1 = ((ItemInventoryContainer)this.container).inventoryItem;
      if (var1.canChangePouchName()) {
         if (this.label.isTyping()) {
            this.edit.setIcon(Settings.UI.container_rename_save);
            this.renameTip = new LocalMessage("ui", "savebutton");
         } else {
            InventoryItem var2 = ((ItemInventoryContainer)this.container).getInventoryItem();
            if (var2 == null) {
               return;
            }

            if (!this.label.getText().equals(var1.getPouchName(var2))) {
               var1.setPouchName(var2, this.label.getText());
               ((ItemInventoryContainer)this.container).renameButton.runAndSend(this.label.getText());
            }

            this.edit.setIcon(Settings.UI.container_rename);
            this.renameTip = new LocalMessage("ui", "renamebutton");
            this.label.setText(var2.getItemDisplayName());
         }

         this.edit.setTooltips(this.renameTip);
      }
   }

   protected void addSlots(FormFlow var1) {
      this.slots = new FormContainerSlot[((ItemInventoryContainer)this.container).INVENTORY_END - ((ItemInventoryContainer)this.container).INVENTORY_START + 1];
      int var2 = var1.next();

      for(int var3 = 0; var3 < this.slots.length; ++var3) {
         int var4 = var3 + ((ItemInventoryContainer)this.container).INVENTORY_START;
         int var5 = var3 % 10;
         if (var5 == 0) {
            var2 = var1.next(40);
         }

         this.slots[var3] = (FormContainerSlot)this.addComponent(new FormContainerSlot(this.client, this.container, var4, 4 + var5 * 40, var2));
      }

   }
}
