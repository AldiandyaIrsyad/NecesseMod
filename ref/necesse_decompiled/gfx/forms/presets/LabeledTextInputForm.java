package necesse.gfx.forms.presets;

import java.util.regex.Pattern;
import necesse.engine.Screen;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.gameFont.FontOptions;

public abstract class LabeledTextInputForm extends Form {
   protected FormTextInput input;
   protected FormLocalTextButton confirmButton;
   public boolean escapeOrBackMeansCancel;

   public LabeledTextInputForm(String var1, int var2, GameMessage var3, boolean var4, Pattern var5, GameMessage var6, GameMessage var7) {
      super((String)var1, var2, 80);
      this.escapeOrBackMeansCancel = true;
      FormFlow var8 = new FormFlow(5);
      this.addComponent((FormLocalLabel)var8.nextY(new FormLocalLabel(var3, new FontOptions(16), var4 ? 0 : -1, var4 ? this.getWidth() / 2 : 5, 5, this.getWidth() - 10), 5));
      this.input = (FormTextInput)this.addComponent(new FormTextInput(4, var8.next(40), FormInputSize.SIZE_32_TO_40, this.getWidth() - 8, 50));
      if (var5 != null) {
         this.input.setRegexMatchFull(var5);
      }

      this.input.onChange((var1x) -> {
         GameMessage var2 = this.getInputError(this.input.getText());
         this.confirmButton.setActive(var2 == null);
         this.confirmButton.setLocalTooltip(var2);
      });
      this.input.onSubmit((var1x) -> {
         if (var1x.event.getID() == 257 || var1x.event.getID() == 335) {
            String var2 = this.input.getText();
            GameMessage var3 = this.getInputError(var2);
            if (var3 == null) {
               this.onConfirmed(var2);
            }
         }

      });
      this.input.setControllerTypingHeader(var3);
      int var9 = var8.next(40);
      this.confirmButton = (FormLocalTextButton)this.addComponent(new FormLocalTextButton(var6, 4, var9, this.getWidth() / 2 - 6));
      this.confirmButton.onClicked((var1x) -> {
         String var2 = this.input.getText();
         GameMessage var3 = this.getInputError(var2);
         if (var3 == null) {
            this.onConfirmed(var2);
         }

      });
      ((FormLocalTextButton)this.addComponent(new FormLocalTextButton(var7, this.getWidth() / 2 + 2, var9, this.getWidth() / 2 - 6))).onClicked((var1x) -> {
         this.onCancelled();
      });
      this.setHeight(var8.next());
      this.onWindowResized();
   }

   public LabeledTextInputForm(String var1, int var2, GameMessage var3, boolean var4, String var5, GameMessage var6, GameMessage var7) {
      this(var1, var2, var3, var4, Pattern.compile(var5), var6, var7);
   }

   public LabeledTextInputForm(String var1, GameMessage var2, boolean var3, Pattern var4, GameMessage var5, GameMessage var6) {
      this(var1, 400, var2, var3, (Pattern)var4, var5, var6);
   }

   public LabeledTextInputForm(String var1, GameMessage var2, boolean var3, String var4, GameMessage var5, GameMessage var6) {
      this(var1, var2, var3, Pattern.compile(var4), var5, var6);
   }

   public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
      super.handleInputEvent(var1, var2, var3);
      if (var1.state && var1.getID() == 256 && this.escapeOrBackMeansCancel) {
         var1.use();
         this.onCancelled();
      }

   }

   public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
      super.handleControllerEvent(var1, var2, var3);
      if (var1.buttonState && var1.getState() == ControllerInput.MENU_BACK && this.escapeOrBackMeansCancel) {
         var1.use();
         this.onCancelled();
      }

   }

   public void setInput(String var1) {
      this.input.setText(var1);
      GameMessage var2 = this.getInputError(this.input.getText());
      this.confirmButton.setActive(var2 == null);
      this.confirmButton.setLocalTooltip(var2);
   }

   public void selectAllAndSetTyping() {
      this.input.selectAll();
      this.input.setTyping(true);
   }

   public void startTyping() {
      this.input.setTyping(true);
   }

   public String getInputText() {
      return this.input.getText();
   }

   public abstract GameMessage getInputError(String var1);

   public abstract void onConfirmed(String var1);

   public abstract void onCancelled();

   public void onWindowResized() {
      super.onWindowResized();
      this.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
   }
}
