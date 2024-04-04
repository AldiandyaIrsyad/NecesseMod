package necesse.entity.particle;

import java.awt.Color;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;

public class WebWeaverWebParticle extends Particle {
   private final long startupDelay;
   private final long startTime;
   public boolean startupDone = false;
   private int currentIndex = 0;
   protected ParticleTypeSwitcher extraParticlesTypes;

   public WebWeaverWebParticle(Level var1, float var2, float var3, long var4, long var6) {
      super(var1, var2, var3, var4);
      this.extraParticlesTypes = new ParticleTypeSwitcher(new Particle.GType[]{Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC});
      this.startTime = this.getLevel().getWorldEntity().getLocalTime();
      this.startupDelay = var6;
   }

   public void clientTick() {
      super.clientTick();
      if (!this.startupDone) {
         if (this.startupDelay + this.startTime < this.getLevel().getWorldEntity().getLocalTime()) {
            this.startupDone = true;
            return;
         }

         this.currentIndex = (int)((this.getLevel().getWorldEntity().getLocalTime() - this.startTime) / (this.startupDelay / 8L));
      }

      this.spawnParticles();
   }

   private void spawnParticles() {
      long var1 = this.getRemainingLifeTime();
      int var3 = var1 < 500L ? (int)var1 : 1000;
      byte var4;
      float var5;
      float var6;
      Color var7;
      if (this.startupDone) {
         var4 = 1;
         var5 = 0.0F;
         var7 = new Color(255, 246, 79);
         var6 = (float)GameRandom.globalRandom.nextInt(115);
      } else {
         var4 = 10;
         var5 = 15.0F;
         var7 = new Color(166, 204, 52);
         var6 = (float)(16 * this.currentIndex);
      }

      for(int var8 = 0; var8 < var4; ++var8) {
         AtomicReference var9 = new AtomicReference(GameRandom.globalRandom.nextFloat() * 360.0F);
         this.getLevel().entityManager.addParticle(this.x + GameMath.sin((Float)var9.get()) * var6, this.y + GameMath.cos((Float)var9.get()) * var6, this.extraParticlesTypes.next()).sprite(GameResources.magicSparkParticles.sprite(GameRandom.globalRandom.nextInt(3), 0, 22)).color(var7).moves((var4x, var5x, var6x, var7x, var8x) -> {
            float var9x = (Float)var9.accumulateAndGet(var5x * var5 / 250.0F, Float::sum);
            float var10 = var6 * 0.75F;
            var4x.x = this.x + GameMath.sin(var9x) * var6;
            var4x.y = this.y + GameMath.cos(var9x) * var10;
         }).givesLight(75.0F, 0.5F).lifeTime(var3).sizeFades(16, 24);
      }

   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      int var9 = var7.getDrawX(this.getX()) - 116;
      int var10 = var7.getDrawY(this.getY()) - 88;
      long var11 = this.getRemainingLifeTime();
      float var13 = 1.0F;
      if (var11 < 500L) {
         var13 = Math.max(0.0F, (float)var11 / 500.0F);
      }

      TextureDrawOptionsEnd var14;
      if (this.startupDone) {
         var14 = GameResources.spideriteStaffWeb.initDraw().sprite(0, 0, 232, 176).alpha(var13).pos(var9, var10);
      } else {
         var14 = GameResources.spideriteStaffWeb.initDraw().sprite(1 + this.currentIndex, 0, 232, 176).alpha(var13).pos(var9, var10);
      }

      if (var14 != null) {
         var2.add((var1x) -> {
            var14.draw();
         });
      }

   }

   public void despawnNow() {
      if (this.getRemainingLifeTime() > 500L) {
         this.lifeTime = 500L;
         this.spawnTime = this.getWorldEntity().getLocalTime();
      }

   }
}
