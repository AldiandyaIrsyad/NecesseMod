package necesse.entity.mobs.ai.behaviourTree.trees;

import java.awt.Point;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;

public abstract class PlayerChaserAI<T extends Mob> extends ChaserAI<T> {
   public PlayerChaserAI(int var1, int var2, boolean var3, boolean var4) {
      super(var1, var2, var3, var4);
   }

   public GameAreaStream<Mob> streamPossibleTargets(T var1, Point var2, TargetFinderDistance<T> var3) {
      return TargetFinderAINode.streamPlayersAndHumans(var1, var2, var3);
   }
}
