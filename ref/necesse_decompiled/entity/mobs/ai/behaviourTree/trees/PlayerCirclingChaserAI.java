package necesse.entity.mobs.ai.behaviourTree.trees;

import java.awt.Point;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.CirclingChaserAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;

public class PlayerCirclingChaserAI<T extends Mob> extends SequenceAINode<T> {
   public PlayerCirclingChaserAI(int var1, int var2, int var3) {
      this.addChild(new TargetFinderAINode<T>(var1) {
         public GameAreaStream<? extends Mob> streamPossibleTargets(T var1, Point var2, TargetFinderDistance<T> var3) {
            return TargetFinderAINode.streamPlayersAndHumans(var1, var2, var3);
         }
      });
      this.addChild(new CirclingChaserAINode(var2, var3));
   }
}
