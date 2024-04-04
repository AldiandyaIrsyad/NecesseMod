package necesse.engine;

import java.util.ArrayList;
import java.util.Iterator;
import necesse.engine.util.GameMath;
import necesse.gfx.GameResources;
import org.lwjgl.opengl.GL11;

public class GameBezierCurve {
   public ArrayList<GameBezierPoint> points = new ArrayList();

   public GameBezierCurve() {
   }

   public void draw(float var1, float var2, float var3) {
      GameResources.empty.bind();
      GL11.glLoadIdentity();
      GL11.glBegin(1);
      GL11.glColor4f(0.0F, 0.0F, 1.0F, 1.0F);
      Iterator var4 = this.points.iterator();

      GameBezierPoint var5;
      while(var4.hasNext()) {
         var5 = (GameBezierPoint)var4.next();
         GL11.glVertex2f(var5.startX + var1, var5.startY + var2);
         GL11.glVertex2f(var5.targetX + var1, var5.targetY + var2);
      }

      GL11.glEnd();
      var4 = this.points.iterator();

      while(var4.hasNext()) {
         var5 = (GameBezierPoint)var4.next();
         GL11.glBegin(2);
         GL11.glColor4f(1.0F, 1.0F, 0.0F, 1.0F);
         byte var6 = 20;
         byte var7 = 5;

         for(int var8 = 0; var8 < var6; ++var8) {
            double var9 = 6.283185307179586 * (double)var8 / (double)var6;
            double var11 = (double)var7 * Math.cos(var9);
            double var13 = (double)var7 * Math.sin(var9);
            GL11.glVertex2d(var11 + (double)var5.startX + (double)var1, var13 + (double)var5.startY + (double)var2);
         }

         GL11.glEnd();
      }

      GL11.glBegin(3);
      GL11.glColor4f(1.0F, 0.0F, 0.0F, 1.0F);

      for(int var15 = 0; var15 < this.points.size() - 1; ++var15) {
         var5 = (GameBezierPoint)this.points.get(var15);
         GameBezierPoint var16 = (GameBezierPoint)this.points.get(var15 + 1);
         float var17 = var16.targetX - var16.startX;
         float var18 = var16.targetY - var16.startY;
         var16 = new GameBezierPoint(var16.startX, var16.startY, var16.startX - var17, var16.startY - var18);
         float var19 = GameMath.getExactDistance(var5.startX, var5.startY, var5.targetX, var5.targetY) + GameMath.getExactDistance(var5.targetX, var5.targetY, var16.startX, var16.startY) + GameMath.getExactDistance(var5.startX, var5.startY, var5.targetX, var5.targetY);
         int var10 = (int)(var19 / var3) + 1;

         for(int var20 = 0; var20 <= var10; ++var20) {
            float var12 = (float)var20 / (float)var10;
            float var21 = var5.getPointXOnCurve(var16, var12) + var1;
            float var14 = var5.getPointYOnCurve(var16, var12) + var2;
            GL11.glVertex2f(var21, var14);
         }
      }

      GL11.glEnd();
   }
}
