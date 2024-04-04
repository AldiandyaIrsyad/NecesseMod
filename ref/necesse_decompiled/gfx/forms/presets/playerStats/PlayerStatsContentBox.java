package necesse.gfx.forms.presets.playerStats;

import necesse.gfx.forms.components.FormContentBox;

public class PlayerStatsContentBox extends FormContentBox implements PlayerStatsSubForm {
   public final PlayerStatsForm statsForm;

   public PlayerStatsContentBox(PlayerStatsForm var1) {
      super(0, 0, var1.getWidth(), var1.getHeight());
      this.statsForm = var1;
   }

   public void updateDisabled(int var1) {
      this.setPosition(0, var1);
      this.setHeight(this.statsForm.getHeight() - var1);
   }
}
