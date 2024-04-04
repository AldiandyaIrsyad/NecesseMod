package necesse.entity.mobs.ai.behaviourTree.leaves;

import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.entity.mobs.ai.path.TilePathfinding;
import necesse.entity.mobs.friendly.HoneyBeeMob;
import necesse.entity.objectEntity.AbstractBeeHiveObjectEntity;

public class ReturnToApiaryAINode<T extends HoneyBeeMob> extends MoveTaskAINode<T> {
   public long findPathHomeCooldown;
   public boolean movingToApiary;

   public ReturnToApiaryAINode() {
   }

   protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
   }

   public void init(T var1, Blackboard<T> var2) {
   }

   public AINodeResult tickNode(T var1, Blackboard<T> var2) {
      AbstractBeeHiveObjectEntity var3;
      if (this.movingToApiary && var1.apiaryHome != null && var1.shouldReturnToApiary()) {
         if (var2.mover.isCurrentlyMovingFor(this)) {
            return AINodeResult.RUNNING;
         }

         var3 = (AbstractBeeHiveObjectEntity)var1.getLevel().entityManager.getObjectEntity(var1.apiaryHome.x, var1.apiaryHome.y, AbstractBeeHiveObjectEntity.class);
         if (var3 != null && TilePathfinding.isAtOrAdjacentObject(var1.getLevel(), var1.apiaryHome.x, var1.apiaryHome.y, var1.getTileX(), var1.getTileY())) {
            var3.onRoamingBeeReturned(var1);
            var1.remove();
         } else {
            this.movingToApiary = false;
         }
      }

      if (this.findPathHomeCooldown <= var1.getWorldEntity().getTime()) {
         this.findPathHomeCooldown = var1.getWorldEntity().getTime() + (long)(GameRandom.globalRandom.getIntBetween(10, 30) * 1000);
         if (var1.apiaryHome != null && var1.shouldReturnToApiary()) {
            var3 = (AbstractBeeHiveObjectEntity)var1.getLevel().entityManager.getObjectEntity(var1.apiaryHome.x, var1.apiaryHome.y, AbstractBeeHiveObjectEntity.class);
            if (var3 != null && var1.estimateCanMoveTo(var1.apiaryHome.x, var1.apiaryHome.y, true)) {
               return this.moveToTileTask(var1.apiaryHome.x, var1.apiaryHome.y, TilePathfinding.isAtOrAdjacentObject(var1.getLevel(), var1.apiaryHome.x, var1.apiaryHome.y), (var1x) -> {
                  if (var1x.result.foundTarget) {
                     this.movingToApiary = true;
                     var1x.move((Runnable)null);
                     return AINodeResult.RUNNING;
                  } else {
                     return AINodeResult.FAILURE;
                  }
               });
            }

            var1.clearApiaryHome();
         }
      }

      return AINodeResult.FAILURE;
   }

   // $FF: synthetic method
   // $FF: bridge method
   public AINodeResult tickNode(Mob var1, Blackboard var2) {
      return this.tickNode((HoneyBeeMob)var1, var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public void init(Mob var1, Blackboard var2) {
      this.init((HoneyBeeMob)var1, var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
      this.onRootSet(var1, (HoneyBeeMob)var2, var3);
   }
}
