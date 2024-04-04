package necesse.entity.particle;

import java.awt.Color;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class EmpressAcidParticle extends Particle {
   public int sprite;

   public EmpressAcidParticle(Level var1, float var2, float var3, long var4) {
      super(var1, var2, var3, var4);
      this.sprite = GameRandom.globalRandom.nextInt(3);
   }

   public void init() {
      this.spawnParticles();
   }

   private void spawnParticles() {
      for(int var1 = 0; var1 < 10; ++var1) {
         this.getLevel().entityManager.addParticle(this.x + GameRandom.globalRandom.floatGaussian() * 16.0F, this.y + GameRandom.globalRandom.floatGaussian() * 12.0F, Particle.GType.IMPORTANT_COSMETIC).color(new Color(166, 204, 52)).sizeFades(5, 15).sprite(GameResources.bubbleParticle.sprite(0, 0, 12)).movesFrictionAngle((float)GameRandom.globalRandom.getIntBetween(0, 360), 50.0F, 0.5F).givesLight(75.0F, 0.5F);
      }

   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var5.getLightLevel(this.getX() / 32, this.getY() / 32);
      int var10 = var7.getDrawX(this.getX()) - 16;
      int var11 = var7.getDrawY(this.getY()) - 16;
      float var12 = this.getLifeCyclePercent();
      long var13 = this.getRemainingLifeTime();
      float var15 = Math.max(0.0F, (float)var13 / 500.0F);
      TextureDrawOptionsEnd var16 = GameResources.empressAcid.initDraw().sprite(this.sprite, 0, 32).pos(var10, var11).light(var9).alpha(var15);
      var2.add((var1x) -> {
         var16.draw();
      });
   }

   public void despawnNow() {
      if (this.getRemainingLifeTime() > 500L) {
         this.lifeTime = 500L;
         this.spawnTime = this.getWorldEntity().getLocalTime();
      }

   }
}
