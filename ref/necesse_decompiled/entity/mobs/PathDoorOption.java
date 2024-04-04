package necesse.entity.mobs;

import java.awt.Point;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Stream;
import necesse.engine.GlobalData;
import necesse.engine.util.GameUtils;
import necesse.engine.util.pathfinding.PathResult;
import necesse.engine.util.pathfinding.Pathfinding;
import necesse.entity.mobs.ai.path.RegionPathfinding;
import necesse.entity.mobs.ai.path.SemiRegionPathResult;
import necesse.level.gameObject.DoorObject;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.SemiRegion;
import necesse.level.maps.regionSystem.SemiRegionEventListener;

public abstract class PathDoorOption {
   public final String debugName;
   public final Level level;
   public int maxIterations = 500;
   private HashMap<Integer, HashMap<Integer, Cache>> canRegionPathTo = new HashMap();

   public PathDoorOption(String var1, Level var2) {
      this.debugName = var1;
      this.level = var2;
   }

   public SemiRegionPathResult canPathThrough(SemiRegion var1) {
      if (var1.getType().isDoor) {
         return SemiRegionPathResult.CHECK_EACH_TILE;
      } else {
         return var1.getType().isSolid ? SemiRegionPathResult.INVALID : SemiRegionPathResult.VALID;
      }
   }

   public boolean canPathThroughCheckTile(SemiRegion var1, int var2, int var3) {
      if (this.canBreakDown(var2, var3)) {
         return true;
      } else {
         GameObject var4 = this.level.getObject(var2, var3);
         if (!var4.isDoor) {
            return false;
         } else {
            return ((DoorObject)var4).isOpen(this.level, var2, var3, this.level.getObjectRotation(var2, var3)) || this.canOpen(var2, var3);
         }
      }
   }

   public boolean canPass(int var1, int var2) {
      return this.level.regionManager.getRegionType(var1, var2) == SemiRegion.RegionType.OPEN;
   }

   public boolean canPassDoor(DoorObject var1, int var2, int var3) {
      return var1.isOpen(this.level, var2, var3, this.level.getObjectRotation(var2, var3)) || this.canOpen(var2, var3);
   }

   public abstract boolean canBreakDown(int var1, int var2);

   public abstract boolean canOpen(int var1, int var2);

   public abstract boolean canClose(int var1, int var2);

   public abstract boolean doorChangeInvalidatesCache(DoorObject var1, DoorObject var2, int var3, int var4);

   public int getTotalCachedPaths() {
      return this.canRegionPathTo.values().stream().mapToInt(HashMap::size).sum();
   }

   public Collection<Integer> getSourcePathRegionIDs() {
      return this.canRegionPathTo.keySet();
   }

   public Collection<Integer> getDestinationPathRegionIDs(int var1) {
      HashMap var2 = (HashMap)this.canRegionPathTo.get(var1);
      return (Collection)(var2 == null ? new LinkedList() : var2.keySet());
   }

   public Collection<SemiRegion> getCachedPath(int var1, int var2) {
      HashMap var3 = (HashMap)this.canRegionPathTo.get(var1);
      if (var3 == null) {
         return new LinkedList();
      } else {
         Cache var4 = (Cache)var3.get(var2);
         return var4 == null ? new LinkedList() : var4.path;
      }
   }

   public void invalidateCache() {
      this.canRegionPathTo.forEach((var0, var1) -> {
         var1.forEach((var0x, var1x) -> {
            var1x.invalidateListeners();
         });
      });
      this.canRegionPathTo.clear();
   }

   private HashMap<Integer, Cache> getFromRegionID(int var1) {
      return (HashMap)this.canRegionPathTo.compute(var1, (var0, var1x) -> {
         return var1x == null ? new HashMap() : var1x;
      });
   }

   private void onCacheInvalidated(int var1, int var2) {
      HashMap var3 = (HashMap)this.canRegionPathTo.get(var1);
      if (var3 != null) {
         var3.remove(var2);
         if (var3.isEmpty()) {
            this.canRegionPathTo.remove(var1);
         }
      }

   }

