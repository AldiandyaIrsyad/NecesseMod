package necesse.entity.mobs.ai.behaviourTree.util;

import necesse.engine.network.NetworkClient;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;

public class TargetValidity<T extends Mob> {
   public TargetValidity() {
   }

   public boolean isValidTarget(AINode<T> var1, T var2, Mob var3, boolean var4) {
      return var3 != null && !var3.removed() && var3.isVisible() && var2.isSamePlace(var3) && var3.canBeTargeted(var2, (NetworkClient)null);
   }
}
