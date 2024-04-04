package necesse.entity.mobs.ai.behaviourTree.trees;

import java.util.stream.Stream;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderShooterAINode;

public abstract class StationaryPlayerShooterAI<T extends Mob> extends TargetFinderShooterAINode<T> {
   public StationaryPlayerShooterAI(int var1, String var2) {
      super(var1, var2);
   }

   public StationaryPlayerShooterAI(int var1) {
      super(var1);
   }

   public Stream<Mob> streamTargets(T var1, int var2) {
      return GameUtils.streamServerClients(var1.getLevel()).map((var0) -> {
         return var0.playerMob;
      });
   }
}
