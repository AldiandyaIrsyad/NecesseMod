package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.util.HashSet;
import java.util.Iterator;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.entity.mobs.ai.path.TilePathfinding;
import necesse.entity.mobs.friendly.HoneyBeeMob;
import necesse.entity.mobs.friendly.QueenBeeMob;
import necesse.entity.objectEntity.AbstractBeeHiveObjectEntity;

public class MigrateToApiaryAINode<T extends QueenBeeMob> extends MoveTaskAINode<T> {
   public long findPathHomeCooldown;
   public boolean movingToApiary;

   public MigrateToApiaryAINode() {
   }

   protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
   }

   public void init(T var1, Blackboard<T> var2) {
   }

   public AINodeResult tickNode(T var1, Blackboard<T> var2) {
      AbstractBeeHiveObjectEntity var3;
      if (this.movingToApiary && var1.migrationApiary != null && var1.shouldMigrate()) {
         if (var2.mover.isCurrentlyMovingFor(this)) {
            return AINodeResult.RUNNING;
         }

         var3 = (AbstractBeeHiveObjectEntity)var1.getLevel().entityManager.getObjectEntity(var1.migrationApiary.x, var1.migrationApiary.y, AbstractBeeHiveObjectEntity.class);
         if (var3 != null && TilePathfinding.isAtOrAdjacentObject(var1.getLevel(), var1.migrationApiary.x, var1.migrationApiary.y, var1.getTileX(), var1.getTileY())) {
            if (var3.canTakeMigratingQueen()) {
               HashSet var4 = new HashSet();
               Iterator var5 = var1.honeyBeeUniqueIDs.iterator();

               while(var5.hasNext()) {
                  int var6 = (Integer)var5.next();
                  Mob var7 = (Mob)var1.getLevel().entityManager.mobs.get(var6, false);
                  if (var7 instanceof HoneyBeeMob) {
                     HoneyBeeMob var8 = (HoneyBeeMob)var7;
                     var8.setApiaryHome(var1.migrationApiary.x, var1.migrationApiary.y);
                     var8.followingQueen.uniqueID = -1;
                  } else {
                     var4.add(var6);
                  }
               }

               var4.forEach((var1x) -> {
                  var1.honeyBeeUniqueIDs.remove(var1x);
               });
               var3.migrateQueen(var1);
               var1.remove();
            } else {
               var1.clearMigrationApiary();
            }
         } else {
            this.movingToApiary = false;
         }
      }

      if (this.findPathHomeCooldown <= var1.getWorldEntity().getTime()) {
         this.findPathHomeCooldown = var1.getWorldEntity().getTime() + (long)(GameRandom.globalRandom.getIntBetween(10, 30) * 1000);
         if (var1.migrationApiary != null && var1.shouldMigrate()) {
            var3 = (AbstractBeeHiveObjectEntity)var1.getLevel().entityManager.getObjectEntity(var1.migrationApiary.x, var1.migrationApiary.y, AbstractBeeHiveObjectEntity.class);
            if (var3 != null && var1.estimateCanMoveTo(var1.migrationApiary.x, var1.migrationApiary.y, true)) {
               return this.moveToTileTask(var1.migrationApiary.x, var1.migrationApiary.y, TilePathfinding.isAtOrAdjacentObject(var1.getLevel(), var1.migrationApiary.x, var1.migrationApiary.y), (var1x) -> {
                  if (var1x.result.foundTarget) {
                     this.movingToApiary = true;
                     var1x.move((Runnable)null);
                     return AINodeResult.RUNNING;
                  } else {
                     return AINodeResult.FAILURE;
                  }
               });
            }

            var1.clearMigrationApiary();
         }
      }

      return AINodeResult.FAILURE;
   }

   // $FF: synthetic method
   // $FF: bridge method
   public AINodeResult tickNode(Mob var1, Blackboard var2) {
      return this.tickNode((QueenBeeMob)var1, var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public void init(Mob var1, Blackboard var2) {
      this.init((QueenBeeMob)var1, var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
      this.onRootSet(var1, (QueenBeeMob)var2, var3);
   }
}
