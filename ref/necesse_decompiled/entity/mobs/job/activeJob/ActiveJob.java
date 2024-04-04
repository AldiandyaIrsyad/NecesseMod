package necesse.entity.mobs.job.activeJob;

import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.jobs.JobMoveToTile;

public abstract class ActiveJob {
   public final EntityJobWorker worker;
   public final JobTypeHandler.TypePriority priority;

   public ActiveJob(EntityJobWorker var1, JobTypeHandler.TypePriority var2) {
      this.worker = var1;
      this.priority = var2;
   }

   public void onMadeCurrent() {
   }

   public abstract JobMoveToTile getMoveToTile(JobMoveToTile var1);

   public abstract boolean isAt(JobMoveToTile var1);

   public abstract void tick(boolean var1, boolean var2);

   public abstract boolean isValid(boolean var1);

   public boolean shouldClearSequence() {
      return true;
   }

   public ActiveJobHitResult onHit(MobWasHitEvent var1, boolean var2) {
      return ActiveJobHitResult.CLEAR_SEQUENCE;
   }

   public ActiveJobTargetFoundResult onTargetFound(Mob var1, boolean var2, boolean var3) {
      return ActiveJobTargetFoundResult.CONTINUE;
   }

   public void onCancelled(boolean var1, boolean var2, boolean var3) {
   }

   public abstract ActiveJobResult perform();

   public Level getLevel() {
      return this.worker.getMobWorker().getLevel();
   }
}
