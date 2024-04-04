package necesse.entity.mobs.mobMovement;

import java.awt.Point;
import java.awt.geom.Point2D;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.Mob;

public abstract class MobMovementCircle extends MobMovement {
   public int range;
   public float speed;
   public float angleOffset;
   public boolean reversed;

   public MobMovementCircle() {
   }

   public MobMovementCircle(Mob var1, int var2, float var3, float var4, boolean var5) {
      this();
      this.range = var2;
      this.speed = var3;
      this.angleOffset = var4;
      this.reversed = var5;
   }

   public MobMovementCircle(Mob var1, float var2, float var3, int var4, float var5, boolean var6) {
      this(var1, var4, var5, 0.0F, var6);
      float var7 = getTimeAngle(var1.getWorldEntity().getTime(), var5, var6);
      Point2D.Float var8 = GameMath.normalize(var2 - (float)var1.getX(), var3 - (float)var1.getY());
      this.angleOffset = GameMath.fixAngle((float)Math.toDegrees(Math.atan2((double)var8.y, (double)var8.x)) - 90.0F - var7);
   }

   public void setupPacket(Mob var1, PacketWriter var2) {
      var2.putNextInt(this.range);
      var2.putNextFloat(this.speed);
      var2.putNextFloat(this.angleOffset);
      var2.putNextBoolean(this.reversed);
   }

   public void applyPacket(Mob var1, PacketReader var2) {
      this.range = var2.getNextInt();
      this.speed = var2.getNextFloat();
      this.angleOffset = var2.getNextFloat();
      this.reversed = var2.getNextBoolean();
      this.tick(var1);
   }

   public abstract Point2D.Float getCenterPos();

   public float getCurrentAngle(Mob var1) {
      return getTimeAngle(var1.getWorldEntity().getTime(), this.speed, this.reversed) + this.angleOffset;
   }

   public Point2D.Float getCurrentPos(Mob var1) {
      Point2D.Float var2 = this.getCenterPos();
      if (var2 != null) {
         Point2D.Float var3 = getOffsetPositionFloat(var1, this.range, this.speed, this.angleOffset, this.reversed);
         return new Point2D.Float(var2.x + var3.x, var2.y + var3.y);
      } else {
         return null;
      }
   }

   public boolean tick(Mob var1) {
      Point2D.Float var2 = this.getCurrentPos(var1);
      if (var2 != null) {
         this.moveTo(var1, var2.x, var2.y, (float)var1.moveAccuracy);
      }

      return false;
   }

   public static float convertToRotSpeed(int var0, float var1) {
      float var2 = (float)((double)(var0 * 2) * Math.PI);
      float var3 = var2 / var1;
      return 10.0F / var3 * 4.0F;
   }

   public static Point getOffsetPosition(Mob var0, int var1, float var2, float var3, boolean var4) {
      Point2D.Float var5 = getOffsetPositionFloat(var0, var1, var2, var3, var4);
      return new Point((int)var5.x, (int)var5.y);
   }

   public static Point2D.Float getOffsetPositionFloat(Mob var0, int var1, float var2, float var3, boolean var4) {
      float var5 = getTimeAngle(var0.getWorldEntity().getTime(), var2, var4);
      var5 += var3;
      double var6 = (double)GameMath.cos(var5);
      double var8 = (double)GameMath.sin(var5);
      return new Point2D.Float((float)(var8 * (double)var1), (float)(-(var6 * (double)var1)));
   }

   public static float getTimeAngle(long var0, float var2, boolean var3) {
      float var4 = (float)((double)var0 / 1000.0 * (double)var2 * 36.0 % 360.0);
      return var3 ? -var4 : var4;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof MobMovementCircle)) {
         return false;
      } else {
         MobMovementCircle var2 = (MobMovementCircle)var1;
         return this.range == var2.range && this.speed == var2.speed && this.angleOffset == var2.angleOffset && this.reversed == var2.reversed;
      }
   }
}
