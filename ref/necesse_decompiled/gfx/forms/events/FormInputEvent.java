package necesse.gfx.forms.events;

import necesse.engine.control.InputEvent;
import necesse.gfx.forms.components.FormComponent;

public class FormInputEvent<T extends FormComponent> extends FormEvent<T> {
   public final InputEvent event;

   public FormInputEvent(T var1, InputEvent var2) {
      super(var1);
      this.event = var2;
   }
}
