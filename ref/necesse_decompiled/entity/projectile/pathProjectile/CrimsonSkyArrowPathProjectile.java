package necesse.entity.projectile.pathProjectile;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
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

public class CrimsonSkyArrowPathProjectile extends PathProjectile {
   public float maxHeightAtPercent = 0.2F;
   public float maxHeight = 300.0F;
   public float startX;
   public float startY;
   public float targetX;
   public float targetY;
   public static FireworksExplosion piercerPopExplosion;

   public CrimsonSkyArrowPathProjectile() {
   }

   public CrimsonSkyArrowPathProjectile(Level var1, Mob var2, float var3, float var4, float var5, float var6, float var7, GameDamage var8, int var9) {
      this.setLevel(var1);
      this.setOwner(var2);
      this.x = var3;
      this.y = var4;
      this.startX = var3;
      this.startY = var4;
      this.setTarget(var5, var6);
      this.speed = var7;
      this.targetX = var5;
      this.targetY = var6;
      float var10 = var5 - var3;
      float var11 = var6 - var4;
      this.distance = (int)Math.sqrt((double)(var10 * var10 + var11 * var11));
      this.setDamage(var8);
      this.knockback = var9;
   }

   public void init() {
      super.init();
      this.height = 18.0F;
      this.piercing = 0;
      this.isSolid = false;
      this.canBreakObjects = false;
      this.setWidth(6.0F, false);
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextFloat(this.startX);
      var1.putNextFloat(this.startY);
      var1.putNextFloat(this.targetX);
      var1.putNextFloat(this.targetY);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.startX = var1.getNextFloat();
      this.startY = var1.getNextFloat();
      this.targetX = var1.getNextFloat();
      this.targetY = var1.getNextFloat();
   }

   public Point2D.Float getPosition(double var1) {
      float var3 = GameMath.limit(this.traveledDistance / (float)this.distance, 0.0F, 1.0F);
      Point2D.Float var4 = GameMath.normalize(this.targetX - this.startX, this.targetY - this.startY);
      float var5 = this.startX + var4.x * var3 * (float)this.distance;
      float var6 = this.startY + var4.y * var3 * (float)this.distance;
      float var7;
      float var8;
      if (var3 < this.maxHeightAtPercent) {
         var7 = var3 / this.maxHeightAtPercent;
         var8 = this.maxHeight * var7;
         return new Point2D.Float(var5, var6 - var8);
      } else {
         var7 = (var3 - this.maxHeightAtPercent) / (1.0F - this.maxHeightAtPercent);
         var8 = this.maxHeight * var7;
         return new Point2D.Float(var5, var6 - this.maxHeight + var8);
      }
   }

   public void doHitLogic(Mob var1, LevelObjectHit var2, float var3, float var4) {
      super.doHitLogic(var1, var2, var3, var4);
      float var5;
      float var6;
      if (var1 != null) {
         var5 = var1.x;
         var6 = var1.y;
      } else {
         var5 = var3;
         var6 = var4;
      }

      byte var7 = 70;
      if (!this.isServer()) {
         FireworksExplosion var8 = new FireworksExplosion(FireworksPath.sphere((float)GameRandom.globalRandom.getIntBetween(var7 - 10, var7)));
         var8.colorGetter = (var0, var1x, var2x) -> {
            return ParticleOption.randomizeColor(310.0F, 0.5F, 0.4F, 20.0F, 0.1F, 0.1F);
         };
         var8.trailChance = 0.5F;
         var8.particles = 50;
         var8.lifetime = 500;
         var8.popOptions = piercerPopExplosion;
         var8.particleLightHue = 310.0F;
         var8.explosionSound = (var0, var1x, var2x) -> {
            Screen.playSound(GameResources.fireworkExplosion, SoundEffect.effect(var0.x, var0.y).pitch((Float)var2x.getOneOf((Object[])(0.95F, 1.0F, 1.05F))).volume(0.6F).falloffDistance(1500));
         };
         var8.spawnExplosion(this.getLevel(), var5, var6, this.getHeight(), GameRandom.globalRandom);
      }

      if (!this.isClient()) {
         Rectangle var9 = new Rectangle((int)var5 - var7, (int)var6 - var7, var7 * 2, var7 * 2);
         this.streamTargets(this.getOwner(), var9).filter((var4x) -> {
            return this.canHit(var4x) && var4x.getDistance(var5, var6) <= (float)var7;
         }).forEach((var3x) -> {
            var3x.isServerHit(this.getDamage(), var3x.x - var3, var3x.y - var4, (float)this.knockback, this);
         });
      }

   }

   public void applyDamage(Mob var1, float var2, float var3) {
   }

   public Color getParticleColor() {
      return new Color(108, 37, 92);
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
         var1.add(new EntityDrawable(this) {
            public void draw(TickManager var1) {
               var12.draw();
            }
         });
         this.addShadowDrawables(var2, var10, var11, var9, this.getAngle(), 0);
      }
   }

   static {
      piercerPopExplosion = new FireworksExplosion(FireworksExplosion.popPath);
      piercerPopExplosion.particles = 1;
      piercerPopExplosion.lifetime = 200;
      piercerPopExplosion.minSize = 6;
      piercerPopExplosion.maxSize = 10;
      piercerPopExplosion.trailChance = 0.0F;
      piercerPopExplosion.popChance = 0.0F;
      piercerPopExplosion.colorGetter = (var0, var1, var2) -> {
         return ParticleOption.randomizeColor(310.0F, 0.8F, 0.7F, 20.0F, 0.1F, 0.1F);
      };
      piercerPopExplosion.explosionSound = null;
   }
}
