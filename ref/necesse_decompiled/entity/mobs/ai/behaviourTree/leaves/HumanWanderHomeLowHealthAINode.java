package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.Point;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.friendly.human.HumanMob;

public class HumanWanderHomeLowHealthAINode<T extends HumanMob> extends WanderHomeAtConditionAINode<T> {
   public long nextApplyBuffTime;

   public HumanWanderHomeLowHealthAINode() {
   }

   public AINodeResult tickNode(T var1, Blackboard<T> var2) {
      if (var2.mover.isCurrentlyMovingFor(this)) {
         if (this.nextApplyBuffTime <= var1.getTime()) {
            var1.buffManager.addBuff(new ActiveBuff(BuffRegistry.SETTLER_SPRINT, var1, 2.0F, (Attacker)null), true);
            this.nextApplyBuffTime = var1.getTime() + 1500L;
         }
      } else {
         this.nextApplyBuffTime = 0L;
      }

      return super.tickNode(var1, var2);
   }

   public boolean shouldGoHome(T var1) {
      return !var1.isTravelingHuman() && var1.isSettlerOnCurrentLevel() ? var1.isHiding : false;
   }

   public Point getHomeTile(T var1) {
      return !var1.isSettlerOnCurrentLevel() ? null : var1.home;
   }

   public boolean isHomeRoom(T var1) {
      return var1.isSettlerOnCurrentLevel();
   }

   public boolean isHomeHouse(T var1) {
      return true;
   }

   // $FF: synthetic method
   // $FF: bridge method
   public boolean isHomeHouse(Mob var1) {
      return this.isHomeHouse((HumanMob)var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public boolean isHomeRoom(Mob var1) {
      return this.isHomeRoom((HumanMob)var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public Point getHomeTile(Mob var1) {
      return this.getHomeTile((HumanMob)var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public boolean shouldGoHome(Mob var1) {
      return this.shouldGoHome((HumanMob)var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public AINodeResult tickNode(Mob var1, Blackboard var2) {
      return this.tickNode((HumanMob)var1, var2);
   }
}
