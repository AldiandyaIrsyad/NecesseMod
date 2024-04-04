package necesse.entity.mobs.mobMovement;

import java.awt.geom.Point2D;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.mobs.Mob;

public class MobMovementSpiralLevelPos extends MobMovementSpiral {
   public float centerX;
   public float centerY;

   public MobMovementSpiralLevelPos() {
   }

   public MobMovementSpiralLevelPos(Mob var1, float var2, float var3, float var4, int var5, float var6, float var7, float var8, boolean var9) {
      super(var1, var4, var5, var6, var7, var8, var9);
      this.centerX = var2;
      this.centerY = var3;
   }

   public MobMovementSpiralLevelPos(Mob var1, float var2, float var3, float var4, int var5, float var6, float var7, boolean var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
      this.centerX = var2;
      this.centerY = var3;
   }

   public void setupPacket(Mob var1, PacketWriter var2) {
      var2.putNextFloat(this.centerX);
      var2.putNextFloat(this.centerY);
      super.setupPacket(var1, var2);
   }

   public void applyPacket(Mob var1, PacketReader var2) {
      this.centerX = var2.getNextFloat();
      this.centerY = var2.getNextFloat();
      super.applyPacket(var1, var2);
   }

   public Point2D.Float getCenterPos() {
      return new Point2D.Float(this.centerX, this.centerY);
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof MobMovementSpiralLevelPos)) {
         return false;
      } else {
         MobMovementSpiralLevelPos var2 = (MobMovementSpiralLevelPos)var1;
         return this.centerX == var2.centerX && this.centerY == var2.centerY && super.equals(var1);
      }
   }
}
