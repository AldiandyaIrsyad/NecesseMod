package necesse.entity.mobs.ai.behaviourTree.trees;

import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.BirdCritterRunAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.IdleAnimationAINode;
import necesse.entity.mobs.friendly.critters.BirdMob;

public class BirdCritterAI<T extends BirdMob> extends SelectorAINode<T> {
   public BirdCritterRunAINode<T> runNode;

   public BirdCritterAI() {
      this.addChild(new IdleAnimationAINode<T>() {
         public int getIdleAnimationCooldown(GameRandom var1) {
            return var1.getIntBetween(100, 200);
         }

         public void runIdleAnimation(BirdMob var1) {
            var1.peckAbility.runAndSend();
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void runIdleAnimation(Mob var1) {
            this.runIdleAnimation((BirdMob)var1);
         }
      });
      this.addChild(this.runNode = new BirdCritterRunAINode());
   }
}
