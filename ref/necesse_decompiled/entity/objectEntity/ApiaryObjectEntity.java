package necesse.entity.objectEntity;

import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.level.maps.Level;

public class ApiaryObjectEntity extends AbstractBeeHiveObjectEntity {
   public static int maxStoredHoney = 10;
   public static int maxFrames = 5;
   public static int maxBees = 20;

   public ApiaryObjectEntity(Level var1, int var2, int var3) {
      super(var1, "apiary", var2, var3);
   }

   public void onMouseHover(PlayerMob var1, boolean var2) {
      super.onMouseHover(var1, var2);
      StringTooltips var3 = new StringTooltips();
      var3.add(this.getObject().getDisplayName());
      if (this.hasQueen) {
         var3.add(Localization.translate("ui", "producinghoney"));
      } else {
         var3.add(Localization.translate("ui", "missingqueen"));
      }

      if (var2) {
         this.addDebugTooltips(var3);
      }

      Screen.addTooltip(var3, TooltipLocation.INTERACT_FOCUS);
   }

   public int getMaxBees() {
      return maxBees;
   }

   public int getMaxFrames() {
      return maxFrames;
   }

   public int getMaxStoredHoney() {
      return maxStoredHoney;
   }

   public boolean canCreateQueens() {
      return true;
   }
}
