package necesse.entity.mobs.job.activeJob;

import necesse.level.maps.levelData.jobs.JobMoveToTile;

public class IgnoreFailureActiveJob extends ActiveJob {
   public final ActiveJob job;

   public IgnoreFailureActiveJob(ActiveJob var1) {
      super(var1.worker, var1.priority);
      this.job = var1;
   }

   public JobMoveToTile getMoveToTile(JobMoveToTile var1) {
      return this.job.getMoveToTile(var1);
   }

   public boolean isAt(JobMoveToTile var1) {
      return this.job.isAt(var1);
   }

   public void tick(boolean var1, boolean var2) {
      this.job.tick(var1, var2);
   }

   public boolean isValid(boolean var1) {
      return this.job.isValid(var1);
   }

   public void onCancelled(boolean var1, boolean var2, boolean var3) {
      this.job.onCancelled(var1, var2, var3);
   }

   public ActiveJobResult perform() {
      ActiveJobResult var1 = this.job.perform();
      return var1 != ActiveJobResult.FAILED && var1 != null ? var1 : ActiveJobResult.FINISHED;
   }
}
