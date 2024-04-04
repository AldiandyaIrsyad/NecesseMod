package necesse.entity.mobs.ai.behaviourTree.trees;

import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.CritterRunAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.WandererAINode;
import necesse.entity.mobs.friendly.critters.CritterMob;

public class CritterAI<T extends CritterMob> extends SelectorAINode<T> {
   public final WandererAINode<T> wanderer;

   public CritterAI(AINode<T> var1, WandererAINode<T> var2) {
      if (var1 != null) {
         this.addChild(var1);
      }

      this.addChild(this.wanderer = var2);
   }

   public CritterAI(AINode<T> var1) {
      this(var1, new WandererAINode(10000));
   }

   public CritterAI() {
      this(new CritterRunAINode());
   }
}
