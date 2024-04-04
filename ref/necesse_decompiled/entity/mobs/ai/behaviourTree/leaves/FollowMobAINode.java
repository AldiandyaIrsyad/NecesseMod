package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.Point;
import java.util.ArrayList;
import java.util.function.BiPredicate;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.PriorityMap;
import necesse.engine.util.pathfinding.PathResult;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.entity.mobs.ai.path.TilePathfinding;
import necesse.level.maps.TilePosition;

public abstract class FollowMobAINode<T extends Mob> extends MoveTaskAINode<T> {
   public Mob lastTarget;
   public long changePositionCountdown;
   public long nextFindPathTime;
   public boolean ranLastTick;
   public boolean startedMoving;
   public int maxChangePositionCooldown = 4000;
   public int minChangePositionCooldown = 8000;
   public int tileRadius = 3;

   public FollowMobAINode() {
   }

   protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
      var3.onEvent("newCommandSet", (var1x) -> {
         this.changePositionCountdown = 0L;
      });
   }

   public void init(T var1, Blackboard<T> var2) {
      if (!this.ranLastTick) {
         this.startedMoving = false;
      }

      this.ranLastTick = false;
   }

   public AINodeResult tickNode(T var1, Blackboard<T> var2) {
      Mob var3 = this.getFollowingMob(var1);
      if (this.lastTarget != var3) {
         this.changePositionCountdown = 0L;
      }

      this.lastTarget = var3;
      return this.tickFollowing(var3, var1, var2);
   }

   public AINodeResult tickFollowing(Mob var1, T var2, Blackboard<T> var3) {
      if (var1 == null) {
         return AINodeResult.FAILURE;
      } else {
         this.ranLastTick = true;
         if (this.lastTarget == var1 && this.startedMoving && !var3.mover.isCurrentlyMovingFor(this)) {
            this.startedMoving = false;
            this.onMovedToFollowTarget(var1, var2, var3, true);
         }

         if (!var3.mover.isMoving()) {
            this.changePositionCountdown -= 50L;
         }

         if (this.changePositionCountdown <= 0L || this.nextFindPathTime <= var2.getWorldEntity().getLocalTime() && GameMath.diagonalMoveDistance(var2.getX(), var2.getY(), var1.getX(), var1.getY()) >= (double)((float)this.tileRadius * 1.5F * 32.0F)) {
            this.changePositionCountdown = (long)GameRandom.globalRandom.getIntBetween(this.maxChangePositionCooldown, this.maxChangePositionCooldown);
            Point var4 = this.findNewPosition(var2, var1);
            if (var4 != null) {
               return this.moveToTileTask(var4.x, var4.y, (BiPredicate)null, (var4x) -> {
                  if (var4x.moveIfWithin(-1, -1, (Runnable)null)) {
                     this.startedMoving = true;
                     int var5 = var4x.getNextPathTimeBasedOnPathTime(var2.getSpeed(), 1.5F, 500, 0.1F);
                     this.nextFindPathTime = var2.getWorldEntity().getLocalTime() + (long)var5;
                  } else {
                     this.nextFindPathTime = var2.getWorldEntity().getLocalTime() + (long)GameRandom.globalRandom.getIntBetween(10000, 15000);
                     this.onMovedToFollowTarget(var1, var2, var3, false);
                  }

                  return AINodeResult.SUCCESS;
               });
            }

            this.nextFindPathTime = var2.getWorldEntity().getLocalTime() + (long)GameRandom.globalRandom.getIntBetween(10000, 15000);
            this.onMovedToFollowTarget(var1, var2, var3, false);
         }

         return AINodeResult.SUCCESS;
      }
   }

   public void onMovedToFollowTarget(Mob var1, T var2, Blackboard<T> var3, boolean var4) {
   }

   public Point findNewPosition(T var1, Mob var2) {
      int var3 = var2.getTileX();
      int var4 = var2.getTileY();
      PriorityMap var5 = new PriorityMap();

      int var7;
      for(int var6 = -this.tileRadius; var6 <= this.tileRadius; ++var6) {
         for(var7 = -this.tileRadius; var7 <= this.tileRadius; ++var7) {
            if (var6 != 0 || var7 != 0) {
               Point var8 = new Point(var3 + var6, var4 + var7);
               if (!var1.getLevel().isSolidTile(var8.x, var8.y) && var1.estimateCanMoveTo(var8.x, var8.y, false) && this.canPath(var3, var4, var8.x, var8.y, this.tileRadius + 5)) {
                  var5.add(var1.getTileWanderPriority(new TilePosition(var1.getLevel(), var8)), var8);
               }
            }
         }
      }

      ArrayList var9 = var5.getBestObjects(20);
      if (var9.isEmpty()) {
         return null;
      } else {
         var7 = GameRandom.globalRandom.nextInt(var9.size());
         return (Point)var9.get(var7);
      }
   }

   public boolean canPath(int var1, int var2, int var3, int var4, int var5) {
      return this.canPath(new Point(var1, var2), new Point(var3, var4), var5);
   }

   public boolean canPath(Point var1, Point var2, int var3) {
      TilePathfinding var4 = new TilePathfinding(this.mob().getLevel().tickManager(), this.mob().getLevel(), this.mob(), (BiPredicate)null, this.getBlackboard().mover.getPathOptions(this));
      PathResult var5 = var4.findPath(var1, var2, var3);
      return var5.foundTarget;
   }

   public abstract Mob getFollowingMob(T var1);
}
