package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.Point;
import java.util.function.BiPredicate;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.entity.mobs.ai.path.TilePathfinding;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.friendly.human.MoveToPoint;

public class HumanMoveToAINode<T extends HumanMob> extends MoveTaskAINode<T> {
   public long pathFindingCooldown;

   public HumanMoveToAINode() {
   }

   protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
      var3.onEvent("newTargetFound", (var1x) -> {
         if (var2.objectUser != null) {
            var2.objectUser.stopUsing();
         }

      });
   }

   public void init(T var1, Blackboard<T> var2) {
   }

   public AINodeResult tickNode(T var1, Blackboard<T> var2) {
      MoveToPoint var3 = var1.getMoveToPoint();
      if (var3 == null) {
         return AINodeResult.FAILURE;
      } else {
         if (var1.objectUser != null) {
            var1.objectUser.stopUsing();
         }

         float var4 = (float)var3.distance((double)((float)var1.getX() / 32.0F), (double)((float)var1.getY() / 32.0F));
         if (this.pathFindingCooldown < var1.getWorldEntity().getLocalTime()) {
            this.pathFindingCooldown = var1.getWorldEntity().getLocalTime() + 2000L;
            BiPredicate var6;
            if (var3.acceptAdjacentTiles) {
               var6 = TilePathfinding.isAtOrAdjacentObject(var1.getLevel(), var3.x, var3.y);
            } else {
               var6 = null;
            }

            return this.moveToTileTask(var3.x, var3.y, var6, (var4x) -> {
               boolean var5;
               if (var3.moveIfPathFailed(var4)) {
                  var5 = var4x.moveIfWithin(-1, -1, () -> {
                     this.pathFindingCooldown = 0L;
                  });
               } else {
                  var5 = var4x.moveIfWithin(-1, 1, () -> {
                     this.pathFindingCooldown = 0L;
                  });
               }

               if (var5) {
                  int var6 = var4x.getNextPathTimeBasedOnPathTime(var1.getSpeed(), 1.5F, 2000, 0.1F);
                  this.pathFindingCooldown = var1.getWorldEntity().getLocalTime() + (long)var6;
               }

               if (var3.isAtLocation(var4, var5)) {
                  var3.onAtLocation();
               }

               return AINodeResult.SUCCESS;
            });
         } else {
            Point var5 = var2.mover.getFinalDestination();
            if (var5 != null && var3.isAtLocation(var4, var5.x == var3.x && var5.y == var3.y)) {
               var3.onAtLocation();
            }

            return AINodeResult.SUCCESS;
         }
      }
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
