package necesse.engine.sound;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Iterator;
import necesse.engine.util.ComputedValue;
import necesse.engine.util.GameMath;

public interface LinesSoundEmitter extends PrimitiveSoundEmitter {
   Iterable<Line2D> getSoundLines();

   default float getSoundDistance(float var1, float var2) {
      Point2D.Float var3 = new Point2D.Float(var1, var2);
      float var4 = -1.0F;
      Iterable var5 = this.getSoundLines();
      Iterator var6 = var5.iterator();

      while(true) {
         ComputedValue var9;
         do {
            if (!var6.hasNext()) {
               return var4;
            }

            Line2D var7 = (Line2D)var6.next();
            Point2D var8 = GameMath.getClosestPointOnLine(var7, var3, false);
            var9 = new ComputedValue(() -> {
               return (float)var8.distance(var3);
            });
         } while(var4 != -1.0F && !((Float)var9.get() < var4));

         var4 = (Float)var9.get();
      }
   }

   default Point2D.Float getSoundDirection(float var1, float var2) {
      Point2D.Float var3 = new Point2D.Float(var1, var2);
      Point2D var4 = null;
      float var5 = -1.0F;
      Iterable var6 = this.getSoundLines();
      Iterator var7 = var6.iterator();

      while(true) {
         Point2D var9;
         ComputedValue var10;
         do {
            if (!var7.hasNext()) {
               if (var4 == null) {
                  return null;
               }

               return GameMath.normalize((float)var4.getX() - var1, (float)var4.getY() - var2);
            }

            Line2D var8 = (Line2D)var7.next();
            var9 = GameMath.getClosestPointOnLine(var8, var3, false);
            var10 = new ComputedValue(() -> {
               return (float)var9.distance(var3);
            });
         } while(var4 != null && !((Float)var10.get() < var5));

         var4 = var9;
         var5 = (Float)var10.get();
      }
   }
}
