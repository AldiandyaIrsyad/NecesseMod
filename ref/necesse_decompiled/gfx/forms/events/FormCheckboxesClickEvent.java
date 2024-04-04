package necesse.gfx.forms.events;

import necesse.engine.control.InputEvent;
import necesse.gfx.forms.components.FormCheckBox;

public class FormCheckboxesClickEvent extends FormInputEvent<FormCheckBox> {
   public final int checkboxIndex;

   public FormCheckboxesClickEvent(FormCheckBox var1, InputEvent var2, int var3) {
      super(var1, var2);
      this.checkboxIndex = var3;
   }
}
