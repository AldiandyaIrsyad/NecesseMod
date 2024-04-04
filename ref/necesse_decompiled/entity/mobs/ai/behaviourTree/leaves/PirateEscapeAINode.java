package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.util.function.BiPredicate;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.hostile.pirates.PirateMob;

public class PirateEscapeAINode<T extends PirateMob> extends MoveTaskAINode<T> {
   public final int maxDistance;
   public boolean firedEvent;
   public long nextPathFindTime;

   public PirateEscapeAINode(int var1) {
      this.maxDistance = var1;
   }

   protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
      var3.onEvent("resetPathTime", (var1x) -> {
         this.nextPathFindTime = 0L;
      });
   }

   public void init(T var1, Blackboard<T> var2) {
   }

   public AINodeResult tickNode(T var1, Blackboard<T> var2) {
      if (var1.baseTile == null) {
         return AINodeResult.FAILURE;
      } else if (var1.getDistance((float)(var1.baseTile.x * 32 + 16), (float)(var1.baseTile.y * 32 + 16)) > (float)this.maxDistance) {
         if (!this.firedEvent) {
            this.nextPathFindTime = 0L;
            this.onEscape(var1);
            this.firedEvent = true;
         }

         if (!var1.buffManager.hasBuff(BuffRegistry.PIRATE_ESCAPE)) {
            var1.addBuff(new ActiveBuff(BuffRegistry.PIRATE_ESCAPE, var1, 0, (Attacker)null), true);
         }

         if (this.nextPathFindTime <= var1.getWorldEntity().getLocalTime()) {
            this.nextPathFindTime = var1.getWorldEntity().getLocalTime() + 1000L;
            return this.moveToTileTask(var1.baseTile.x, var1.baseTile.y, (BiPredicate)null, (var2x) -> {
               if (var2x.moveIfWithin(-1, 1, () -> {
                  this.nextPathFindTime = 0L;
               })) {
                  int var3 = var2x.getNextPathTimeBasedOnPathTime(var1.getSpeed(), 1.5F, 2000, 0.1F);
                  this.nextPathFindTime = var1.getWorldEntity().getLocalTime() + (long)var3;
               }

               return AINodeResult.SUCCESS;
            });
         } else {
            return AINodeResult.SUCCESS;
         }
      } else {
         this.firedEvent = false;
         if (var1.buffManager.hasBuff(BuffRegistry.PIRATE_ESCAPE)) {
            var1.buffManager.removeBuff(BuffRegistry.PIRATE_ESCAPE, true);
         }

         return AINodeResult.FAILURE;
      }
   }

   public void onEscape(T var1) {
   }

   // $FF: synthetic method
   // $FF: bridge method
   public AINodeResult tickNode(Mob var1, Blackboard var2) {
      return this.tickNode((PirateMob)var1, var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public void init(Mob var1, Blackboard var2) {
      this.init((PirateMob)var1, var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
      this.onRootSet(var1, (PirateMob)var2, var3);
   }
}
