package necesse.entity.mobs.job.activeJob;

import java.awt.geom.Point2D;
import necesse.engine.GameTileRange;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.ProjectileRegistry;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.GameLinkedListJobSequence;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.projectile.Projectile;
import necesse.inventory.item.Item;
import necesse.level.maps.levelData.jobs.EntityLevelJob;

public class HuntMobActiveJob extends MobActiveJob<Mob> {
   public EntityLevelJob<? extends Mob> job;
   public GameLinkedListJobSequence sequence;
   protected String meleeItemStringID;
   protected GameDamage meleeDamage;
   protected int meleeRange;
   protected int meleeAimSpeed;
   protected int meleeCooldown;
   protected String rangedItemStringID;
   protected GameDamage rangedDamage;
   protected int rangedRange;
   protected int rangedAimSpeed;
   protected int rangedCooldown;
   protected String rangedProjectileStringID;
   protected int rangedProjectileSpeed;
   public long lastAttackTime;
   public int lastAttackCooldown;

   public HuntMobActiveJob(EntityJobWorker var1, JobTypeHandler.TypePriority var2, EntityLevelJob<? extends Mob> var3, GameTileRange var4, GameLinkedListJobSequence var5, String var6, GameDamage var7, int var8, int var9, int var10) {
      super(var1, var2, (Mob)var3.target, var4);
      this.job = var3;
      this.sequence = var5;
      this.meleeItemStringID = var6;
      this.meleeDamage = var7;
      this.meleeRange = var8;
      this.meleeAimSpeed = var9;
      this.meleeCooldown = var10;
   }

   public HuntMobActiveJob addRangedAttack(String var1, GameDamage var2, int var3, int var4, int var5, String var6, int var7) {
      this.rangedItemStringID = var1;
      this.rangedDamage = var2;
      this.rangedRange = var3;
      this.rangedAimSpeed = var4;
      this.rangedCooldown = var5;
      this.rangedProjectileStringID = var6;
      this.rangedProjectileSpeed = var7;
      return this;
   }

   protected Point2D.Float getProjectileTargetPos() {
      Mob var1 = this.worker.getMobWorker();
      return Projectile.getPredictedTargetPos((Mob)this.target, var1.x, var1.y, (float)this.rangedProjectileSpeed, -10.0F);
   }

   public boolean isAtTarget() {
      double var1 = this.getDistanceToTarget();
      if (var1 <= (double)this.meleeRange) {
         return this.hasLOS();
      } else if (this.rangedProjectileStringID != null && var1 <= (double)this.rangedRange && this.hasLOS()) {
         Point2D.Float var3 = this.getProjectileTargetPos();
         return this.hasProjectileLOS(var3.x, var3.y);
      } else {
         return false;
      }
   }

   public int getCompleteRange() {
      return this.rangedProjectileStringID != null ? Math.max(this.meleeRange, this.rangedRange) : this.meleeRange;
   }

   public boolean isJobValid(boolean var1) {
      return this.job.reservable.isAvailable(this.worker.getMobWorker()) && ((Mob)this.target).canTakeDamage();
   }

   public boolean isInvalidIfTargetRemoved(boolean var1) {
      return this.lastAttackTime == 0L;
   }

   public void tick(boolean var1, boolean var2) {
      this.job.reservable.reserve(this.worker.getMobWorker());
   }

   public ActiveJobResult perform() {
      if (this.worker.isInWorkAnimation()) {
         return ActiveJobResult.PERFORMING;
      } else if (!((Mob)this.target).removed() && ((Mob)this.target).getHealth() > 0) {
         Mob var1 = this.worker.getMobWorker();
         if (this.lastAttackTime + (long)this.lastAttackCooldown > var1.getWorldEntity().getTime()) {
            return ActiveJobResult.PERFORMING;
         } else {
            double var2 = this.getDistanceToTarget();
            if (var2 <= (double)(this.meleeRange + 10)) {
               if (this.hasLOS()) {
                  this.lastAttackCooldown = this.meleeCooldown;
                  if (this.meleeItemStringID != null) {
                     this.worker.showAttackAnimation(((Mob)this.target).getX(), ((Mob)this.target).getY(), ItemRegistry.getItem(this.meleeItemStringID), this.meleeAimSpeed);
                  } else {
                     this.worker.showPlaceAnimation(((Mob)this.target).getX(), ((Mob)this.target).getY(), (Item)null, this.meleeAimSpeed);
                  }

                  ((Mob)this.target).isServerHit(this.meleeDamage, ((Mob)this.target).x - var1.x, ((Mob)this.target).y - var1.y, 100.0F, var1);
                  this.lastAttackTime = var1.getWorldEntity().getTime();
                  return ActiveJobResult.PERFORMING;
               } else {
                  return ActiveJobResult.MOVE_TO;
               }
            } else if (this.rangedProjectileStringID != null && var2 <= (double)(this.rangedRange + 50)) {
               if (this.hasLOS()) {
                  Point2D.Float var4 = this.getProjectileTargetPos();
                  if (this.hasProjectileLOS(var4.x, var4.y)) {
                     this.lastAttackCooldown = this.rangedCooldown;
                     if (this.rangedItemStringID != null) {
                        this.worker.showAttackAnimation((int)var4.x, (int)var4.y, ItemRegistry.getItem(this.rangedItemStringID), this.rangedAimSpeed);
                     } else {
                        this.worker.showPlaceAnimation((int)var4.x, (int)var4.y, (Item)null, this.rangedAimSpeed);
                     }

                     Projectile var5 = ProjectileRegistry.getProjectile(this.rangedProjectileStringID, ((Mob)this.target).getLevel(), var1.x, var1.y, var4.x, var4.y, (float)this.rangedProjectileSpeed, this.rangedRange + 150, this.rangedDamage, var1);
                     var5.moveDist(10.0);
                     ((Mob)this.target).getLevel().entityManager.projectiles.add(var5);
                     this.lastAttackTime = var1.getWorldEntity().getTime();
                     return ActiveJobResult.PERFORMING;
                  } else {
                     return ActiveJobResult.MOVE_TO;
                  }
               } else {
                  return ActiveJobResult.MOVE_TO;
               }
            } else {
               return ActiveJobResult.MOVE_TO;
            }
         }
      } else {
         if (this.sequence != null) {
            PickupItemEntityActiveJob.addItemPickupJobs(this.worker, this.priority, this.maxRange, ((Mob)this.target).itemsDropped, this.sequence);
         }

         return ActiveJobResult.FINISHED;
      }
   }

   public ActiveJobResult performTarget() {
      return ActiveJobResult.FAILED;
   }
}
