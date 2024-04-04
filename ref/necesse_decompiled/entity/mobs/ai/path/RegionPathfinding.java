package necesse.entity.mobs.ai.path;

import java.awt.Point;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;
import necesse.engine.tickManager.Performance;
import necesse.engine.util.GameMath;
import necesse.engine.util.HashProxyLinkedList;
import necesse.engine.util.pathfinding.PathResult;
import necesse.engine.util.pathfinding.Pathfinding;
import necesse.entity.mobs.PathDoorOption;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.SemiRegion;

public class RegionPathfinding extends Pathfinding<SemiRegion> {
   public PathDoorOption doorOption;
   public BiPredicate<SemiRegion, SemiRegion> isAtTarget;

   public static boolean canMoveToTile(Level var0, int var1, int var2, int var3, int var4, PathDoorOption var5, boolean var6) {
      return canMoveToTile(var0, var1, var2, var3, var4, var5, var6, 200);
   }

   public static boolean canMoveToTile(Level var0, int var1, int var2, int var3, int var4, PathDoorOption var5, boolean var6, int var7) {
      int var8 = var0.regionManager.getRegionID(var1, var2);
      int var9 = var0.regionManager.getRegionID(var3, var4);
      if (var8 == var9) {
         return true;
      } else if (!var6) {
         BiPredicate var13 = (var0x, var1x) -> {
            return var0x.getRegionID() == var1x.getRegionID();
         };
         return findMoveToTile(var0, var1, var2, var3, var4, var5, var13, var7).foundTarget;
      } else {
         HashSet var10 = new HashSet();
         var10.add(var0.regionManager.getSemiRegion(var3, var4));
         Iterator var11 = var0.getObject(var3, var4).getMultiTile(var0, var3, var4).getAdjacentTiles(var3, var4, true).iterator();

         while(var11.hasNext()) {
            Point var12 = (Point)var11.next();
            if (var0.regionManager.getRegionID(var12.x, var12.y) == var8) {
               return true;
            }

            var10.add(var0.regionManager.getSemiRegion(var12.x, var12.y));
         }

         BiPredicate var14 = (var1x, var2x) -> {
            return var1x.getRegionID() == var2x.getRegionID() || var10.contains(var1x);
         };
         return findMoveToTile(var0, var1, var2, var3, var4, var5, var14, var7).foundTarget;
      }
   }

   public static PathResult<SemiRegion, RegionPathfinding> findMoveToTile(Level var0, int var1, int var2, int var3, int var4, PathDoorOption var5, BiPredicate<SemiRegion, SemiRegion> var6) {
      return findMoveToTile(var0, var1, var2, var3, var4, var5, var6, 200);
   }

   public static PathResult<SemiRegion, RegionPathfinding> findMoveToTile(Level var0, int var1, int var2, int var3, int var4, PathDoorOption var5, BiPredicate<SemiRegion, SemiRegion> var6, int var7) {
      RegionPathfinding var8 = new RegionPathfinding(var5, var6);
      SemiRegion var9 = var0.regionManager.getSemiRegion(var1, var2);
      SemiRegion var10 = var0.regionManager.getSemiRegion(var3, var4);
      return var8.findPath(var9, var10, var7);
   }

   public RegionPathfinding(PathDoorOption var1, BiPredicate<SemiRegion, SemiRegion> var2) {
      this.doorOption = var1;
      this.isAtTarget = var2;
   }

   public <C extends Pathfinding<SemiRegion>> PathResult<SemiRegion, C> findPath(SemiRegion var1, SemiRegion var2, int var3) {
      return var1 != null && var2 != null ? (PathResult)Performance.record(var1.region.level.tickManager(), "regionPathfinding", (Supplier)(() -> {
         return super.findPath(var1, var2, var3);
      })) : super.findPath(var1, var2, var3);
   }

   protected boolean isAtTarget(SemiRegion var1, SemiRegion var2) {
      if (this.isAtTarget == null) {
         return var1 == var2;
      } else {
         return this.isAtTarget.test(var1, var2);
      }
   }

   protected void handleConnectedNodes(Pathfinding<SemiRegion>.Node var1, HashProxyLinkedList<Pathfinding<SemiRegion>.Node, SemiRegion> var2, HashProxyLinkedList<Pathfinding<SemiRegion>.Node, SemiRegion> var3, HashSet<SemiRegion> var4, Function<SemiRegion, Boolean> var5, BiConsumer<Pathfinding<SemiRegion>.Node, Pathfinding<SemiRegion>.Node> var6, Runnable var7) {
      Iterator var8 = ((SemiRegion)var1.item).adjacentRegions.iterator();

      while(var8.hasNext()) {
         SemiRegion var9 = (SemiRegion)var8.next();
         Pathfinding.Node var10 = (Pathfinding.Node)var2.getObject(var9);
         if (var10 != null) {
            if (var10.reverseDirection != var1.reverseDirection) {
               var6.accept(var10, var1);
               return;
            }
         } else {
            Pathfinding.Node var11 = (Pathfinding.Node)var3.getObject(var9);
            if (var11 != null) {
               if (var11.reverseDirection != var1.reverseDirection) {
                  var6.accept(var11, var1);
                  return;
               }
            } else {
               SemiRegionPathResult var12 = this.doorOption.canPathThrough(var9);
               boolean var13;
               if (var12 == SemiRegionPathResult.VALID) {
                  var13 = true;
               } else if (var12 == SemiRegionPathResult.CHECK_EACH_TILE) {
                  var13 = var9.streamLevelTiles().anyMatch((var2x) -> {
                     Point var3 = var9.getLevelTile(var2x);
                     return this.doorOption.canPathThroughCheckTile(var9, var3.x, var3.y);
                  });
               } else {
                  var13 = false;
               }

               if (var13) {
                  if ((Boolean)var5.apply(var9)) {
                     return;
                  }
               } else {
                  var4.add(var9);
               }
            }
         }
      }

   }

   protected double getNodeHeuristicCost(SemiRegion var1, SemiRegion var2) {
      return GameMath.diagonalMoveDistance(var1.region.regionX, var1.region.regionY, var2.region.regionX, var2.region.regionY);
   }

   protected double getNodeCost(SemiRegion var1) {
      return 0.0;
   }

   protected double getNodePathCost(SemiRegion var1, SemiRegion var2) {
      return 1.0;
   }

   public static double estimatePathTileLength(LinkedList<Pathfinding<SemiRegion>.Node> var0) {
      double var1 = 0.0;
      Pathfinding.Node var3 = null;

      Pathfinding.Node var5;
      for(Iterator var4 = var0.iterator(); var4.hasNext(); var3 = var5) {
         var5 = (Pathfinding.Node)var4.next();
         if (var3 != null) {
            var1 += ((SemiRegion)var3.item).getLevelTile(((SemiRegion)var3.item).getAverageCell()).distance(((SemiRegion)var5.item).getLevelTile(((SemiRegion)var5.item).getAverageCell()));
         }
      }

      return var1;
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected double getNodePathCost(Object var1, Object var2) {
      return this.getNodePathCost((SemiRegion)var1, (SemiRegion)var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected double getNodeCost(Object var1) {
      return this.getNodeCost((SemiRegion)var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected double getNodeHeuristicCost(Object var1, Object var2) {
      return this.getNodeHeuristicCost((SemiRegion)var1, (SemiRegion)var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected boolean isAtTarget(Object var1, Object var2) {
      return this.isAtTarget((SemiRegion)var1, (SemiRegion)var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public PathResult findPath(Object var1, Object var2, int var3) {
      return this.findPath((SemiRegion)var1, (SemiRegion)var2, var3);
   }
}
