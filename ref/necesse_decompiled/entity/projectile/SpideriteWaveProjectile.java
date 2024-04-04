package necesse.entity.projectile;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.SpideriteWaveGroundWebEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.ParticleOption;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;

public class SpideriteWaveProjectile extends Projectile {
   private double distBuffer;

   public SpideriteWaveProjectile() {
   }

   public SpideriteWaveProjectile(Level var1, float var2, float var3, float var4, float var5, float var6, int var7, GameDamage var8, Mob var9) {
      this.setLevel(var1);
      this.x = var2;
      this.y = var3;
      this.setTarget(var4, var5);
      this.speed = var6;
      this.setDamage(var8);
      this.setOwner(var9);
      this.setDistance(var7);
   }

   public void init() {
      super.init();
      this.maxMovePerTick = 32;
      this.height = 0.0F;
      this.isSolid = false;
      this.givesLight = true;
      this.canHitMobs = false;
      this.particleRandomOffset = 10.0F;
      this.particleDirOffset = 0.0F;
   }

   public void onMoveTick(Point2D.Float var1, double var2) {
      super.onMoveTick(var1, var2);
      this.distBuffer += var2;
      if (this.isServer()) {
         while(this.distBuffer > 32.0) {
            this.distBuffer -= 32.0;
            SpideriteWaveGroundWebEvent var4 = new SpideriteWaveGroundWebEvent(this.getOwner(), this.getX(), this.getY(), GameRandom.globalRandom, this.getDamage());
            this.getLevel().entityManager.addLevelEvent(var4);
         }
      }

   }

   public Color getParticleColor() {
      return new Color(255, 246, 79);
   }

   public Trail getTrail() {
      return null;
   }

   protected void modifySpinningParticle(ParticleOption var1) {
      super.modifySpinningParticle(var1);
      var1.sprite(GameResources.magicSparkParticles.sprite(GameRandom.globalRandom.nextInt(3), 0, 22)).sizeFades(10, 50).givesLight(75.0F, 0.5F);
   }

   protected int getExtraSpinningParticles() {
      return 10;
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
   }
}
