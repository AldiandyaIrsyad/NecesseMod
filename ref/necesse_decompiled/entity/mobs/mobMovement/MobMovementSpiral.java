package necesse.entity.mobs.mobMovement;

import java.awt.geom.Point2D;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.Mob;

public abstract class MobMovementSpiral extends MobMovement {
   public long startTime;
   public float outerRadius;
   public int semiCircles;
   public float radiusDecreasePerSemiCircle;
   public float speed;
   public float startAngle;
   public boolean clockwise;
   public int currentSemiCircle;
   public long currentTimePassed;

   public static int getSemiCircles(float var0, float var1, float var2) {
      float var3 = var0 - var2;
      return (int)(var3 / var1);
   }

   public MobMovementSpiral() {
   }

   public MobMovementSpiral(Mob var1, float var2, int var3, float var4, float var5, float var6, boolean var7) {
      this();
      this.startTime = var1.getWorldEntity().getTime();
      this.outerRadius = var2;
      this.semiCircles = var3;
      this.radiusDecreasePerSemiCircle = var4;
      this.speed = var5;
      this.startAngle = var6;
      this.clockwise = var7;
   }

   public MobMovementSpiral(Mob var1, float var2, float var3, float var4, int var5, float var6, float var7, boolean var8) {
      this(var1, var4, var5, var6, var7, 0.0F, var8);
      Point2D.Float var9 = GameMath.normalize(var2 - var1.x, var3 - var1.y);
      this.startAngle = GameMath.fixAngle(GameMath.getAngle(var9) - 90.0F);
   }

   public void setupPacket(Mob var1, PacketWriter var2) {
      var2.putNextLong(this.startTime);
      var2.putNextFloat(this.outerRadius);
      var2.putNextInt(this.semiCircles);
      var2.putNextFloat(this.radiusDecreasePerSemiCircle);
      var2.putNextFloat(this.speed);
      var2.putNextFloat(this.startAngle);
      var2.putNextBoolean(this.clockwise);
   }

   public void applyPacket(Mob var1, PacketReader var2) {
      this.startTime = var2.getNextLong();
      this.outerRadius = var2.getNextFloat();
      this.semiCircles = var2.getNextInt();
      this.radiusDecreasePerSemiCircle = var2.getNextFloat();
      this.speed = var2.getNextFloat();
      this.startAngle = var2.getNextFloat();
      this.clockwise = var2.getNextBoolean();
      this.tick(var1);
   }

   public abstract Point2D.Float getCenterPos();

   public float getCurrentRadius() {
      return Math.abs(this.outerRadius - (float)this.currentSemiCircle * this.radiusDecreasePerSemiCircle);
   }

   public Point2D.Float getCurrentPos(Mob var1) {
      Point2D.Float var2 = this.getCenterPos();
      if (var2 == null) {
         return null;
      } else {
         while(true) {
            float var3 = this.getCurrentRadius();
            int var4 = this.getTimeForSemiCircle(var3);
            long var5 = var1.getWorldEntity().getTime() - this.startTime;
            long var7 = var5 - this.currentTimePassed;
            if (var7 <= (long)var4) {
               float var9 = (float)var7 / (float)var4;
               float var10 = var9 * 180.0F;
               if (this.currentSemiCircle % 2 != 0) {
                  var10 += 180.0F;
               }

               if (!this.clockwise) {
                  var10 = -var10;
               }

               var10 += this.startAngle;
               double var11 = (double)GameMath.cos(var10);
               double var13 = (double)GameMath.sin(var10);
               return new Point2D.Float(var2.x + (float)(var13 * (double)var3), var2.y + (float)(-(var11 * (double)var3)));
            }

            this.currentTimePassed += (long)var4;
            ++this.currentSemiCircle;
         }
      }
   }

   public boolean tick(Mob var1) {
      Point2D.Float var2 = this.getCurrentPos(var1);
      if (var2 != null) {
         this.moveTo(var1, var2.x, var2.y, (float)var1.moveAccuracy);
      }

      return this.currentSemiCircle > this.semiCircles;
   }

   public static int getTimeForSemiCircle(float var0, float var1) {
      float var2 = (float)((double)var0 * Math.PI);
      float var3 = 1000.0F * var1 / 250.0F;
      float var4 = var2 / var3;
      return (int)(var4 * 1000.0F);
   }

   public int getTimeForSemiCircle(float var1) {
      return getTimeForSemiCircle(var1, this.speed);
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof MobMovementSpiral)) {
         return false;
      } else {
         MobMovementSpiral var2 = (MobMovementSpiral)var1;
         return this.startTime == var2.startTime && this.outerRadius == var2.outerRadius && this.semiCircles == var2.semiCircles && this.radiusDecreasePerSemiCircle == var2.radiusDecreasePerSemiCircle && this.speed == var2.speed && this.startAngle == var2.startAngle && this.clockwise == var2.clockwise;
      }
   }
}
