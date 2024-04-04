package necesse.gfx.forms.presets.containerComponent.mob;

import java.awt.Rectangle;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.Control;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameColor;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormItemPreview;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.containerSlot.FormContainerBrokerSlot;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.mob.PawnbrokerContainer;

public class PawnbrokerContainerForm<T extends PawnbrokerContainer> extends ShopContainerForm<T> {
   public Form pawnForm;
   public FormContainerSlot[] slots;
   public FormLabel profitText;
   public int lastProfit;

   public PawnbrokerContainerForm(Client var1, T var2) {
      super(var1, var2, 408, 170, 240);
      this.pawnForm = (Form)this.addComponent(new Form("pawnForm", 408, (var2.inventory.getSize() + 9) / 10 * 40 + 70 + 8), (var1x, var2x) -> {
         var2.setIsPawning.runAndSend(var2x);
      });
      String var3 = MobRegistry.getLocalization(var2.pawnbrokerMob.getID()).translate();
      FormLabel var4 = (FormLabel)this.pawnForm.addComponent(new FormLabel("", new FontOptions(20), -1, 4, 4), -1000);
      FormFlow var5 = new FormFlow(this.pawnForm.getWidth() - 4);
      FormContentIconButton var6 = (FormContentIconButton)this.pawnForm.addComponent(new FormContentIconButton(var5.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.inventory_quickstack_out, new GameMessage[0]) {
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
      FormContentIconButton var7 = (FormContentIconButton)this.pawnForm.addComponent(new FormContentIconButton(var5.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.container_loot_all, new GameMessage[0]) {
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
      var7.mirrorY();
      var7.onClicked((var1x) -> {
         var2.transferAll.runAndSend();
      });
      var7.setCooldown(500);
      FormContentIconButton var8 = (FormContentIconButton)this.pawnForm.addComponent(new FormContentIconButton(var5.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.inventory_quickstack_in, new GameMessage[]{new LocalMessage("ui", "inventoryrestock")}));
      var8.onClicked((var1x) -> {
         var2.restockButton.runAndSend();
      });
      var8.setCooldown(500);
      FormContentIconButton var9 = (FormContentIconButton)this.pawnForm.addComponent(new FormContentIconButton(var5.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.container_loot_all, new GameMessage[]{new LocalMessage("ui", "inventorylootall")}));
      var9.onClicked((var1x) -> {
         var2.lootButton.runAndSend();
      });
      var9.setCooldown(500);
      var3 = GameUtils.maxString(var3, new FontOptions(20), var5.next() - 8);
      var4.setText(var3, var5.next() - 8);
      this.slots = new FormContainerSlot[var2.INVENTORY_END - var2.INVENTORY_START + 1];

      for(int var10 = 0; var10 < this.slots.length; ++var10) {
         int var11 = var10 + var2.INVENTORY_START;
         int var12 = var10 % 10;
         int var13 = var10 / 10;
         this.slots[var10] = (FormContainerSlot)this.pawnForm.addComponent(new FormContainerBrokerSlot(var1, var2, var11, 4 + var12 * 40, 4 + var13 * 40 + 30));
      }

      FormLocalLabel var14 = (FormLocalLabel)this.pawnForm.addComponent(new FormLocalLabel("ui", "brokerprofit", new FontOptions(16), -1, 10, this.pawnForm.getHeight() - 27));
      FormItemPreview var15 = (FormItemPreview)this.pawnForm.addComponent(new FormItemPreview(var14.getX() + var14.getBoundingBox().width, this.pawnForm.getHeight() - 27 - 12, "coin"));
      this.profitText = (FormLabel)this.pawnForm.addComponent(new FormLabel("x " + var2.getProfit(), new FontOptions(16), -1, var15.getX() + 30, this.pawnForm.getHeight() - 27));
      this.lastProfit = var2.getProfit();
      ((FormLocalTextButton)this.pawnForm.addComponent(new FormLocalTextButton("ui", "tradersell", this.pawnForm.getWidth() - 154, this.pawnForm.getHeight() - 40, 150))).onClicked((var1x) -> {
         var2.sellButton.runAndSend();
      });
   }

   protected void setupExtraDialogueOptions() {
      super.setupExtraDialogueOptions();
      this.dialogueForm.addDialogueOption(new LocalMessage("ui", "brokerwantpawn"), () -> {
         this.makeCurrent(this.pawnForm);
      });
   }

   public void onWindowResized() {
      super.onWindowResized();
      ContainerComponent.setPosFocus(this.pawnForm);
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      if (this.lastProfit != ((PawnbrokerContainer)this.container).getProfit()) {
         this.profitText.setText("x " + ((PawnbrokerContainer)this.container).getProfit());
         this.lastProfit = ((PawnbrokerContainer)this.container).getProfit();
      }

      super.draw(var1, var2, var3);
   }
}
