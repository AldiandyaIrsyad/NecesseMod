package necesse.entity.mobs.ai.behaviourTree.trees;

import java.util.function.Supplier;
import necesse.engine.registries.ProjectileRegistry;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.ChaserAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.EscapeAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.WandererAINode;
import necesse.entity.projectile.Projectile;
import necesse.level.maps.levelBuffManager.LevelModifiers;

public abstract class PlayerChaserWandererAI<T extends Mob> extends SelectorAINode<T> {
   public final EscapeAINode<T> escapeAINode;
   public final PlayerChaserAI<T> playerChaserAI;
   public final WandererAINode<T> wandererAINode;

   public PlayerChaserWandererAI(final Supplier<Boolean> var1, int var2, int var3, int var4, boolean var5, boolean var6) {
      this.addChild(this.escapeAINode = new EscapeAINode<T>() {
         public boolean shouldEscape(T var1x, Blackboard<T> var2) {
            if (var1x.isHostile && !var1x.isSummoned && (Boolean)var1x.getLevel().buffManager.getModifier(LevelModifiers.ENEMIES_RETREATING)) {
               return true;
            } else {
               return var1 != null && (Boolean)var1.get();
            }
         }
      });
      this.addChild(this.playerChaserAI = new PlayerChaserAI<T>(var2, var3, var5, var6) {
         public boolean canHitTarget(T var1, float var2, float var3, Mob var4) {
            return PlayerChaserWandererAI.this.canHitTarget(var1, var2, var3, var4);
         }

         public boolean attackTarget(T var1, Mob var2) {
            return PlayerChaserWandererAI.this.attackTarget(var1, var2);
         }
      });
      this.addChild(this.wandererAINode = new WandererAINode(var4));
   }

   public boolean canHitTarget(T var1, float var2, float var3, Mob var4) {
      return ChaserAINode.hasLineOfSightToTarget(var1, var2, var3, var4);
   }

   public abstract boolean attackTarget(T var1, Mob var2);

   public boolean shootSimpleProjectile(T var1, Mob var2, String var3, GameDamage var4, int var5, int var6) {
      return this.shootSimpleProjectile(var1, var2, var3, var4, var5, var6, 10);
   }

   public boolean shootSimpleProjectile(T var1, Mob var2, String var3, GameDamage var4, int var5, int var6, int var7) {
      if (var1.canAttack()) {
         var1.attack(var2.getX(), var2.getY(), false);
         Projectile var8 = ProjectileRegistry.getProjectile(var3, var1.getLevel(), var1.x, var1.y, var2.x, var2.y, (float)var5, var6, var4, var1);
         var8.moveDist((double)var7);
         var1.getLevel().entityManager.projectiles.add(var8);
         return true;
      } else {
         return false;
      }
   }
}
