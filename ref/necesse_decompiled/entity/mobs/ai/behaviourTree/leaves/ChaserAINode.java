package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.MovedRectangle;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.entity.mobs.ai.behaviourTree.event.AIWasHitEvent;
import necesse.entity.mobs.ai.behaviourTree.util.MoveToTileAITask;
import necesse.entity.mobs.mobMovement.MobMovementRelative;
import necesse.level.maps.CollisionFilter;

public abstract class ChaserAINode<T extends Mob> extends MoveTaskAINode<T> {
   public BiPredicate<Mob, MoveToTileAITask.AIPathResult> moveIfFailedPath;
   public String targetKey;
   public long nextPathFindTime;
   public Mob lastTarget;
   public int attackDistance;
   public int stoppingDistance;
   public int minimumAttackDistance;
   public int directSearchDistance;
   public boolean preferLand;
   public boolean smartPositioning;
   public boolean changePositionOnHit;
   public boolean changePositionConstantly;
   public int maxAttacksPerPosition;
   public String chaserTargetKey;
   protected boolean movingToTarget;
   protected int attacksSincePositionChange;

   public ChaserAINode(String var1, int var2, boolean var3, boolean var4) {
      this.preferLand = true;
      this.maxAttacksPerPosition = -1;
      this.chaserTargetKey = "chaserTarget";
      this.movingToTarget = false;
      this.targetKey = var1;
      this.attackDistance = var2;
      this.stoppingDistance = var2 - 20;
      this.smartPositioning = var3;
      this.changePositionOnHit = var4;
   }

   public ChaserAINode(int var1, boolean var2, boolean var3) {
      this("currentTarget", var1, var2, var3);
   }

   protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
      if (this.directSearchDistance == 0) {
         int var4 = (int)var2.getPathMoveOffset().distance(0.0, 0.0);
         this.directSearchDistance = var4 * 8 + 32;
      }

