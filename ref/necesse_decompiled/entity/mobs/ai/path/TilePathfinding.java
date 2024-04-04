package necesse.entity.mobs.ai.path;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import necesse.engine.Screen;
import necesse.engine.tickManager.Performance;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.HashProxyLinkedList;
import necesse.engine.util.pathfinding.PathResult;
import necesse.engine.util.pathfinding.Pathfinding;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PathDoorOption;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.ui.HUD;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.multiTile.MultiTile;

public class TilePathfinding extends Pathfinding<Point> {
   public final TickManager tickManager;
   public final Level level;
   public final Mob mob;
   public final int moveOffsetX;
   public final int moveOffsetY;
   public final PathDoorOption doorOption;
   public final CollisionFilter collisionFilter;
   public final boolean canPassAllTiles;
   public final Rectangle tileCollisionOffsets;
   public BiPredicate<Point, Point> isAtTarget;
   public PathOptions options;
   public static final PathDir[] nonDiagonalPoints;
   public static final PathDir[] diagonalPoints;

   public static BiPredicate<Point, Point> isAtOrAdjacentObject(Level var0, int var1, int var2) {
      MultiTile var3 = var0.getObject(var1, var2).getMultiTile(var0, var1, var2);
      Rectangle var4 = var3.getAdjacentTileRectangle(var1, var2);
      return (var1x, var2x) -> {
         return var4.contains(var1x);
      };
   }

   public static boolean isAtOrAdjacentObject(Level var0, int var1, int var2, int var3, int var4) {
      MultiTile var5 = var0.getObject(var1, var2).getMultiTile(var0, var1, var2);
      Rectangle var6 = var5.getAdjacentTileRectangle(var1, var2);
      return var6.contains(var3, var4);
   }

   public static PathResult<Point, TilePathfinding> findPath(TickManager var0, Mob var1, int var2, int var3, PathOptions var4, BiPredicate<Point, Point> var5, int var6) {
      return findPath(var0, var1.getLevel(), var1, var2, var3, var4, var5, var6);
   }

   public static PathResult<Point, TilePathfinding> findPath(TickManager var0, Level var1, Mob var2, int var3, int var4, PathOptions var5, BiPredicate<Point, Point> var6, int var7) {
      TilePathfinding var8 = new TilePathfinding(var0, var1, var2, var6, var5);
      return var8.findPath(var3, var4, var7);
   }

   public TilePathfinding(TickManager var1, Level var2, Mob var3, BiPredicate<Point, Point> var4, PathOptions var5) {
      this(var1, var2, var3, var4, var5, var3.getPathDoorOption());
   }

   public TilePathfinding(TickManager var1, Level var2, Mob var3, BiPredicate<Point, Point> var4, PathOptions var5, PathDoorOption var6) {
      this.tickManager = var1;
      this.level = var2;
      this.mob = var3;
      this.isAtTarget = var4;
      Point var7 = var3.getPathMoveOffset();
      this.moveOffsetX = var7.x;
      this.moveOffsetY = var7.y;
      this.doorOption = var6;
      this.collisionFilter = var3.getLevelCollisionFilter();
      this.canPassAllTiles = this.collisionFilter == null || !this.collisionFilter.hasAdders();
      if (this.canPassAllTiles) {
         this.tileCollisionOffsets = new Rectangle();
      } else {
         Rectangle var8 = var3.getCollision(this.moveOffsetX, this.moveOffsetY);
         int var9 = var8.x / 32;
         int var10 = var8.y / 32;
         int var11 = (var8.x + var8.width) / 32;
         int var12 = (var8.y + var8.height) / 32;
         this.tileCollisionOffsets = new Rectangle(var9, var10, var11 - var9 + 1, var12 - var10 + 1);
      }

      this.options = var5;
      this.useBestOfConnected = true;
   }

   public PathResult<Point, TilePathfinding> findPath(int var1, int var2, int var3) {
      Point var4 = this.findClosestTile();
      return this.findPath(var4, new Point(var1, var2), var3);
   }

   public <C extends Pathfinding<Point>> PathResult<Point, C> findPath(Point var1, Point var2, int var3) {
      return (PathResult)Performance.record(this.tickManager, "tilePathfinding", (Supplier)(() -> {
         PathResult var4 = super.findPath(var1, var2, var3);
         HUD.submitPath(var4);
         return var4;
      }));
   }

