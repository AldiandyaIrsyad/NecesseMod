package necesse.entity.projectile;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.explosionEvent.BoulderHitExplosionEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public class SwampBoulderProjectile extends Projectile {
   protected long spawnTime;

   public SwampBoulderProjectile() {
   }

   public SwampBoulderProjectile(Level var1, Mob var2, float var3, float var4, float var5, float var6, float var7, int var8, GameDamage var9, int var10) {
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

   public SwampBoulderProjectile(Level var1, Mob var2, float var3, float var4, float var5, int var6, GameDamage var7, int var8) {
      this(var1, var2, var2.x, var2.y, var3, var4, var5, var6, var7, var8);
   }

   public void init() {
      super.init();
      this.spawnTime = this.getWorldEntity().getTime();
      this.isSolid = false;
      this.canHitMobs = false;
      this.trailOffset = 0.0F;
   }

   public float tickMovement(float var1) {
      float var2 = super.tickMovement(var1);
      float var3 = this.traveledDistance / (float)this.distance;
      float var4 = GameMath.sin(var3 * 180.0F);
      this.height = var4 * 200.0F;
      return var2;
   }

   public Color getParticleColor() {
      return new Color(52, 67, 48);
   }

   public Trail getTrail() {
      return null;
   }

   public void doHitLogic(Mob var1, LevelObjectHit var2, float var3, float var4) {
      super.doHitLogic(var1, var2, var3, var4);
      if (this.isServer()) {
         Mob var5 = this.getOwner();
         if (var5 != null && !var5.removed()) {
            BoulderHitExplosionEvent var6 = new BoulderHitExplosionEvent(var3, var4, var5);
            this.getLevel().entityManager.addLevelEvent(var6);
         }

      }
   }

   protected void spawnDeathParticles() {
      Color var1 = this.getParticleColor();
      if (var1 != null) {
         for(int var2 = 0; var2 < 20; ++var2) {
            int var3 = GameRandom.globalRandom.nextInt(360);
            Point2D.Float var4 = GameMath.getAngleDir((float)var3);
            this.getLevel().entityManager.addParticle(this.x, this.y, Particle.GType.CRITICAL).movesConstant((float)GameRandom.globalRandom.getIntBetween(20, 50) * var4.x, (float)GameRandom.globalRandom.getIntBetween(20, 50) * var4.y).color(this.getParticleColor()).height(this.getHeight());
         }
      }

   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      if (!this.removed()) {
         GameLight var9 = var5.getLightLevel(this.getX() / 32, this.getY() / 32);
         int var10 = var7.getDrawX(this.x) - this.texture.getWidth() / 2;
         int var11 = var7.getDrawY(this.y) - this.texture.getHeight() / 2;
         float var12 = (float)(this.getWorldEntity().getTime() - this.spawnTime) / 1.5F;
         if (this.dx < 0.0F) {
            var12 = -var12;
         }

         final TextureDrawOptionsEnd var13 = this.texture.initDraw().light(var9).rotate(var12, this.texture.getWidth() / 2, this.texture.getHeight() / 2).pos(var10, var11 - (int)this.getHeight());
         var1.add(new EntityDrawable(this) {
            public void draw(TickManager var1) {
               var13.draw();
            }
         });
         float var14 = Math.abs(GameMath.limit(this.height / 300.0F, 0.0F, 1.0F) - 1.0F);
         int var15 = var7.getDrawX(this.x) - this.shadowTexture.getWidth() / 2;
         int var16 = var7.getDrawY(this.y) - this.shadowTexture.getHeight() / 2;
         TextureDrawOptionsEnd var17 = this.shadowTexture.initDraw().light(var9).alpha(var14).pos(var15, var16);
         var2.add((var1x) -> {
            var17.draw();
         });
      }
   }
}
