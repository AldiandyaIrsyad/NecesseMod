package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.tickManager.Performance;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.entity.mobs.ai.behaviourTree.event.AIEvent;
import necesse.entity.mobs.ai.behaviourTree.event.AIWasHitEvent;
import necesse.entity.mobs.ai.behaviourTree.event.TargetAIEvent;
import necesse.entity.mobs.ai.path.TilePathfinding;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.job.JobSequence;
import necesse.entity.mobs.job.activeJob.ActiveJob;
import necesse.entity.mobs.job.activeJob.ActiveJobHitResult;
import necesse.entity.mobs.job.activeJob.ActiveJobTargetFoundResult;
import necesse.level.maps.levelData.jobs.JobMoveToTile;

public class HumanJobMoveToAINode<T extends HumanMob> extends MoveTaskAINode<T> {
   public int pathsSinceProgress;
   public double lastPathDistToTarget;
   public long nextMoveTime;
   public long nextValidCheck;
   public JobMoveToTile jobMoveTile;

   public HumanJobMoveToAINode() {
   }

   protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
   }

   public void init(T var1, Blackboard<T> var2) {
      this.nextMoveTime = 0L;
      this.nextValidCheck = 0L;
      this.jobMoveTile = null;
      this.pathsSinceProgress = 0;
      this.lastPathDistToTarget = -1.0;
   }

   protected boolean checkValid(T var1, Blackboard<T> var2) {
      long var3 = var1.getWorldEntity().getTime();
      if (this.nextValidCheck <= var3) {
         ActiveJob var5 = (ActiveJob)var2.getObject(ActiveJob.class, "currentJob");
         JobSequence var6 = (JobSequence)var2.getObject(JobSequence.class, "currentJobSequence");
         if (!var5.isValid(false)) {
            var2.mover.stopMoving(var1);
            var2.put("currentJob", (Object)null);
            if (var5.shouldClearSequence()) {
               var2.put("currentJobSequence", (Object)null);
            }

            return false;
         }

         if (var6 != null && !var6.isValid()) {
            var2.mover.stopMoving(var1);
            var2.put("currentJob", (Object)null);
            var2.put("currentJobSequence", (Object)null);
            return false;
         }

         this.nextValidCheck = var3 + 2000L;
      }

      return true;
   }

   public AINodeResult tickWorking(T var1, Blackboard<T> var2) {
      ActiveJob var3 = (ActiveJob)var2.getObject(ActiveJob.class, "currentJob");
      return var3 == null || var3.priority != null && var3.priority.disabledByPlayer ? super.tickWorking(var1, var2) : (AINodeResult)Performance.record(var1.getLevel().tickManager(), "humanJobMoveTo", (Supplier)(() -> {
         JobSequence var4 = (JobSequence)var2.getObject(JobSequence.class, "currentJobSequence");
         boolean var5 = !this.checkValid(var1, var2);
         if (!var5 && !var1.isJobCancelled()) {
            var3.tick(true, true);
            if (var4 != null) {
               var4.tick();
            }

            return AINodeResult.RUNNING;
         } else {
            var3.onCancelled(var5, true, true);
            if (var4 != null) {
               var4.cancel(var5);
            }

            var1.resetJobCancelled();
            this.clearTask();
            return AINodeResult.FAILURE;
         }
      }));
   }

   public AINodeResult tickNode(T var1, Blackboard<T> var2) {
      ActiveJob var3 = (ActiveJob)var2.getObject(ActiveJob.class, "currentJob");
      return var3 != null ? (AINodeResult)Performance.record(var1.getLevel().tickManager(), "humanJobMoveTo", (Supplier)(() -> {
         JobSequence var4 = (JobSequence)var2.getObject(JobSequence.class, "currentJobSequence");
         if (var3.priority != null && var3.priority.disabledByPlayer) {
            var3.onCancelled(false, true, true);
            if (var4 != null) {
               var4.cancel(false);
            }

            var2.put("currentJobSequence", (Object)null);
            var2.put("currentJob", (Object)null);
            return AINodeResult.FAILURE;
         } else {
            Iterator var5 = var2.getLastHits().iterator();

            while(var5.hasNext()) {
               AIWasHitEvent var6 = (AIWasHitEvent)var5.next();
               ActiveJobHitResult var7 = var3.onHit(var6.event, false);
               switch (var7) {
                  case CLEAR_SEQUENCE:
                     var3.onCancelled(false, true, true);
                     if (var4 != null) {
                        var4.cancel(false);
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
               }
            }

            var5 = var2.getLastCustomEvents("newTargetFound").iterator();

            while(var5.hasNext()) {
               AIEvent var12 = (AIEvent)var5.next();
               if (var12 instanceof TargetAIEvent) {
                  Mob var15 = ((TargetAIEvent)var12).target;
                  ActiveJobTargetFoundResult var8 = var3.onTargetFound(var15, true, true);
                  ActiveJobTargetFoundResult var9 = var4 == null ? null : var4.onTargetFound(var15);
                  if (var9 == ActiveJobTargetFoundResult.FAIL) {
                     var8 = var9;
                  }

                  switch (var8) {
                     case CONTINUE:
                     default:
                        break;
                     case FAIL:
                        var3.onCancelled(false, true, true);
                        if (var4 != null) {
                           var4.cancel(false);
                        }

                        var2.put("currentJobSequence", (Object)null);
                        var2.put("currentJob", (Object)null);
                        var3.worker.onTargetFoundCausedFailed(var15);
                        return AINodeResult.FAILURE;
                  }
               }
            }

            boolean var11 = !this.checkValid(var1, var2);
            if (!var11 && !var1.isJobCancelled()) {
               var3.tick(true, true);
               if (var4 != null) {
                  var4.tick();
                  GameMessage var13 = var4.getActivityDescription();
                  if (var13 != null) {
                     var1.setActivity("job", 10000, var13);
                  }
               }

               if (!var1.isBeingInteractedWith()) {
                  JobMoveToTile var14 = this.jobMoveTile;
                  this.jobMoveTile = var3.getMoveToTile(this.jobMoveTile);
                  if (this.jobMoveTile == null) {
                     var2.mover.stopMoving(var1);
                     return AINodeResult.SUCCESS;
                  } else if (var3.isAt(this.jobMoveTile)) {
                     var2.mover.stopMoving(var1);
                     return AINodeResult.SUCCESS;
                  } else if (this.jobMoveTile.custom != null) {
                     var2.mover.setCustomMovement(this, this.jobMoveTile.custom);
                     this.pathsSinceProgress = 0;
                     return AINodeResult.RUNNING;
                  } else {
                     if (!this.jobMoveTile.equals(var14)) {
                        this.nextMoveTime = 0L;
                     }

                     long var16 = var1.getWorldEntity().getLocalTime();
                     if (this.pathsSinceProgress > 5) {
                        var2.put("currentJob", (Object)null);
                        return AINodeResult.FAILURE;
                     } else {
                        if (this.nextMoveTime <= var16) {
                           this.nextMoveTime = var16 + 5000L;
                           if (var1.estimateCanMoveTo(this.jobMoveTile.tileX, this.jobMoveTile.tileY, this.jobMoveTile.acceptAdjacentTiles)) {
                              BiPredicate var17;
                              if (this.jobMoveTile.acceptAdjacentTiles) {
                                 var17 = TilePathfinding.isAtOrAdjacentObject(var1.getLevel(), this.jobMoveTile.tileX, this.jobMoveTile.tileY);
                              } else {
                                 var17 = null;
                              }

                              JobMoveToTile var10 = this.jobMoveTile;
                              return this.moveToTileTask(var10.tileX, var10.tileY, var17, var10.maxPathIterations, (var3x) -> {
                                 if (!var3x.result.foundTarget && (!var10.acceptAdjacentTiles || !var3x.isResultWithin(1))) {
                                    Point var4 = (Point)var3x.result.getLastPathResult();
                                    if (var4 == null) {
                                       ++this.pathsSinceProgress;
                                    } else {
                                       double var5 = var4.distance((Point2D)var3x.result.target);
                                       if (!(this.lastPathDistToTarget < 0.0) && var5 != 0.0 && !(var5 < this.lastPathDistToTarget)) {
                                          ++this.pathsSinceProgress;
                                       } else {
                                          this.pathsSinceProgress = 0;
                                          this.lastPathDistToTarget = var5;
                                       }
                                    }
                                 } else {
                                    this.pathsSinceProgress = 0;
                                    this.lastPathDistToTarget = 0.0;
                                 }

                                 if (var3x.moveIfWithin(-1, -1, () -> {
                                    this.nextMoveTime = 0L;
                                 })) {
                                    int var7 = var3x.getNextPathTimeBasedOnPathTime(var1.getSpeed(), 1.5F, 5000, 0.1F);
                                    this.nextMoveTime = var1.getWorldEntity().getLocalTime() + (long)var7;
                                 }

                                 return AINodeResult.RUNNING;
                              });
                           }

                           ++this.pathsSinceProgress;
                        }

                        return AINodeResult.RUNNING;
                     }
                  }
               } else {
                  var2.mover.stopMoving(var1);
                  return AINodeResult.RUNNING;
               }
            } else {
               var3.onCancelled(var11, true, true);
               if (var4 != null) {
                  var4.cancel(var11);
               }

               var1.resetJobCancelled();
               return AINodeResult.FAILURE;
            }
         }
      })) : AINodeResult.FAILURE;
   }

   // $FF: synthetic method
   // $FF: bridge method
   public AINodeResult tickWorking(Mob var1, Blackboard var2) {
      return this.tickWorking((HumanMob)var1, var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public AINodeResult tickNode(Mob var1, Blackboard var2) {
      return this.tickNode((HumanMob)var1, var2);
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
