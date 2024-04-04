package necesse.level.gameObject;

import java.awt.Rectangle;
import necesse.engine.localization.Localization;
import necesse.entity.mobs.PlayerMob;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.CustomTilePosition;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.SemiRegion;

public class DoorObject extends SwitchObject {
   public DoorObject(Rectangle var1, int var2, boolean var3) {
      super(var1, var2, var3);
      this.regionType = SemiRegion.RegionType.DOOR;
   }

   public void onWireUpdate(Level var1, int var2, int var3, int var4, boolean var5) {
      if (var1.isServer()) {
         if (var5) {
            if (!this.isSwitched) {
               this.onSwitched(var1, var2, var3);
            }
         } else if (this.isSwitched) {
            this.onSwitched(var1, var2, var3);
         }

      }
   }

   public boolean isOpen(Level var1, int var2, int var3, int var4) {
      return this.isSwitched;
   }

   public boolean pathCollidesIfOpen(Level var1, int var2, int var3, CollisionFilter var4, Rectangle var5) {
      if (var4 == null) {
         return false;
      } else {
         CustomTilePosition var6 = new CustomTilePosition(var1, var2, var3, var1.getTileID(var2, var3), this.counterID, var1.getObjectRotation(var2, var3));
         return var4.check(var5, var6);
      }
   }

   public boolean pathCollidesIfBreakDown(Level var1, int var2, int var3, CollisionFilter var4, Rectangle var5) {
      return this.pathCollidesIfOpen(var1, var2, var3, var4, var5);
   }

   public void onPathOpened(Level var1, int var2, int var3) {
      this.onSwitched(var1, var2, var3);
   }

   public void onPathBreakDown(Level var1, int var2, int var3, int var4, int var5, int var6) {
      this.onSwitched(var1, var2, var3);
   }

   public double getBreakDownPathCost(Level var1, int var2, int var3) {
      return 40.0;
   }

   public void interact(Level var1, int var2, int var3, PlayerMob var4) {
      if (var4.isServerClient()) {
         var4.getServerClient().newStats.doors_used.increment(1);
      }

      super.interact(var1, var2, var3, var4);
   }

   public String getInteractTip(Level var1, int var2, int var3, PlayerMob var4, boolean var5) {
      return Localization.translate("controls", this.isSwitched ? "closetip" : "opentip");
   }
}
