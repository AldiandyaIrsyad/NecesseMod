package necesse.gfx.forms.events;

import necesse.gfx.forms.components.FormComponent;

public class FormIndexEvent<T extends FormComponent> extends FormEvent<T> {
   public final int index;

   public FormIndexEvent(T var1, int var2) {
      super(var1);
      this.index = var2;
   }
}
