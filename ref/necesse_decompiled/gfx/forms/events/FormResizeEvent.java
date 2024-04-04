package necesse.gfx.forms.events;

import necesse.engine.control.InputEvent;
import necesse.gfx.forms.components.FormComponent;

public class FormResizeEvent<T extends FormComponent> extends FormInputEvent<T> {
   public int x;
   public int y;
   public int width;
   public int height;

   public FormResizeEvent(T var1, InputEvent var2, int var3, int var4, int var5, int var6) {
      super(var1, var2);
      this.x = var3;
      this.y = var4;
      this.width = var5;
      this.height = var6;
   }
}
