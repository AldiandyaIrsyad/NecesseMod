package necesse.gfx.forms.presets.containerComponent.item;

import java.awt.Rectangle;
import necesse.engine.network.client.Client;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.containerComponent.ContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.item.RenameItemContainer;

public class RenameItemContainerForm<T extends RenameItemContainer> extends ContainerForm<T> {
   public FormTextInput inputField;
   public FormLocalTextButton renameButton;

   public RenameItemContainerForm(Client var1, T var2) {
      super(var1, 300, 300, var2);
      FormFlow var3 = new FormFlow(10);
      if (!var2.itemSlot.isClear()) {
         this.addComponent((FormLocalLabel)var3.nextY(new FormLocalLabel(ItemRegistry.getLocalization(var2.itemSlot.getItem().item.getID()), new FontOptions(20), 0, this.getWidth() / 2, 10, this.getWidth() - 20), 10));
      }

      this.inputField = (FormTextInput)this.addComponent((FormTextInput)var3.nextY(new FormTextInput(4, 0, FormInputSize.SIZE_24, this.getWidth() - 8, RenameItemContainer.MAX_NAME_LENGTH)));
      this.inputField.rightClickToClear = true;
      this.inputField.onSubmit((var2x) -> {
         var2.renameButton.runAndSend(this.inputField.getText());
      });
      if (!var2.itemSlot.isClear()) {
         InventoryItem var4 = var2.itemSlot.getItem();
         GNDItem var5 = var4.getGndData().getItem("name");
         if (var5 != null && !GNDItem.isDefault(var5)) {
            this.inputField.setText(var5.toString());
         }
      }

      var3.next(10);
      int var6 = Math.min(150, this.getWidth() - 20);
      this.renameButton = (FormLocalTextButton)this.addComponent((FormLocalTextButton)var3.nextY(new FormLocalTextButton("ui", "renamebutton", this.getWidth() / 2 - var6 / 2, 60, var6, FormInputSize.SIZE_24, ButtonColor.BASE), 10));
      this.renameButton.onClicked((var2x) -> {
         var2.renameButton.runAndSend(this.inputField.getText());
      });
      this.setHeight(var3.next());
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      this.renameButton.setActive(((RenameItemContainer)this.container).canRename(this.inputField.getText()));
      super.draw(var1, var2, var3);
   }
}
