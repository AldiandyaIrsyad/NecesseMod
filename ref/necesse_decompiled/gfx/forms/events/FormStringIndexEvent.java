package necesse.gfx.forms.events;

import necesse.gfx.forms.components.FormComponent;

public class FormStringIndexEvent<T extends FormComponent> extends FormStringEvent<T> {
   public final int index;

   public FormStringIndexEvent(T var1, String var2, int var3) {
      super(var1, var2);
      this.index = var3;
   }
}
