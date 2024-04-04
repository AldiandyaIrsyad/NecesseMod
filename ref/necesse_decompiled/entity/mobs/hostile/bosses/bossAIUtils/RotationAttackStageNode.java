package necesse.entity.mobs.hostile.bosses.bossAIUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.CompositeAINode;

public class RotationAttackStageNode<T extends Mob> extends CompositeAINode<T> implements AttackStageInterface<T> {
   public ArrayList<AINode<T>> nodes;
   public int currentNode = -1;

   public RotationAttackStageNode(AINode<T>... var1) {
      this.nodes = new ArrayList(Arrays.asList(var1));
   }

   protected AINodeResult tickChildren(AINode<T> var1, AINodeResult var2, Iterable<AINode<T>> var3, T var4, Blackboard<T> var5) {
      AINode var6 = (AINode)this.nodes.get(this.currentNode);
      AINodeResult var7 = var6.tick(var4, var5);
      if (var7 == AINodeResult.RUNNING) {
         this.runningNode = var6;
      }

      return var7;
   }

   public void init(T var1, Blackboard<T> var2) {
      Iterator var3 = this.nodes.iterator();

      while(var3.hasNext()) {
         AINode var4 = (AINode)var3.next();
         var4.init(var1, var2);
      }

   }

   public void onStarted(T var1, Blackboard<T> var2) {
      this.currentNode = (this.currentNode + 1) % this.nodes.size();
      AINode var3 = (AINode)this.nodes.get(this.currentNode);
      if (var3 instanceof AttackStageInterface) {
         ((AttackStageInterface)var3).onStarted(var1, var2);
      }

   }

   public void onEnded(T var1, Blackboard<T> var2) {
      AINode var3 = (AINode)this.nodes.get(this.currentNode);
      if (var3 instanceof AttackStageInterface) {
         ((AttackStageInterface)var3).onStarted(var1, var2);
      }

   }
}
