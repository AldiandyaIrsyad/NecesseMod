package necesse.engine.util;

import java.awt.Point;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.level.maps.Level;

public abstract class GroundPillar extends Point {
   public Behaviour behaviour = new DistanceTimedBehaviour(1000, 0, 500, 30.0, 20.0, 100.0);
   public final double spawnDistance;
   public final long spawnTime;

   public GroundPillar(int var1, int var2, double var3, long var5) {
      super(var1, var2);
      this.spawnDistance = var3;
      this.spawnTime = var5;
   }

   public double getHeight(long var1, double var3) {
      return this.behaviour.getHeight(var1 - this.spawnTime, var3 - this.spawnDistance);
   }

   public boolean shouldRemove(long var1, double var3) {
      return this.behaviour.shouldRemove(var1 - this.spawnTime, var3 - this.spawnDistance);
   }

   public abstract DrawOptions getDrawOptions(Level var1, long var2, double var4, GameCamera var6);

   public static class DistanceTimedBehaviour extends Behaviour {
      public int timeout;
      public int timeAppear;
      public int timeDisappear;
      public double maxDistance;
      public double distanceAppear;
      public double distanceFalloff;

      public DistanceTimedBehaviour(int var1, int var2, int var3, double var4, double var6, double var8) {
         this.timeout = var1;
         this.timeAppear = var2;
         this.timeDisappear = var3;
         this.maxDistance = var4;
         this.distanceAppear = var6;
         this.distanceFalloff = var8;
      }

      public double getHeight(long var1, double var3) {
         if (!(var3 < this.distanceAppear) && var1 >= (long)this.timeAppear) {
            double var5 = 1.0;
            if (var3 > this.distanceAppear + this.maxDistance) {
               var5 = Math.min(var5, Math.abs((var3 - (this.distanceAppear + this.maxDistance)) / this.distanceFalloff - 1.0));
            }

            if (var1 > (long)(this.timeout + this.timeAppear)) {
               if (var3 < this.distanceAppear + this.distanceFalloff) {
                  return Math.abs((var3 - this.distanceAppear) / this.distanceFalloff - 1.0);
               }

               long var7 = var1 - (long)(this.timeout + this.timeAppear);
               var5 = Math.min(var5, Math.abs((double)var7 / (double)this.timeDisappear - 1.0));
            }

            return var5;
         } else {
            return Math.min(var3 / this.distanceAppear, (double)var1 / (double)this.timeAppear);
         }
      }

      public boolean shouldRemove(long var1, double var3) {
         if (var3 < this.distanceAppear) {
            return false;
         } else if (var3 < this.distanceAppear + this.distanceFalloff) {
            return false;
         } else if (var1 > (long)(this.timeout + this.timeDisappear + this.timeAppear)) {
            return true;
         } else {
            return var3 > this.distanceAppear + this.maxDistance + this.distanceFalloff;
         }
      }
   }

   public abstract static class Behaviour {
      public Behaviour() {
      }

      public abstract double getHeight(long var1, double var3);

      public abstract boolean shouldRemove(long var1, double var3);
   }

   public static class DistanceBehaviour extends Behaviour {
      public double maxDistance;
      public double distanceAppear;
      public double distanceFalloff;

      public DistanceBehaviour(double var1, double var3, double var5) {
         this.maxDistance = var1;
         this.distanceAppear = var3;
         this.distanceFalloff = var5;
      }

      public double getHeight(long var1, double var3) {
         if (var3 < this.distanceAppear) {
            return var3 / this.distanceAppear;
         } else {
            return var3 > this.distanceAppear + this.maxDistance ? Math.abs((var3 - (this.distanceAppear + this.maxDistance)) / this.distanceFalloff - 1.0) : 1.0;
         }
      }

      public boolean shouldRemove(long var1, double var3) {
         if (var3 < this.distanceAppear) {
            return false;
         } else if (var3 < this.distanceAppear + this.distanceFalloff) {
            return false;
         } else {
            return var3 > this.distanceAppear + this.maxDistance + this.distanceFalloff;
         }
      }
   }

   public static class TimedBehaviour extends Behaviour {
      public int timeout;
      public int timeAppear;
      public int timeDisappear;

      public TimedBehaviour(int var1, int var2, int var3) {
         this.timeout = var1;
         this.timeAppear = var2;
         this.timeDisappear = var3;
      }

      public double getHeight(long var1, double var3) {
         if (var1 < (long)this.timeAppear) {
            return (double)var1 / (double)this.timeAppear;
         } else if (var1 > (long)(this.timeout + this.timeAppear)) {
            long var5 = var1 - (long)(this.timeout + this.timeAppear);
            return Math.abs((double)var5 / (double)this.timeDisappear - 1.0);
         } else {
            return 1.0;
         }
      }

      public boolean shouldRemove(long var1, double var3) {
         return var1 > (long)(this.timeout + this.timeDisappear + this.timeAppear);
      }
   }
}
