package necesse.entity.mobs.job.activeJob;

import necesse.engine.registries.LevelJobRegistry;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.level.maps.levelData.jobs.JobMoveToTile;
import necesse.level.maps.levelData.jobs.LevelJob;

public class SetPrioritizeNextJobActiveJob extends ActiveJob {
   public int jobID;
   public boolean resetPrioritizeNextJobIfFound;

   public SetPrioritizeNextJobActiveJob(EntityJobWorker var1, JobTypeHandler.TypePriority var2, int var3, boolean var4) {
      super(var1, var2);
      this.jobID = var3;
      this.resetPrioritizeNextJobIfFound = var4;
   }

   public SetPrioritizeNextJobActiveJob(EntityJobWorker var1, JobTypeHandler.TypePriority var2, Class<? extends LevelJob> var3, boolean var4) {
      this(var1, var2, LevelJobRegistry.getJobID(var3), var4);
   }

   public JobMoveToTile getMoveToTile(JobMoveToTile var1) {
      return null;
   }

   public boolean isAt(JobMoveToTile var1) {
      return true;
   }

   public void onMadeCurrent() {
      super.onMadeCurrent();
      this.worker.setPrioritizeNextJob(this.jobID, this.resetPrioritizeNextJobIfFound);
   }

   public void tick(boolean var1, boolean var2) {
   }

   public boolean isValid(boolean var1) {
      return true;
   }

   public ActiveJobResult perform() {
      return ActiveJobResult.FINISHED;
   }
}
