package necesse.level.maps.generationModules;

import java.awt.Point;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.TicketSystemList;
import necesse.entity.mobs.Mob;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.presets.Preset;

public class CellAutomaton {
   private HashSet<Point> alive = new HashSet();
   public static final Point[] allNeighbours = new Point[]{new Point(-1, -1), new Point(0, -1), new Point(1, -1), new Point(-1, 0), new Point(1, 0), new Point(-1, 1), new Point(0, 1), new Point(1, 1)};
   public static final Point[] closeNeighbours = new Point[]{new Point(0, -1), new Point(-1, 0), new Point(1, 0), new Point(0, 1)};

   public CellAutomaton() {
   }

   public CellAutomaton setAlive(int var1, int var2) {
      this.alive.add(new Point(var1, var2));
      return this;
   }

   public CellAutomaton setDead(int var1, int var2) {
      this.alive.remove(new Point(var1, var2));
      return this;
   }

   public boolean isAlive(int var1, int var2) {
      return this.alive.contains(new Point(var1, var2));
   }

   private static int countAliveNeighbours(HashMap<Point, Boolean> var0, int var1, int var2, Point[] var3) {
      int var4 = 0;
      Point[] var5 = var3;
      int var6 = var3.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         Point var8 = var5[var7];
         int var9 = var1 + var8.x;
         int var10 = var2 + var8.y;
         if ((Boolean)var0.getOrDefault(new Point(var9, var10), false)) {
            ++var4;
         }
      }

