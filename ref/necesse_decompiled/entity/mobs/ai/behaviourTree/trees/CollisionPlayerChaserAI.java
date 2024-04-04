package necesse.entity.mobs.ai.behaviourTree.trees;

import java.awt.Point;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;

public class CollisionPlayerChaserAI<T extends Mob> extends CollisionChaserAI<T> {
   public CollisionPlayerChaserAI(int var1, GameDamage var2, int var3) {
      super(var1, var2, var3);
   }

   public GameAreaStream<Mob> streamPossibleTargets(T var1, Point var2, TargetFinderDistance<T> var3) {
      return TargetFinderAINode.streamPlayersAndHumans(var1, var2, var3);
   }
}
