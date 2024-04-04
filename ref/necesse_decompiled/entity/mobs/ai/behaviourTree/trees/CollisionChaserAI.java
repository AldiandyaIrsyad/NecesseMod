package necesse.entity.mobs.ai.behaviourTree.trees;

import java.awt.Point;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.decorators.SucceederAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.CollisionChaserAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.LooseTargetTimerAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;

public abstract class CollisionChaserAI<T extends Mob> extends SequenceAINode<T> {
   public final LooseTargetTimerAINode<T> looseTargetTimerAINode;
   public final TargetFinderAINode<T> targetFinderAINode;
   public final CollisionChaserAINode<T> collisionChaserAINode;

   public CollisionChaserAI(int var1, GameDamage var2, int var3) {
      this.addChild(new SucceederAINode(this.looseTargetTimerAINode = new LooseTargetTimerAINode()));
      this.addChild(this.targetFinderAINode = new TargetFinderAINode<T>(var1) {
         public GameAreaStream<? extends Mob> streamPossibleTargets(T var1, Point var2, TargetFinderDistance<T> var3) {
            return CollisionChaserAI.this.streamPossibleTargets(var1, var2, var3);
         }
      });
      this.addChild(this.collisionChaserAINode = new CollisionChaserAINode(var2, var3));
   }

   public abstract GameAreaStream<Mob> streamPossibleTargets(T var1, Point var2, TargetFinderDistance<T> var3);
}
