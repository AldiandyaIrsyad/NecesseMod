package necesse.entity.particle;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class FleshParticle extends Particle {
   public GameTextureSection sprite;
   public int rotation;
   public boolean mirrored;
   public float height;
   public float dh;

   public FleshParticle(Level var1, GameTextureSection var2, float var3, float var4, float var5, float var6) {
      super(var1, var3, var4, 5000L);
      this.sprite = var2;
      this.x = var3;
      this.y = var4;
      this.friction = 0.5F;
      this.rotation = GameRandom.globalRandom.nextInt(360);
      this.mirrored = GameRandom.globalRandom.nextBoolean();
      this.dx = (float)GameRandom.globalRandom.nextGaussian() * 20.0F;
      this.dy = (float)GameRandom.globalRandom.nextGaussian() * 20.0F;
      Point2D.Float var7 = GameMath.normalize(var5, var6);
      this.dx += var7.x * 50.0F;
      this.dy += var7.y * 50.0F;
      this.hasCollision = true;
      this.collision = new Rectangle(-5, -5, 10, 10);
      this.height = GameRandom.globalRandom.getFloatBetween(10.0F, 20.0F);
      this.dh = GameRandom.globalRandom.getFloatBetween(20.0F, 30.0F);
   }

   public FleshParticle(Level var1, GameTextureSection var2, float var3, float var4, float var5, float var6, float var7) {
      this(var1, var2, var3, var4 - GameRandom.globalRandom.nextFloat() * var5, var6, var7);
   }

   public FleshParticle(Level var1, GameTexture var2, int var3, int var4, int var5, float var6, float var7, float var8, float var9) {
      this(var1, (new GameTextureSection(var2)).sprite(var3, var4, var5), var6, var7, var8, var9);
   }

   public FleshParticle(Level var1, GameTexture var2, int var3, int var4, int var5, int var6, float var7, float var8, float var9, float var10) {
      this(var1, (new GameTextureSection(var2)).sprite(var3, var4, var5, var6), var7, var8, var9, var10);
   }

   public FleshParticle(Level var1, GameTexture var2, int var3, int var4, int var5, float var6, float var7, float var8, float var9, float var10) {
      this(var1, var2, var3, var4, var5, var6, var7 - GameRandom.globalRandom.nextFloat() * var8, var9, var10);
   }

   public FleshParticle(Level var1, GameTexture var2, int var3, int var4, int var5, int var6, float var7, float var8, float var9, float var10, float var11) {
      this(var1, var2, var3, var4, var5, var6, var7, var8 - GameRandom.globalRandom.nextFloat() * var9, var10, var11);
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
         int var11 = this.sprite.getWidth() / 2;
         int var12 = this.sprite.getHeight() / 2;
         int var13 = var7.getDrawX(this.x) - var11;
         int var14 = var7.getDrawY(this.y) - var12 + 4 - (int)Math.max(0.0F, this.height);
         float var15 = 1.0F;
         if (var9 > 0.5F) {
            var15 = Math.abs(var9 - 1.0F) * 2.0F;
         }

         final TextureDrawOptionsEnd var16 = this.sprite.initDraw().light(var10).alpha(var15).rotate((float)this.rotation, var11, var12).mirror(this.mirrored, false).pos(var13, var14);
         var1.add(new EntityDrawable(this) {
            public void draw(TickManager var1) {
               var16.draw();
            }
         });
      }
   }
}
