package necesse.gfx.forms.presets;

import necesse.engine.Screen;
import necesse.engine.localization.message.LocalMessage;

public class ModSaveConfirmationForm extends ConfirmationForm {
   public ModSaveConfirmationForm(String var1) {
      super(var1);
   }

   public void setupModSaveConfirmation(Runnable var1) {
      this.setupConfirmation(new LocalMessage("ui", "modssavenotice"), new LocalMessage("ui", "modsquit"), new LocalMessage("ui", "modslater"), () -> {
         Screen.requestClose();
      }, var1);
   }
}
