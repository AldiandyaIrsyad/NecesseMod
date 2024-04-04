package necesse.gfx.forms.presets.containerComponent.object;

import java.awt.Rectangle;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.Control;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.gfx.GameColor;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.fairType.parsers.TypeParser;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabelEdit;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.presets.containerComponent.ContainerFormSwitcher;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementObjectStatusFormManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.object.OEInventoryContainer;

public class OEInventoryContainerForm<T extends OEInventoryContainer> extends ContainerFormSwitcher<T> {
   public Form inventoryForm = (Form)this.addComponent(new Form(408, 100), (var1x, var2x) -> {
      if (!var2x) {
         this.label.setTyping(false);
         this.runEditUpdate();
      }

   });
   public SettlementObjectStatusFormManager settlementObjectFormManager;
   public FormLabelEdit label;
   public FormContentIconButton edit;
   public FormContainerSlot[] slots;
   public LocalMessage renameTip;

   public static TypeParser<?>[] getParsers(FontOptions var0) {
      return new TypeParser[]{TypeParsers.GAME_COLOR, TypeParsers.REMOVE_URL, TypeParsers.URL_OPEN, TypeParsers.ItemIcon(var0.getSize()), TypeParsers.InputIcon(var0)};
   }

   public OEInventoryContainerForm(Client var1, T var2) {
      super(var1, var2);
      OEInventory var3 = var2.oeInventory;
      FontOptions var4 = new FontOptions(20);
      this.label = (FormLabelEdit)this.inventoryForm.addComponent(new FormLabelEdit("", var4, Settings.UI.activeTextColor, 4, 4, this.inventoryForm.getWidth() - 8, 50), -1000);
      this.label.onMouseChangedTyping((var1x) -> {
         this.runEditUpdate();
      });
      this.label.onSubmit((var1x) -> {
         this.runEditUpdate();
      });
      this.label.allowCaretSetTyping = var3.canSetInventoryName();
      this.label.allowItemAppend = true;
      this.label.setParsers(getParsers(var4));
      this.label.setText(var3.getInventoryName().translate());
      FormFlow var5 = new FormFlow(this.inventoryForm.getWidth() - 4);
      this.renameTip = new LocalMessage("ui", "renamebutton");
      if (var3.canSetInventoryName()) {
         this.edit = (FormContentIconButton)this.inventoryForm.addComponent(new FormContentIconButton(var5.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.container_rename, new GameMessage[]{this.renameTip}));
         this.edit.onClicked((var1x) -> {
            this.label.setTyping(!this.label.isTyping());
            this.runEditUpdate();
         });
      }

      FormContentIconButton var6;
      FormContentIconButton var7;
      if (var3.canQuickStackInventory()) {
         var6 = (FormContentIconButton)this.inventoryForm.addComponent(new FormContentIconButton(var5.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.inventory_quickstack_out, new GameMessage[0]) {
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
         var6.onClicked((var1x) -> {
            var2.quickStackButton.runAndSend();
         });
         var6.setCooldown(500);
         var7 = ((<undefinedtype>)this.inventoryForm.addComponent(new FormContentIconButton(var5.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.container_loot_all, new GameMessage[0]) {
            public GameTooltips getTooltips(PlayerMob var1) {
               StringTooltips var2 = new StringTooltips(Localization.translate("ui", "inventorytransferall"));
               if (!Screen.isKeyDown(340) && !Screen.isKeyDown(344)) {
                  var2.add(Localization.translate("ui", "shiftmoreinfo"), GameColor.LIGHT_GRAY);
               } else {
                  var2.add(Localization.translate("ui", "inventorytransferallinfo", "key", TypeParsers.getInputParseString(Control.INV_LOCK) + "+" + TypeParsers.getInputParseString(-100)), (GameColor)GameColor.LIGHT_GRAY, 400);
               }

               return var2;
            }
         })).mirrorY();
         var7.onClicked((var1x) -> {
            var2.transferAll.runAndSend();
         });
         var7.setCooldown(500);
      }

      if (var3.canRestockInventory()) {
         var6 = (FormContentIconButton)this.inventoryForm.addComponent(new FormContentIconButton(var5.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.inventory_quickstack_in, new GameMessage[]{new LocalMessage("ui", "inventoryrestock")}));
         var6.onClicked((var1x) -> {
            var2.restockButton.runAndSend();
         });
         var6.setCooldown(500);
      }

      var6 = (FormContentIconButton)this.inventoryForm.addComponent(new FormContentIconButton(var5.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.container_loot_all, new GameMessage[]{new LocalMessage("ui", "inventorylootall")}));
      var6.onClicked((var1x) -> {
         var2.lootButton.runAndSend();
      });
      var6.setCooldown(500);
      if (var3.canSortInventory()) {
         var7 = (FormContentIconButton)this.inventoryForm.addComponent(new FormContentIconButton(var5.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.inventory_sort, new GameMessage[]{new LocalMessage("ui", "inventorysort")}));
         var7.onClicked((var1x) -> {
            var2.sortButton.runAndSend();
         });
         var7.setCooldown(500);
      }

      this.settlementObjectFormManager = var2.settlementObjectManager.getFormManager(this, this.inventoryForm, var1);
      this.settlementObjectFormManager.addConfigButtonRow(this.inventoryForm, var5, 4, -1);
      this.label.setWidth(var5.next() - 8);
      FormFlow var8 = new FormFlow(34);
      this.addSlots(var8);
      var8.next(4);
      this.inventoryForm.setHeight(var8.next());
      this.makeCurrent(this.inventoryForm);
   }

   public void onWindowResized() {
      super.onWindowResized();
      ContainerComponent.setPosFocus(this.inventoryForm);
      this.settlementObjectFormManager.onWindowResized();
   }

   public boolean shouldOpenInventory() {
      return true;
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

         this.slots[var3] = (FormContainerSlot)this.inventoryForm.addComponent(new FormContainerSlot(this.client, this.container, var4, 4 + var5 * 40, var2));
      }

   }

   private void runEditUpdate() {
      OEInventory var1 = ((OEInventoryContainer)this.container).oeInventory;
      if (var1.canSetInventoryName()) {
         if (this.label.isTyping()) {
            this.edit.setIcon(Settings.UI.container_rename_save);
            this.renameTip = new LocalMessage("ui", "savebutton");
         } else {
            if (!this.label.getText().equals(var1.getInventoryName().translate())) {
               var1.setInventoryName(this.label.getText());
               ((OEInventoryContainer)this.container).renameButton.runAndSend(this.label.getText());
            }

            this.edit.setIcon(Settings.UI.container_rename);
            this.renameTip = new LocalMessage("ui", "renamebutton");
            this.label.setText(var1.getInventoryName().translate());
         }

         this.edit.setTooltips(this.renameTip);
      }
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      this.settlementObjectFormManager.updateButtons();
      super.draw(var1, var2, var3);
   }
}
