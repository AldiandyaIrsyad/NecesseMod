package necesse.entity.mobs;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameMath;

public class WormMoveLine extends Line2D.Float {
   public final boolean isMoveJump;
   public boolean isUnderground;
   public final float movedDist;
   private boolean calculatedDist;
   private boolean calculatedDir;
   private double dist;
   private Point2D.Float dir;

   public WormMoveLine(Point2D var1, Point2D var2, boolean var3, float var4, boolean var5) {
      super(var1, var2);
      this.isMoveJump = var3;
      this.movedDist = var4;
      this.isUnderground = var5;
   }

   public WormMoveLine(PacketReader var1, WormMoveLineSpawnData var2) {
      this.x1 = var1.getNextFloat();
      this.y1 = var1.getNextFloat();
      Point2D.Float var3 = new Point2D.Float(this.x1, this.y1);
      if (var2.lastPoint == null) {
         var2.lastPoint = var3;
      }

      this.isUnderground = var1.getNextBoolean();
      this.isMoveJump = var1.getNextBoolean();
      if (this.isMoveJump) {
         this.x2 = var3.x;
         this.y2 = var3.y;
         this.movedDist = var1.getNextFloat();
         var2.lastPoint = null;
      } else {
         this.x2 = var2.lastPoint.x;
         this.y2 = var2.lastPoint.y;
         this.movedDist = var2.movedDist;
         var2.movedDist = (float)((double)var2.movedDist + this.getP1().distance(this.getP2()));
         var2.lastPoint = var3;
      }

   }

   public void writeSpawnPacket(PacketWriter var1, float var2, float var3, float var4) {
      var1.putNextFloat(var2);
      var1.putNextFloat(var3);
      var1.putNextBoolean(this.isUnderground);
      var1.putNextBoolean(this.isMoveJump);
      if (this.isMoveJump) {
         var1.putNextFloat(this.movedDist + var4);
      }

   }

   public Point2D.Float dir() {
      if (!this.calculatedDir) {
         this.dir = GameMath.normalize(this.x2 - this.x1, this.y2 - this.y1);
         this.calculatedDir = true;
      }

      return this.dir;
   }

   public double dist() {
      if (!this.calculatedDist) {
         this.dist = this.getP1().distance(this.getP2());
         this.calculatedDist = true;
      }

      return this.dist;
   }
}
