package necesse.entity.projectile.followingProjectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.network.server.ServerClient;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.explosionEvent.VoidWizardExplosionEvent;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.hostile.bosses.VoidWizard;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public class VoidWizardHomingProjectile extends FollowingProjectile {
   private long spawnTime;

   public VoidWizardHomingProjectile() {
   }

   public VoidWizardHomingProjectile(Level var1, Mob var2, Mob var3, GameDamage var4) {
      this.setLevel(var1);
      this.x = var2.x;
      this.y = var2.y;
      this.speed = 85.0F;
      this.setTarget(var3.x, var3.y);
      if (var2.getDistance(var3) < 960.0F) {
         this.target = var3;
      } else {
         this.target = var2;
      }

      this.setDamage(var4);
      this.knockback = 0;
      this.setDistance(100000);
      this.setOwner(var2);
      this.setAngle(this.getAngle() + (GameRandom.globalRandom.nextFloat() - 0.5F) * 40.0F);
      this.moveDist(60.0);
   }

   public void init() {
      super.init();
      this.turnSpeed = 0.075F;
      this.givesLight = true;
      this.height = 18.0F;
      this.setWidth(8.0F);
      this.spawnTime = this.getWorldEntity().getTime();
      this.doesImpactDamage = true;
   }

   protected CollisionFilter getLevelCollisionFilter() {
      return super.getLevelCollisionFilter().addFilter((var0) -> {
         return var0.object().object.isWall || var0.object().object.isRock;
      });
   }

   public void serverTick() {
      super.serverTick();
      Mob var1 = this.getOwner();
      if (var1 != null && !var1.removed()) {
         if (!this.target.isSamePlace(this.getOwner())) {
            this.onHit((Mob)null, (LevelObjectHit)null, this.x, this.y, false, (ServerClient)null);
         }

      } else {
         this.remove();
      }
   }

   public void doHitLogic(Mob var1, LevelObjectHit var2, float var3, float var4) {
      super.doHitLogic(var1, var2, var3, var4);
      if (this.isServer()) {
         VoidWizardExplosionEvent var5 = new VoidWizardExplosionEvent(var3, var4, this.getOwner());
         this.getLevel().entityManager.addLevelEvent(var5);
      }
   }

   public Color getParticleColor() {
      return VoidWizard.getWizardProjectileColor(this.getOwner());
   }

   public Trail getTrail() {
      return new Trail(this, this.getLevel(), (Color)null, 32.0F, 500, 18.0F) {
         public Color getColor() {
            return VoidWizard.getWizardProjectileColor(VoidWizardHomingProjectile.this.getOwner());
         }
      };
   }

   public void updateTarget() {
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      if (!this.removed()) {
         GameLight var9 = var5.getLightLevel(this);
         int var10 = var7.getDrawX(this.x) - this.texture.getWidth() / 2;
         int var11 = var7.getDrawY(this.y) - this.texture.getHeight() / 2;
         float var12 = (float)(this.getWorldEntity().getTime() - this.spawnTime);
         final TextureDrawOptionsEnd var13 = this.texture.initDraw().light(var9.minLevelCopy(Math.min(var9.getLevel() + 100.0F, 150.0F))).rotate(var12, this.texture.getWidth() / 2, this.texture.getHeight() / 2).pos(var10, var11 - (int)this.getHeight());
         var1.add(new EntityDrawable(this) {
            public void draw(TickManager var1) {
               var13.draw();
            }
         });
         this.addShadowDrawables(var2, var10, var11, var9, var12, this.texture.getHeight() / 2);
      }
   }

   public DeathMessageTable getDeathMessages() {
      return this.getDeathMessages("voidwiz", 4);
   }
}
