package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.util.Iterator;
import java.util.function.Supplier;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.tickManager.Performance;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.event.AIEvent;
import necesse.entity.mobs.ai.behaviourTree.event.AIWasHitEvent;
import necesse.entity.mobs.ai.behaviourTree.event.TargetAIEvent;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.job.JobSequence;
import necesse.entity.mobs.job.activeJob.ActiveJob;
import necesse.entity.mobs.job.activeJob.ActiveJobHitResult;
import necesse.entity.mobs.job.activeJob.ActiveJobResult;
import necesse.entity.mobs.job.activeJob.ActiveJobTargetFoundResult;

public class HumanDoJobAINode<T extends HumanMob> extends AINode<T> {
   public HumanDoJobAINode() {
   }

   protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
   }

   public void init(T var1, Blackboard<T> var2) {
   }

   public AINodeResult tick(T var1, Blackboard<T> var2) {
      ActiveJob var3 = (ActiveJob)var2.getObject(ActiveJob.class, "currentJob");
      return var3 != null ? (AINodeResult)Performance.record(var1.getLevel().tickManager(), "humanJobPerform", (Supplier)(() -> {
         return (AINodeResult)Performance.record(var1.getLevel().tickManager(), var3.priority == null ? "null" : var3.priority.type.getStringID(), (Supplier)(() -> {
            JobSequence var3x = (JobSequence)var2.getObject(JobSequence.class, "currentJobSequence");
            if (var3.priority != null && var3.priority.disabledByPlayer) {
               var3.onCancelled(false, true, true);
               if (var3x != null) {
                  var3x.cancel(false);
               }

               var2.put("currentJobSequence", (Object)null);
               var2.put("currentJob", (Object)null);
               return AINodeResult.FAILURE;
            } else {
               Iterator var4 = var2.getLastHits().iterator();

               while(var4.hasNext()) {
                  AIWasHitEvent var5 = (AIWasHitEvent)var4.next();
                  if (!var5.event.wasPrevented) {
                     ActiveJobHitResult var6 = var3.onHit(var5.event, false);
                     switch (var6) {
                        case CLEAR_SEQUENCE:
                           var3.onCancelled(false, true, true);
                           if (var3x != null) {
                              var3x.cancel(false);
                           }

                           var2.put("currentJobSequence", (Object)null);
                           var2.put("currentJob", (Object)null);
                           var3.worker.onHitCausedFailed(true);
                           return AINodeResult.FAILURE;
                        case CLEAR_THIS:
                           var3.onCancelled(false, true, true);
                           var2.put("currentJob", (Object)null);
                           var3.worker.onHitCausedFailed(false);
                           return AINodeResult.SUCCESS;
                        case MOVE_TO:
                           return AINodeResult.SUCCESS;
                     }
                  }
               }

               var4 = var2.getLastCustomEvents("newTargetFound").iterator();

               while(var4.hasNext()) {
                  AIEvent var10 = (AIEvent)var4.next();
                  if (var10 instanceof TargetAIEvent) {
                     Mob var13 = ((TargetAIEvent)var10).target;
                     ActiveJobTargetFoundResult var7 = var3.onTargetFound(var13, true, false);
                     if (var3x != null) {
                        ActiveJobTargetFoundResult var8 = var3x.onTargetFound(var13);
                        if (var8 == ActiveJobTargetFoundResult.FAIL) {
                           var7 = var8;
                        }
                     }

                     switch (var7) {
                        case CONTINUE:
                        default:
                           break;
                        case FAIL:
                           var3.onCancelled(false, true, true);
                           if (var3x != null) {
                              var3x.cancel(false);
                           }

                           var2.put("currentJobSequence", (Object)null);
                           var2.put("currentJob", (Object)null);
                           var3.worker.onTargetFoundCausedFailed(var13);
                           return AINodeResult.FAILURE;
                     }
                  }
               }

               boolean var9 = !var3.isValid(true);
               if (!var9 && !var1.isJobCancelled()) {
                  var3.tick(true, false);
                  if (var3x != null) {
                     var3x.tick();
                     GameMessage var11 = var3x.getActivityDescription();
                     if (var11 != null) {
                        var1.setActivity("job", 10000, var11);
                     }
                  }

                  if (!var1.isBeingInteractedWith()) {
                     ActiveJobResult var12 = var3.perform();
                     switch (var12) {
                        case PERFORMING:
                           return AINodeResult.RUNNING;
                        case MOVE_TO:
                           return AINodeResult.SUCCESS;
                        case FINISHED:
                           var2.put("currentJob", (Object)null);
                           return AINodeResult.SUCCESS;
                        default:
                           var2.put("currentJobSequence", (Object)null);
                           var2.put("currentJob", (Object)null);
                           return AINodeResult.FAILURE;
                     }
                  } else {
                     var2.mover.stopMoving(var1);
                     return AINodeResult.RUNNING;
                  }
               } else {
                  var2.mover.stopMoving(var1);
                  var2.put("currentJob", (Object)null);
                  if (var3.shouldClearSequence()) {
                     var2.put("currentJobSequence", (Object)null);
                  }

                  var3.onCancelled(var9, true, true);
                  if (var3x != null) {
                     var3x.cancel(var9);
                  }

                  var1.resetJobCancelled();
                  return AINodeResult.FAILURE;
               }
            }
         }));
      })) : AINodeResult.FAILURE;
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
