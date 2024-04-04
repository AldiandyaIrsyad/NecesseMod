package necesse.entity.mobs.ai.behaviourTree.trees;

import java.util.function.Supplier;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.EscapeAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.WandererAINode;
import necesse.level.maps.levelBuffManager.LevelModifiers;

public class CollisionPlayerChaserWandererAI<T extends Mob> extends SelectorAINode<T> {
   public final EscapeAINode<T> escapeAINode;
   public final CollisionPlayerChaserAI<T> collisionPlayerChaserAI;
   public final WandererAINode<T> wandererAINode;

   public CollisionPlayerChaserWandererAI(final Supplier<Boolean> var1, int var2, GameDamage var3, int var4, int var5) {
      this.addChild(this.escapeAINode = new EscapeAINode<T>() {
         public boolean shouldEscape(T var1x, Blackboard<T> var2) {
            if (var1x.isHostile && !var1x.isSummoned && (Boolean)var1x.getLevel().buffManager.getModifier(LevelModifiers.ENEMIES_RETREATING)) {
               return true;
            } else {
               return var1 != null && (Boolean)var1.get();
            }
         }
      });
      this.addChild(this.collisionPlayerChaserAI = new CollisionPlayerChaserAI(var2, var3, var4));
      this.addChild(this.wandererAINode = new WandererAINode(var5));
   }
}
