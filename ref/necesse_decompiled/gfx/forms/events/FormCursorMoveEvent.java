package necesse.gfx.forms.events;

import necesse.gfx.forms.components.FormComponent;

public class FormCursorMoveEvent<T extends FormComponent> extends FormEvent<T> {
   public final boolean causedByMouse;

   public FormCursorMoveEvent(T var1, boolean var2) {
      super(var1);
      this.causedByMouse = var2;
   }
}
