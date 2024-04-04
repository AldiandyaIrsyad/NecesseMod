package necesse.gfx.forms.events;

import necesse.engine.control.InputEvent;
import necesse.gfx.forms.components.FormComponent;

public class FormGrabbedEvent<T extends FormComponent> extends FormInputEvent<T> {
   public final boolean grabbed;

   public FormGrabbedEvent(T var1, InputEvent var2, boolean var3) {
      super(var1, var2);
      this.grabbed = var3;
   }
}
