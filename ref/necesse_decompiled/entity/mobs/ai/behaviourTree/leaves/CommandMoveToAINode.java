package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.function.BiPredicate;
import java.util.stream.Stream;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.entity.mobs.ai.path.PathDir;
import necesse.entity.mobs.ai.path.TilePathfinding;
import necesse.entity.mobs.mobMovement.MobMovementLevelPos;
import necesse.level.maps.regionSystem.SemiRegion;

public abstract class CommandMoveToAINode<T extends Mob> extends MoveTaskAINode<T> {
   public long nextPathFindTime;
   public long nextCheckArrivedTime;
   public boolean isDirectMovingTo;

   public CommandMoveToAINode() {
   }

   protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
      var3.onEvent("newCommandSet", (var1x) -> {
         this.nextPathFindTime = 0L;
         this.nextCheckArrivedTime = 0L;
      });
   }

   public void init(T var1, Blackboard<T> var2) {
   }

   public AINodeResult tickNode(T var1, Blackboard<T> var2) {
      if (this.isDirectMovingTo && var2.mover.isCurrentlyMovingFor(this) && var1.hasArrivedAtTarget()) {
         this.nextCheckArrivedTime = var1.getWorldEntity().getLocalTime() + 1000L;
         this.onArrived(var1);
         var2.mover.stopMoving(var1);
         this.isDirectMovingTo = false;
         this.updateActivityDescription(var1, new LocalMessage("activities", "watchingfortargets"));
         this.tickStandingStill(var1);
         return AINodeResult.SUCCESS;
      } else {
         Point var3 = this.getLevelPosition(var1);
         if (var3 == null) {
            this.isDirectMovingTo = false;
            this.updateActivityDescription(var1, (GameMessage)null);
            return AINodeResult.FAILURE;
         } else if (this.nextCheckArrivedTime > var1.getWorldEntity().getLocalTime()) {
            this.updateActivityDescription(var1, new LocalMessage("activities", "watchingfortargets"));
            this.tickStandingStill(var1);
            return AINodeResult.SUCCESS;
         } else {
            int var4 = var3.x / 32;
            int var5 = var3.y / 32;
            if (isAtPosition(var1, var3.x, var3.y, new HashSet())) {
               this.nextCheckArrivedTime = var1.getWorldEntity().getLocalTime() + 1000L;
               this.onArrived(var1);
               var2.mover.stopMoving(var1);
               this.isDirectMovingTo = false;
               this.updateActivityDescription(var1, new LocalMessage("activities", "watchingfortargets"));
               this.tickStandingStill(var1);
               return AINodeResult.SUCCESS;
            } else if (var1.getTileX() == var4 && var1.getTileY() == var5) {
               if (!this.isDirectMovingTo) {
                  var2.mover.setCustomMovement(this, new MobMovementLevelPos((float)var3.x, (float)var3.y));
                  this.isDirectMovingTo = true;
               }

               this.updateActivityDescription(var1, new LocalMessage("activities", "movingtoposition"));
               this.tickMoving(var1);
               return AINodeResult.SUCCESS;
            } else {
               this.isDirectMovingTo = false;
               if (this.nextPathFindTime <= var1.getWorldEntity().getLocalTime()) {
                  if (var1.estimateCanMoveTo(var4, var5, false)) {
                     return this.moveToTileTask(var4, var5, (BiPredicate)null, (var2x) -> {
                        if (var2x.moveIfWithin(-1, -1, () -> {
                           this.nextPathFindTime = 0L;
                        })) {
                           int var3 = var2x.getNextPathTimeBasedOnPathTime(var1.getSpeed(), 1.5F, 2000, 0.1F);
                           this.nextPathFindTime = var1.getWorldEntity().getLocalTime() + (long)var3;
                        } else {
                           this.nextPathFindTime = var1.getWorldEntity().getLocalTime() + (long)GameRandom.globalRandom.getIntBetween(10000, 16000);
                        }

                        this.updateActivityDescription(var1, new LocalMessage("activities", "movingtoposition"));
                        this.tickMoving(var1);
                        return AINodeResult.SUCCESS;
                     });
                  } else {
                     this.nextCheckArrivedTime = var1.getWorldEntity().getLocalTime() + 4000L;
                     this.onCannotMoveTo(var1, var3.x, var3.y);
                     this.isDirectMovingTo = false;
                     this.updateActivityDescription(var1, new LocalMessage("activities", "watchingfortargets"));
                     return AINodeResult.SUCCESS;
                  }
               } else {
                  if (!var2.mover.isMoving() || !var2.mover.isCurrentlyMovingFor(this)) {
                     this.nextPathFindTime = 0L;
                  }

                  this.updateActivityDescription(var1, new LocalMessage("activities", "movingtoposition"));
                  this.tickMoving(var1);
                  return AINodeResult.SUCCESS;
               }
            }
         }
      }
   }

   public static boolean isAtPosition(Mob var0, int var1, int var2, HashSet<Integer> var3) {
      Rectangle var4 = var0.getCollision();
      if (var4.contains(var1, var2)) {
         return true;
      } else {
         var3.add(var0.getUniqueID());
         Rectangle var5 = new Rectangle(var4.x - 4, var4.y - 4, var4.width + 8, var4.height + 8);
         int var6 = (int)GameMath.max(GameMath.diagonalMoveDistance(0, 0, var5.width, var5.height), 100.0);
         return ((Stream)var0.getLevel().entityManager.mobs.streamInRegionsInRange(var0.x, var0.y, var6).filter((var3x) -> {
            return var3x != var0 && !var3.contains(var3x.getUniqueID()) && var5.intersects(var3x.getCollision());
         }).sequential()).anyMatch((var3x) -> {
            var3.add(var3x.getUniqueID());
            return isAtPosition(var3x, var1, var2, var3);
         });
      }
   }

   public static Point findClosestMoveToTile(Mob var0, int var1, int var2) {
      int var3 = var1 / 32;
      int var4 = var2 / 32;
      if (var0.estimateCanMoveTo(var3, var4, false)) {
         return new Point(var3, var4);
      } else {
         SemiRegion var5 = var0.getLevel().regionManager.getSemiRegion(var3, var4);
         return var5 == null ? null : findClosestMoveToTile(var0, var1, var2, new HashSet(Collections.singleton(var5)), new HashSet());
      }
   }

   private static Point findClosestMoveToTile(Mob var0, int var1, int var2, HashSet<SemiRegion> var3, HashSet<SemiRegion> var4) {
      Iterator var5 = (new HashSet(var3)).iterator();

      while(var5.hasNext()) {
         SemiRegion var6 = (SemiRegion)var5.next();
         var6.addAllConnected(var3, (var0x) -> {
            return var0x.getType() != SemiRegion.RegionType.OPEN;
         });
      }

      Point var21 = null;
      double var22 = 0.0;
      Iterator var8 = var3.iterator();

      while(true) {
         SemiRegion var9;
         do {
            if (!var8.hasNext()) {
               if (var21 == null) {
                  boolean var23 = false;
                  Iterator var24 = (new HashSet(var3)).iterator();

                  while(var24.hasNext()) {
                     SemiRegion var25 = (SemiRegion)var24.next();
                     if (var25.addAllConnected(var3, (var0x) -> {
                        return var0x.getType() == SemiRegion.RegionType.OPEN;
                     })) {
                        var23 = true;
                     }
                  }

                  if (!var23) {
                     return null;
                  }

                  return findClosestMoveToTile(var0, var1, var2, var3, var4);
               }

               return var21;
            }

            var9 = (SemiRegion)var8.next();
         } while(var4.contains(var9));

         Iterator var10 = var9.getLevelTiles().iterator();

         while(var10.hasNext()) {
            Point var11 = (Point)var10.next();
            PathDir[] var12 = TilePathfinding.nonDiagonalPoints;
            int var13 = var12.length;

            for(int var14 = 0; var14 < var13; ++var14) {
               PathDir var15 = var12[var14];
               int var16 = var11.x + var15.x;
               int var17 = var11.y + var15.y;
               SemiRegion var18 = var0.getLevel().regionManager.getSemiRegion(var16, var17);
               if (!var3.contains(var18) && var0.estimateCanMoveTo(var16, var17, false)) {
                  if (var21 == null) {
                     var21 = new Point(var16, var17);
                     var22 = GameMath.diagonalMoveDistance(var16 * 32 + 16, var17 * 32 + 16, var1, var2);
                  } else {
                     double var19 = GameMath.diagonalMoveDistance(var16 * 32 + 16, var17 * 32 + 16, var1, var2);
                     if (var19 < var22) {
                        var21 = new Point(var16, var17);
                        var22 = var19;
                     }
                  }
               }
            }
         }

         var4.add(var9);
      }
   }

   public abstract Point getLevelPosition(T var1);

   public abstract void onArrived(T var1);

   public void tickMoving(T var1) {
   }

   public void tickStandingStill(T var1) {
   }

   public abstract void onCannotMoveTo(T var1, int var2, int var3);

   public abstract void updateActivityDescription(T var1, GameMessage var2);
}
