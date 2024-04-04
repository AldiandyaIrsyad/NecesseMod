package necesse.entity.mobs.ai.behaviourTree;

import necesse.entity.mobs.Mob;

public abstract class CompositeAINode<T extends Mob> extends CompositeTypedAINode<T, AINode<T>> {
   public CompositeAINode() {
   }
}
