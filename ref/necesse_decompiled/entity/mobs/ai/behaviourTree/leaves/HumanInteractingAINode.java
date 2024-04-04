package necesse.entity.mobs.ai.behaviourTree.leaves;

import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.friendly.human.HumanMob;

public class HumanInteractingAINode<T extends HumanMob> extends AINode<T> {
   public HumanInteractingAINode() {
   }

   protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
   }

   public void init(T var1, Blackboard<T> var2) {
   }

   public AINodeResult tick(T var1, Blackboard<T> var2) {
      if (var1.isBeingInteractedWith()) {
         if (var1.objectUser != null) {
            var1.objectUser.stopUsing();
         }

         var2.mover.stopMoving(var1);
         return AINodeResult.SUCCESS;
      } else {
         return AINodeResult.FAILURE;
      }
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
