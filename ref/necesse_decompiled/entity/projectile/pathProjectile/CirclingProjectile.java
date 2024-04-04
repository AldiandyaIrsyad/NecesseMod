package necesse.entity.projectile.pathProjectile;

import java.awt.geom.Point2D;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameMath;

public abstract class CirclingProjectile extends PathProjectile {
   protected float currentAngle;

   public CirclingProjectile() {
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextFloat(this.currentAngle);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.currentAngle = var1.getNextFloat();
   }

   public void init() {
      super.init();
      Point2D.Float var1 = this.getAnglePosition();
      this.x = var1.x;
      this.y = var1.y;
   }

   public Point2D.Float getPosition(double var1) {
      this.currentAngle = (float)((double)this.currentAngle + var1 / (6.283185307179586 * (double)this.getRadius()) * 360.0);
      return this.getAnglePosition();
   }

   public Point2D.Float getAnglePosition() {
      float var1 = this.rotatesClockwise() ? -GameMath.sin(this.currentAngle) : GameMath.sin(this.currentAngle);
      float var2 = GameMath.cos(this.currentAngle);
      Point2D.Float var3 = this.getCenterPos();
      float var4 = this.getRadius();
      return new Point2D.Float(var3.x + var1 * var4, var3.y + var2 * var4);
   }

   public abstract Point2D.Float getCenterPos();

   public abstract float getRadius();

   public boolean rotatesClockwise() {
      return true;
   }
}
