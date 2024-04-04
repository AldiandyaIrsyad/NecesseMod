package necesse.entity.projectile;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Comparator;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.ComputedObjectValue;
import necesse.engine.util.ComputedValue;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.followingProjectile.PhantomMissileProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public class PhantomBobbleProjectile extends Projectile {
   private long spawnTime;
   private float startSpeed;

   public PhantomBobbleProjectile() {
   }

   public PhantomBobbleProjectile(float var1, float var2, float var3, float var4, float var5, int var6, GameDamage var7, int var8, Mob var9) {
      this.x = var1;
      this.y = var2;
      this.setTarget(var3, var4);
      this.speed = var5;
      this.setDistance(var6);
      this.setDamage(var7);
      this.knockback = var8;
      this.setOwner(var9);
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextFloat(this.startSpeed);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.startSpeed = var1.getNextFloat();
   }

   public void init() {
      super.init();
      this.height = 18.0F;
      this.canHitMobs = false;
      this.startSpeed = this.speed;
      this.setWidth(10.0F);
      this.spawnTime = this.getLevel().getWorldEntity().getTime();
   }

   public void onMoveTick(Point2D.Float var1, double var2) {
      super.onMoveTick(var1, var2);
      float var4 = Math.abs(GameMath.limit(this.traveledDistance / (float)this.distance, 0.0F, 1.0F) - 1.0F);
      this.speed = GameMath.lerp(var4, Math.max(this.startSpeed / 4.0F, 10.0F), this.startSpeed);
   }

   protected void spawnDeathParticles() {
      Color var1 = this.getParticleColor();
      if (var1 != null) {
         float var2 = this.getHeight();

         for(int var3 = 0; var3 < 24; ++var3) {
            this.getLevel().entityManager.addParticle(this.x, this.y, Particle.GType.COSMETIC).movesConstant((float)(GameRandom.globalRandom.getIntBetween(10, 40) * (GameRandom.globalRandom.nextBoolean() ? -1 : 1)), (float)(GameRandom.globalRandom.getIntBetween(10, 40) * (GameRandom.globalRandom.nextBoolean() ? -1 : 1))).color(this.getParticleColor()).height(var2);
         }
      }

      if (!this.isServer()) {
         Screen.playSound(GameResources.fadedeath2, SoundEffect.effect(this).volume(0.3F));
      }

   }

   public void doHitLogic(Mob var1, LevelObjectHit var2, float var3, float var4) {
      super.doHitLogic(var1, var2, var3, var4);
      if (this.isServer()) {
         byte var5;
         if (var1 == null) {
            var5 = 4;
         } else {
            var5 = 2;
         }

         float var6 = var3 - this.dx * 2.0F;
         float var7 = var4 - this.dy * 2.0F;
         Mob var8 = (Mob)GameUtils.streamTargetsRange(this.getOwner(), this.getX(), this.getY(), this.distance * 10).filter((var0) -> {
            return var0 != null && !var0.removed() && var0.isHostile;
         }).map((var1x) -> {
            return new ComputedObjectValue(var1x, () -> {
               return var1x.getDistance((float)this.getX(), (float)this.getY());
            });
         }).filter((var1x) -> {
            return (Float)var1x.get() <= (float)(this.distance * 10);
         }).min(Comparator.comparing(ComputedValue::get)).map((var0) -> {
            return (Mob)var0.object;
         }).orElse((Object)null);
         float var9 = GameMath.getAngle(new Point2D.Float(this.dx, this.dy));
         float var10 = 90.0F;

         for(int var11 = 0; var11 < var5; ++var11) {
            Point2D.Float var12 = GameMath.getAngleDir(var9 + GameRandom.globalRandom.getFloatBetween(-var10, var10));
            PhantomMissileProjectile var13 = new PhantomMissileProjectile(this.getLevel(), this.getOwner(), var6, var7, var6 + var12.x * 100.0F, var7 + var12.y * 100.0F, this.startSpeed * 2.5F, this.distance * 15, this.getDamage().modFinalMultiplier(0.5F), this.knockback);
            if (this.modifier != null) {
               this.modifier.initChildProjectile(var13, 0.5F, var5);
            }

            if (var8 != null) {
               var13.targetPos = new Point(var8.getX() + GameRandom.globalRandom.getIntBetween(-20, 20), var8.getY() + GameRandom.globalRandom.getIntBetween(-20, 20));
            }

            if (var1 != null) {
               var13.startHitCooldown(var1);
            }

            this.getLevel().entityManager.projectiles.add(var13);
         }

      }
   }

   public Color getParticleColor() {
      return new Color(108, 37, 92);
   }

   public Trail getTrail() {
      return new Trail(this, this.getLevel(), this.getParticleColor(), 12.0F, 200, this.getHeight());
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      if (!this.removed()) {
         GameLight var9 = var5.getLightLevel(this);
         int var10 = var7.getDrawX(this.x) - this.texture.getWidth() / 2;
         int var11 = var7.getDrawY(this.y) - this.texture.getHeight() / 2;
         final TextureDrawOptionsEnd var12 = this.texture.initDraw().light(var9).rotate(this.getAngle(), this.texture.getWidth() / 2, this.texture.getHeight() / 2).pos(var10, var11 - (int)this.getHeight());
         var1.add(new EntityDrawable(this) {
            public void draw(TickManager var1) {
               var12.draw();
            }
         });
         TextureDrawOptionsEnd var13 = this.shadowTexture.initDraw().light(var9).rotate(this.getAngle(), this.shadowTexture.getWidth() / 2, this.shadowTexture.getHeight() / 2).pos(var10, var11);
         var2.add((var1x) -> {
            var13.draw();
         });
      }
   }

   public float getAngle() {
      return (float)(this.getWorldEntity().getTime() - this.spawnTime) / 2.0F;
   }
}
