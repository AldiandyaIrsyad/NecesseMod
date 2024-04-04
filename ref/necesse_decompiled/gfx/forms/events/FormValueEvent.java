package necesse.gfx.forms.events;

import necesse.gfx.forms.components.FormComponent;

public class FormValueEvent<T extends FormComponent, V> extends FormEvent<T> {
   public final V value;

   public FormValueEvent(T var1, V var2) {
      super(var1);
      this.value = var2;
   }
}
