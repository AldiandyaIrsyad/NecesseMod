package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.PriorityMap;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.entity.mobs.ai.behaviourTree.event.AIEvent;
import necesse.entity.mobs.ai.behaviourTree.event.AIWasHitEvent;
import necesse.level.maps.TilePosition;
import necesse.level.maps.levelData.settlementData.ZoneTester;
import necesse.level.maps.regionSystem.SemiRegion;

public class WandererAINode<T extends Mob> extends MoveTaskAINode<T> {
   public int frequency;
   public int randomMod = 0;
   public int searchRadius;
   public Predicate<T> hideInside;
   public boolean runAwayFromAttacker = true;
   public boolean runAwayFromAttackerToBase;
   public Function<T, ZoneTester> getZoneTester;
   private boolean isRunningAway;

   public WandererAINode(int var1) {
      this.frequency = var1;
      this.searchRadius = 10;
   }

   protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
      var3.onEvent("wanderNow", (var1x) -> {
         this.randomMod = this.frequency;
      });
      if (this.hideInside != null) {
         Point var4 = this.getBase(var2);
         if (var4 != null && this.hideInside.test(var2)) {
            ArrayList var5 = null;
            if (this.isBaseRoom(var2, var4)) {
               var5 = var2.getLevel().regionManager.getRoom(var4.x, var4.y);
            } else if (this.isBaseHouse(var2, var4)) {
               var5 = var2.getLevel().regionManager.getHouse(var4.x, var4.y);
            }

            if (var5 != null) {
               int var6 = var2.getTileX();
               int var7 = var2.getTileY();
               if (var5.stream().anyMatch((var2x) -> {
                  return var2x.hasTile(var6, var7);
               })) {
                  this.getBlackboard().put("isHidingInside", true);
               }
            }
         }
      }

   }

   public void init(T var1, Blackboard<T> var2) {
   }

   public AINodeResult tickNode(T var1, Blackboard<T> var2) {
      Point var3;
      if (this.runAwayFromAttacker || this.runAwayFromAttackerToBase) {
         var3 = this.runAwayFromAttackerToBase ? this.getBase(var1) : null;
         if (var3 != null || this.runAwayFromAttacker) {
            Iterator var4 = var2.getLastHits().iterator();

            while(var4.hasNext()) {
               AIWasHitEvent var5 = (AIWasHitEvent)var4.next();
               Mob var6 = var5.event.attacker != null ? var5.event.attacker.getAttackOwner() : null;
               if (var6 != null) {
                  if (var3 != null) {
                     ArrayList var7 = null;
                     if (this.isBaseRoom(var1, var3)) {
                        var7 = var1.getLevel().regionManager.getRoom(var3.x, var3.y);
                     } else if (this.isBaseHouse(var1, var3)) {
                        var7 = var1.getLevel().regionManager.getHouse(var3.x, var3.y);
                     }

                     if (var7 != null) {
                        int var15 = this.getBaseRadius(var1) * 8;
                        Objects.requireNonNull(var1);
                        Point var16 = findWanderingPointInsideRegions(var1, var7, var15, var1::getTileWanderPriority, 20, 5);
                        if (var16 != null) {
                           return this.moveToTileTask(var16.x, var16.y, (BiPredicate)null, (var0) -> {
                              var0.moveIfWithin(-1, -1, (Runnable)null);
                              return AINodeResult.SUCCESS;
                           });
                        }
                        break;
                     }
                  }

                  if (this.runAwayFromAttacker) {
                     Point2D.Float var14 = GameMath.normalize(var1.x - var6.x, var1.y - var6.y);
                     float var8;
                     if (Math.abs(var14.x) > Math.abs(var14.y)) {
                        var8 = 1.0F / Math.abs(var14.x);
                     } else {
                        var8 = 1.0F / Math.abs(var14.y);
                     }

                     var14.x *= var8;
                     var14.y *= var8;
                     int var9 = this.getBaseRadius(var1);
                     int var10 = (int)(var14.x * (float)var9);
                     int var11 = (int)(var14.y * (float)var9);
                     Point var12 = this.findNewPosition(var1, var10, var11);
                     if (var12 != null) {
                        this.isRunningAway = true;
                        return this.moveToTileTask(var12.x, var12.y, (BiPredicate)null, (var0) -> {
                           var0.moveIfWithin(-1, -1, (Runnable)null);
                           return AINodeResult.SUCCESS;
                        });
                     }
                  }
                  break;
               }
            }
         }
      }

      if (var2.mover.hasMobTarget() && !var2.mover.isCurrentlyMovingFor(this)) {
         Mob var13 = var2.mover.getTargetMob();
         var2.mover.directMoveTo(this, var13.getX(), var13.getY());
      }

      if (!var2.mover.isMoving() && this.isRunningAway) {
         var2.submitEvent("ranAway", new AIEvent());
         this.isRunningAway = false;
      }

      if (!var2.mover.isMoving() || !var2.mover.isCurrentlyMovingFor(this)) {
         if (this.frequency - this.randomMod > 0 && GameRandom.globalRandom.nextInt(this.frequency - this.randomMod) >= 50) {
            this.randomMod += 50;
         } else {
            var3 = this.findNewPosition(var1);
            if (var3 == null) {
               var3 = this.findNewPositionInsideBase(var1);
            }

            if (var3 != null) {
               return this.moveToTileTask(var3.x, var3.y, (BiPredicate)null, (var0) -> {
                  var0.moveIfWithin(-1, -1, (Runnable)null);
                  return AINodeResult.SUCCESS;
               });
            }
         }
      }

      return AINodeResult.SUCCESS;
   }

   public Point getBase(T var1) {
      return null;
   }

   public int getBaseRadius(T var1) {
      return this.searchRadius;
   }

   public boolean forceFindAroundBase(T var1) {
      return false;
   }

   public boolean isBaseHouse(T var1, Point var2) {
      return false;
   }

   public boolean isBaseRoom(T var1, Point var2) {
      return false;
   }

   public Point findNewPosition(T var1) {
      return this.findNewPosition(var1, 0, 0);
   }

   public Point findNewPosition(T var1, int var2, int var3) {
      Objects.requireNonNull(var1);
      return this.findNewPosition(var1, var2, var3, var1::getTileWanderPriority);
   }

   public Point findNewPosition(T var1, int var2, int var3, Function<TilePosition, Integer> var4) {
      this.randomMod = 0;
      this.getBlackboard().put("isHidingInside", false);
      Point var5 = this.getBase(var1);
      if (var5 != null && this.hideInside != null && this.hideInside.test(var1)) {
         Point var10 = this.findNewPositionInsideBase(var1, var4);
         if (var10 != null) {
            this.getBlackboard().put("isHidingInside", true);
         }

         return var10;
      } else {
         int var6 = var1.getTileX();
         int var7 = var1.getTileY();
         if (var5 != null) {
            int var8 = this.getBaseRadius(var1);
            Rectangle var9 = new Rectangle(var5.x - var8, var5.y - var8, var8 * 2, var8 * 2);
            if (!var9.contains(var6, var7)) {
               var2 = (int)Math.signum((float)(var5.x - var6)) * this.getBaseRadius(var1) / 2;
               var3 = (int)Math.signum((float)(var5.y - var7)) * this.getBaseRadius(var1) / 2;
            }
         }

         ZoneTester var11 = null;
         if (this.getZoneTester != null) {
            var11 = (ZoneTester)this.getZoneTester.apply(var1);
         }

         return var5 != null && this.forceFindAroundBase(var1) ? findWanderingPointAround(var1, var5.x, var5.y, this.getBaseRadius(var1), var11, var4, 20, 5) : findWanderingPoint(var1, var2, var3, this.getBaseRadius(var1), var11, var4, 20, 5);
      }
   }

   public Point findNewPositionInsideBase(T var1) {
      Objects.requireNonNull(var1);
      return this.findNewPositionInsideBase(var1, var1::getTileWanderPriority);
   }

   public Point findNewPositionInsideBase(T var1, Function<TilePosition, Integer> var2) {
      Point var3 = this.getBase(var1);
      if (var3 == null) {
         return null;
      } else {
         ArrayList var4 = null;
         if (this.isBaseRoom(var1, var3)) {
            var4 = var1.getLevel().regionManager.getRoom(var3.x, var3.y);
         } else if (this.isBaseHouse(var1, var3)) {
            var4 = var1.getLevel().regionManager.getHouse(var3.x, var3.y);
         }

         if (var4 != null) {
            int var5 = this.getBaseRadius(var1) * 8;
            return findWanderingPointInsideRegions(var1, var4, var5, var2, 20, 5);
         } else {
            return null;
         }
      }
   }

   public static Point getWanderingPoint(Mob var0, PriorityMap<Point> var1, int var2, int var3) {
      if (var3 > var2) {
         throw new IllegalArgumentException("Attempts cannot be larger than minimum number of items in random list");
      } else {
         for(ArrayList var4 = var1.getBestObjects(var2); !var4.isEmpty() && var3 > 0; --var3) {
            int var5 = GameRandom.globalRandom.nextInt(var4.size());
            Point var6 = (Point)var4.get(var5);
            if (var0.estimateCanMoveTo(var6.x, var6.y, false)) {
               return var6;
            }

            var4.remove(var5);
         }

         return null;
      }
   }

   public static Point findWanderingPointInsideRegions(Mob var0, List<SemiRegion> var1, int var2, Function<TilePosition, Integer> var3, int var4, int var5) {
      Point var6 = var0.getPathMoveOffset();
      PriorityMap var7 = new PriorityMap();
      int var8 = 0;
      Iterator var9 = var1.iterator();

      while(var9.hasNext()) {
         SemiRegion var10 = (SemiRegion)var9.next();
         if (!var10.getType().isSolid) {
            Iterator var11 = var10.getLevelTiles().iterator();

            while(var11.hasNext()) {
               Point var12 = (Point)var11.next();
               if (!var0.getLevel().isSolidTile(var12.x, var12.y) && !var0.collidesWith(var0.getLevel(), var12.x * 32 + var6.x, var12.y * 32 + var6.y)) {
                  var7.add(var3 == null ? 0 : (Integer)var3.apply(new TilePosition(var0.getLevel(), var12)), var12);
                  ++var8;
                  if (var8 > var2) {
                     break;
                  }
               }
            }

            if (var8 > var2) {
               break;
            }
         }
      }

      return getWanderingPoint(var0, var7, var4, var5);
   }

   public static Point findWanderingPoint(Mob var0, int var1, int var2, int var3, ZoneTester var4, Function<TilePosition, Integer> var5, int var6, int var7) {
      return findWanderingPointAround(var0, var0.getTileX() + var1, var0.getTileY() + var2, var3, var4, var5, var6, var7);
   }

   public static Point findWanderingPoint(Mob var0, int var1, int var2, int var3, ZoneTester var4, int var5, int var6) {
      int var10001 = var0.getTileX() + var1;
      int var10002 = var0.getTileY() + var2;
      Objects.requireNonNull(var0);
      return findWanderingPointAround(var0, var10001, var10002, var3, var4, var0::getTileWanderPriority, var5, var6);
   }

   public static Point findWanderingPointAround(Mob var0, int var1, int var2, int var3, ZoneTester var4, Function<TilePosition, Integer> var5, int var6, int var7) {
      Point var8 = var0.getPathMoveOffset();
      PriorityMap var9 = new PriorityMap();

      for(int var10 = -var3; var10 <= var3; ++var10) {
         for(int var11 = -var3; var11 <= var3; ++var11) {
            Point var12 = new Point(var1 + var10, var2 + var11);
            if ((var4 == null || var4.containsTile(var12.x, var12.y)) && !var0.getLevel().isSolidTile(var12.x, var12.y) && !var0.collidesWith(var0.getLevel(), var12.x * 32 + var8.x, var12.y * 32 + var8.y)) {
               var9.add(var5 == null ? 0 : (Integer)var5.apply(new TilePosition(var0.getLevel(), var12)), var12);
            }
         }
      }

      return getWanderingPoint(var0, var9, var6, var7);
   }

   public static Point findWanderingPointAround(Mob var0, int var1, int var2, int var3, ZoneTester var4, int var5, int var6) {
      Objects.requireNonNull(var0);
      return findWanderingPointAround(var0, var1, var2, var3, var4, var0::getTileWanderPriority, var5, var6);
   }
}
