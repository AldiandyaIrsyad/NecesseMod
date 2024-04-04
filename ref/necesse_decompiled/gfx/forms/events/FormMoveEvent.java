package necesse.gfx.forms.events;

import necesse.engine.control.InputEvent;
import necesse.gfx.forms.components.FormComponent;

public class FormMoveEvent<T extends FormComponent> extends FormInputEvent<T> {
   public int x;
   public int y;

   public FormMoveEvent(T var1, InputEvent var2, int var3, int var4) {
      super(var1, var2);
      this.x = var3;
      this.y = var4;
   }
}
