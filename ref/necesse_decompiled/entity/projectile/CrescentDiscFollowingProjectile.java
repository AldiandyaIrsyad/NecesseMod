package necesse.entity.projectile;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.followingProjectile.FollowingProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class CrescentDiscFollowingProjectile extends FollowingProjectile {
   private final float fadeDistance = 0.1F;
   private float lastAngle;

   public CrescentDiscFollowingProjectile() {
   }

   public CrescentDiscFollowingProjectile(Level var1, float var2, float var3, float var4, float var5, float var6, int var7, GameDamage var8, Mob var9) {
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
      this.setWidth(70.0F, 70.0F);
      this.turnSpeed = 5.0F;
      this.height = 0.0F;
      this.trailOffset = 0.0F;
      this.lastAngle = 0.0F;
      this.isSolid = false;
      this.givesLight = true;
      this.piercing = 2;
   }

   public void clientTick() {
      super.clientTick();
      GameRandom var1 = GameRandom.globalRandom;
      AtomicReference var2 = new AtomicReference(var1.nextFloat() * 360.0F);
      float var3 = 90.0F;
      this.getLevel().entityManager.addParticle(this.x + GameMath.sin((Float)var2.get()) * var3, this.y + GameMath.cos((Float)var2.get()) * var3, Particle.GType.CRITICAL).sprite(GameResources.magicSparkParticles.sprite(var1.nextInt(4), 0, 22)).color(new Color(255, 255, 255)).givesLight(247.0F, 0.3F).moves((var3x, var4, var5, var6, var7) -> {
         float var8 = (Float)var2.accumulateAndGet(-var4 * 10.0F / 250.0F, Float::sum);
         var3x.x = this.x + GameMath.sin(var8) * var3;
         var3x.y = this.y + GameMath.cos(var8) * var3;
      }).lifeTime(1000).sizeFades(16, 24);
   }

   public void onMoveTick(Point2D.Float var1, double var2) {
      super.onMoveTick(var1, var2);
      this.lastAngle += 5.0F * this.getIntensity();
   }

   protected void spawnSpinningParticle() {
   }

   public Color getParticleColor() {
      return new Color(220, 212, 255);
   }

   protected void replaceTrail() {
      super.replaceTrail();
   }

   public Trail getTrail() {
      return null;
   }

   public void updateTarget() {
      if (this.traveledDistance > 50.0F) {
         this.findTarget((var0) -> {
            return var0.isPlayer;
         }, 0.0F, 1000.0F);
      }

   }

   public boolean canHit(Mob var1) {
      return this.getIntensity() == 1.0F && super.canHit(var1);
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      if (!this.removed()) {
         GameLight var9 = var5.getLightLevel(this);
         int var10 = var7.getDrawX(this.x) - this.texture.getWidth() / 2;
         int var11 = var7.getDrawY(this.y) - this.texture.getHeight() / 2;
         final TextureDrawOptionsEnd var12 = this.texture.initDraw().light(var9).rotate(this.lastAngle + this.getIntensity(), this.texture.getWidth() / 2, this.texture.getHeight() / 2).pos(var10, var11 - (int)this.getHeight()).alpha(this.getIntensity());
         var3.add(new EntityDrawable(this) {
            public void draw(TickManager var1) {
               var12.draw();
            }
         });
      }
   }

   private float getIntensity() {
      if (this.traveledDistance < (float)this.distance * 0.1F) {
         return this.traveledDistance / ((float)this.distance * 0.1F);
      } else {
         return (float)this.distance - this.traveledDistance < (float)this.distance * 0.1F ? ((float)this.distance - this.traveledDistance) / ((float)this.distance * 0.1F) : 1.0F;
      }
   }
}
