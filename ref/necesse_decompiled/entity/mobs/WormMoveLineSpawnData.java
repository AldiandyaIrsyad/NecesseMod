package necesse.entity.mobs;

import java.awt.geom.Point2D;

public class WormMoveLineSpawnData {
   public Point2D.Float lastPoint;
   public float movedDist = 0.0F;

   public WormMoveLineSpawnData(float var1, float var2) {
      this.lastPoint = new Point2D.Float(var1, var2);
   }
}
