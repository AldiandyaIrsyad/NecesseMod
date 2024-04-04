package necesse.gfx.forms.floatMenu;

import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormColorHuePicker;
import necesse.gfx.forms.components.FormComponent;

public abstract class ColorHueSelectorFloatMenu extends FormFloatMenu {
   public FormColorHuePicker picker;

   public ColorHueSelectorFloatMenu(FormComponent var1, int var2, int var3, float var4) {
      super(var1);
      Form var5 = new Form("hue", var2, var3);
      this.picker = (FormColorHuePicker)var5.addComponent(new FormColorHuePicker(5, 5, var2 - 10, var3 - 10, var4));
      this.picker.onChanged((var1x) -> {
         this.onChanged(((FormColorHuePicker)var1x.from).getSelectedHue());
      });
      this.setForm(var5);
   }

   public abstract void onChanged(float var1);
}
