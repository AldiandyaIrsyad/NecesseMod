package necesse.entity.mobs.ai.behaviourTree.trees;

import java.awt.Point;
import java.util.function.Supplier;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.ChargingCirclingChaserAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.EscapeAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.WandererAINode;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.level.maps.levelBuffManager.LevelModifiers;

public class PlayerChargingCirclingChaserAI<T extends Mob> extends SelectorAINode<T> {
   public final EscapeAINode<T> escapeAINode;
   public final TargetFinderAINode<T> targetFinder;
   public final ChargingCirclingChaserAINode<T> chaser;

   public PlayerChargingCirclingChaserAI(final Supplier<Boolean> var1, int var2, int var3, int var4) {
      this.addChild(this.escapeAINode = new EscapeAINode<T>() {
         public boolean shouldEscape(T var1x, Blackboard<T> var2) {
            if (var1x.isHostile && !var1x.isSummoned && (Boolean)var1x.getLevel().buffManager.getModifier(LevelModifiers.ENEMIES_RETREATING)) {
               return true;
            } else {
               return var1 != null && (Boolean)var1.get();
            }
         }
      });
      SequenceAINode var5 = new SequenceAINode();
      var5.addChild(this.targetFinder = new TargetFinderAINode<T>(var2) {
         public GameAreaStream<? extends Mob> streamPossibleTargets(T var1, Point var2, TargetFinderDistance<T> var3) {
            return TargetFinderAINode.streamPlayersAndHumans(var1, var2, var3);
         }
      });
      var5.addChild(this.chaser = new ChargingCirclingChaserAINode(var3, var4));
      this.addChild(var5);
      this.addChild(new WandererAINode(0));
   }
}
