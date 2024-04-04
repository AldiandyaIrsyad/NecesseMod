package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.Point;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ObjectUserActive;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.entity.mobs.ai.path.TilePathfinding;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.objectEntity.BedObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.furniture.BedObject;

public class HumanSleepAINode<T extends HumanMob> extends MoveTaskAINode<T> {
   public int ticks;
   public Point moveToBedPos;
   public ObjectUserActive active;

   public HumanSleepAINode() {
   }

   protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
      this.ticks = 20 * GameRandom.globalRandom.getIntBetween(5, 10);
      var3.onWasHit((var2x) -> {
         if (this.active != null && this.active == var2.objectUser) {
            this.active.stopUsing();
            this.active = null;
         }

      });
      var3.onUnloading((var2x) -> {
         if (this.active != null && this.active == var2.objectUser) {
            this.active.stopUsing();
            this.active = null;
         }

      });
   }

   protected void onInterruptRunning(T var1, Blackboard<T> var2) {
      super.onInterruptRunning(var1, var2);
      if (this.active != null && this.active == var1.objectUser) {
         this.active.stopUsing();
         this.active = null;
      }

   }

   public void init(T var1, Blackboard<T> var2) {
   }

   public boolean canSleep(T var1) {
      if (var1.isTravelingHuman()) {
         return false;
      } else if (var1.isBeingInteractedWith()) {
         return false;
      } else if (var1.home == null) {
         return false;
      } else if (var1.getLevel().settlementLayer.isRaidActive()) {
         return false;
      } else {
         return !var1.hasCommandOrders() || var1.isHiding;
      }
   }

   public boolean shouldSleep(T var1) {
      if (var1.isOnStrike()) {
         return false;
      } else {
         return var1.isHiding || var1.getWorldEntity().isNight();
      }
   }

   public AINodeResult tickNode(T var1, Blackboard<T> var2) {
      if (this.active != null && var1.objectUser == this.active) {
         this.getBlackboard().put("isHidingInside", true);
         boolean var6 = false;
         if (!this.canSleep(var1)) {
            var6 = true;
         } else {
            --this.ticks;
            if (this.ticks <= 0) {
               if (!this.shouldSleep(var1)) {
                  var6 = true;
               } else {
                  this.ticks = 20 * GameRandom.globalRandom.getIntBetween(10, 30);
               }
            }
         }

         if (var6) {
            this.active.stopUsing();
            this.active = null;
            this.ticks = 20 * GameRandom.globalRandom.getIntBetween(5, 10);
         } else {
            this.active.keepUsing();
         }

         return AINodeResult.SUCCESS;
      } else {
         if (this.moveToBedPos != null && this.canSleep(var1) && this.canSleep(var1)) {
            if (var2.mover.isCurrentlyMovingFor(this)) {
               return AINodeResult.RUNNING;
            }

            ObjectEntity var3 = var1.getLevel().entityManager.getObjectEntity(this.moveToBedPos.x, this.moveToBedPos.y);
            if (var3 instanceof BedObjectEntity && TilePathfinding.isAtOrAdjacentObject(var1.getLevel(), this.moveToBedPos.x, this.moveToBedPos.y, var1.getTileX(), var1.getTileY()) && ((BedObjectEntity)var3).canUse(var1)) {
               ((BedObjectEntity)var3).startUser(var1);
               this.active = var1.objectUser;
               this.moveToBedPos = null;
               this.ticks = 20 * GameRandom.globalRandom.getIntBetween(10, 30);
               return AINodeResult.RUNNING;
            }
         }

         this.moveToBedPos = null;
         this.active = null;
         if (var2.mover.isCurrentlyMovingFor(this)) {
            var2.mover.stopMoving(var1);
         }

         if (this.canSleep(var1) && this.shouldSleep(var1)) {
            --this.ticks;
            if (this.ticks <= 0) {
               this.ticks = 20 * GameRandom.globalRandom.getIntBetween(5, 10);
               GameObject var5 = var1.getLevel().getObject(var1.home.x, var1.home.y);
               if (var5 instanceof BedObject && var1.estimateCanMoveTo(var1.home.x, var1.home.y, true)) {
                  ObjectEntity var4 = var1.getLevel().entityManager.getObjectEntity(var1.home.x, var1.home.y);
                  if (var4 instanceof BedObjectEntity && ((BedObjectEntity)var4).canUse(var1)) {
                     return this.moveToTileTask(var1.home.x, var1.home.y, TilePathfinding.isAtOrAdjacentObject(var1.getLevel(), var1.home.x, var1.home.y), (var2x) -> {
                        if (var2x.result.foundTarget) {
                           this.moveToBedPos = new Point(var1.home.x, var1.home.y);
                           var2x.move((Runnable)null);
                           return AINodeResult.RUNNING;
                        } else {
                           return null;
                        }
                     });
                  }
               }
            }
         }

         return AINodeResult.FAILURE;
      }
   }

   // $FF: synthetic method
   // $FF: bridge method
   public AINodeResult tickNode(Mob var1, Blackboard var2) {
      return this.tickNode((HumanMob)var1, var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected void onInterruptRunning(Mob var1, Blackboard var2) {
      this.onInterruptRunning((HumanMob)var1, var2);
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