   public Point findClosestTile() {
      Point var1 = null;
      double var2 = -1.0;
      int var4 = (this.mob.getX() - this.moveOffsetX + 16) / 32;
      int var5 = (this.mob.getY() - this.moveOffsetY + 16) / 32;
      CollisionFilter var6 = this.collisionFilter;
      Object var7 = new HashSet();
      if (var6 != null) {
         ArrayList var8 = this.level.getCollisions(this.mob.getCollision(), var6);
         if (!var8.isEmpty()) {
            var7 = (Set)var8.stream().filter((var0) -> {
               return !var0.invalidPos();
            }).map((var0) -> {
               return new Point(var0.tileX, var0.tileY);
            }).collect(Collectors.toSet());
            var6 = var6.copy().addFilter((var1x) -> {
               return !var7.contains(new Point(var1x.tileX, var1x.tileY));
            });
         }
      }

      Point var10;
      int var14;
      for(var14 = -1; var14 <= 1; ++var14) {
         for(int var9 = -1; var9 <= 1; ++var9) {
            if (var14 == 0 || var9 == 0) {
               var10 = new Point(var4 + var14, var5 + var9);
               if (var10.x >= 0 && var10.x < this.level.width && var10.y >= 0 && var10.y < this.level.height && !((Set)var7).contains(var10)) {
                  Point var11 = new Point(var10.x * 32 + this.moveOffsetX, var10.y * 32 + this.moveOffsetY);
                  if (this.canMoveNearbyTile(var10.x, var10.y, var11, var6, var14 != 0, var9 != 0)) {
                     double var12 = var11.distance((double)this.mob.x, (double)this.mob.y);
                     if (var2 < 0.0 || var12 < var2) {
                        var2 = var12;
                        var1 = var10;
                     }
                  }
               }
            }
         }
      }

      if (var1 == null) {
         for(var14 = 2; var14 < 7; ++var14) {
            Point var15 = new Point(var4 + var14, var5);
            if (var15.x >= 0 && var15.x < this.level.width && !((Set)var7).contains(var15)) {
               var10 = new Point(var15.x * 32 + this.moveOffsetX, var15.y * 32 + this.moveOffsetY);
               if (this.canMoveNearbyTile(var15.x, var15.y, var10, var6, true, false)) {
                  var1 = var15;
                  break;
               }

               var15 = new Point(var4 - var14, var5);
               if (var15.x >= 0 && var15.x < this.level.width && !((Set)var7).contains(var15)) {
                  var10 = new Point(var15.x * 32 + this.moveOffsetX, var15.y * 32 + this.moveOffsetY);
                  if (this.canMoveNearbyTile(var15.x, var15.y, var10, var6, true, false)) {
                     var1 = var15;
                     break;
                  }

                  var15 = new Point(var4, var5 + var14);
                  if (var15.y >= 0 && var15.y < this.level.height && !((Set)var7).contains(var15)) {
                     var10 = new Point(var15.x * 32 + this.moveOffsetX, var15.y * 32 + this.moveOffsetY);
                     if (this.canMoveNearbyTile(var15.x, var15.y, var10, var6, false, true)) {
                        var1 = var15;
                        break;
                     }

                     var15 = new Point(var4, var5 - var14);
                     if (var15.y >= 0 && var15.y < this.level.height && !((Set)var7).contains(var15)) {
                        var10 = new Point(var15.x * 32 + this.moveOffsetX, var15.y * 32 + this.moveOffsetY);
                        if (this.canMoveNearbyTile(var15.x, var15.y, var10, var6, false, true)) {
                           var1 = var15;
                           break;
                        }
                     }
                  }
               }
            }
         }
      }

      return var1;
   }

   private boolean canMoveNearbyTile(int var1, int var2, Point var3, CollisionFilter var4, boolean var5, boolean var6) {
      if (this.options.canMoveLine(this.tickManager, this.level, this.mob, var4, this.mob.getX(), this.mob.getY(), var3.x, var3.y)) {
         return true;
      } else if (var5 && this.options.canMoveLine(this.tickManager, this.level, this.mob, var4, this.mob.getX(), this.mob.getY(), var3.x, this.mob.getY()) && this.options.canMoveLine(this.tickManager, this.level, this.mob, var4, var3.x, this.mob.getY(), var3.x, var3.y)) {
         return true;
      } else {
         return var6 && this.options.canMoveLine(this.tickManager, this.level, this.mob, var4, this.mob.getX(), this.mob.getY(), this.mob.getX(), var3.y) && this.options.canMoveLine(this.tickManager, this.level, this.mob, var4, this.mob.getX(), var3.y, var3.x, var3.y);
      }
   }

