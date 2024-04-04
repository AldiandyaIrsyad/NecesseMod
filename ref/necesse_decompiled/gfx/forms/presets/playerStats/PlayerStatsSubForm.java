package necesse.gfx.forms.presets.playerStats;

public interface PlayerStatsSubForm {
   void updateDisabled(int var1);

   default boolean backPressed() {
      return false;
   }
}
