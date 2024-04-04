package necesse.entity.projectile.pathProjectile;

import java.awt.geom.Point2D;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;

public abstract class PositionedCirclingProjectile extends CirclingProjectile {
   protected float centerX;
   protected float centerY;

   public PositionedCirclingProjectile() {
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextFloat(this.centerX);
      var1.putNextFloat(this.centerY);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.centerX = var1.getNextFloat();
      this.centerY = var1.getNextFloat();
   }

   public Point2D.Float getCenterPos() {
      return new Point2D.Float(this.centerX, this.centerY);
   }
}
