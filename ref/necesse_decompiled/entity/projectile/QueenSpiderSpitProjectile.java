package necesse.entity.projectile;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.QueenSpiderSpitEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.hostile.bosses.QueenSpiderMob;
import necesse.entity.particle.Particle;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public class QueenSpiderSpitProjectile extends Projectile {
   protected long spawnTime;

   public QueenSpiderSpitProjectile() {
   }

   public QueenSpiderSpitProjectile(Level var1, Mob var2, float var3, float var4, float var5, float var6, float var7, int var8, GameDamage var9, int var10) {
      this.setLevel(var1);
      this.x = var3;
      this.y = var4;
      this.speed = var7;
      this.setTarget(var5, var6);
      this.setDamage(var9);
      this.knockback = var10;
      this.setDistance(var8);
      this.setOwner(var2);
   }

   public QueenSpiderSpitProjectile(Level var1, Mob var2, float var3, float var4, float var5, int var6, GameDamage var7, int var8) {
      this(var1, var2, var2.x, var2.y, var3, var4, var5, var6, var7, var8);
   }

   public void init() {
      super.init();
      this.spawnTime = this.getWorldEntity().getTime();
      this.isSolid = false;
      this.canHitMobs = false;
      this.trailOffset = 0.0F;
      this.particleRandomOffset = 1.0F;
      this.particleDirOffset = 0.0F;
   }

   public float tickMovement(float var1) {
      float var2 = super.tickMovement(var1);
      float var3 = GameMath.limit(this.traveledDistance / (float)this.distance, 0.0F, 1.0F);
      float var4 = Math.abs(var3 - 1.0F);
      float var5 = GameMath.sin(var3 * 180.0F);
      this.height = (float)((int)(var5 * 100.0F + 50.0F * var4));
      return var2;
   }

   public Color getParticleColor() {
      return new Color(44, 96, 150);
   }

   public Trail getTrail() {
      return null;
   }

   protected int getExtraSpinningParticles() {
      return super.getExtraSpinningParticles() + 2;
   }

   public void doHitLogic(Mob var1, LevelObjectHit var2, float var3, float var4) {
      super.doHitLogic(var1, var2, var3, var4);
      if (this.isServer()) {
         Mob var5 = this.getOwner();
         if (var5 != null && !var5.removed()) {
            QueenSpiderSpitEvent var6 = new QueenSpiderSpitEvent(var5, (int)var3, (int)var4, GameRandom.globalRandom, this.getDamage(), QueenSpiderMob.SPIT_LINGER_SECONDS);
            this.getLevel().entityManager.addLevelEvent(var6);
         }

      }
   }

   protected void spawnDeathParticles() {
      Color var1 = this.getParticleColor();
      if (var1 != null) {
         for(int var2 = 0; var2 < 10; ++var2) {
            int var3 = GameRandom.globalRandom.nextInt(360);
            Point2D.Float var4 = GameMath.getAngleDir((float)var3);
            this.getLevel().entityManager.addParticle(this.x, this.y, Particle.GType.CRITICAL).movesConstant((float)GameRandom.globalRandom.getIntBetween(20, 50) * var4.x, (float)GameRandom.globalRandom.getIntBetween(20, 50) * var4.y).color(this.getParticleColor()).height(this.getHeight());
         }
      }

   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      if (!this.removed()) {
         GameLight var9 = var5.getLightLevel(this.getX() / 32, this.getY() / 32);
         float var10 = Math.abs(GameMath.limit(this.height / 250.0F, 0.0F, 1.0F) - 1.0F);
         int var11 = var7.getDrawX(this.x) - this.shadowTexture.getWidth() / 2;
         int var12 = var7.getDrawY(this.y) - this.shadowTexture.getHeight() / 2;
         TextureDrawOptionsEnd var13 = this.shadowTexture.initDraw().light(var9).rotate(this.angle).alpha(var10).pos(var11, var12);
         var3.add((var1x) -> {
            var13.draw();
         });
      }
   }
}
