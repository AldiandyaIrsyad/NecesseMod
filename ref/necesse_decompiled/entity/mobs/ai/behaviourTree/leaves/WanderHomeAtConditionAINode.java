package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.BiPredicate;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.level.maps.levelData.settlementData.ZoneTester;

public abstract class WanderHomeAtConditionAINode<T extends Mob> extends MoveTaskAINode<T> {
   public long nextPathFindTime;

   public WanderHomeAtConditionAINode() {
   }

   protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
   }

   public void init(T var1, Blackboard<T> var2) {
   }

   public AINodeResult tickNode(T var1, Blackboard<T> var2) {
      if (this.shouldGoHome(var1)) {
         Point var3;
         if (this.nextPathFindTime <= var1.getWorldEntity().getLocalTime()) {
            var3 = this.getHomeTile(var1);
            if (var3 != null) {
               boolean var4 = this.shouldFindNewPos(var1, var3);
               if (var4) {
                  Point var5 = null;
                  if (var1.getLevel().isOutside(var3.x, var3.y)) {
                     int var6 = this.getOutsideHomeRadius(var1);
                     var5 = WandererAINode.findWanderingPointAround(var1, var3.x, var3.y, var6, (ZoneTester)null, (var2x) -> {
                        return var1.getDistance((float)(var2x.tileX * 32 + 16), (float)(var2x.tileY * 32 + 16)) > (float)(var6 * 32) ? Integer.MIN_VALUE : var1.getTileWanderPriority(var2x);
                     }, 20, 5);
                  } else {
                     ArrayList var7 = null;
                     if (this.isHomeRoom(var1)) {
                        var7 = var1.getLevel().regionManager.getRoom(var3.x, var3.y);
                     } else if (this.isHomeHouse(var1)) {
                        var7 = var1.getLevel().regionManager.getHouse(var3.x, var3.y);
                     }

                     if (var7 != null) {
                        Objects.requireNonNull(var1);
                        var5 = WandererAINode.findWanderingPointInsideRegions(var1, var7, 25, var1::getTileWanderPriority, 20, 5);
                     }
                  }

                  if (var5 != null) {
                     return this.moveToTileTask(var5.x, var5.y, (BiPredicate)null, (var2x) -> {
                        if (var2x.moveIfWithin(-1, -1, () -> {
                           this.nextPathFindTime = 0L;
                        })) {
                           int var3 = var2x.getNextPathTimeBasedOnPathTime(var1.getSpeed(), 1.5F, 2000, 0.1F);
                           this.nextPathFindTime = var1.getWorldEntity().getLocalTime() + (long)var3;
                        } else {
                           this.nextPathFindTime = var1.getWorldEntity().getLocalTime() + (long)GameRandom.globalRandom.getIntBetween(10000, 16000);
                        }

                        return AINodeResult.RUNNING;
                     });
                  }

                  this.nextPathFindTime = var1.getWorldEntity().getLocalTime() + (long)GameRandom.globalRandom.getIntBetween(10000, 16000);
               }
            }
         }

         if (var2.mover.isMoving() && var2.mover.isCurrentlyMovingFor(this)) {
            var3 = this.getHomeTile(var1);
            return var3 != null && !this.shouldFindNewPos(var1, var3) ? AINodeResult.FAILURE : AINodeResult.RUNNING;
         } else {
            return AINodeResult.FAILURE;
         }
      } else {
         this.nextPathFindTime = 0L;
         return AINodeResult.FAILURE;
      }
   }

   protected int getOutsideHomeRadius(T var1) {
      return 6;
   }

   protected boolean shouldFindNewPos(T var1, Point var2) {
      boolean var3 = var1.getLevel().isOutside(var2.x, var2.y);
      return var3 ? var1.getDistance((float)(var2.x * 32 + 16), (float)(var2.y * 32 + 16)) > (float)(this.getOutsideHomeRadius(var1) * 32) : var1.getLevel().getRoomID(var1.getX() / 32, var1.getY() / 32) != var1.getLevel().getRoomID(var2.x, var2.y);
   }

   public abstract boolean shouldGoHome(T var1);

   public abstract Point getHomeTile(T var1);

   public abstract boolean isHomeRoom(T var1);

   public abstract boolean isHomeHouse(T var1);
}
