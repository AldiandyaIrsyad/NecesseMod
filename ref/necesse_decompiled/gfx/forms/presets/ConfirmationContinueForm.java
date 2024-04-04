package necesse.gfx.forms.presets;

import java.util.function.Consumer;
import necesse.engine.Screen;
import necesse.engine.localization.message.GameMessage;
import necesse.gfx.forms.components.ContinueComponent;
import necesse.gfx.forms.components.FormContentBox;

public class ConfirmationContinueForm extends ConfirmationForm implements ContinueComponent {
   private boolean isContinued;
   private Runnable continueEvent;

   public ConfirmationContinueForm(String var1) {
      super(var1);
   }

   public ConfirmationContinueForm(String var1, int var2, int var3) {
      super(var1, var2, var3);
   }

   public void setupConfirmation(Consumer<FormContentBox> var1, GameMessage var2, GameMessage var3, Runnable var4, Runnable var5) {
      super.setupConfirmation(var1, var2, var3, var4, () -> {
         var5.run();
         this.applyContinue();
      });
   }

   public void setupConfirmation(GameMessage var1, GameMessage var2, GameMessage var3, Runnable var4, Runnable var5) {
      super.setupConfirmation(var1, var2, var3, var4, () -> {
         var5.run();
         this.applyContinue();
      });
   }

   public void onContinue(Runnable var1) {
      this.continueEvent = var1;
   }

   public void applyContinue() {
      if (this.canContinue()) {
         if (this.continueEvent != null) {
            this.continueEvent.run();
         }

         this.isContinued = true;
      }

   }

   public boolean isContinued() {
      return this.isContinued;
   }

   public void onWindowResized() {
      super.onWindowResized();
      this.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
   }
}
