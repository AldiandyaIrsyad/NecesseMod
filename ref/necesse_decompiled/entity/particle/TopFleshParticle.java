package necesse.entity.particle;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class TopFleshParticle extends Particle {
   GameTexture texture;
   int spriteX;
   int spriteY;
   int spriteRes;
   int rotation;
   boolean mirrored;
   public float height;
   public float dh;

   public TopFleshParticle(Level var1, GameTexture var2, int var3, int var4, int var5, float var6, float var7, float var8, float var9) {
      super(var1, var6, var7, 5000L);
      this.texture = var2;
      this.spriteX = var3;
      this.spriteY = var4;
      this.spriteRes = var5;
      this.x = var6;
      this.y = var7;
      this.friction = 0.5F;
      this.rotation = GameRandom.globalRandom.nextInt(360);
      this.mirrored = GameRandom.globalRandom.nextBoolean();
      this.dx = (float)GameRandom.globalRandom.nextGaussian() * 20.0F;
      this.dy = (float)GameRandom.globalRandom.nextGaussian() * 20.0F;
      Point2D.Float var10 = this.getNorm(var8, var9);
      this.dx += var10.x * 50.0F;
      this.dy += var10.y * 50.0F;
      this.hasCollision = false;
      this.collision = new Rectangle(-5, -5, 10, 10);
      this.height = GameRandom.globalRandom.getFloatBetween(10.0F, 20.0F);
      this.dh = GameRandom.globalRandom.getFloatBetween(20.0F, 30.0F);
   }

   public TopFleshParticle(Level var1, GameTexture var2, int var3, int var4, int var5, float var6, float var7, float var8, float var9, float var10) {
      this(var1, var2, var3, var4, var5, var6, var7 - GameRandom.globalRandom.nextFloat() * var8, var9, var10);
   }

   public void tickMovement(float var1) {
      super.tickMovement(var1);
      if (this.height != -1.0F) {
         float var2 = 30.0F * var1 / 250.0F;
         this.dh -= var2;
         this.height += this.dh * var1 / 250.0F;
         if (this.height < 0.0F) {
            this.dh = -this.dh * 0.5F;
            this.height = -this.height;
            if (Math.abs(this.dh) < var2 * 2.0F) {
               this.height = -1.0F;
               this.dh = 0.0F;
            }
         }
      }

   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      float var9 = this.getLifeCyclePercent();
      if (!this.removed()) {
         GameLight var10 = var5.getLightLevel(this);
         int var11 = var7.getDrawX(this.x) - this.spriteRes / 2;
         int var12 = var7.getDrawY(this.y) - this.spriteRes / 2 + 4 - (int)Math.max(0.0F, this.height);
         float var13 = 1.0F;
         if (var9 > 0.5F) {
            var13 = Math.abs(var9 - 1.0F) * 2.0F;
         }

         TextureDrawOptionsEnd var14 = this.texture.initDraw().sprite(this.spriteX, this.spriteY, this.spriteRes).light(var10).alpha(var13).rotate((float)this.rotation, this.spriteRes / 2, this.spriteRes / 2).mirror(this.mirrored, false).pos(var11, var12);
         var3.add((var1x) -> {
            var14.draw();
         });
      }
   }

   public Point2D.Float getNorm(float var1, float var2) {
      float var3 = (float)(new Point2D.Float(0.0F, 0.0F)).distance((double)var1, (double)var2);
      return var3 == 0.0F ? new Point2D.Float(0.0F, 0.0F) : new Point2D.Float(var1 / var3, var2 / var3);
   }
}
