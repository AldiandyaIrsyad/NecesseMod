package necesse.entity.mobs.job;

import necesse.engine.localization.message.GameMessage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.job.activeJob.ActiveJob;
import necesse.entity.mobs.job.activeJob.ActiveJobTargetFoundResult;

public class SingleJobSequence implements JobSequence {
   private ActiveJob job;
   public GameMessage activityDescription;

   public SingleJobSequence(ActiveJob var1, GameMessage var2) {
      this.job = var1;
      this.activityDescription = var2;
   }

   public GameMessage getActivityDescription() {
      return this.activityDescription;
   }

   public void setActivityDescription(GameMessage var1) {
      this.activityDescription = var1;
   }

   public void tick() {
      if (this.job != null) {
         this.job.tick(false, false);
      }

   }

   public boolean hasNext() {
      return this.job != null;
   }

   public ActiveJob next() {
      ActiveJob var1 = this.job;
      this.job = null;
      return var1;
   }

   public boolean isValid() {
      return this.job == null || this.job.isValid(false);
   }

   public ActiveJobTargetFoundResult onTargetFound(Mob var1) {
      return this.job == null ? ActiveJobTargetFoundResult.CONTINUE : this.job.onTargetFound(var1, false, false);
   }

   public void cancel(boolean var1) {
      if (this.job != null) {
         this.job.onCancelled(var1, false, false);
      }

   }
}
