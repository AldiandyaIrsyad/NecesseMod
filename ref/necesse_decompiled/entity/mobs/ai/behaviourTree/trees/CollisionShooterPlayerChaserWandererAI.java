package necesse.entity.mobs.ai.behaviourTree.trees;

import java.util.function.Supplier;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.CooldownAttackTargetAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.EscapeAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.WandererAINode;
import necesse.level.maps.levelBuffManager.LevelModifiers;

public abstract class CollisionShooterPlayerChaserWandererAI<T extends Mob> extends SelectorAINode<T> {
   public CollisionShooterPlayerChaserWandererAI(final Supplier<Boolean> var1, int var2, GameDamage var3, int var4, CooldownAttackTargetAINode.CooldownTimer var5, int var6, int var7, int var8) {
      this.addChild(new EscapeAINode<T>() {
         public boolean shouldEscape(T var1x, Blackboard<T> var2) {
            if (var1x.isHostile && !var1x.isSummoned && (Boolean)var1x.getLevel().buffManager.getModifier(LevelModifiers.ENEMIES_RETREATING)) {
               return true;
            } else {
               return var1 != null && (Boolean)var1.get();
            }
         }
      });
      CollisionPlayerChaserAI var9 = new CollisionPlayerChaserAI(var2, var3, var4);
      var9.addChild(new CooldownAttackTargetAINode<T>(var5, var6, var7) {
         public boolean attackTarget(T var1, Mob var2) {
            return CollisionShooterPlayerChaserWandererAI.this.shootAtTarget(var1, var2);
         }
      });
      this.addChild(var9);
      this.addChild(new WandererAINode(var8));
   }

   public abstract boolean shootAtTarget(T var1, Mob var2);
}
