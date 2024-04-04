package necesse.entity.trails;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.LinkedList;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import org.lwjgl.opengl.GL11;

public class TrailDrawSection {
   public final Trail trail;
   public int pointsStartIndex;
   public int pointsEndIndex;
   public int yTile;

   public TrailDrawSection(Trail var1, int var2, int var3, int var4) {
      this.trail = var1;
      this.yTile = var2;
      this.pointsStartIndex = var3;
      this.pointsEndIndex = var4;
   }

   public static TrailPointConsumer fadeLightColorSetter(int var0, Level var1, Color var2) {
      return fadeLightColorSetter(var1 == null ? 0L : var1.getWorldEntity().getLocalTime(), var0, var1, var2);
   }

   public static TrailPointConsumer fadeLightColorSetter(long var0, int var2, Level var3, Color var4) {
      return (var5, var6, var7) -> {
         GameLight var9 = var3 == null ? new GameLight(150.0F) : var3.getLightLevel((int)var5.getX() / 32, (int)var5.getY() / 32);
         float var10 = (float)(var0 - var7);
         var9.getGLColorSetter((float)var4.getRed() / 255.0F, (float)var4.getGreen() / 255.0F, (float)var4.getBlue() / 255.0F, (float)var4.getAlpha() / 255.0F - var10 / (float)var2).run();
      };
   }

   public static TrailPointConsumer lightColorSetter(Level var0, Color var1) {
      return (var2, var3, var4) -> {
         GameLight var6 = var0 == null ? new GameLight(150.0F) : var0.getLightLevel((int)var2.getX() / 32, (int)var2.getY() / 32);
         var6.getGLColorSetter((float)var1.getRed() / 255.0F, (float)var1.getGreen() / 255.0F, (float)var1.getBlue() / 255.0F, (float)var1.getAlpha() / 255.0F).run();
      };
   }

   public DrawOptions getSpriteTrailsDraw(GameSprite var1, GameCamera var2, TrailPointConsumer var3) {
      synchronized(this.trail) {
         TrailPointList var5 = this.trail.points;
         int var6 = this.pointsStartIndex;
         int var7 = this.pointsEndIndex;
         return getSpriteTrailsDraw(var1, var5, var6, var7, var2, var3);
      }
   }

   public static DrawOptions getSpriteTrailsDraw(GameSprite var0, TrailPointList var1, int var2, int var3, GameCamera var4, TrailPointConsumer var5) {
      int var6 = var0.texture.getWidth();
      int var7 = var0.texture.getHeight();
      float var8 = TextureDrawOptions.pixel(var0.spriteX, 1, var0.spriteWidth, var6);
      float var9 = TextureDrawOptions.pixel(var0.spriteX + 1, -1, var0.spriteWidth, var6);
      float var10 = TextureDrawOptions.pixel(var0.spriteY, 1, var0.spriteHeight, var7);
      float var11 = TextureDrawOptions.pixel(var0.spriteY + 1, -1, var0.spriteHeight, var7);
      LinkedList var12 = new LinkedList();
      var3 = Math.min(var3, var1.size() - 1);

      for(int var13 = var2; var13 <= var3; ++var13) {
         var12.add(new TrailDrawOption(var1.get(var13), var13 % 2));
      }

      return () -> {
         var0.texture.bind();
         GL11.glLoadIdentity();
         GL11.glBegin(5);
         Iterator var8x = var12.iterator();

         while(true) {
            TrailDrawOption var9x;
            Point2D var10x;
            int var11x;
            int var12x;
            Point2D var13;
            int var14;
            int var15;
            do {
               do {
                  if (!var8x.hasNext()) {
                     GL11.glEnd();
                     return;
                  }

                  var9x = (TrailDrawOption)var8x.next();
                  var10x = var9x.draw1;
                  var11x = var4.getDrawX((int)var10x.getX());
                  var12x = var4.getDrawY((int)(var10x.getY() - (double)var9x.height));
                  var13 = var9x.draw2;
                  var14 = var4.getDrawX((int)var13.getX());
                  var15 = var4.getDrawY((int)(var13.getY() - (double)var9x.height));
               } while(var10x.getX() == 0.0 && var10x.getY() == 0.0);
            } while(var13.getX() == 0.0 && var13.getY() == 0.0);

            var5.apply(var10x, var13, var9x.spawnTime);
            if (var9x.tex == 0) {
               GL11.glTexCoord2f(var8, var10);
            } else {
               GL11.glTexCoord2f(var8, var11);
            }

            GL11.glVertex2f((float)var11x, (float)var12x);
            if (var9x.tex == 0) {
               GL11.glTexCoord2f(var9, var10);
            } else {
               GL11.glTexCoord2f(var9, var11);
            }

            GL11.glVertex2f((float)var14, (float)var15);
         }
      };
   }

   @FunctionalInterface
   public interface TrailPointConsumer {
      void apply(Point2D var1, Point2D var2, long var3);
   }

   private static class TrailDrawOption {
      public final Point2D draw1;
      public final Point2D draw2;
      public final long spawnTime;
      public final float height;
      public final int tex;

      public TrailDrawOption(TrailPointList.TrailPoint var1, int var2) {
         this.draw1 = var1.getDrawPos1();
         this.draw2 = var1.getDrawPos2();
         this.spawnTime = var1.spawnTime;
         this.height = var1.vector.height;
         this.tex = var2;
      }
   }
}
