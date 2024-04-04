package necesse.entity.projectile;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.TheCrimsonSkyEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.ParticleOption;
import necesse.entity.particle.fireworks.FireworksExplosion;
import necesse.entity.particle.fireworks.FireworksPath;
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

public class CrimsonSkyArrowProjectile extends Projectile {
   protected boolean isFallingProjectile;
   protected Point2D.Float targetPoints;
   protected GameDamage damage;
   protected float eventResilienceGain;

   public CrimsonSkyArrowProjectile() {
   }

   public CrimsonSkyArrowProjectile(Level var1, Mob var2, float var3, float var4, float var5, float var6, float var7, int var8, GameDamage var9, float var10, int var11, Point2D.Float var12, boolean var13) {
      this.setLevel(var1);
      this.setOwner(var2);
      this.x = var3;
      this.y = var4;
      this.setTarget(var5, var6);
      this.speed = var7;
      this.distance = var8;
      this.setDamage(var9);
      this.damage = var9;
      this.eventResilienceGain = var10;
      this.knockback = var11;
      this.targetPoints = var12;
      this.isFallingProjectile = var13;
   }

   public void init() {
      super.init();
      this.height = 40.0F;
      this.piercing = 0;
      this.isSolid = false;
      this.heightBasedOnDistance = true;
      this.trailOffset = -25.0F;
      this.removeIfOutOfBounds = false;
      this.canBreakObjects = false;
      this.setWidth(6.0F, false);
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextBoolean(this.isFallingProjectile);
      if (this.targetPoints != null) {
         var1.putNextBoolean(true);
         var1.putNextFloat(this.targetPoints.x);
         var1.putNextFloat(this.targetPoints.y);
      } else {
         var1.putNextBoolean(false);
      }

   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.isFallingProjectile = var1.getNextBoolean();
      if (var1.getNextBoolean()) {
         this.targetPoints = new Point2D.Float(var1.getNextFloat(), var1.getNextFloat());
      } else {
         this.targetPoints = null;
      }

   }

   public boolean canHit(Mob var1) {
      return false;
   }

   public void doHitLogic(Mob var1, LevelObjectHit var2, float var3, float var4) {
      super.doHitLogic(var1, var2, var3, var4);
      if (!this.isFallingProjectile) {
         CrimsonSkyArrowProjectile var5 = new CrimsonSkyArrowProjectile(this.getLevel(), this.getOwner(), this.targetPoints.x, this.targetPoints.y - (float)this.distance, this.targetPoints.x, this.targetPoints.y, this.speed, this.distance, this.damage, this.eventResilienceGain, this.knockback, (Point2D.Float)null, true);
         var5.getUniqueID((new GameRandom((long)this.getUniqueID())).nextSeeded(67));
         this.getLevel().entityManager.projectiles.addHidden(var5);
      } else {
         float var6;
         float var10;
         if (var1 != null) {
            var10 = var1.x;
            var6 = var1.y;
         } else {
            var10 = var3;
            var6 = var4;
         }

         short var7 = 200;
         byte var8 = 65;
         if (!this.isServer()) {
            Screen.playSound(GameResources.bowhit, SoundEffect.effect(this));
            Screen.playSound(GameResources.slimesplash, SoundEffect.effect(this));
            FireworksExplosion var9 = new FireworksExplosion(FireworksPath.sphere((float)GameRandom.globalRandom.getIntBetween(var8 - 10, var8)));
            var9.colorGetter = (var0, var1x, var2x) -> {
               return ParticleOption.randomizeColor(348.0F, 0.94F, 0.8F, 2.0F, 0.0F, 0.0F);
            };
            var9.trailChance = 0.5F;
            var9.particles = 40;
            var9.lifetime = var7;
            var9.popOptions = null;
            var9.particleLightHue = 0.0F;
            var9.explosionSound = null;
            var9.spawnExplosion(this.getLevel(), var10, var6, this.getHeight(), GameRandom.globalRandom);
         }

         if (!this.isClient()) {
            Rectangle var11 = new Rectangle((int)var10 - var8, (int)var6 - var8, var8 * 2, var8 * 2);
            this.streamTargets(this.getOwner(), var11).filter((var4x) -> {
               return var4x.canBeHit(this) && var4x.getDistance(var10, var6) <= (float)var8;
            }).forEach((var3x) -> {
               var3x.isServerHit(this.getDamage(), var3x.x - var3, var3x.y - var4, (float)this.knockback, this);
            });
         }

         this.getLevel().entityManager.addLevelEventHidden(new TheCrimsonSkyEvent(this.getOwner(), (int)var3, (int)var4, new GameRandom((long)this.getUniqueID()), this.getDamage(), this.eventResilienceGain));
      }

   }

   public Color getParticleColor() {
      return new Color(234, 14, 58);
   }

   public Trail getTrail() {
      Trail var1 = new Trail(this, this.getLevel(), new Color(154, 8, 8), 12.0F, 200, this.getHeight());
      var1.drawOnTop = true;
      return var1;
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      if (!this.removed()) {
         GameLight var9 = var5.getLightLevel(this);
         int var10 = var7.getDrawX(this.x) - this.texture.getWidth() / 2;
         int var11 = var7.getDrawY(this.y);
         final TextureDrawOptionsEnd var12 = this.texture.initDraw().light(var9).rotate(this.getAngle(), this.texture.getWidth() / 2, 0).pos(var10, var11 - (int)this.getHeight());
         var3.add(new EntityDrawable(this) {
            public void draw(TickManager var1) {
               var12.draw();
            }
         });
         this.addShadowDrawables(var2, var10, var11, var9, this.getAngle(), 0);
      }
   }
}
