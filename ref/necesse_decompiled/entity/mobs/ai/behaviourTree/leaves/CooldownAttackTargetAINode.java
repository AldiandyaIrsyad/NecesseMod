package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.geom.Line2D;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.level.maps.CollisionFilter;

public abstract class CooldownAttackTargetAINode<T extends Mob> extends AINode<T> {
   public String targetKey;
   public CooldownTimer cooldownTimer;
   public int attackCooldown;
   public int attackDistance;
   public long attackTimer;

   public CooldownAttackTargetAINode(String var1, CooldownTimer var2, int var3, int var4) {
      this.targetKey = var1;
      this.cooldownTimer = var2;
      this.attackCooldown = var3;
      this.attackDistance = var4;
   }

   public CooldownAttackTargetAINode(CooldownTimer var1, int var2, int var3) {
      this("currentTarget", var1, var2, var3);
   }

   protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
   }

   public void init(T var1, Blackboard<T> var2) {
   }

   public AINodeResult tick(T var1, Blackboard<T> var2) {
      Mob var3 = (Mob)var2.getObject(Mob.class, this.targetKey);
      if (this.cooldownTimer == CooldownAttackTargetAINode.CooldownTimer.TICK) {
         this.attackTimer += 50L;
      }

      if (var3 != null) {
         if (this.cooldownTimer == CooldownAttackTargetAINode.CooldownTimer.HAS_TARGET) {
            this.attackTimer += 50L;
         }

         if (this.attackDistance < 0 || var1.getDistance(var3) < (float)this.attackDistance) {
            if (this.cooldownTimer == CooldownAttackTargetAINode.CooldownTimer.IN_RANGE) {
               this.attackTimer += 50L;
            }

            if (this.canAttackTarget(var1, var3)) {
               if (this.cooldownTimer == CooldownAttackTargetAINode.CooldownTimer.CAN_ATTACK) {
                  this.attackTimer += 50L;
               }

               if (this.attackTimer >= (long)this.attackCooldown && this.attackTarget(var1, var3)) {
                  this.attackTimer = 0L;
               }
            }
         }
      }

      if (this.attackTimer > (long)this.attackCooldown) {
         this.attackTimer = (long)this.attackCooldown;
      }

      return AINodeResult.SUCCESS;
   }

   public void randomizeAttackTimer() {
      this.attackTimer = (long)GameRandom.globalRandom.nextInt(this.attackCooldown);
   }

   public boolean hasLineOfSight(T var1, Mob var2) {
      return !var1.getLevel().collides((Line2D)(new Line2D.Float(var1.x, var1.y, var2.x, var2.y)), (CollisionFilter)(new CollisionFilter()).projectileCollision());
   }

   public boolean canAttackTarget(T var1, Mob var2) {
      return this.hasLineOfSight(var1, var2);
   }

   public abstract boolean attackTarget(T var1, Mob var2);

   public static enum CooldownTimer {
      TICK,
      HAS_TARGET,
      IN_RANGE,
      CAN_ATTACK;

      private CooldownTimer() {
      }

      // $FF: synthetic method
      private static CooldownTimer[] $values() {
         return new CooldownTimer[]{TICK, HAS_TARGET, IN_RANGE, CAN_ATTACK};
      }
   }
}
