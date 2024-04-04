package necesse.engine.util;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Iterator;
import necesse.engine.Screen;
import necesse.gfx.camera.GameCamera;

public class InverseKinematics {
   public GameLinkedList<Limb> limbs;

   public InverseKinematics(float var1, float var2, float var3, float var4, float var5, float var6) {
      this.limbs = new GameLinkedList();
      this.limbs.add(new Limb(var1, var2, var3, var4, var5, var6));
   }

   public InverseKinematics(float var1, float var2, float var3, float var4) {
      this(var1, var2, var3, var4, 180.0F, 180.0F);
   }

   public static InverseKinematics startFromPoints(float var0, float var1, float var2, float var3, float var4, float var5) {
      Point2D.Float var6 = new Point2D.Float(var2 - var0, var3 - var1);
      float var7 = (float)var6.distance(0.0, 0.0);
      float var8 = GameMath.getAngle(var6);
      return new InverseKinematics(var0, var1, var8, var7, var4, var5);
   }

   public static InverseKinematics startFromPoints(float var0, float var1, float var2, float var3) {
      return startFromPoints(var0, var1, var2, var3, 180.0F, 180.0F);
   }

   public void addJointAngle(float var1, float var2, float var3, float var4) {
      Limb var5 = (Limb)this.limbs.getLast();
      this.limbs.add(new Limb(var5.outboundX, var5.outboundY, var1, var2, var3, var4));
   }

   public void addJointAngle(float var1, float var2) {
      this.addJointAngle(var1, var2, 180.0F, 180.0F);
   }

   public void addJointPoint(float var1, float var2, float var3, float var4) {
      Limb var5 = (Limb)this.limbs.getLast();
      Point2D.Float var6 = new Point2D.Float(var1 - var5.outboundX, var2 - var5.outboundY);
      float var7 = (float)var6.distance(0.0, 0.0);
      float var8 = GameMath.getAngle(var6);
      this.limbs.add(new Limb(var5.outboundX, var5.outboundY, var8, var7, var3, var4));
   }

   public void addJointPoint(float var1, float var2) {
      this.addJointPoint(var1, var2, 180.0F, 180.0F);
   }

   public int getTotalJoints() {
      return this.limbs.size();
   }

   public Limb removeLastLimb() {
      if (this.limbs.size() == 1) {
         throw new IllegalStateException("Cannot remove the last joint");
      } else {
         return (Limb)this.limbs.removeLast();
      }
   }

   public int apply(float var1, float var2, float var3, float var4, int var5) {
      if (this.limbs.size() == 1) {
         this.apply(var1, var2);
         return 1;
      } else {
         Limb var6 = (Limb)this.limbs.getFirst();
         double var7 = (new Point2D.Float(var1, var2)).distance((double)var6.inboundX, (double)var6.inboundY);
         float var9 = 0.0F;
         float var10 = 0.0F;

         Limb var12;
         for(Iterator var11 = this.limbs.iterator(); var11.hasNext(); var9 += var12.length) {
            var12 = (Limb)var11.next();
            if (var12.length > var10) {
               var10 = var12.length;
            }
         }

         if (var10 >= var9 - var10) {
            float var19 = var10 - (var9 - var10);
            if (var7 < (double)var19) {
               var4 = (float)((double)var4 + ((double)var19 - var7));
            }
         }

         if (var7 > (double)var9) {
            var4 = (float)((double)var4 + (var7 - (double)var9));
         }

         double var20 = (double)var3;
         int var13 = 0;

         double var15;
         double var17;
         do {
            ++var13;
            this.apply(var1, var2);
            Limb var14 = (Limb)this.limbs.getLast();
            var15 = (new Point2D.Float(var14.outboundX, var14.outboundY)).distance((double)var1, (double)var2);
            var17 = Math.abs(var15 - var20);
            var20 = var15;
         } while(!(var17 <= (double)var3) && var13 < var5 && !(var15 <= (double)var4));

         return var13;
      }
   }

   public void apply(float var1, float var2) {
      Limb var3 = (Limb)this.limbs.getFirst();
      Point2D.Float var4 = new Point2D.Float(var3.inboundX, var3.inboundY);
      Point2D.Float var5 = new Point2D.Float(var1, var2);
      this.finalToRoot(var5);
      this.rootToFinal(var4);
   }