      var3.onEvent("resetPathTime", (var1x) -> {
         this.nextPathFindTime = 0L;
      });
      var3.onEvent("resetTarget", (var1x) -> {
         this.getBlackboard().put(this.chaserTargetKey, (Object)null);
      });
   }

   public void init(T var1, Blackboard<T> var2) {
      var2.put(this.chaserTargetKey, (Object)null);
   }

   public AINodeResult tickNode(T var1, Blackboard<T> var2) {
      Mob var3 = (Mob)var2.getObject(Mob.class, this.targetKey);
      if (this.lastTarget != var3) {
         this.nextPathFindTime = 0L;
      }

      this.lastTarget = var3;
      if (var3 != null) {
         boolean var4 = false;
         if (this.smartPositioning && this.changePositionOnHit) {
            Iterator var5 = var2.getLastHits().iterator();
            if (var5.hasNext()) {
               AIWasHitEvent var6 = (AIWasHitEvent)var5.next();
               var4 = true;
            }
         }

         float var19 = var1.getDistance(var3);
         boolean var20 = false;
         if (this.isTargetWithinAttackRange(var1, var3, var19) && this.canHitTarget(var1, var1.x, var1.y, var3)) {
            var20 = true;
            if (this.attackTarget(var1, var3)) {
               ++this.attacksSincePositionChange;
               if (this.maxAttacksPerPosition > 0 && this.attacksSincePositionChange >= this.maxAttacksPerPosition) {
                  var4 = true;
               }
            }
         }

         if (this.smartPositioning && this.changePositionConstantly && !var2.mover.isCurrentlyMovingFor(this)) {
            var4 = true;
         }

         if (var4) {
            this.movingToTarget = true;
            this.attacksSincePositionChange = 0;
            this.moveToTarget(var1, var3, var2, (BiConsumer)null, () -> {
               this.movingToTarget = false;
               this.nextPathFindTime = 0L;
            });
         } else {
            boolean var7 = false;
            if (!this.smartPositioning) {
               ChaseDirection var8 = this.getDirectChaseDirection(var1, var3, var19, var20);
               switch (var8) {
                  case INVALID:
                  default:
                     break;
                  case STAY:
                     var7 = true;
                     var2.mover.stopMoving(var1);
                     break;
                  case TOWARDS:
                     var7 = true;
                     var2.mover.setMobTarget(this, var3);
                     break;
                  case AWAY:
                     var7 = true;
                     Point2D.Float var9 = GameMath.normalize(var1.x - var3.x, var1.y - var3.y);
                     float var10 = GameMath.getAngle(var9);
                     byte var11 = 8;
                     int var12 = 360 / var11;
                     var10 = GameMath.fixAngle(var10 + (float)var12 / 2.0F);
                     int var13 = (int)(var10 / (float)var12);
                     Point2D.Float var14 = new Point2D.Float();

                     int var15;
                     for(var15 = 0; var15 < var11 / 2; ++var15) {
                        int var16 = var13 - var15;
                        var14 = GameMath.getAngleDir((float)(var16 * var12));
                        int var17 = (int)(var1.x + var14.x * (float)this.minimumAttackDistance);
                        int var18 = (int)(var1.y + var14.y * (float)this.minimumAttackDistance);
                        if (!var1.getLevel().collides((Shape)(new MovedRectangle(var1, var17, var18)), (CollisionFilter)var1.getLevelCollisionFilter())) {
                           break;
                        }

                        if (var15 != 0) {
                           var16 = var13 + var15;
                           var14 = GameMath.getAngleDir((float)(var16 * var12));
                           var17 = (int)(var1.x + var14.x * (float)this.minimumAttackDistance);
                           var18 = (int)(var1.y + var14.y * (float)this.minimumAttackDistance);
                           if (!var1.getLevel().collides((Shape)(new MovedRectangle(var1, var17, var18)), (CollisionFilter)var1.getLevelCollisionFilter())) {
                              break;
                           }
                        }
                     }

                     var15 = this.minimumAttackDistance + var1.moveAccuracy;
                     var2.mover.setCustomMovement(this, new MobMovementRelative(var3, var14.x * (float)var15, var14.y * (float)var15));
               }

               if (var8 != ChaserAINode.ChaseDirection.INVALID) {
                  this.nextPathFindTime = 0L;
                  this.movingToTarget = true;
                  this.attacksSincePositionChange = 0;
               }
            }

            if (!var7 && !var20 && this.nextPathFindTime <= var1.getWorldEntity().getLocalTime()) {
               this.moveToTarget(var1, var3, var2, (var2x, var3x) -> {
                  if (var2x != ChaserAINode.PathResult.SUCCESS && var2x != ChaserAINode.PathResult.MOVED_NO_SUCCESS) {
                     this.movingToTarget = false;
                     this.nextPathFindTime = var1.getWorldEntity().getLocalTime() + (long)(1000 * (GameRandom.globalRandom.nextInt(5) + 3));
                  } else {
                     this.movingToTarget = true;
                     this.attacksSincePositionChange = 0;
                     int var4;
                     if (var2x == ChaserAINode.PathResult.MOVED_NO_SUCCESS) {
                        var4 = var3x.getNextPathTimeBasedOnPathTime(var1.getSpeed(), 1.5F, 5000, 0.1F);
                        this.nextPathFindTime = var1.getWorldEntity().getLocalTime() + (long)var4;
                     } else {
                        var4 = var3x.getNextPathTimeBasedOnPathTime(var1.getSpeed(), 1.5F, 500, 0.1F);
                        this.nextPathFindTime = var1.getWorldEntity().getLocalTime() + (long)var4;
                     }
                  }

               }, () -> {
                  this.movingToTarget = false;
                  this.nextPathFindTime = 0L;
               });
            }
         }
      } else {
         this.movingToTarget = false;
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

   public boolean isTargetWithinAttackRange(T var1, Mob var2, float var3) {
      return var3 <= (float)this.attackDistance && var3 >= (float)this.minimumAttackDistance;
   }

   public ChaseDirection getDirectChaseDirection(T var1, Mob var2, float var3, boolean var4) {
      if (var3 >= (float)this.stoppingDistance && var3 >= (float)this.directSearchDistance) {
         return ChaserAINode.ChaseDirection.INVALID;
      } else if (var1.getLevel().collides((Shape)(new MovedRectangle(var1, var2.getX(), var2.getY())), (CollisionFilter)var1.getLevelCollisionFilter())) {
         return ChaserAINode.ChaseDirection.INVALID;
      } else if (var3 < (float)this.minimumAttackDistance) {
         return ChaserAINode.ChaseDirection.AWAY;
      } else {
         return var4 && var3 < (float)this.stoppingDistance ? ChaserAINode.ChaseDirection.STAY : ChaserAINode.ChaseDirection.TOWARDS;
      }
   }

   public boolean canHitTarget(T var1, float var2, float var3, Mob var4) {
      return hasLineOfSightToTarget(var1, var2, var3, var4);
   }

   public static boolean hasLineOfSightToTarget(Mob var0, float var1, float var2, Mob var3) {
      return !var0.getLevel().collides((Line2D)(new Line2D.Float(var1, var2, var3.x, var3.y)), (CollisionFilter)(new CollisionFilter()).projectileCollision());
   }

   public static boolean isTargetHitboxWithinRange(Mob var0, float var1, float var2, Mob var3, float var4) {
      Point2D.Float var5 = GameMath.normalize(var3.x - var1, var3.y - var2);
      return var3.getHitBox().intersectsLine((double)var1, (double)var2, (double)(var1 + var5.x * var4), (double)(var2 + var5.y * var4));
   }

   public abstract boolean attackTarget(T var1, Mob var2);

   public void moveToTarget(T var1, Mob var2, Blackboard<T> var3, BiConsumer<PathResult, MoveToTileAITask.AIPathResult> var4, Runnable var5) {
      if (var2 == null) {
         if (var4 != null) {
            var4.accept(ChaserAINode.PathResult.FAILED, (Object)null);
         }

      } else if (!this.smartPositioning) {
         this.moveToTileTask(var2.getX() / 32, var2.getY() / 32, (BiPredicate)null, (var4x) -> {
            boolean var5x = var4x.moveIfWithin(-1, this.moveIfFailedPath != null && this.moveIfFailedPath.test(var2, var4x) ? -1 : 1, var5);
            if (var4 != null) {
               if (var5x) {
                  if (var4x.isResultWithin(1)) {
                     var4.accept(ChaserAINode.PathResult.SUCCESS, var4x);
                  } else {
                     var4.accept(ChaserAINode.PathResult.MOVED_NO_SUCCESS, var4x);
                  }
               } else {
                  var4.accept(ChaserAINode.PathResult.FAILED, var4x);
               }
            }

            return this.lastTick(this.movingToTarget, var2);
         });
      } else {
         ArrayList var6 = new ArrayList();
         ArrayList var7 = new ArrayList();
         int var8 = (this.attackDistance - 1) / 32;

         for(int var9 = -var8; var9 <= var8; ++var9) {
            int var10 = var2.getX() / 32 + var9;

            for(int var11 = -var8; var11 <= var8; ++var11) {
               int var12 = var2.getY() / 32 + var11;
               if (!var1.getLevel().isSolidTile(var10, var12)) {
                  float var13 = var2.getDistance((float)(var10 * 32 + 16), (float)(var12 * 32 + 16));
                  if (!(var13 < 64.0F) && !(var13 > (float)this.attackDistance) && this.canHitTarget(var1, (float)(var10 * 32 + 16), (float)(var12 * 32 + 16), var2)) {
                     if (this.preferLand && var1.getLevel().isLiquidTile(var10, var12)) {
                        var7.add(new Point(var10, var12));
                     } else {
                        var6.add(new Point(var10, var12));
                     }
                  }
               }
            }
         }

         Point var14 = null;
         if (var6.size() != 0) {
            var14 = (Point)var6.get(GameRandom.globalRandom.nextInt(var6.size()));
         } else if (var7.size() != 0) {
            var14 = (Point)var7.get(GameRandom.globalRandom.nextInt(var7.size()));
         }

         if (var14 != null) {
            this.moveToTileTask(var14.x, var14.y, (BiPredicate)null, (var4x) -> {
               boolean var5x = var4x.moveIfWithin(-1, this.moveIfFailedPath != null && this.moveIfFailedPath.test(var2, var4x) ? -1 : 1, var5);
               if (var4 != null) {
                  if (var5x) {
                     if (var4x.isResultWithin(1)) {
                        var4.accept(ChaserAINode.PathResult.SUCCESS, var4x);
                     } else {
                        var4.accept(ChaserAINode.PathResult.MOVED_NO_SUCCESS, var4x);
                     }
                  } else {
                     var4.accept(ChaserAINode.PathResult.FAILED, var4x);
                  }
               }

               return this.lastTick(this.movingToTarget, var2);
            });
         } else {
            if (var4 != null) {
               var4.accept(ChaserAINode.PathResult.FAILED, (Object)null);
            }

         }
      }
   }

   public static enum ChaseDirection {
      TOWARDS,
      STAY,
      AWAY,
      INVALID;

      private ChaseDirection() {
      }

      // $FF: synthetic method
      private static ChaseDirection[] $values() {
         return new ChaseDirection[]{TOWARDS, STAY, AWAY, INVALID};
      }
   }

   protected static enum PathResult {
      SUCCESS,
      MOVED_NO_SUCCESS,
      FAILED;

      private PathResult() {
      }

      // $FF: synthetic method
      private static PathResult[] $values() {
         return new PathResult[]{SUCCESS, MOVED_NO_SUCCESS, FAILED};
      }
   }
}
