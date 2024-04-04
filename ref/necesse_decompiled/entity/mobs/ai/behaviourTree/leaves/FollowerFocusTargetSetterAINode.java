package necesse.entity.mobs.ai.behaviourTree.leaves;

import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;

public class FollowerFocusTargetSetterAINode<T extends Mob> extends AINode<T> {
   public String focusTargetKey;

   public FollowerFocusTargetSetterAINode(String var1) {
      this.focusTargetKey = var1;
   }

   public FollowerFocusTargetSetterAINode() {
      this("focusTarget");
   }

   protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
   }

   public void init(T var1, Blackboard<T> var2) {
   }

   public AINodeResult tick(T var1, Blackboard<T> var2) {
      var2.put(this.focusTargetKey, (Object)null);
      if (var1.isFollowing()) {
         ServerClient var3 = var1.getFollowingServerClient();
         if (var3 != null) {
            var2.put(this.focusTargetKey, var3.summonFocus);
         }
      }

      return AINodeResult.SUCCESS;
   }
}
