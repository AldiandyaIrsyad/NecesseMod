package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
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

public class VoidWizardMissileProjectile extends Projectile {
   private int tickCounter;

   public VoidWizardMissileProjectile() {
   }

   public VoidWizardMissileProjectile(Level var1, Mob var2, Mob var3, GameDamage var4) {
      this.setLevel(var1);
      this.x = var2.x;
      this.y = var2.y;
      this.setTarget(var3.x, var3.y);
      this.setDamage(var4);
      this.knockback = 0;
      this.setDistance(10000);
      this.setOwner(var2);
      this.moveDist(60.0);
   }

   public void init() {
      super.init();
      this.height = 18.0F;
      this.piercing = 100;
      this.tickCounter = 0;
      this.speed = 0.0F;
   }

   protected CollisionFilter getLevelCollisionFilter() {
      return super.getLevelCollisionFilter().addFilter((var0) -> {
         return var0.object().object.isWall || var0.object().object.isRock;
      });
   }

   public Color getParticleColor() {
      return VoidWizard.getWizardProjectileColor(this.getOwner());
   }

   public Trail getTrail() {
      return new Trail(this, this.getLevel(), (Color)null, 12.0F, 500, this.getHeight()) {
         public Color getColor() {
            return VoidWizard.getWizardProjectileColor(VoidWizardMissileProjectile.this.getOwner());
         }
      };
   }

   public void doHitLogic(Mob var1, LevelObjectHit var2, float var3, float var4) {
      super.doHitLogic(var1, var2, var3, var4);
      if (this.isServer()) {
         if (var1 != null) {
            ActiveBuff var5 = new ActiveBuff("brokenarmor", var1, 20.0F, this.getOwner());
            var1.addBuff(var5, true);
         }

      }
   }

   public void clientTick() {
      super.clientTick();
      ++this.tickCounter;
      if (this.tickCounter == 10) {
         this.speed = 400.0F;
      }

   }

   public void serverTick() {
      super.serverTick();
      ++this.tickCounter;
      if (this.tickCounter == 10) {
         this.speed = 400.0F;
      }

   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      if (!this.removed()) {
         GameLight var9 = var5.getLightLevel(this);
         int var10 = var7.getDrawX(this.x) - this.texture.getWidth() / 2;
         int var11 = var7.getDrawY(this.y);
         final TextureDrawOptionsEnd var12 = this.texture.initDraw().rotate(this.getAngle(), this.texture.getWidth() / 2, 0).pos(var10, var11 - (int)this.getHeight());
         var1.add(new EntityDrawable(this) {
            public void draw(TickManager var1) {
               var12.draw();
            }
         });
         this.addShadowDrawables(var2, var10, var11, var9, this.getAngle(), 0);
      }
   }

   public DeathMessageTable getDeathMessages() {
      return this.getDeathMessages("voidwiz", 4);
   }
}
