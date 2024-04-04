package necesse.gfx.forms.presets.containerComponent.settlement;

import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.gameFont.FontOptions;

public class SettlementLoadingForm extends Form {
   public SettlementLoadingForm(int var1, int var2) {
      super("loading", var1, var2);
      FormLocalLabel var3 = (FormLocalLabel)this.addComponent(new FormLocalLabel("ui", "loadingdotdot", new FontOptions(16), 0, var1 / 2, var2 / 2, var1 - 20));
      var3.setY(var1 / 2 - var3.getBoundingBox().height / 2);
   }
}
