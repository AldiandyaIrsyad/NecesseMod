package necesse.level.maps.liquidManager;

import java.awt.Point;

class NextHeightTile extends Point {
   public final int height;
   public int sameHeightTraveled;

   public NextHeightTile(int var1, int var2, int var3, int var4) {
      super(var1, var2);
      this.height = var3;
      this.sameHeightTraveled = var4;
   }
}
