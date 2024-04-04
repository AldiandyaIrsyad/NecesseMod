package necesse.entity.mobs.ai.behaviourTree.trees;

import java.awt.Point;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.ChaserAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;

public abstract class ChaserAI<T extends Mob> extends SequenceAINode<T> {
   public final TargetFinderAINode<T> targetFinderAINode;
   public final ChaserAINode<T> chaserAINode;

   public ChaserAI(int var1, int var2, boolean var3, boolean var4) {
      this.addChild(this.targetFinderAINode = new TargetFinderAINode<T>(var1) {
         public GameAreaStream<? extends Mob> streamPossibleTargets(T var1, Point var2, TargetFinderDistance<T> var3) {
            return ChaserAI.this.streamPossibleTargets(var1, var2, var3);
         }
      });
      this.addChild(this.chaserAINode = new ChaserAINode<T>(var2, var3, var4) {
         public boolean canHitTarget(T var1, float var2, float var3, Mob var4) {
            return ChaserAI.this.canHitTarget(var1, var2, var3, var4);
         }

         public boolean attackTarget(T var1, Mob var2) {
            return ChaserAI.this.attackTarget(var1, var2);
         }
      });
   }

   public abstract GameAreaStream<Mob> streamPossibleTargets(T var1, Point var2, TargetFinderDistance<T> var3);

   public boolean canHitTarget(T var1, float var2, float var3, Mob var4) {
      return this.chaserAINode.canHitTarget(var1, var2, var3, var4);
   }

   public abstract boolean attackTarget(T var1, Mob var2);
}
