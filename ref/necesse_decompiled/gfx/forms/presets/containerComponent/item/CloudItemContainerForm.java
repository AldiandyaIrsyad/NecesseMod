package necesse.gfx.forms.presets.containerComponent.item;

import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.Control;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.client.Client;
import necesse.engine.registries.ItemRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameColor;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.presets.containerComponent.ContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.item.CloudItemContainer;

public class CloudItemContainerForm<T extends CloudItemContainer> extends ContainerForm<T> {
   protected FormContainerSlot[] slots;

   protected CloudItemContainerForm(Client var1, T var2, int var3) {
      super(var1, 408, var3, var2);
      FontOptions var4 = new FontOptions(20);
      FormLocalLabel var5 = (FormLocalLabel)this.addComponent(new FormLocalLabel(new StaticMessage(""), var4, -1, 4, 4), -1000);
      FormFlow var6 = new FormFlow(this.getWidth() - 4);
      FormContentIconButton var7 = (FormContentIconButton)this.addComponent(new FormContentIconButton(var6.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.inventory_quickstack_out, new GameMessage[0]) {
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
      FormContentIconButton var8 = (FormContentIconButton)this.addComponent(new FormContentIconButton(var6.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.container_loot_all, new GameMessage[0]) {
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
      FormContentIconButton var9 = (FormContentIconButton)this.addComponent(new FormContentIconButton(var6.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.inventory_quickstack_in, new GameMessage[]{new LocalMessage("ui", "inventoryrestock")}));
      var9.onClicked((var1x) -> {
         var2.restockButton.runAndSend();
      });
      var9.setCooldown(500);
      FormContentIconButton var10 = (FormContentIconButton)this.addComponent(new FormContentIconButton(var6.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.container_loot_all, new GameMessage[]{new LocalMessage("ui", "inventorylootall")}));
      var10.onClicked((var1x) -> {
         var2.lootButton.runAndSend();
      });
      var10.setCooldown(500);
      FormContentIconButton var11 = (FormContentIconButton)this.addComponent(new FormContentIconButton(var6.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.inventory_sort, new GameMessage[]{new LocalMessage("ui", "inventorysort")}));
      var11.onClicked((var1x) -> {
         var2.sortButton.runAndSend();
      });
      var11.setCooldown(500);
      var5.setLocalization(ItemRegistry.getLocalization(var2.itemID), var6.next() - 8);
      this.addSlots();
   }

   public CloudItemContainerForm(Client var1, T var2) {
      this(var1, var2, (var2.CLOUD_END - var2.CLOUD_START + 1 + 9) / 10 * 40 + 38);
   }

   protected void addSlots() {
      this.slots = new FormContainerSlot[((CloudItemContainer)this.container).CLOUD_END - ((CloudItemContainer)this.container).CLOUD_START + 1];

      for(int var1 = 0; var1 < this.slots.length; ++var1) {
         int var2 = var1 + ((CloudItemContainer)this.container).CLOUD_START;
         int var3 = var1 % 10;
         int var4 = var1 / 10;
         this.slots[var1] = (FormContainerSlot)this.addComponent(new FormContainerSlot(this.client, this.container, var2, 4 + var3 * 40, 4 + var4 * 40 + 30));
      }

   }
}
