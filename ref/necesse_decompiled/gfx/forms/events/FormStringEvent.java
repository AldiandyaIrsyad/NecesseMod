package necesse.gfx.forms.events;

import necesse.gfx.forms.components.FormComponent;

public class FormStringEvent<T extends FormComponent> extends FormEvent<T> {
   public final String str;

   public FormStringEvent(T var1, String var2) {
      super(var1);
      this.str = var2;
   }

   public boolean equals(String var1) {
      return this.str.equals(var1);
   }

   public boolean equals(FormStringEvent var1) {
      if (this == var1) {
         return true;
      } else {
         return this.equals(var1.str) && this.from == var1.from;
      }
   }
}
