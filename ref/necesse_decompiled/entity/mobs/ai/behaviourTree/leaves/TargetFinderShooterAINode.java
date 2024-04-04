package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.geom.Line2D;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.level.maps.CollisionFilter;

public abstract class TargetFinderShooterAINode<T extends Mob> extends AINode<T> {
   public int shootDistance;
   public String focusTargetKey;

   public TargetFinderShooterAINode(int var1, String var2) {
      this.shootDistance = var1;
      this.focusTargetKey = var2;
   }

   public TargetFinderShooterAINode(int var1) {
      this(var1, "focusTarget");
   }

   protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
   }

   public void init(T var1, Blackboard<T> var2) {
   }

   public AINodeResult tick(T var1, Blackboard<T> var2) {
      if (this.canAttack(var1)) {
         Mob var4 = (Mob)var2.getObject(Mob.class, this.focusTargetKey);
         Mob var3;
         if (var4 != null && this.canShootTarget(var1, var4)) {
            var3 = var4;
         } else {
            Stream var10000 = this.streamTargets(var1, this.shootDistance).filter((var2x) -> {
               return this.canShootTarget(var1, var2x);
            });
            Objects.requireNonNull(var1);
            var3 = (Mob)var10000.min(Comparator.comparingDouble(var1::getDistance)).orElse((Object)null);
         }

         if (var3 != null) {
            this.shootTarget(var1, var3);
            return AINodeResult.SUCCESS;
         }
      }

      return AINodeResult.FAILURE;
   }

   public boolean canAttack(T var1) {
      return var1.canAttack();
   }

   public abstract Stream<Mob> streamTargets(T var1, int var2);

   public boolean canShootTarget(T var1, Mob var2) {
      return var1.getDistance(var2) <= (float)this.shootDistance && !var1.getLevel().collides((Line2D)(new Line2D.Float(var1.x, var1.y, var2.x, var2.y)), (CollisionFilter)(new CollisionFilter()).projectileCollision());
   }

   public abstract void shootTarget(T var1, Mob var2);
}
