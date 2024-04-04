package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.Shape;
import java.util.function.BiPredicate;
import necesse.engine.util.GameRandom;
import necesse.engine.util.MovedRectangle;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobHitCooldowns;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.entity.mobs.ai.behaviourTree.util.MoveToTileAITask;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.level.maps.CollisionFilter;

public class CollisionChaserAINode<T extends Mob> extends MoveTaskAINode<T> {
   public BiPredicate<Mob, MoveToTileAITask.AIPathResult> moveIfFailedPath;
   public String targetKey;
   public GameDamage damage;
   public int knockback;
   public long nextPathFindTime;
   public boolean pathIfStopped;
   public int directSearchDistance;
   public int stoppingDistance;
   public MobHitCooldowns hitCooldowns;
   public String chaserTargetKey;
   protected boolean movingToTarget;

   public CollisionChaserAINode(String var1, GameDamage var2, int var3) {
      this.hitCooldowns = new MobHitCooldowns();
      this.chaserTargetKey = "chaserTarget";
      this.movingToTarget = false;
      this.targetKey = var1;
      this.damage = var2;
      this.knockback = var3;
   }

   public CollisionChaserAINode(GameDamage var1, int var2) {
      this("currentTarget", var1, var2);
   }

   protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
      if (this.directSearchDistance == 0) {
         int var4 = (int)var2.getPathMoveOffset().distance(0.0, 0.0);
         this.directSearchDistance = var4 * 8 + 32;
      }

      var3.onEvent("resetPathTime", (var1x) -> {
         this.nextPathFindTime = 0L;
      });
   }

   public void init(T var1, Blackboard<T> var2) {
      var2.put(this.chaserTargetKey, (Object)null);
   }

   public AINodeResult tick(T var1, Blackboard<T> var2) {
      Mob var3 = (Mob)var2.getObject(Mob.class, this.targetKey);
      if (var3 != null && this.damage != null && var3.getCollision().intersects(var1.getCollision()) && this.hitCooldowns.canHit(var3) && var3.canBeHit(var1)) {
         this.hitCooldowns.startCooldown(var3);
         Object var4 = var1;
         PlayerMob var5 = var1.getFollowingServerPlayer();
         if (var5 != null) {
            var4 = var5;
         }

         var3.isServerHit(this.damage, var3.x - ((Mob)var4).x, var3.y - ((Mob)var4).y, (float)this.knockback, var1);
      }

      return super.tick(var1, var2);
   }

   public AINodeResult tickNode(T var1, Blackboard<T> var2) {
      Mob var3 = (Mob)var2.getObject(Mob.class, this.targetKey);
      if (var3 == null) {
         this.movingToTarget = false;
      } else {
         float var4 = var1.getDistance(var3);
         if ((var4 < (float)this.directSearchDistance || var4 < (float)this.stoppingDistance) && !var1.getLevel().collides((Shape)(new MovedRectangle(var1, var3.getX(), var3.getY())), (CollisionFilter)var1.getLevelCollisionFilter())) {
            if (var4 < (float)this.stoppingDistance) {
               var2.mover.stopMoving(var1);
            } else {
               var2.mover.setMobTarget(this, var3, true);
            }

            this.nextPathFindTime = 0L;
            this.movingToTarget = true;
         } else if (this.nextPathFindTime <= var1.getWorldEntity().getLocalTime() || this.pathIfStopped && (var1.hasArrivedAtTarget() || !var2.mover.isMoving())) {
            this.pathIfStopped = false;
            return this.moveToTileTask(var3.getX() / 32, var3.getY() / 32, (BiPredicate)null, (var3x) -> {
               if (var3x.moveIfWithin(-1, this.moveIfFailedPath != null && this.moveIfFailedPath.test(var1, var3x) ? -1 : 1, () -> {
                  this.movingToTarget = false;
                  this.nextPathFindTime = 0L;
               })) {
                  int var4;
                  if (var3x.isResultWithin(1)) {
                     var4 = var3x.getNextPathTimeBasedOnPathTime(var1.getSpeed(), 1.5F, 500, 0.1F);
                     this.nextPathFindTime = var1.getWorldEntity().getLocalTime() + (long)var4;
                     this.pathIfStopped = true;
                  } else {
                     var4 = var3x.getNextPathTimeBasedOnPathTime(var1.getSpeed(), 1.5F, 5000, 0.1F);
                     this.nextPathFindTime = var1.getWorldEntity().getLocalTime() + (long)var4;
                  }

                  this.movingToTarget = true;
               } else {
                  this.movingToTarget = false;
                  this.nextPathFindTime = var1.getWorldEntity().getLocalTime() + (long)(1000 * (GameRandom.globalRandom.nextInt(5) + 3));
               }

               return this.lastTick(this.movingToTarget, var3);
            });
         }
      }

      return this.lastTick(this.movingToTarget, var3);
   }

   protected AINodeResult lastTick(boolean var1, Mob var2) {
      if (var1) {
         this.getBlackboard().put(this.chaserTargetKey, var2);
         return AINodeResult.SUCCESS;
      } else {
         return AINodeResult.FAILURE;
      }
   }

   public void addDebugTooltips(ListGameTooltips var1) {
      super.addDebugTooltips(var1);
      var1.add("movingToTarget: " + this.movingToTarget);
      var1.add("nextPathFindTime: " + (this.nextPathFindTime - this.mob().getWorldEntity().getLocalTime()));
      var1.add("pathIfStopped: " + this.pathIfStopped);
   }
}
