package necesse.gfx.forms.events;

import necesse.gfx.forms.components.FormCheckBox;

public class FormCheckboxesEventHandler {
   public final FormCheckBox[] checkBoxes;

   public FormCheckboxesEventHandler(FormCheckBox[] var1) {
      this.checkBoxes = var1;
   }

   public FormCheckboxesEventHandler onClicked(FormEventListener<FormCheckboxesClickEvent> var1) {
      for(int var2 = 0; var2 < this.checkBoxes.length; ++var2) {
         this.checkBoxes[var2].onClicked((var2x) -> {
            FormCheckboxesClickEvent var3 = new FormCheckboxesClickEvent((FormCheckBox)var2x.from, var2x.event, var2);
            var1.onEvent(var3);
            if (var3.hasPreventedDefault()) {
               var2x.preventDefault();
            }

         });
      }

      return this;
   }

   public boolean[] getStates() {
      boolean[] var1 = new boolean[this.checkBoxes.length];

      for(int var2 = 0; var2 < this.checkBoxes.length; ++var2) {
         var1[var2] = this.checkBoxes[var2].checked;
      }

      return var1;
   }
}
