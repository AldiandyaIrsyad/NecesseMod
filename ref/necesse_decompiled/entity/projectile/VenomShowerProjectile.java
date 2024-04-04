package necesse.entity.projectile;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;

public class VenomShowerProjectile extends Projectile {
   private boolean hitMob = false;

   public VenomShowerProjectile() {
   }

   public VenomShowerProjectile(Level var1, float var2, float var3, float var4, float var5, int var6, GameDamage var7, Mob var8) {
      this.setLevel(var1);
      this.x = var2;
      this.y = var3;
      this.setTarget(var4, var5);
      this.speed = (float)var6 * 1.1F;
      this.setDamage(var7);
      this.setOwner(var8);
      this.setDistance(var6);
      this.canBounce = false;
   }

   public void init() {
      super.init();
      this.height = 16.0F;
      this.piercing = 2;
      this.setWidth(25.0F);
      if (this.isClient()) {
         int var1 = this.distance / 6;
         this.spawnSprayParticles(var1);
      }

   }

   public void onMoveTick(Point2D.Float var1, double var2) {
      super.onMoveTick(var1, var2);
      float var4 = this.traveledDistance / (float)this.distance;
      this.speed = GameMath.lerp(var4, (float)this.distance * 1.1F, 5.0F);
   }

   public void spawnSprayParticles(int var1) {
      ParticleTypeSwitcher var2 = new ParticleTypeSwitcher(new Particle.GType[]{Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC});

      for(int var3 = 0; var3 < var1; ++var3) {
         float var4 = this.x + GameRandom.globalRandom.floatGaussian() * 8.0F;
         float var5 = this.y + GameRandom.globalRandom.floatGaussian() * 8.0F;
         float var6 = this.getHeight();
         float var7 = GameRandom.globalRandom.getFloatBetween(var6 - 2.0F, var6 + 4.0F);
         float var8 = GameRandom.globalRandom.getFloatBetween(0.0F, 60.0F);
         float var9 = GameRandom.globalRandom.getFloatBetween(-10.0F, -5.0F);
         float var10 = GameRandom.globalRandom.getFloatBetween(8.0F, 20.0F);
         float var11 = (float)this.distance - this.traveledDistance;
         float var12 = GameRandom.globalRandom.getFloatBetween(0.1F, 1.0F);
         float var13 = var12 * (var11 + 50.0F);
         float var14 = 1.0F;
         int var15 = (int)(250.0F * var12);
         int var16 = GameRandom.globalRandom.getIntBetween(250 + var15, 750 + var15);
         int var17 = GameRandom.globalRandom.getIntBetween(500, 1000);
         int var18 = var16 + var17;
         ParticleOption.HeightMover var19 = new ParticleOption.HeightMover(var7, var8, var10, 2.0F, var9, 0.0F);
         ParticleOption.FrictionMover var20 = new ParticleOption.FrictionMover(this.dx * var13, this.dy * var13, var14);
         ParticleOption.CollisionMover var21 = new ParticleOption.CollisionMover(this.getLevel(), var20);
         this.getLevel().entityManager.addParticle(var4, var5, var2.next()).fadesAlphaTime(0, var17).color(new Color(20, 120, 50)).sizeFadesInAndOut(10, 15, 100, 0).height((ParticleOption.HeightGetter)var19).rotates().moves((var4x, var5x, var6x, var7x, var8x) -> {
            if (var19.currentHeight > var9 && (!this.removed() || !this.hitMob)) {
               var21.tick(var4x, var5x, var6x, var7x, var8x);
            }

         }).givesLight(110.0F, 0.7F).lifeTime(var18);
      }

   }

   public GameDamage getDamage() {
      float var1 = this.traveledDistance / (float)this.distance;
      float var2 = GameMath.lerp(var1, 1.2F, 0.2F);
      return super.getDamage().modFinalMultiplier(var2);
   }

   public void doHitLogic(Mob var1, LevelObjectHit var2, float var3, float var4) {
      super.doHitLogic(var1, var2, var3, var4);
      this.hitMob = var1 != null;
   }

   public Color getParticleColor() {
      return null;
   }

   public Trail getTrail() {
      return null;
   }

   protected Color getWallHitColor() {
      return null;
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
   }
}
