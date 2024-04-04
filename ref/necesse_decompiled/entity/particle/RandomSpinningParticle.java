package necesse.entity.particle;

import java.awt.Color;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import necesse.engine.tickManager.Performance;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class RandomSpinningParticle extends Particle {
   private int spriteX;
   private int spriteY;
   protected int height;
   private float red;
   private float green;
   private float blue;
   private float alpha;
   private final LinkedList<SpinningParticleData> particles;

   public RandomSpinningParticle(Level var1, int var2, int var3, Color var4, float var5, float var6, float var7, float var8, int var9, int var10) {
      super(var1, var5, var6, (long)var10);
      this.particles = new LinkedList();
      this.spriteX = var2;
      this.spriteY = var3;
      this.height = var9;
      this.red = (float)var4.getRed() / 255.0F;
      this.green = (float)var4.getGreen() / 255.0F;
      this.blue = (float)var4.getBlue() / 255.0F;
      this.alpha = (float)var4.getAlpha() / 255.0F;
      this.dx = var7;
      this.dy = var8;
      this.addParticle(0);
   }

   public RandomSpinningParticle(Level var1, int var2, int var3, float var4, float var5, float var6, float var7, int var8, int var9) {
      this(var1, var2, var3, new Color(1.0F, 1.0F, 1.0F), var4, var5, var6, var7, var8, var9);
   }

   public RandomSpinningParticle(Level var1, int var2, int var3, float var4, float var5, float var6, float var7, int var8) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, 1000);
   }

   public RandomSpinningParticle(Level var1, Color var2, float var3, float var4, float var5, float var6, int var7, int var8) {
      this(var1, 0, 1, var2, var3, var4, var5, var6, var7, var8);
   }

   public RandomSpinningParticle(Level var1, Color var2, float var3, float var4, float var5, float var6, int var7) {
      this(var1, 0, 1, var2, var3, var4, var5, var6, var7, 1000);
   }

   public RandomSpinningParticle(Level var1, int var2, int var3, float var4, float var5, int var6) {
      this(var1, var2, var3, var4, var5, 0.0F, 0.0F, var6);
   }

   public RandomSpinningParticle addParticle(int var1, int var2) {
      synchronized(this.particles) {
         this.particles.add(new SpinningParticleData(var1, var2));
         return this;
      }
   }

   public RandomSpinningParticle addParticle(int var1, float var2, float var3) {
      int var4 = var1 == 0 ? 0 : (int)((float)GameRandom.globalRandom.getIntBetween(-var1, var1) * var2);
      int var5 = var1 == 0 ? 0 : (int)((float)GameRandom.globalRandom.getIntBetween(-var1, var1) * var3);
      return this.addParticle(var4, var5);
   }

   public RandomSpinningParticle addParticle(int var1) {
      return this.addParticle(var1, 1.0F, 1.0F);
   }

   public RandomSpinningParticle addParticles(int var1, int var2, float var3, float var4) {
      for(int var5 = 0; var5 < var1; ++var5) {
         this.addParticle(var2, var3, var4);
      }

      return this;
   }

   public RandomSpinningParticle addParticles(int var1, int var2) {
      return this.addParticles(var1, var2, 1.0F, 1.0F);
   }

   public RandomSpinningParticle clearParticles() {
      synchronized(this.particles) {
         this.particles.clear();
         return this;
      }
   }

   public int getHeight(float var1) {
      return this.height;
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      float var9 = this.getLifeCyclePercent();
      if (!this.removed()) {
         GameLight var10 = var5.getLightLevel(this);
         int var11 = var7.getDrawX(this.getX());
         int var12 = var7.getDrawY(this.getY()) - this.getHeight(var9);
         final SharedTextureDrawOptions var13 = new SharedTextureDrawOptions(GameResources.generatedParticlesTexture);
         synchronized(this.particles) {
            Iterator var15 = this.particles.iterator();

            while(true) {
               if (!var15.hasNext()) {
                  break;
               }

               SpinningParticleData var16 = (SpinningParticleData)var15.next();
               var16.addDrawOptions(var13, var11, var12, var9, var10);
            }
         }

         var1.add(new EntityDrawable(this) {
            public void draw(TickManager var1) {
               SharedTextureDrawOptions var10002 = var13;
               Objects.requireNonNull(var10002);
               Performance.record(var1, "spinParticle", (Runnable)(var10002::draw));
            }
         });
      }
   }

   public class SpinningParticleData {
      private int xOffset;
      private int yOffset;
      private int rotateOffset;
      private float rotateSpeed;
      private float sizeMod;

      public SpinningParticleData(int var2, int var3) {
         this.xOffset = var2;
         this.yOffset = var3;
         this.rotateSpeed = GameRandom.globalRandom.nextFloat() * 100.0F + 100.0F;
         if (GameRandom.globalRandom.nextBoolean()) {
            this.rotateSpeed = -this.rotateSpeed;
         }

         this.rotateOffset = GameRandom.globalRandom.nextInt(360);
         this.sizeMod = GameRandom.globalRandom.nextFloat() + 0.8F;
      }

      public void addDrawOptions(SharedTextureDrawOptions var1, int var2, int var3, float var4, GameLight var5) {
         int var6 = var2 + this.xOffset;
         int var7 = var3 + this.yOffset;
         float var8 = var4 * this.rotateSpeed + (float)this.rotateOffset;
         float var9 = Math.abs(var4 - 1.0F) + 0.2F;
         int var10 = (int)(10.0F * this.sizeMod * var9);
         int var11 = (int)(10.0F * this.sizeMod * var9);
         var1.add(GameResources.particles.sprite(RandomSpinningParticle.this.spriteX, RandomSpinningParticle.this.spriteY, 8)).colorLight(RandomSpinningParticle.this.red, RandomSpinningParticle.this.green, RandomSpinningParticle.this.blue, RandomSpinningParticle.this.alpha, var5).rotate(var8, var10 / 2, var11 / 2).size(var10, var11).pos(var6 - var10 / 2, var7 - var11 / 2);
      }
   }
}