   protected boolean isAtTarget(Point var1, Point var2) {
      int var3;
      int var4;
      if (this.isAtTarget == null) {
         for(var3 = 0; var3 < this.tileCollisionOffsets.width; ++var3) {
            for(var4 = 0; var4 < this.tileCollisionOffsets.height; ++var4) {
               if (var1.x + var3 == var2.x && var1.y + var4 == var2.y) {
                  return true;
               }
            }
         }

         return false;
      } else {
         for(var3 = 0; var3 < this.tileCollisionOffsets.width; ++var3) {
            for(var4 = 0; var4 < this.tileCollisionOffsets.height; ++var4) {
               if (this.isAtTarget.test(new Point(var1.x + var3, var1.y + var4), var2)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   protected boolean handleNewNode(Point var1, Pathfinding<Point>.Node var2, HashProxyLinkedList<Pathfinding<Point>.Node, Point> var3, HashProxyLinkedList<Pathfinding<Point>.Node, Point> var4) {
      return false;
   }

   protected void handleConnectedNodes(Pathfinding<Point>.Node var1, HashProxyLinkedList<Pathfinding<Point>.Node, Point> var2, HashProxyLinkedList<Pathfinding<Point>.Node, Point> var3, HashSet<Point> var4, Function<Point, Boolean> var5, BiConsumer<Pathfinding<Point>.Node, Pathfinding<Point>.Node> var6, Runnable var7) {
      Point var8 = (Point)var1.item;
      double var9 = var1.nodeCost;
      PathDir[] var11 = diagonalPoints;
      int var12 = var11.length;

      for(int var13 = 0; var13 < var12; ++var13) {
         PathDir var14 = var11[var13];
         Point var15 = new Point(var8.x + var14.x, var8.y + var14.y);
         if (var15.x >= 0 && var15.x < this.level.width && var15.y >= 0 && var15.y < this.level.height) {
            Pathfinding.Node var16 = (Pathfinding.Node)var2.getObject(var15);
            if (var16 != null) {
               if (var16.reverseDirection != var1.reverseDirection) {
                  var6.accept(var16, var1);
                  return;
               }
            } else {
               Pathfinding.Node var17 = (Pathfinding.Node)var3.getObject(var15);
               if (var17 != null) {
                  if (var17.reverseDirection != var1.reverseDirection) {
                     var6.accept(var17, var1);
                     return;
                  }
               } else if (this.checkCanPassDoorOrTile(var15)) {
                  if ((!var14.isDiagonal || this.checkCanPassDiagonalWithCost(var8, var9, var14.point)) && (Boolean)var5.apply(var15)) {
                     return;
                  }
               } else {
                  var4.add(var15);
               }
            }
         }
      }

   }

   protected boolean checkCanPassDiagonalWithCost(Point var1, double var2, Point var4) {
      return (Boolean)Performance.record(this.tickManager, "checkCanPassDiagonal", (Supplier)(() -> {
         if (this.canPassAllTiles) {
            return true;
         } else {
            boolean var5 = var4.x < 0;
            int var6 = var5 ? var1.x + this.tileCollisionOffsets.x + var4.x : var1.x + this.tileCollisionOffsets.x + var4.x + this.tileCollisionOffsets.width - 1;
            if (!this.options.canPassTile(this.tickManager, this.level, this.mob, this.doorOption, this.collisionFilter, var6, var1.y)) {
               return false;
            } else if (this.options.getTileCost(this.level, this.mob, this.doorOption, var6, var1.y) > var2) {
               return false;
            } else {
               boolean var7 = var4.y < 0;
               int var8 = var7 ? var1.y + this.tileCollisionOffsets.y + var4.y : var1.y + this.tileCollisionOffsets.y + var4.y + this.tileCollisionOffsets.height - 1;
               if (!this.options.canPassTile(this.tickManager, this.level, this.mob, this.doorOption, this.collisionFilter, var1.x, var8)) {
                  return false;
               } else {
                  return this.options.getTileCost(this.level, this.mob, this.doorOption, var1.x, var8) > var2 ? false : true;
               }
            }
         }
      }));
   }

   protected double getTileCost(int var1, int var2) {
      return (Double)Performance.record(this.tickManager, "getTileCost", (Supplier)(() -> {
         double var3 = 0.0;

         for(int var5 = 0; var5 < this.tileCollisionOffsets.width; ++var5) {
            int var6 = var1 + this.tileCollisionOffsets.x + var5;

            for(int var7 = 0; var7 < this.tileCollisionOffsets.height; ++var7) {
               int var8 = var2 + this.tileCollisionOffsets.y + var7;
               var3 = Math.max(var3, this.options.getTileCost(this.level, this.mob, this.doorOption, var6, var8));
            }
         }

         return var3;
      }));
   }

   protected boolean checkCanPassDoorOrTile(Point var1) {
      return (Boolean)Performance.record(this.tickManager, "checkIsAndCanPassDoor", (Supplier)(() -> {
         if (this.canPassAllTiles) {
            return true;
         } else {
            for(int var2 = 0; var2 < this.tileCollisionOffsets.width; ++var2) {
               int var3 = var1.x + this.tileCollisionOffsets.x + var2;

               for(int var4 = 0; var4 < this.tileCollisionOffsets.height; ++var4) {
                  int var5 = var1.y + this.tileCollisionOffsets.y + var4;
                  if (!this.options.checkCanPassDoorOrTile(this.tickManager, this.level, this.mob, this.doorOption, this.collisionFilter, var3, var5)) {
                     return false;
                  }
               }
            }

            return true;
         }
      }));
   }

   protected boolean checkCanPassDiagonalNoRecord(Point var1, Point var2) {
      if (this.canPassAllTiles) {
         return true;
      } else {
         boolean var3 = var2.x < 0;
         int var4 = var3 ? var1.x + this.tileCollisionOffsets.x + var2.x : var1.x + this.tileCollisionOffsets.x + var2.x + this.tileCollisionOffsets.width - 1;
         if (!this.options.canPassTile(this.tickManager, this.level, this.mob, this.doorOption, this.collisionFilter, var4, var1.y)) {
            return false;
         } else {
            boolean var5 = var2.y < 0;
            int var6 = var5 ? var1.y + this.tileCollisionOffsets.y + var2.y : var1.y + this.tileCollisionOffsets.y + var2.y + this.tileCollisionOffsets.height - 1;
            return this.options.canPassTile(this.tickManager, this.level, this.mob, this.doorOption, this.collisionFilter, var1.x, var6);
         }
      }
   }

   protected boolean checkCanPassDoorOrTileNoRecord(Point var1) {
      if (this.canPassAllTiles) {
         return true;
      } else {
         for(int var2 = 0; var2 < this.tileCollisionOffsets.width; ++var2) {
            int var3 = var1.x + this.tileCollisionOffsets.x + var2;

            for(int var4 = 0; var4 < this.tileCollisionOffsets.height; ++var4) {
               int var5 = var1.y + this.tileCollisionOffsets.y + var4;
               if (!this.options.checkCanPassDoorOrTile(this.tickManager, this.level, this.mob, this.doorOption, this.collisionFilter, var3, var5)) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   protected double getNodeComparable(Pathfinding<Point>.Node var1) {
      return this.options.nodePriority.cost.getCost(var1);
   }

   protected double getNodeHeuristicCost(Point var1, Point var2) {
      int var3 = var2.x - var1.x;
      int var4 = var2.y - var1.y;
      return (double)(Math.abs(var3) + Math.abs(var4));
   }

   protected double getNodeCost(Point var1) {
      return this.getTileCost(var1.x, var1.y);
   }

   protected double getNodePathCost(Point var1, Point var2) {
      return GameMath.diagonalMoveDistance(var1, var2);
   }

   public static DrawOptions getPathLineDrawOptions(List<Pathfinding<Point>.Node> var0, GameCamera var1) {
      DrawOptionsList var2 = new DrawOptionsList();
      Pathfinding.Node var3 = null;
      int var4 = 0;

      Pathfinding.Node var6;
      for(Iterator var5 = var0.iterator(); var5.hasNext(); var3 = var6) {
         var6 = (Pathfinding.Node)var5.next();
         if (var3 != null) {
            Color var7 = Color.getHSBColor((float)var4 / (float)var0.size(), 1.0F, 1.0F);
            int var8 = var1.getTileDrawX(((Point)var3.item).x) + 16;
            int var9 = var1.getTileDrawY(((Point)var3.item).y) + 16;
            int var10 = var1.getTileDrawX(((Point)var6.item).x) + 16;
            int var11 = var1.getTileDrawY(((Point)var6.item).y) + 16;
            if (var1.getBounds().intersectsLine((double)var8, (double)var9, (double)var10, (double)var11)) {
               var2.add(() -> {
                  Screen.drawLineRGBA(var8, var9, var10, var11, (float)var7.getRed() / 255.0F, (float)var7.getGreen() / 255.0F, (float)var7.getBlue() / 255.0F, 1.0F);
               });
            }
         }

         ++var4;
      }

      return var2;
   }

   public static void drawPathLine(List<Pathfinding<Point>.Node> var0, GameCamera var1) {
      getPathLineDrawOptions(var0, var1).draw();
   }

   public static void drawPathProcess(PathResult<Point, TilePathfinding> var0, GameCamera var1) {
      (new TilePathFindingDrawOptions(var0, var1)).draw();
   }

   public static DrawOptions getPathDrawOptions(Mob var0, ArrayList<? extends Point> var1, GameCamera var2) {
      DrawOptionsList var3 = new DrawOptionsList();
      if (var1 != null) {
         Point var4 = var0 == null ? new Point(16, 16) : var0.getPathMoveOffset();

         for(int var5 = 0; var5 < var1.size(); ++var5) {
            Color var6 = var5 % 2 == 0 ? new Color(0.0F, 0.0F, 1.0F) : new Color(0.0F, 1.0F, 0.0F);
            Point var7 = (Point)var1.get(var5);
            int var8 = var2.getTileDrawX(var7.x) + var4.x;
            int var9 = var2.getTileDrawY(var7.y) + var4.y;
            int var10;
            int var11;
            if (var5 == 0) {
               if (var0 == null) {
                  continue;
               }

               var10 = var2.getDrawX(var0.x);
               var11 = var2.getDrawY(var0.y);
            } else {
               Point var12 = (Point)var1.get(var5 - 1);
               var10 = var2.getTileDrawX(var12.x) + var4.x;
               var11 = var2.getTileDrawY(var12.y) + var4.y;
            }

            var3.add(() -> {
               Screen.drawLineRGBA(var8, var9, var10, var11, (float)var6.getRed() / 255.0F, (float)var6.getGreen() / 255.0F, (float)var6.getBlue() / 255.0F, 1.0F);
            });
         }
      }

      return var3;
   }

   public static void drawPath(Mob var0, ArrayList<? extends Point> var1, GameCamera var2) {
      getPathDrawOptions(var0, var1, var2).draw();
   }

   public static boolean isResultWithin(PathResult<Point, TilePathfinding> var0, int var1, int var2, int var3) {
      if (var0.foundTarget) {
         return true;
      } else {
         Point var4 = (Point)var0.getLastPathResult();
         if (var4 != null) {
            int var5 = Math.abs(var4.x - var1);
            int var6 = Math.abs(var4.y - var2);
            int var7 = Math.max(var5, var6);
            return var7 <= var3;
         } else {
            return false;
         }
      }
   }

   public static ArrayList<FinalPathPoint> reducePathPoints(TilePathfinding var0, LinkedList<Pathfinding<Point>.Node> var1) {
      ArrayList var2 = new ArrayList();
      if (var1 != null && !var1.isEmpty()) {
         Pathfinding.Node var3;
         if (var1.size() > 1) {
            var3 = (Pathfinding.Node)var1.getFirst();
            if (var0.level == null || !var0.level.isSolidTile(((Point)var3.item).x, ((Point)var3.item).y)) {
               var1.removeFirst();
            }
         }

         var3 = (Pathfinding.Node)var1.removeFirst();
         var2.add(new FinalPathPoint(((Point)var3.item).x, ((Point)var3.item).y, () -> {
            return var0.checkCanPassDoorOrTileNoRecord((Point)var3.item);
         }));
         if (var1.isEmpty()) {
            return var2;
         } else {
            PathDir var5 = PathDir.getDir((Point)((Pathfinding.Node)var1.getFirst()).item, (Point)var3.item);
            LinkedList var6 = new LinkedList();
            int var7 = -1;
            boolean var8 = false;

            while(!var1.isEmpty()) {
               ++var7;
               Pathfinding.Node var9 = (Pathfinding.Node)var1.removeFirst();
               PathDir var10 = PathDir.getDir((Point)var9.item, (Point)var3.item);
               if (!var8 && var10 == var5 && var7 < 5) {
                  if (var0.level.getObjectID(((Point)var3.item).x, ((Point)var3.item).y) != 0) {
                     LevelObject var11 = var0.level.getLevelObject(((Point)var3.item).x, ((Point)var3.item).y);
                     if (var11.object.isDoor || var11.isSolid()) {
                        var2.add(new FinalPathPoint(((Point)var3.item).x, ((Point)var3.item).y, () -> {
                           return var6.stream().allMatch(Supplier::get);
                        }));
                        var6 = new LinkedList();
                        var8 = true;
                        var7 = 0;
                     }
                  }
               } else {
                  var2.add(new FinalPathPoint(((Point)var3.item).x, ((Point)var3.item).y, () -> {
                     return var6.stream().allMatch(Supplier::get);
                  }));
                  var6 = new LinkedList();
                  var8 = false;
                  var7 = 0;
               }

               if (var10 != null && var10.isDiagonal) {
                  Point var13 = (Point)var3.item;
                  var6.add(() -> {
                     return var0.checkCanPassDiagonalNoRecord(var13, var10.point);
                  });
               } else {
                  var6.add(() -> {
                     return var0.checkCanPassDoorOrTileNoRecord((Point)var9.item);
                  });
               }

               var5 = var10;
               var3 = var9;
               if (var1.isEmpty()) {
                  var2.add(new FinalPathPoint(((Point)var9.item).x, ((Point)var9.item).y, () -> {
                     return var6.stream().allMatch(Supplier::get);
                  }));
                  var6 = new LinkedList();
               }
            }

            return var2;
         }
      } else {
         return var2;
      }
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected double getNodePathCost(Object var1, Object var2) {
      return this.getNodePathCost((Point)var1, (Point)var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected double getNodeCost(Object var1) {
      return this.getNodeCost((Point)var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected double getNodeHeuristicCost(Object var1, Object var2) {
      return this.getNodeHeuristicCost((Point)var1, (Point)var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected boolean handleNewNode(Object var1, Pathfinding.Node var2, HashProxyLinkedList var3, HashProxyLinkedList var4) {
      return this.handleNewNode((Point)var1, var2, var3, var4);
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected boolean isAtTarget(Object var1, Object var2) {
      return this.isAtTarget((Point)var1, (Point)var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public PathResult findPath(Object var1, Object var2, int var3) {
      return this.findPath((Point)var1, (Point)var2, var3);
   }

   static {
      nonDiagonalPoints = new PathDir[]{PathDir.UP, PathDir.RIGHT, PathDir.DOWN, PathDir.LEFT};
      diagonalPoints = PathDir.values();
   }

   public static enum NodePriority {
      HEURISTIC_TILE_COST((var0) -> {
         return var0.heuristicCost + var0.nodeCost;
      }),
      HEURISTIC_COST((var0) -> {
         return var0.heuristicCost;
      }),
      PATH_TILE_COST((var0) -> {
         return var0.pathCost + var0.nodeCost;
      }),
      PATH_COST((var0) -> {
         return var0.pathCost;
      }),
      TOTAL_COST((var0) -> {
         return var0.heuristicCost + var0.pathCost + var0.nodeCost;
      });

      public final NodePriorityFunction cost;

      private NodePriority(NodePriorityFunction var3) {
         this.cost = var3;
      }

      // $FF: synthetic method
      private static NodePriority[] $values() {
         return new NodePriority[]{HEURISTIC_TILE_COST, HEURISTIC_COST, PATH_TILE_COST, PATH_COST, TOTAL_COST};
      }
   }

   @FunctionalInterface
   public interface NodePriorityFunction {
      double getCost(Pathfinding<Point>.Node var1);
   }
}
