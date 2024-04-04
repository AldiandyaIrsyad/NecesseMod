package necesse.entity.particle;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SmokeDirectionParticle extends Particle {
   float angle;
   public Color col;

   public SmokeDirectionParticle(Level var1, float var2, float var3, float var4, float var5, float var6) {
      super(var1, var2, var3, 1000L);
      this.setTarget(var4, var5);
      this.dx *= var6;
      this.dy *= var6;
      this.friction = 1.0F;
      this.col = new Color(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      float var9 = this.getLifeCyclePercent();
      if (!this.removed()) {
         GameLight var10 = var5.getLightLevel(this);
         int var11 = this.getX() - var7.getX() - 16;
         int var12 = this.getY() - var7.getY() - 16 - 16;
         int var13 = var9 < 0.5F ? 0 : 1;
         byte var14 = 4;
         if (var9 < 0.1F) {
            var14 = 0;
         } else if (var9 < 0.2F) {
            var14 = 1;
         } else if (var9 < 0.3F) {
            var14 = 2;
         } else if (var9 < 0.4F) {
            var14 = 3;
         } else if (var9 < 0.5F) {
            var14 = 4;
         } else if (var9 < 0.6F) {
            var14 = 0;
         } else if (var9 < 0.7F) {
            var14 = 1;
         } else if (var9 < 0.8F) {
            var14 = 2;
         } else if (var9 < 0.9F) {
            var14 = 3;
         }

         final TextureDrawOptionsEnd var15 = GameResources.smokeParticles.initDraw().sprite(var14, var13 + 4, 64).colorLight(this.col, var10).rotate(this.getAngle() - 90.0F, 16, 16).size(32, 32).pos(var11, var12);
         var1.add(new EntityDrawable(this) {
            public void draw(TickManager var1) {
               var15.draw();
            }
         });
      }
   }

   public void moveDist(float var1) {
      Point2D.Float var2 = this.getDir(this.dx, this.dy);
      this.x += var2.x * var1;
      this.y += var2.y * var1;
   }

   public void setTarget(float var1, float var2) {
      Point2D.Float var3 = this.getDir(var1 - this.x, var2 - this.y);
      this.dx = var3.x;
      this.dy = var3.y;
      this.angle = (float)Math.toDegrees(Math.atan((double)(this.dy / this.dx)));
      if (this.dx < 0.0F) {
         this.angle += 270.0F;
      } else {
         this.angle += 90.0F;
      }

   }

   public Point2D.Float getDir(float var1, float var2) {
      Point2D.Float var3 = new Point2D.Float(var1, var2);
      float var4 = (float)var3.distance(0.0, 0.0);
      float var5 = var3.x / var4;
      float var6 = var3.y / var4;
      return new Point2D.Float(var5, var6);
   }

   public float getAngle() {
      return this.angle % 360.0F;
   }
}
