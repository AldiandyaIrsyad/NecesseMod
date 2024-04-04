package necesse.entity.particle.fireworks;

import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.function.Function;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.Ray;
import necesse.engine.util.RayLinkedList;
import necesse.entity.particle.ParticleOption;

public class FireworksPath {
   private static float phi = (float)(Math.PI * (3.0 - Math.sqrt(5.0)));
   public static Polygon star = new Polygon();
   public static Polygon heart;
   public ParticleOption.FloatGetter delta = (var0, var1x, var2x) -> {
      return (float)Math.pow((double)var2x, 0.30000001192092896);
   };
   public float dx;
   public float dy;
   public float dh;

   public static FireworksRocketParticle.ParticleGetter<FireworksPath> sphere(float var0) {
      return (var1, var2, var3) -> {
         float var4 = 1.0F - var2 * 2.0F;
         float var5 = (float)Math.sqrt((double)(1.0F - var4 * var4));
         float var6 = phi * (float)var1;
         float var7 = (float)(Math.cos((double)var6) * (double)var5);
         float var8 = (float)(Math.sin((double)var6) * (double)var5);
         float var9 = 0.25F;
         return new FireworksPath(var7 * var0, var8 * var0 * (1.0F - var9), -var8 * var0 * var9);
      };
   }

   public static FireworksRocketParticle.ParticleGetter<FireworksPath> disc(float var0) {
      return (var1, var2, var3) -> {
         float var4 = GameMath.cos(var2 * 360.0F);
         float var5 = GameMath.sin(var2 * 360.0F) * 0.3F;
         float var6 = var1 % 2 == 0 ? 1.15F : 0.85F;
         float var7 = 0.25F;
         return new FireworksPath(var4 * var6 * var0, var5 * var6 * var0 * (1.0F - var7), -var5 * var6 * var0 * var7);
      };
   }

   public static FireworksRocketParticle.ParticleGetter<FireworksPath> splash(float var0, float var1) {
      Point2D.Float var2 = GameMath.getAngleDir(var0);
      return (var2x, var3, var4) -> {
         float var5 = var2.x / 2.0F + GameMath.cos(var3 * 360.0F);
         float var6 = (var2.y / 2.0F + GameMath.sin(var3 * 360.0F)) * 0.8F;
         float var7 = var4.getFloatBetween(0.0F, var1);
         float var8 = 0.25F;
         return new FireworksPath(var5 * var7, var6 * var7 * (1.0F - var8), -var6 * var7 * var8);
      };
   }

   public static FireworksRocketParticle.ParticleGetter<FireworksPath> shape(Shape var0, float var1) {
      return shape(var0, var1, (var0x) -> {
         return 1.0F;
      });
   }

   public static FireworksRocketParticle.ParticleGetter<FireworksPath> shape(Shape var0, float var1, Function<GameRandom, Float> var2) {
      int var3 = 0;
      double var4 = 0.0;
      double var6 = 0.0;
      RayLinkedList var8 = new RayLinkedList();
      double[] var9 = null;
      double[] var10 = new double[6];

      for(PathIterator var11 = var0.getPathIterator((AffineTransform)null); !var11.isDone(); var11.next()) {
         int var12 = var11.currentSegment(var10);
         if (var12 == 4) {
            var9 = null;
         } else if (var12 == 0) {
            var9 = new double[]{var10[0], var10[1]};
         } else if (var12 == 1) {
            if (var9 != null) {
               var4 += var9[0] + var10[0];
               var6 += var9[1] + var10[1];
               var3 += 2;
               Ray var13 = new Ray(var9[0], var9[1], var10[0], var10[1], false);
               var8.addLast(var13);
               var8.totalDist += var13.dist;
            }

            var9 = new double[]{var10[0], var10[1]};
         }
      }

      Rectangle2D var18 = var0.getBounds2D();
      double var19 = (double)var1 / (new Point2D.Double(var18.getWidth(), var18.getHeight())).distance(0.0, 0.0) * 2.5;
      double var14 = var4 / (double)var3;
      double var16 = var6 / (double)var3;
      return (var8x, var9x, var10x) -> {
         double var11 = var8.totalDist * (double)var9x;
         double var13 = 0.0;
         double var15 = 0.0;

         Ray var18;
         for(Iterator var17 = var8.iterator(); var17.hasNext(); var11 -= var18.dist) {
            var18 = (Ray)var17.next();
            if (var11 <= var18.dist) {
               Point2D.Double var19x = GameMath.normalize(var18.x2 - var18.x1, var18.y2 - var18.y1);
               Point2D.Double var20 = new Point2D.Double(var18.x1 + var19x.x * var11, var18.y1 + var19x.y * var11);
               Point2D.Double var21 = new Point2D.Double(var20.x - var14, var20.y - var16);
               Float var22 = (Float)var2.apply(var10x);
               var13 = var21.x * (double)var22 * var19;
               var15 = var21.y * (double)var22 * var19;
               break;
            }
         }

         return new FireworksPath((float)var13, (float)var15, 0.0F);
      };
   }

   public FireworksPath(float var1, float var2, float var3) {
      this.dx = var1;
      this.dy = var2;
      this.dh = var3;
   }

   static {
      star.addPoint(0, 0);
      star.addPoint(1, 2);
      star.addPoint(3, 2);
      star.addPoint(1, 3);
      star.addPoint(2, 5);
      star.addPoint(0, 4);
      star.addPoint(-2, 5);
      star.addPoint(-1, 3);
      star.addPoint(-3, 2);
      star.addPoint(-1, 2);
      star.addPoint(0, 0);
      heart = new Polygon();
      heart.addPoint(0, 0);
      heart.addPoint(1, -1);
      heart.addPoint(2, -1);
      heart.addPoint(3, 0);
      heart.addPoint(3, 1);
      heart.addPoint(0, 4);
      heart.addPoint(-3, 1);
      heart.addPoint(-3, 0);
      heart.addPoint(-2, -1);
      heart.addPoint(-1, -1);
      heart.addPoint(0, 0);
   }
}
