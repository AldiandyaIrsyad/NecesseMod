package necesse.gfx.forms.floatMenu;

import java.awt.Color;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.presets.ColorSelectorForm;

public abstract class ColorSelectorFloatMenu extends FormFloatMenu {
   private boolean appliedPressed = false;

   public ColorSelectorFloatMenu(FormComponent var1, Color var2) {
      super(var1);
      this.setForm(new ColorSelectorForm(var2) {
         public void onApplied(Color var1) {
            ColorSelectorFloatMenu.this.onApplied(var1);
            ColorSelectorFloatMenu.this.appliedPressed = true;
            ColorSelectorFloatMenu.this.remove();
         }

         public void onSelected(Color var1) {
            ColorSelectorFloatMenu.this.onSelected(var1);
         }
      });
   }

   public abstract void onApplied(Color var1);

   public abstract void onSelected(Color var1);

   public void dispose() {
      if (!this.isDisposed() && !this.appliedPressed) {
         this.onApplied((Color)null);
      }

      super.dispose();
   }
}
