package necesse.entity.mobs.job;

import necesse.engine.localization.message.GameMessage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.job.activeJob.ActiveJob;
import necesse.entity.mobs.job.activeJob.ActiveJobTargetFoundResult;

public interface JobSequence {
   GameMessage getActivityDescription();

   void setActivityDescription(GameMessage var1);

   void tick();

   boolean hasNext();

   ActiveJob next();

   boolean isValid();

   ActiveJobTargetFoundResult onTargetFound(Mob var1);

   void cancel(boolean var1);
}
