package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.util.function.Supplier;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.tickManager.Performance;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.job.JobSequence;
import necesse.entity.mobs.job.activeJob.ActiveJob;

public class HumanJobSearchingAINode<T extends HumanMob> extends AINode<T> {
   public HumanJobSearchingAINode() {
   }

   protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
   }

   public void init(T var1, Blackboard<T> var2) {
   }

   public AINodeResult tick(T var1, Blackboard<T> var2) {
      if (!var1.isBeingInteractedWith()) {
         AINodeResult var3 = (AINodeResult)Performance.record(var1.getLevel().tickManager(), "humanJobSearch", (Supplier)(() -> {
            ActiveJob var2x = (ActiveJob)var2.getObject(ActiveJob.class, "currentJob");
            if (var2x != null) {
               return AINodeResult.SUCCESS;
            } else {
               JobSequence var3 = (JobSequence)var2.getObject(JobSequence.class, "currentJobSequence");
               if (var3 == null) {
                  var1.resetJobCancelled();
                  var3 = var1.findJob();
               } else if (var1.isJobCancelled()) {
                  var3.cancel(false);
                  var1.resetJobCancelled();
               }

               if (var3 != null && var3.hasNext()) {
                  if (var1.objectUser != null) {
                     var1.objectUser.stopUsing();
                  }

                  ActiveJob var4 = var3.next();
                  if (var4 != null) {
                     var4.onMadeCurrent();
                  }

                  var2.put("currentJobSequence", var3);
                  var2.put("currentJob", var4);
                  GameMessage var5 = var3.getActivityDescription();
                  if (var5 != null) {
                     var1.setActivity("job", 10000, var5);
                  }

                  return AINodeResult.SUCCESS;
               } else {
                  return null;
               }
            }
         }));
         if (var3 != null) {
            return var3;
         }

         var2.put("currentJobSequence", (Object)null);
         var2.put("currentJob", (Object)null);
         var1.resetJobCancelled();
      }

      var1.clearActivity((String)null);
      return AINodeResult.FAILURE;
   }

   // $FF: synthetic method
   // $FF: bridge method
   public AINodeResult tick(Mob var1, Blackboard var2) {
      return this.tick((HumanMob)var1, var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public void init(Mob var1, Blackboard var2) {
      this.init((HumanMob)var1, var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
      this.onRootSet(var1, (HumanMob)var2, var3);
   }
}
