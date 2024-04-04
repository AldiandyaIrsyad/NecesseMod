package necesse.gfx.forms.events;

import necesse.gfx.forms.components.FormComponent;

public class FormEvent<T extends FormComponent> {
   private boolean preventDefault;
   public final T from;

   public FormEvent(T var1) {
      this.from = var1;
   }

   public final void preventDefault() {
      this.preventDefault = true;
   }

   public boolean hasPreventedDefault() {
      return this.preventDefault;
   }
}
