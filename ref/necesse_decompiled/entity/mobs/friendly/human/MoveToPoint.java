package necesse.entity.mobs.friendly.human;

import java.awt.Point;

public abstract class MoveToPoint extends Point {
   public boolean acceptAdjacentTiles;

   public MoveToPoint(int var1, int var2, boolean var3) {
      super(var1, var2);
      this.acceptAdjacentTiles = var3;
   }

   public MoveToPoint(Point var1, boolean var2) {
      super(var1);
      this.acceptAdjacentTiles = var2;
   }

   public abstract boolean moveIfPathFailed(float var1);

   public abstract boolean isAtLocation(float var1, boolean var2);

   public abstract void onAtLocation();
}
