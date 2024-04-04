package necesse.gfx.forms;

import necesse.gfx.forms.components.FormComponent;

public class FormResizeWrapper {
   public final FormComponent component;
   public final Runnable resizeLogic;

   public FormResizeWrapper(FormComponent var1, Runnable var2) {
      this.component = var1;
      this.resizeLogic = var2;
   }
}
