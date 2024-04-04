package necesse.gfx.forms.presets;

import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.ui.ButtonColor;

public class DisabledPreForm extends Form {
   public DisabledPreForm(int var1, GameMessage var2, final GameMessage var3) {
      super(var1, 40);
      this.drawBase = false;
      this.addComponent(new FormLocalLabel(var2, (new FontOptions(16)).color(200, 50, 50), 0, this.getWidth() / 2, 0, var1 - 4));
      this.addComponent(new FormContentIconButton(this.getWidth() / 2 - 10, 20, FormInputSize.SIZE_20, ButtonColor.BASE, Settings.UI.button_help_20, new GameMessage[0]) {
         public GameTooltips getTooltips(PlayerMob var1) {
            StringTooltips var2 = new StringTooltips();
            var2.add(var3.translate(), 400);
            return var2;
         }
      });
   }
}