   protected Point2D.Float finalToRoot(Point2D.Float var1) {
      GameLinkedList.Element var2 = this.limbs.getLastElement();

      for(GameLinkedList.Element var3 = null; var2 != null; var2 = var2.prev()) {
         Limb var4 = (Limb)var2.object;
         var4.angle = GameMath.getAngle(new Point2D.Float(var1.x - var4.inboundX, var1.y - var4.inboundY));
         if (var3 != null && (var4.maxLeftAngle < 180.0F || var4.maxRightAngle < 180.0F)) {
            float var5 = ((Limb)var3.object).angle;
            float var6 = GameMath.getAngleDifference(var5, var4.angle);
            if (var6 < -var4.maxLeftAngle) {
               var4.angle -= -var6 - var4.maxLeftAngle;
            } else if (var6 > var4.maxRightAngle) {
               var4.angle += var6 - var4.maxRightAngle;
            }
         }

         var4.outboundX = var1.x;
         var4.outboundY = var1.y;
         var4.fixInboundPos();
         var1 = new Point2D.Float(var4.inboundX, var4.inboundY);
         var3 = var2;
      }

      return var1;
   }

   protected void rootToFinal(Point2D.Float var1) {
      Point2D.Float var2 = new Point2D.Float(var1.x, var1.y);

      for(GameLinkedList.Element var3 = this.limbs.getFirstElement(); var3 != null; var3 = var3.next()) {
         Limb var4 = (Limb)var3.object;
         var4.inboundX = var2.x;
         var4.inboundY = var2.y;
         var4.fixOutboundPos();
         var2 = new Point2D.Float(var4.outboundX, var4.outboundY);
      }

   }

   public static void apply(GameLinkedList<Limb>.Element var0, boolean var1, float var2, float var3, boolean var4) {
      GameLinkedList.Element var5 = var0;

      GameLinkedList.Element var6;
      for(var6 = var0; var5.hasPrev(); var5 = var5.prev()) {
      }

      while(var6.hasNext()) {
         var6 = var6.next();
      }

      Point2D.Float var7;
      if (var0 == var5 && var1) {
         var7 = new Point2D.Float(((Limb)var6.object).outboundX, ((Limb)var6.object).outboundY);
         finalToRoot(var0, true, -1, new Point2D.Float(var2, var3));
         if (var4) {
            rootToFinal(var6, (GameLinkedList.Element)null, true, -1, var7);
         }
      } else if (var0 == var6 && !var1) {
         var7 = new Point2D.Float(((Limb)var5.object).inboundX, ((Limb)var5.object).inboundY);
         finalToRoot(var0, false, 1, new Point2D.Float(var2, var3));
         if (var4) {
            rootToFinal(var5, (GameLinkedList.Element)null, false, 1, var7);
         }
      } else {
         var7 = new Point2D.Float(((Limb)var6.object).outboundX, ((Limb)var6.object).outboundY);
         Point2D.Float var8 = new Point2D.Float(((Limb)var5.object).inboundX, ((Limb)var5.object).inboundY);
         if (var1) {
            finalToRoot(var0.prev(), false, 1, new Point2D.Float(var2, var3));
            finalToRoot(var0, true, -1, new Point2D.Float(var2, var3));
            if (var4) {
               rootToFinal(var5, (GameLinkedList.Element)null, false, 1, var8);
            }
         } else {
            finalToRoot(var0, false, 1, new Point2D.Float(var2, var3));
            finalToRoot(var0.next(), true, -1, new Point2D.Float(var2, var3));
            if (var4) {
               rootToFinal(var6, (GameLinkedList.Element)null, true, -1, var7);
            }
         }
      }

   }

   protected static void finalToRoot(GameLinkedList<Limb>.Element var0, boolean var1, int var2, Point2D.Float var3) {
      while(var0 != null) {
         Limb var4 = (Limb)var0.object;
         if (var1) {
            var4.angle = GameMath.getAngle(new Point2D.Float(var4.outboundX - var3.x, var4.outboundY - var3.y));
            var4.inboundX = var3.x;
            var4.inboundY = var3.y;
            var4.fixOutboundPos();
            var3 = new Point2D.Float(var4.outboundX, var4.outboundY);
         } else {
            var4.angle = GameMath.getAngle(new Point2D.Float(var3.x - var4.inboundX, var3.y - var4.inboundY));
            var4.outboundX = var3.x;
            var4.outboundY = var3.y;
            var4.fixInboundPos();
            var3 = new Point2D.Float(var4.inboundX, var4.inboundY);
         }

         if (var2 < 0) {
            var0 = var0.next();
         } else {
            var0 = var0.prev();
         }
      }

   }