   private void cacheFoundPath(PathResult<SemiRegion, RegionPathfinding> var1, int var2, int var3) {
      Iterator var12;
      if (var1.foundTarget) {
         HashMap var4 = new HashMap();

         for(int var5 = 0; var5 < var1.path.size(); ++var5) {
            SemiRegion var6 = (SemiRegion)((Pathfinding.Node)var1.path.get(var5)).item;
            var4.put(var6.getRegionID(), var1.path.subList(var5, var1.path.size()));
         }

         var12 = var4.keySet().iterator();

         while(var12.hasNext()) {
            int var14 = (Integer)var12.next();
            if (var14 != var3) {
               HashMap var7 = this.getFromRegionID(var14);
               Cache var8 = (Cache)var7.get(var3);
               if (var8 == null) {
                  List var9 = (List)var4.get(var14);
                  var7.put(var3, new Cache(true, var9.stream().map((var0) -> {
                     return (SemiRegion)var0.item;
                  }), () -> {
                     this.onCacheInvalidated(var14, var3);
                  }));
               }
            }
         }
      } else {
         HashSet var11 = new HashSet();
         var12 = var1.path.iterator();

         while(var12.hasNext()) {
            Pathfinding.Node var15 = (Pathfinding.Node)var12.next();
            var11.add(((SemiRegion)var15.item).getRegionID());
         }

         HashSet var13 = new HashSet();
         HashSet var16 = new HashSet();
         Iterator var17 = GameUtils.mapIterable(GameUtils.concatIterators(var1.closedNodes.iterator(), var1.openNodes.iterator()), (var0) -> {
            return (SemiRegion)var0.item;
         }).iterator();

         while(var17.hasNext()) {
            SemiRegion var18 = (SemiRegion)var17.next();
            var13.add(var18);
            var16.remove(var18);
            Iterator var20 = var18.adjacentRegions.iterator();

            while(var20.hasNext()) {
               SemiRegion var10 = (SemiRegion)var20.next();
               if (!var13.contains(var10)) {
                  var16.add(var10);
               }
            }
         }

         var17 = var11.iterator();

         while(var17.hasNext()) {
            int var19 = (Integer)var17.next();
            if (var19 != var3) {
               HashMap var21 = this.getFromRegionID(var19);
               Cache var22 = (Cache)var21.get(var3);
               if (var22 == null) {
                  var21.put(var3, new Cache(false, var16.stream(), () -> {
                     this.onCacheInvalidated(var19, var3);
                  }));
               }
            }
         }
      }

   }

   public boolean canMoveToTile(int var1, int var2, int var3, int var4, boolean var5) {
      int var6 = this.level.regionManager.getRegionID(var1, var2);
      int var7 = this.level.regionManager.getRegionID(var3, var4);
      if (var6 == var7) {
         return true;
      } else {
         HashMap var8 = this.getFromRegionID(var6);
         Cache var9 = (Cache)var8.get(var7);
         if (var9 != null && var9.canPath) {
            return true;
         } else if (!var5) {
            if (var9 != null) {
               return var9.canPath;
            } else {
               BiPredicate var16 = (var0, var1x) -> {
                  return var0.getRegionID() == var1x.getRegionID();
               };
               PathResult var17 = RegionPathfinding.findMoveToTile(this.level, var1, var2, var3, var4, this, var16, this.maxIterations);
               this.cacheFoundPath(var17, var6, var7);
               return var17.foundTarget;
            }
         } else {
            HashSet var10 = new HashSet();
            var10.add(var7);
            boolean var11 = true;

            int var14;
            for(Iterator var12 = this.level.getObject(var3, var4).getMultiTile(this.level, var3, var4).getAdjacentTiles(var3, var4, true).iterator(); var12.hasNext(); var10.add(var14)) {
               Point var13 = (Point)var12.next();
               var14 = this.level.regionManager.getRegionID(var13.x, var13.y);
               if (var14 == var6) {
                  return true;
               }

               Cache var15 = (Cache)var8.get(var14);
               if (var15 != null) {
                  if (var15.canPath) {
                     return true;
                  }
               } else {
                  var11 = false;
               }
            }

            if (var11) {
               return false;
            } else {
               BiPredicate var18 = (var1x, var2x) -> {
                  return var1x.getRegionID() == var2x.getRegionID() || var10.contains(var1x.getRegionID());
               };
               PathResult var19 = RegionPathfinding.findMoveToTile(this.level, var1, var2, var3, var4, this, var18, this.maxIterations);
               if (var19.foundTarget) {
                  var7 = ((SemiRegion)((Pathfinding.Node)var19.path.get(var19.path.size() - 1)).item).getRegionID();
               }

               this.cacheFoundPath(var19, var6, var7);
               return var19.foundTarget;
            }
         }
      }
   }

   private class Cache {
      private boolean canPath;
      private LinkedList<SemiRegion> path;
      private LinkedList<SemiRegionEventListener> invalidHandlers = new LinkedList();

      public Cache(boolean var2, Stream<SemiRegion> var3, Runnable var4) {
         this.canPath = var2;
         if (GlobalData.isDevMode()) {
            this.path = new LinkedList();
         }

         ((Stream)var3.sequential()).forEach((var2x) -> {
            if (GlobalData.isDevMode()) {
               this.path.add(var2x);
            }

            SemiRegionEventListener var3 = var2x.addListener(() -> {
               this.invalidateListeners();
               var4.run();
            }, (var2, var3x, var4x, var5) -> {
               if (PathDoorOption.this.doorChangeInvalidatesCache(var2, var3x, var4x, var5)) {
                  this.invalidateListeners();
                  var4.run();
               }

            });
            this.invalidHandlers.add(var3);
         });
      }

      public void invalidateListeners() {
         Iterator var1 = this.invalidHandlers.iterator();

         while(var1.hasNext()) {
            SemiRegionEventListener var2 = (SemiRegionEventListener)var1.next();
            var2.submitHandlerInvalidated();
         }

         this.invalidHandlers.clear();
         if (this.path != null) {
            this.path.clear();
         }

      }
   }
}
