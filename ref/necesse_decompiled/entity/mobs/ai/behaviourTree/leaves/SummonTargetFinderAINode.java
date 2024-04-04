package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.Point;
import java.util.Comparator;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameRandom;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.entity.mobs.ai.behaviourTree.util.SummonTargetFinderDistance;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;

public class SummonTargetFinderAINode<T extends Mob> extends MoveTaskAINode<T> {
   public TargetFinderDistance<T> distance;
   public String currentTargetKey;
   public int loseTargetTimer;
   public int noTargetFoundTimer;
   public int loseTargetMinCooldown;
   public int loseTargetMaxCooldown;

   public SummonTargetFinderAINode(TargetFinderDistance<T> var1, String var2) {
      this.loseTargetMinCooldown = 2000;
      this.loseTargetMaxCooldown = 4000;
      this.distance = var1;
      this.currentTargetKey = var2;
      this.startLoseTargetTimer();
   }

   public SummonTargetFinderAINode(TargetFinderDistance<T> var1) {
      this(var1, "currentTarget");
   }

   public SummonTargetFinderAINode(int var1) {
      this(new SummonTargetFinderDistance(var1));
   }

   protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
   }

   public void init(T var1, Blackboard<T> var2) {
   }

   public AINodeResult tickNode(T var1, Blackboard<T> var2) {
      Mob var3 = (Mob)var2.getObject(Mob.class, this.currentTargetKey);
      Mob var4 = var3;
      boolean var5 = false;
      int var6 = this.distance.searchDistance;
      ServerClient var7 = var1.getFollowingServerClient();
      Mob var8 = var7 == null ? null : var7.summonFocus;
      Point var9 = var7 == null ? var1.getPositionPoint() : var7.playerMob.getPositionPoint();

      AINodeResult var10;
      try {
         if (var4 != null || this.noTargetFoundTimer < 0) {
            if (var4 != null && this.loseTargetMinCooldown >= 0 && this.loseTargetMaxCooldown >= 0) {
               this.loseTargetTimer -= 50;
               if (this.loseTargetTimer <= 0) {
                  var4 = null;
                  var5 = true;
                  this.startLoseTargetTimer();
               }
            }

            if (var4 != null && (!this.isValidTarget(var1, var7, var4) || this.distance.getDistance(var9, var4) > (float)this.distance.getTargetLostDistance(var1, var4) || !this.checkCanMoveTo(var7, var4))) {
               var4 = null;
               var5 = true;
            }

            if (var5) {
               this.distance.searchDistance += this.distance.targetLostAddedDistance;
            }

            if (var8 != null && var4 != var8 && this.isValidTarget(var1, var7, var8) && this.distance.getDistance(var9, var8) < (float)this.distance.getSearchDistance(var1, var8) && this.checkCanMoveTo(var7, var8)) {
               var4 = var8;
            }

            if (var4 == null) {
               GameAreaStream var14 = this.streamPossibleTargets(var1, var9, this.distance);
               if (var14 != null) {
                  var4 = (Mob)var14.filter((var4x) -> {
                     return this.isValidTarget(var1, var7, var4x) && this.distance.getDistance(var9, var4x) < (float)this.distance.getSearchDistance(var1, var4x);
                  }).filter((var2x) -> {
                     return this.checkCanMoveTo(var7, var2x);
                  }).findBestDistance(0, Comparator.comparingInt((var1x) -> {
                     return (int)var1.getDistance(var1x);
                  })).orElse((Object)null);
               }
            }

            if (var3 != var4) {
               var2.put(this.currentTargetKey, var4);
            }

            var10 = var4 != null ? AINodeResult.SUCCESS : AINodeResult.FAILURE;
            return var10;
         }

         this.noTargetFoundTimer -= 50;
         var10 = AINodeResult.FAILURE;
      } finally {
         this.distance.searchDistance = var6;
      }

      return var10;
   }

   public void startLoseTargetTimer() {
      this.loseTargetTimer = GameRandom.globalRandom.getIntBetween(this.loseTargetMinCooldown, this.loseTargetMaxCooldown);
   }

   public GameAreaStream<? extends Mob> streamPossibleTargets(T var1, Point var2, TargetFinderDistance<T> var3) {
      return var3.streamMobsInRange(var2, var1).filter((var0) -> {
         return var0 != null && !var0.removed() && var0.isVisible();
      }).filter((var0) -> {
         return var0.isHostile;
      });
   }

   public boolean isValidTarget(T var1, ServerClient var2, Mob var3) {
      if (var3 != null && !var3.removed() && var3.isVisible() && var1.isSamePlace(var3) && var3.canBeTargeted(var1, var2)) {
         Point var4 = var2 != null && var2.playerMob != null ? var2.playerMob.getPositionPoint() : var1.getPositionPoint();
         if (var3.getLevel().getLightLevel(var3).getLevel() <= 0.0F && !var3.isBoss() && var3.getDistance((float)var4.x, (float)var4.y) >= 256.0F) {
            return false;
         } else {
            return var3.isHostile || var2 == null || var2.summonFocus == var3;
         }
      } else {
         return false;
      }
   }

   public boolean checkCanMoveTo(ServerClient var1, Mob var2) {
      return var1 == null ? true : var1.followerTargetCooldowns.canMoveTo(var2);
   }
}
