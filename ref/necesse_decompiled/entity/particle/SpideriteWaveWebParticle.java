package necesse.entity.particle;

import java.awt.Color;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;

public class SpideriteWaveWebParticle extends Particle {
   public int sprite;
   private int particleBuffer;

   public SpideriteWaveWebParticle(Level var1, float var2, float var3, long var4) {
      super(var1, var2, var3, var4);
      this.sprite = GameRandom.globalRandom.nextInt(4);
      this.particleBuffer = 0;
   }

   public void clientTick() {
      super.clientTick();
      ++this.particleBuffer;
      if (this.particleBuffer > 10) {
         this.drawParticles();
         this.particleBuffer -= 10;
      }

   }

   private void drawParticles() {
      this.getLevel().entityManager.addParticle(this.x + GameRandom.globalRandom.floatGaussian() * 16.0F, this.y + GameRandom.globalRandom.floatGaussian() * 12.0F, Particle.GType.IMPORTANT_COSMETIC).color(new Color(255, 246, 79)).sprite(GameResources.magicSparkParticles.sprite(GameRandom.globalRandom.nextInt(3), 0, 22)).givesLight(75.0F, 0.5F);
   }

   public void despawnNow() {
      if (this.getRemainingLifeTime() > 500L) {
         this.lifeTime = 500L;
         this.spawnTime = this.getWorldEntity().getLocalTime();
      }

   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      int var9 = var7.getDrawX(this.getX()) - 16;
      int var10 = var7.getDrawY(this.getY()) - 16;
      long var11 = this.getRemainingLifeTime();
      float var13 = GameMath.limit((float)var11 / 500.0F, 0.0F, 1.0F);
      TextureDrawOptionsEnd var14 = GameResources.webParticles.initDraw().sprite(this.sprite, 0, 32).pos(var9, var10).color(new Color(166, 204, 52)).alpha(var13);
      var2.add((var1x) -> {
         var14.draw();
      });
   }
}
