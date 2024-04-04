package necesse.entity.mobs.job.activeJob;

import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.level.maps.levelData.jobs.JobMoveToTile;

public abstract class SimplePerformActiveJob extends ActiveJob {
   public SimplePerformActiveJob(EntityJobWorker var1, JobTypeHandler.TypePriority var2) {
      super(var1, var2);
   }

   public JobMoveToTile getMoveToTile(JobMoveToTile var1) {
      return null;
   }

   public boolean isAt(JobMoveToTile var1) {
      return true;
   }

   public void tick(boolean var1, boolean var2) {
   }

   public boolean isValid(boolean var1) {
      return true;
   }
}