      return var4;
   }

   public void doCellularAutomaton(int var1, int var2, int var3, Point[] var4) {
      for(int var5 = 0; var5 < var3; ++var5) {
         HashMap var6 = new HashMap();
         Iterator var7 = this.alive.iterator();

         while(var7.hasNext()) {
            Point var8 = (Point)var7.next();

            for(int var9 = -1; var9 <= 1; ++var9) {
               for(int var10 = -1; var10 <= 1; ++var10) {
                  int var11 = var9 + var8.x;
                  int var12 = var10 + var8.y;
                  boolean var13 = var9 == 0 && var10 == 0;
                  var6.merge(new Point(var11, var12), var13, (var0, var1x) -> {
                     return var0 || var1x;
                  });
               }
            }
         }

         this.alive.clear();
         var6.forEach((var5x, var6x) -> {
            int var7 = countAliveNeighbours(var6, var5x.x, var5x.y, var4);
            if (var6x) {
               if (var7 < var1) {
                  this.alive.remove(var5x);
               } else {
                  this.alive.add(var5x);
               }
            } else if (var7 > var2) {
               this.alive.add(var5x);
            }

         });
      }

   }

   public void doCellularAutomaton(int var1, int var2, int var3) {
      this.doCellularAutomaton(var1, var2, var3, allNeighbours);
   }

   public void cleanHardEdges() {
      this.doCellularAutomaton(3, Integer.MAX_VALUE, 1, closeNeighbours);
   }

   public Collection<Point> getAliveUnordered() {
      return this.alive;
   }

   public Stream<Point> streamAliveOrdered() {
      Comparator var1 = Comparator.comparingInt((var0) -> {
         return var0.x;
      });
      var1 = var1.thenComparingInt((var0) -> {
         return var0.y;
      });
      return this.getAliveUnordered().stream().sorted(var1);
   }

   public CellAutomaton forEachTile(Level var1, GenerationTools.PlaceFunction var2) {
      this.streamAliveOrdered().forEachOrdered((var2x) -> {
         if (var2x.x >= 0 && var2x.x < var1.width && var2x.y >= 0 && var2x.y < var1.height) {
            var2.place(var1, var2x.x, var2x.y);
         }

      });
      return this;
   }

   public CellAutomaton placeObjects(Level var1, int var2) {
      return this.forEachTile(var1, (var1x, var2x, var3) -> {
         var1x.setObject(var2x, var3, var2);
      });
   }

   public CellAutomaton replaceObjects(Level var1, int var2, int var3) {
      return this.forEachTile(var1, (var2x, var3x, var4) -> {
         if (var2x.getObjectID(var3x, var4) == var2) {
            var2x.setObject(var3x, var4, var3);
         }

      });
   }

   public CellAutomaton placeTiles(Level var1, int var2) {
      return this.forEachTile(var1, (var1x, var2x, var3) -> {
         var1x.setTile(var2x, var3, var2);
      });
   }

   public CellAutomaton replaceTiles(Level var1, int var2, int var3) {
      return this.forEachTile(var1, (var2x, var3x, var4) -> {
         if (var2x.getTileID(var3x, var4) == var2) {
            var2x.setTile(var3x, var4, var3);
         }

      });
   }

   public CellAutomaton forEachEdge(Level var1, GenerationTools.PlaceFunction var2) {
      return this.forEachTile(var1, (var2x, var3, var4) -> {
         if (this.isCellEdge(var3, var4)) {
            var2.place(var2x, var3, var4);
         }

      });
   }

   public CellAutomaton placeEdgeWalls(Level var1, int var2, boolean var3) {
      GameObject var4 = ObjectRegistry.getObject(var2);
      LinkedList var5 = new LinkedList();
      this.forEachTile(var1, (var4x, var5x, var6x) -> {
         if (this.isCellEdge(var5x, var6x)) {
            var4x.setObject(var5x, var6x, 0);
            if (var4.canPlace(var4x, var5x, var6x, 0) == null) {
               var4.placeObject(var4x, var5x, var6x, 0);
               if (var3) {
                  var5.add(new Point(var5x, var6x));
               }
            }
         }

      });
      LinkedList var6 = new LinkedList();
      Iterator var7 = var5.iterator();

      while(true) {
         Point var8;
         while(var7.hasNext()) {
            var8 = (Point)var7.next();
            if (!this.isAlive(var8.x + 1, var8.y) && var1.getObjectID(var8.x + 1, var8.y) != var2 && (var1.getObjectID(var8.x + 1, var8.y - 1) == var2 || var1.getObjectID(var8.x + 1, var8.y + 1) == var2)) {
               var1.setObject(var8.x + 1, var8.y, 0);
               if (var4.canPlace(var1, var8.x + 1, var8.y, 0) == null) {
                  var6.add(new Point(var8.x + 1, var8.y));
               }
            } else if (!this.isAlive(var8.x - 1, var8.y) && var1.getObjectID(var8.x - 1, var8.y) != var2 && (var1.getObjectID(var8.x - 1, var8.y - 1) == var2 || var1.getObjectID(var8.x - 1, var8.y + 1) == var2)) {
               var1.setObject(var8.x - 1, var8.y, 0);
               if (var4.canPlace(var1, var8.x - 1, var8.y, 0) == null) {
                  var6.add(new Point(var8.x - 1, var8.y));
               }
            } else if (this.isAlive(var8.x, var8.y + 1) || var1.getObjectID(var8.x, var8.y + 1) == var2 || var1.getObjectID(var8.x - 1, var8.y + 1) != var2 && var1.getObjectID(var8.x + 1, var8.y + 1) != var2) {
               if (!this.isAlive(var8.x, var8.y - 1) && var1.getObjectID(var8.x, var8.y - 1) != var2 && (var1.getObjectID(var8.x - 1, var8.y - 1) == var2 || var1.getObjectID(var8.x + 1, var8.y - 1) == var2)) {
                  var1.setObject(var8.x, var8.y - 1, 0);
                  if (var4.canPlace(var1, var8.x, var8.y - 1, 0) == null) {
                     var6.add(new Point(var8.x, var8.y - 1));
                  }
               }
            } else {
               var1.setObject(var8.x, var8.y + 1, 0);
               if (var4.canPlace(var1, var8.x, var8.y + 1, 0) == null) {
                  var6.add(new Point(var8.x, var8.y + 1));
               }
            }
         }

         var7 = var6.iterator();

         while(var7.hasNext()) {
            var8 = (Point)var7.next();
            var4.placeObject(var1, var8.x, var8.y, 0);
         }

         return this;
      }
   }

   public int countAlive(int var1, int var2, Point... var3) {
      return (int)Arrays.stream(var3).filter((var3x) -> {
         return this.isAlive(var1 + var3x.x, var2 + var3x.y);
      }).count();
   }

   public int countDead(int var1, int var2, Point... var3) {
      return var3.length - this.countAlive(var1, var2, var3);
   }

   public boolean isAllAlive(int var1, int var2, Point... var3) {
      return Arrays.stream(var3).allMatch((var3x) -> {
         return this.isAlive(var1 + var3x.x, var2 + var3x.y);
      });
   }

   public boolean isCellEdge(int var1, int var2) {
      return !this.isAllAlive(var1, var2, closeNeighbours);
   }

   public boolean isCellEdge(Point var1) {
      return this.isCellEdge(var1.x, var1.y);
   }

   public void placeFurniturePresets(TicketSystemList<Preset> var1, float var2, Level var3, GameRandom var4) {
      this.forEachTile(var3, (var4x, var5, var6) -> {
         if (var4.getChance(var2)) {
            GenerationTools.generateFurniture(var4x, var4, var5, var6, var1, (var1x) -> {
               return var1x.objectID() == 0 && this.isAlive(var1x.tileX, var1x.tileY);
            });
         }

      });
   }

   public CellAutomaton spawnMobs(Level var1, GameRandom var2, String var3, int var4, int var5, int var6, int var7, Predicate<Point> var8) {
      Mob var9 = MobRegistry.getMob(var3, var1);
      Point var10 = var9.getPathMoveOffset();
      List var11 = (List)this.streamAliveOrdered().filter(var8).map((var1x) -> {
         return new Point(var1x.x * 32 + var10.x, var1x.y * 32 + var10.y);
      }).filter((var2x) -> {
         return !var9.collidesWith(var1, var2x.x, var2x.y);
      }).collect(Collectors.toList());
      int var12 = var2.getIntBetween(var4, var5);
      int var13 = GameMath.limit(var11.size() / var12, var6, var7);

      for(int var14 = 0; var14 < var13; ++var14) {
         Point var15 = (Point)var2.getOneOf(var11);
         if (var15 != null) {
            Mob var16 = MobRegistry.getMob(var3, var1);
            var1.entityManager.addMob(var16, (float)var15.x, (float)var15.y);
            var16.canDespawn = false;
         }
      }

      return this;
   }

   public CellAutomaton spawnMobs(Level var1, GameRandom var2, String var3, int var4, int var5, int var6, int var7) {
      return this.spawnMobs(var1, var2, var3, var4, var5, var6, var7, (var1x) -> {
         return var1.getObjectID(var1x.x, var1x.y) == 0;
      });
   }
}
