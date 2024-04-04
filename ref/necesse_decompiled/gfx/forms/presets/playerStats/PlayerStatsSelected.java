package necesse.gfx.forms.presets.playerStats;

public interface PlayerStatsSelected {
   void onSelected();

   default boolean backPressed() {
      return false;
   }
}