   protected static void rootToFinal(GameLinkedList<Limb>.Element var0, GameLinkedList<Limb>.Element var1, boolean var2, int var3, Point2D.Float var4) {
      Point2D.Float var5 = new Point2D.Float(var4.x, var4.y);

      while(var0 != null) {
         if (var0 == var1) {
            return;
         }

         Limb var6 = (Limb)var0.object;
         if (var2) {
            var6.outboundX = var5.x;
            var6.outboundY = var5.y;
            var6.fixInboundPos();
            var5 = new Point2D.Float(var6.inboundX, var6.inboundY);
         } else {
            var6.inboundX = var5.x;
            var6.inboundY = var5.y;
            var6.fixOutboundPos();
            var5 = new Point2D.Float(var6.outboundX, var6.outboundY);
         }

         if (var3 < 0) {
            var0 = var0.prev();
         } else {
            var0 = var0.next();
         }
      }

   }

   public void drawDebug(GameCamera var1, Color var2, Color var3) {
      this.drawDebug(var1, 0, 0, var2, var3);
   }

   public void drawDebug(GameCamera var1, int var2, int var3, Color var4, Color var5) {
      Iterator var6 = this.limbs.iterator();

      while(var6.hasNext()) {
         Limb var7 = (Limb)var6.next();
         Screen.drawLine(var2 + var1.getDrawX(var7.inboundX), var3 + var1.getDrawY(var7.inboundY), var2 + var1.getDrawX(var7.outboundX), var3 + var1.getDrawY(var7.outboundY), var4);
         float var8 = Math.min(50.0F, var7.length / 2.0F);
         Point2D.Float var9;
         if (var7.maxLeftAngle < 180.0F) {
            var9 = GameMath.getAngleDir(var7.angle - var7.maxLeftAngle);
            Screen.drawLine(var2 + var1.getDrawX(var7.outboundX), var3 + var1.getDrawY(var7.outboundY), var2 + var1.getDrawX(var7.outboundX + var9.x * var8), var3 + var1.getDrawY(var7.outboundY + var9.y * var8), var5);
         }

         if (var7.maxRightAngle < 180.0F) {
            var9 = GameMath.getAngleDir(var7.angle + var7.maxRightAngle);
            Screen.drawLine(var2 + var1.getDrawX(var7.outboundX), var3 + var1.getDrawY(var7.outboundY), var2 + var1.getDrawX(var7.outboundX + var9.x * var8), var3 + var1.getDrawY(var7.outboundY + var9.y * var8), var5);
         }

         Screen.drawCircle(var2 + var1.getDrawX(var7.inboundX), var3 + var1.getDrawY(var7.inboundY), 4, 12, var4, false);
         Screen.drawCircle(var2 + var1.getDrawX(var7.outboundX), var3 + var1.getDrawY(var7.outboundY), 4, 12, var4, false);
      }

   }

   public static class Limb {
      public float inboundX;
      public float inboundY;
      public float outboundX;
      public float outboundY;
      public float angle;
      public float length;
      public float maxLeftAngle;
      public float maxRightAngle;

      public Limb(float var1, float var2, float var3, float var4, float var5, float var6) {
         this.inboundX = var1;
         this.inboundY = var2;
         this.angle = var3;
         this.length = var4;
         this.maxLeftAngle = var5;
         this.maxRightAngle = var6;
         this.fixOutboundPos();
      }

      public void fixInboundPos() {
         Point2D.Float var1 = GameMath.getAngleDir(this.angle + 180.0F);
         this.inboundX = this.outboundX + var1.x * this.length;
         this.inboundY = this.outboundY + var1.y * this.length;
      }

      public void fixOutboundPos() {
         Point2D.Float var1 = GameMath.getAngleDir(this.angle);
         this.outboundX = this.inboundX + var1.x * this.length;
         this.outboundY = this.inboundY + var1.y * this.length;
      }
   }
}
