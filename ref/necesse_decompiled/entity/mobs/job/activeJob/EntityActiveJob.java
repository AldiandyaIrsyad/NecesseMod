package necesse.entity.mobs.job.activeJob;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Line2D;
import necesse.engine.GameTileRange;
import necesse.engine.util.GameMath;
import necesse.engine.util.MovedRectangle;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.mobMovement.MobMovement;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.levelData.jobs.JobMoveToTile;

public abstract class EntityActiveJob<T extends Entity> extends ActiveJob {
   public final T target;
   public final GameTileRange maxRange;
   public boolean acceptAdjacentTiles;

   public EntityActiveJob(EntityJobWorker var1, JobTypeHandler.TypePriority var2, T var3, GameTileRange var4) {
      super(var1, var2);
      this.target = var3;
      this.maxRange = var4;
   }

   public JobMoveToTile getMoveToTile(JobMoveToTile var1) {
      Mob var2 = this.worker.getMobWorker();
      int var3 = this.getDirectMoveToDistance();
      return var3 > 0 && (new Point(var2.getX(), var2.getY())).distance((double)this.target.x, (double)this.target.y) <= (double)var3 && this.hasMoveLOS() ? new JobMoveToTile(this.getDirectMoveTo(this.target)) : new JobMoveToTile(this.target.getX() / 32, this.target.getY() / 32, this.acceptAdjacentTiles);
   }

   public boolean isAt(JobMoveToTile var1) {
      return this.isAtTarget();
   }

   public boolean isAtTarget() {
      return this.getDistanceToTarget() <= (double)this.getCompleteRange() && this.hasLOS();
   }

   public int getCompleteRange() {
      return (int)(GameMath.diagonalDistance * 2.0 * 32.0);
   }

   public double getDistanceToTarget() {
      Mob var1 = this.worker.getMobWorker();
      return (new Point(var1.getX(), var1.getY())).distance((double)this.target.x, (double)this.target.y);
   }

   public boolean hasLOS() {
      Mob var1 = this.worker.getMobWorker();
      Line2D.Float var2 = new Line2D.Float(var1.x, var1.y, this.target.x, this.target.y);
      return !var1.getLevel().collides((Line2D)var2, (CollisionFilter)var1.getLevelCollisionFilter());
   }

   public boolean hasMoveLOS() {
      Mob var1 = this.worker.getMobWorker();
      return !var1.getLevel().collides((Shape)(new MovedRectangle(var1, this.target.getX(), this.target.getY())), (CollisionFilter)var1.getLevelCollisionFilter());
   }

   public int getDirectMoveToDistance() {
      return 160;
   }

   public abstract MobMovement getDirectMoveTo(T var1);

   public boolean isValid(boolean var1) {
      if (this.target != null && (!this.isInvalidIfTargetRemoved(var1) || !this.target.removed()) && this.target.isSamePlace(this.worker.getMobWorker())) {
         if (!this.worker.getJobRestrictZone().containsTile(this.target.getTileX(), this.target.getTileY())) {
            return false;
         } else {
            if (this.maxRange != null) {
               Point var2 = this.worker.getJobSearchTile();
               if (!this.maxRange.isWithinRange(var2, this.target.getTileX(), this.target.getTileY())) {
                  return false;
               }
            }

            return this.isJobValid(var1);
         }
      } else {
         return false;
      }
   }

   public abstract boolean isJobValid(boolean var1);

   public boolean isInvalidIfTargetRemoved(boolean var1) {
      return true;
   }

   public ActiveJobResult perform() {
      return !this.isAtTarget() ? ActiveJobResult.MOVE_TO : this.performTarget();
   }

   public abstract ActiveJobResult performTarget();

   public String toString() {
      return super.toString() + "{" + this.target.getUniqueID() + ":" + this.target + "}";
   }
}
