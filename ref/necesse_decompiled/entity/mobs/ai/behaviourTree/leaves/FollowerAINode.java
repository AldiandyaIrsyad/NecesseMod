package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.Point;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import necesse.engine.network.server.FollowerPosition;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.ComputedValue;
import necesse.engine.util.GameRandom;
import necesse.engine.util.MovedRectangle;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.entity.mobs.mobMovement.MobMovement;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.level.maps.CollisionFilter;

public abstract class FollowerAINode<T extends Mob> extends MoveTaskAINode<T> {
   private FollowerPosition targetPoint = null;
   private int attachedStage;
   private long nextPathFindTime;
   public int teleportDistance;
   public int stoppingDistance;
   public int directSearchDistance;
   public int teleportToAccuracy = 1;

   public FollowerAINode(int var1, int var2) {
      this.teleportDistance = var1;
      this.stoppingDistance = var2;
   }

   protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
      if (this.directSearchDistance == 0) {
         int var4 = (int)var2.getPathMoveOffset().distance(0.0, 0.0);
         this.directSearchDistance = var4 * 8 + 32;
      }

      var3.onWasHit((var1x) -> {
         this.nextPathFindTime = 0L;
      });
      var3.onEvent("resetPathTime", (var1x) -> {
         this.nextPathFindTime = 0L;
      });
   }

   public void init(T var1, Blackboard<T> var2) {
   }

   public AINodeResult tickNode(T var1, Blackboard<T> var2) {
      Mob var3 = this.getFollowingMob(var1);
      if (var3 != null && var3.isSamePlace(var1)) {
         ServerClient var4 = var1.getFollowingServerClient();
         this.targetPoint = var4 == null ? null : var4.getFollowerPos(var1, var3, this.targetPoint);
         FollowerPosition var5 = this.targetPoint != null ? this.targetPoint : new FollowerPosition(0, 0);
         float var6 = var1.getDistance(var3.x, var3.y);
         float var7 = (float)(new Point(var5.x, var5.y)).distance(0.0, 0.0);
         float var8 = var1.getDistance(var3.x + (float)var5.x, var3.y + (float)var5.y);
         if (this.teleportDistance > 0 && var6 > (float)this.teleportDistance && var8 > (float)this.teleportDistance + var7) {
            this.teleportTo(var1, var3, this.teleportToAccuracy);
            this.attachedStage = 0;
         } else {
            MovedRectangle var9 = new MovedRectangle(var1, (int)(var3.x + (float)var5.x), (int)(var3.y + (float)var5.y));
            ComputedValue var10 = new ComputedValue(() -> {
               return !var1.getLevel().collides((Shape)var9, (CollisionFilter)var1.getLevelCollisionFilter());
            });
            if (this.attachedStage == 0) {
               if ((Boolean)var10.get()) {
                  this.attachedStage = 2;
               } else if (this.nextPathFindTime <= var1.getWorldEntity().getLocalTime()) {
                  this.nextPathFindTime = var1.getWorldEntity().getLocalTime() + 1000L;
                  return this.moveToTileTask(var3.getX() / 32, var3.getY() / 32, (BiPredicate)null, (var2x) -> {
                     if (var2x.moveIfWithin(-1, -1, () -> {
                        this.nextPathFindTime = 0L;
                     })) {
                        int var3 = var2x.getNextPathTimeBasedOnPathTime(var1.getSpeed(), 1.5F, 1000, 0.1F);
                        this.nextPathFindTime = var1.getWorldEntity().getLocalTime() + (long)var3;
                     }

                     return AINodeResult.SUCCESS;
                  });
               }
            } else if (this.attachedStage == 1) {
               if (var8 > var7 + (float)Math.max(this.stoppingDistance, this.directSearchDistance)) {
                  this.attachedStage = 0;
               } else if ((var8 < (float)this.stoppingDistance || var8 < (float)this.directSearchDistance) && (Boolean)var10.get()) {
                  this.attachedStage = 2;
               } else {
                  MobMovement var11 = (MobMovement)var5.movementGetter.apply(var3);
                  var2.mover.setCustomMovement(this, var11);
               }
            } else {
               var2.mover.setCustomMovement(this, (MobMovement)var5.movementGetter.apply(var3));
               if (this.targetPoint == null && var8 < (float)this.stoppingDistance) {
                  var2.mover.stopMoving(var1);
               }

               if (!(Boolean)var10.get()) {
                  this.attachedStage = 0;
               }
            }
         }

         return AINodeResult.SUCCESS;
      } else {
         if (var2.mover.isCurrentlyMovingFor(this)) {
            var2.mover.stopMoving(var1);
         }

         return AINodeResult.FAILURE;
      }
   }

   public void teleportTo(T var1, Mob var2, int var3) {
      teleportCloseTo(var1, var2, var3);
      this.getBlackboard().mover.stopMoving(var1);
      var1.sendMovementPacket(true);
      this.nextPathFindTime = 0L;
   }

   public abstract Mob getFollowingMob(T var1);

   public static void teleportCloseTo(Mob var0, Mob var1, int var2) {
      int var3 = var1.getX() / 32;
      int var4 = var1.getY() / 32;
      ArrayList var5 = new ArrayList();

      for(int var6 = var3 - var2; var6 <= var3 + var2; ++var6) {
         for(int var7 = var4 - var2; var7 <= var4 + var2; ++var7) {
            if (var6 != var3 || var7 != var4) {
               int var8 = var6 * 32 + 16;
               int var9 = var7 * 32 + 16;
               if (!var0.collidesWith(var0.getLevel(), var8, var9)) {
                  var5.add(new Point(var8, var9));
               }
            }
         }
      }

      Point var10 = (Point)GameRandom.globalRandom.getOneOf((List)var5);
      if (var10 == null) {
         var10 = new Point(var1.getX(), var1.getY());
      }

      var0.setPos((float)var10.x, (float)var10.y, true);
   }

   public void addDebugTooltips(ListGameTooltips var1) {
      super.addDebugTooltips(var1);
      var1.add("attachedStage: " + this.attachedStage);
      var1.add("nextPathFindTime: " + (this.nextPathFindTime - this.mob().getWorldEntity().getLocalTime()));
   }
}
