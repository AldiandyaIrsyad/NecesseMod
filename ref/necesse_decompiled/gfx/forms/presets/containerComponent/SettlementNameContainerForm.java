package necesse.gfx.forms.presets.containerComponent;

import necesse.engine.Screen;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.client.Client;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.inventory.container.SettlementNameContainer;

public class SettlementNameContainerForm extends ContainerForm<SettlementNameContainer> {
   private final FormTextInput input;

   public SettlementNameContainerForm(Client var1, SettlementNameContainer var2) {
      super(var1, 400, 80, var2);
      this.input = (FormTextInput)this.addComponent(new FormTextInput(4, 0, FormInputSize.SIZE_32_TO_40, this.getWidth() - 8, 40));
      this.input.placeHolder = new LocalMessage("settlement", "defname", "biome", var2.level.biome.getLocalization());
      this.input.onSubmit((var1x) -> {
         this.submitName();
      });
      ((FormLocalTextButton)this.addComponent(new FormLocalTextButton("ui", "confirmbutton", 4, 40, this.getWidth() - 8))).onClicked((var1x) -> {
         this.submitName();
      });
   }

   public void submitName() {
      ((SettlementNameContainer)this.container).submitButton.runAndSend(this.getCurrentNameInput().getContentPacket());
   }

   public GameMessage getCurrentNameInput() {
      String var1 = this.input.getText();
      return (GameMessage)(var1.isEmpty() ? this.input.placeHolder : new StaticMessage(var1));
   }

   public void onWindowResized() {
      super.onWindowResized();
      this.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
   }

   public boolean shouldOpenInventory() {
      return false;
   }

   public boolean shouldShowToolbar() {
      return false;
   }
}
