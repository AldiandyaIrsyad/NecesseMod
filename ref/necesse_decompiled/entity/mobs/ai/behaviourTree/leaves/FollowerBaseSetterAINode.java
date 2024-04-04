package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.Point;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;

public class FollowerBaseSetterAINode<T extends Mob> extends AINode<T> {
   public String baseKey = "mobBase";

   public FollowerBaseSetterAINode() {
   }

   protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
   }

   public void init(T var1, Blackboard<T> var2) {
   }

   public AINodeResult tick(T var1, Blackboard<T> var2) {
      var2.put(this.baseKey, (Object)null);
      if (var1.isFollowing()) {
         ServerClient var3 = var1.getFollowingServerClient();
         if (var3 != null) {
            var2.put(this.baseKey, new Point(var3.playerMob.getX(), var3.playerMob.getY()));
         }
      }

      return AINodeResult.SUCCESS;
   }
}
