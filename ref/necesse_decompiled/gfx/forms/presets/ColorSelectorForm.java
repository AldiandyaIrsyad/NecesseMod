package necesse.gfx.forms.presets;

import java.awt.Color;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormColorPicker;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;

public abstract class ColorSelectorForm extends Form {
   private FormColorPicker picker;
   private FormTextButton selectButton;

   public ColorSelectorForm(Color var1) {
      this((String)null, var1);
   }

   public ColorSelectorForm(String var1, Color var2) {
      super((String)var1, 300, 240);
      this.picker = (FormColorPicker)this.addComponent(new FormColorPicker(6, 6, this.getWidth() - 12, this.getHeight() - 40 - 6));
      this.picker.onChanged((var1x) -> {
         Color var2 = this.picker.getSelectedColor();
         this.selectButton.setActive(var2 != null);
         if (var2 != null) {
            this.onSelected(var2);
         }

      });
      this.picker.setSelectedColor(var2);
      this.selectButton = (FormTextButton)this.addComponent(new FormLocalTextButton("ui", "selectbutton", 4, this.getHeight() - 40, this.getWidth() / 2 - 6));
      this.selectButton.onClicked((var1x) -> {
         this.onApplied(this.picker.getSelectedColor());
      });
      this.selectButton.setActive(this.picker.getSelectedColor() != null);
      ((FormLocalTextButton)this.addComponent(new FormLocalTextButton("ui", "cancelbutton", this.getWidth() / 2 + 2, this.getHeight() - 40, this.getWidth() / 2 - 6))).onClicked((var1x) -> {
         this.onApplied((Color)null);
      });
   }

   public abstract void onApplied(Color var1);

   public abstract void onSelected(Color var1);
}
