package necesse.level.maps.generationModules;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.TicketSystemList;
import necesse.entity.mobs.Mob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.TrialEntranceObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.gameObject.GameObject;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;
import necesse.level.maps.TilePosition;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetRotation;
import necesse.level.maps.presets.PresetUtils;

public class GenerationTools {
   private static final Point[] nextToGetters = new Point[]{new Point(0, -1), new Point(-1, 0), new Point(1, 0), new Point(0, 1)};

   public GenerationTools() {
   }

   public static void iterateLevel(Level var0, BiConsumer<Integer, Integer> var1) {
      for(int var2 = 0; var2 < var0.width; ++var2) {
         for(int var3 = 0; var3 < var0.height; ++var3) {
            var1.accept(var2, var3);
         }
      }

   }

   public static void iterateLevel(Level var0, BiFunction<Integer, Integer, Boolean> var1, BiConsumer<Integer, Integer> var2) {
      iterateLevel(var0, (var2x, var3) -> {
         if ((Boolean)var1.apply(var2x, var3)) {
            var2.accept(var2x, var3);
         }

      });
   }

   public static void smoothTile(Level var0, int var1) {
      boolean var2 = false;

      while(!var2) {
         var2 = true;

         for(int var3 = 0; var3 < var0.width; ++var3) {
            for(int var4 = 0; var4 < var0.height; ++var4) {
               if (var0.getTileID(var3, var4) == var1 & var0.getTileID(var3 + 1, var4) != var1 & var0.getTileID(var3 - 1, var4) != var1) {
                  var0.setTile(var3, var4, var0.getTileID(var3 + 1, var4));
                  var2 = false;
               }

               if (var0.getTileID(var3, var4) == var1 & var0.getTileID(var3, var4 + 1) != var1 & var0.getTileID(var3, var4 - 1) != var1) {
                  var0.setTile(var3, var4, var0.getTileID(var3, var4 + 1));
                  var2 = false;
               }
            }
         }
      }

   }

   public static void spawnMobHerds(Level var0, GameRandom var1, String var2, int var3, int var4, int var5, int var6) {
      int var7 = 0;
      int var8 = 0;

      while(var7 < var3 && var8 < var3 * 10) {
         ++var8;
         int var9 = var1.nextInt(var0.width);
         int var10 = var1.nextInt(var0.height);
         if (var0.getTileID(var10, var9) == var4) {
            int var11 = var1.getIntBetween(var5, var6);
            int var12 = 0;
            int var13 = 0;

            while(var12 < var11 && var13 < var6 * 10) {
               ++var13;
               int var14 = var1.getIntBetween(-5, 5);
               int var15 = var1.getIntBetween(-5, 5);
               Mob var16 = MobRegistry.getMob(var2, var0);
               var16.setX((var10 + var14) * 32 + 16);
               var16.setY((var9 + var15) * 32 + 16);
               if (var0.getTileID(var10 + var14, var9 + var15) == var4 && !var16.collidesWith(var0)) {
                  var0.entityManager.mobs.add(var16);
                  ++var12;
                  ++var7;
               }
            }
         }
      }

   }

   public static boolean spawnRandomPreset(Level var0, Preset var1, boolean var2, boolean var3, GameRandom var4, boolean var5, int var6, int var7) {
      if (var2) {
         var1 = PresetUtils.randomizeXMirror(var1, var4);
         var1 = PresetUtils.randomizeYMirror(var1, var4);
      }

      if (var3) {
         var1 = PresetUtils.randomizeRotation(var1, var4);
      }

      ArrayList var8 = new ArrayList();

      int var10;
      int var11;
      for(int var9 = 0; var9 < var6; ++var9) {
         var10 = var4.getIntBetween(2, var0.width - var1.width - 2);
         var11 = var4.getIntBetween(2, var0.height - var1.height - 2);
         if (var5 || var1.canApplyToLevel(var0, var10, var11)) {
            var8.add(new Point(var10, var11));
         }
      }

      boolean var13 = false;

      for(var10 = 0; var10 < var7 && var8.size() != 0; ++var10) {
         var11 = var4.nextInt(var8.size());
         Point var12 = (Point)var8.get(var11);
         var1.applyToLevel(var0, var12.x, var12.y);
         var8.remove(var11);
         var13 = true;
      }

      return var13;
   }

   public static boolean[] generateRandomCellMapArea(GameRandom var0, boolean[] var1, int var2, int var3, int var4, int var5, int var6, float var7) {
      for(int var8 = 0; var8 < var5; ++var8) {
         for(int var9 = 0; var9 < var6; ++var9) {
            int var10 = var3 + var8 + (var4 + var9) * var2;
            if (var10 >= 0 && var10 < var1.length && (var7 >= 1.0F || var0.nextFloat() < var7)) {
               var1[var10] = true;
            }
         }
      }

      return var1;
   }

   public static boolean[] generateRandomCellMap(GameRandom var0, int var1, int var2, float var3) {
      return generateRandomCellMapArea(var0, new boolean[var1 * var2], var1, 0, 0, var1, var2, var3);
   }

   public static int countTrueNeighbours(boolean[] var0, int var1, int var2, int var3, boolean var4) {
      int var5 = 0;

      for(int var6 = -1; var6 <= 1; ++var6) {
         for(int var7 = -1; var7 <= 1; ++var7) {
            int var8 = var2 + var6;
            int var9 = var3 + var7;
            if (var6 != 0 || var7 != 0) {
               int var10 = var8 + var9 * var1;
               if (var10 >= 0 && var10 < var0.length) {
                  if (var0[var10]) {
                     ++var5;
                  }
               } else if (var4) {
                  ++var5;
               }
            }
         }
      }

      return var5;
   }

   public static boolean[] doCellularAutomaton(boolean[] var0, int var1, int var2, int var3, int var4, boolean var5) {
      boolean[] var6 = (boolean[])var0.clone();

      for(int var7 = 0; var7 < var1; ++var7) {
         for(int var8 = 0; var8 < var2; ++var8) {
            int var9 = countTrueNeighbours(var0, var1, var7, var8, var5);
            int var10 = var7 + var8 * var1;
            if (var0[var10]) {
               if (var9 < var3) {
                  var6[var10] = false;
               } else {
                  var6[var10] = true;
               }
            } else if (var9 > var4) {
               var6[var10] = true;
            } else {
               var6[var10] = false;
            }
         }
      }

      return var6;
   }

   public static boolean[] doCellularAutomaton(boolean[] var0, int var1, int var2, int var3, int var4, boolean var5, int var6) {
      for(int var7 = 0; var7 <= var6; ++var7) {
         var0 = doCellularAutomaton(var0, var1, var2, var3, var4, var5);
      }

      return var0;
   }

   public static boolean[] generateRandomVein(GameRandom var0, int var1, int var2) {
      boolean[] var3 = generateRandomCellMap(var0, var1, var2, 0.4F);
      return doCellularAutomaton(var3, var1, var2, 4, 3, false, 4);
   }

   public static void placeRandomVein(Level var0, GameRandom var1, int var2, int var3, int var4, int var5, PlaceFunction var6) {
      int var7 = var1.getIntBetween(var4, var5);
      int var8 = var1.getIntBetween(var4, var5);
      boolean[] var9 = generateRandomVein(var1, var7, var8);

      for(int var10 = 0; var10 < var7; ++var10) {
         for(int var11 = 0; var11 < var8; ++var11) {
            int var12 = var10 + var11 * var7;
            int var13 = var10 + var2 - var7 / 2;
            int var14 = var11 + var3 - var8 / 2;
            if (var9[var12]) {
               var6.place(var0, var13, var14);
            }
         }
      }

   }

   public static void placeRandomVein(Level var0, GameRandom var1, int var2, int var3, int var4, int var5, int var6, int var7, float var8, int var9, int var10, float var11, boolean var12, boolean var13) {
      GameObject var14 = var11 > 0.0F ? ObjectRegistry.getObject(var9) : null;
      placeRandomVein(var0, var1, var2, var3, var4, var5, (var10x, var11x, var12x) -> {
         if (var8 > 0.0F && (var8 >= 1.0F || var1.nextFloat() < var8) && (var7 == -1 || var0.getTileID(var11x, var12x) == var7)) {
            TileRegistry.getTile(var6).placeTile(var0, var11x, var12x);
         }

         if (var14 != null && (var11 >= 1.0F || var1.nextFloat() < var11) && (!var13 || var0.getTileID(var11x, var12x) == var6) && (var10 == -1 || var0.getObjectID(var11x, var12x) == var10) && (var12 || var14.canPlace(var0, var11x, var12x, 0) == null)) {
            var14.placeObject(var0, var11x, var12x, 0);
         }

      });
   }

   public static void placeRandomObjectVeinOnTile(Level var0, GameRandom var1, int var2, int var3, int var4, int var5, int var6, int var7, float var8, boolean var9) {
      placeRandomVein(var0, var1, var2, var3, var4, var5, var6, -1, 0.0F, var7, -1, var8, var9, true);
   }

   public static void placeRandomObjectVein(Level var0, GameRandom var1, int var2, int var3, int var4, int var5, int var6, float var7, boolean var8) {
      placeRandomVein(var0, var1, var2, var3, var4, var5, -1, -1, 0.0F, var6, -1, var7, var8, false);
   }

   public static ArrayList<Point> getValidTiles(Level var0, BiPredicate<Integer, Integer> var1) {
      ArrayList var2 = new ArrayList();
      iterateLevel(var0, (var2x, var3) -> {
         if (var1.test(var2x, var3)) {
            var2.add(new Point(var2x, var3));
         }

      });
      return var2;
   }

   public static int generateOnValidTiles(Level var0, GameRandom var1, int var2, BiPredicate<Integer, Integer> var3, BiPredicate<Integer, Integer> var4) {
      ArrayList var5 = getValidTiles(var0, var3);

      for(int var6 = 0; var6 < var2; ++var6) {
         if (var5.isEmpty()) {
            return var6;
         }

         int var7 = var1.nextInt(var5.size());
         Point var8 = (Point)var5.remove(var7);
         if (!var4.test(var8.x, var8.y)) {
            --var6;
         }
      }

      return var2;
   }

   public static void generateRandomPoints(Level var0, GameRandom var1, float var2, int var3, Consumer<Point> var4) {
      float var5 = (float)var0.width / 10.0F * (float)var0.height / 10.0F * var2;
      int var6 = 0;
      var6 = (int)((float)var6 + var5);
      var5 -= (float)var6;
      if (var1.getChance(var5)) {
         ++var6;
      }

      for(int var7 = 0; var7 < var6; ++var7) {
         int var8 = var1.getIntBetween(var3, var0.width - var3);
         int var9 = var1.getIntBetween(var3, var0.height - var3);
         var4.accept(new Point(var8, var9));
      }

   }

   public static void generateRandomPoints(Level var0, GameRandom var1, float var2, Consumer<Point> var3) {
      generateRandomPoints(var0, var1, var2, 5, var3);
   }

   public static void generateRandomVeins(Level var0, GameRandom var1, float var2, int var3, int var4, int var5, int var6, float var7, int var8, int var9, float var10, boolean var11, boolean var12) {
      generateRandomPoints(var0, var1, var2, (var12x) -> {
         placeRandomVein(var0, var1, var12x.x, var12x.y, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12);
      });
   }

   public static void generateRandomVeins(Level var0, GameRandom var1, float var2, int var3, int var4, PlaceFunction var5) {
      generateRandomPoints(var0, var1, var2, (var5x) -> {
         placeRandomVein(var0, var1, var5x.x, var5x.y, var3, var4, var5);
      });
   }

   public static int generateGuaranteedRandomVeins(Level var0, GameRandom var1, int var2, int var3, int var4, CanPlaceFunction var5, PlaceFunction var6) {
      byte var7 = 10;
      ArrayList var8 = new ArrayList();
      HashSet var9 = new HashSet();

      int var11;
      for(int var10 = var7; var10 < var0.width - var7; ++var10) {
         for(var11 = var7; var11 < var0.height - var7; ++var11) {
            if (var5.canPlace(var0, var10, var11)) {
               var8.add(new Point(var10, var11));
               var9.add(new Point(var10, var11));
            }
         }
      }

      HashSet var23 = new HashSet();
      var11 = 0;

      while(var11 < var2 && !var8.isEmpty()) {
         int var12 = var1.nextInt(var8.size());
         Point var13 = (Point)var8.remove(var12);
         if (!var23.contains(var13)) {
            LinkedList var14 = new LinkedList();
            var14.addLast(var13);
            HashSet var15 = new HashSet();
            int var16 = var1.getIntBetween(var3, var4);
            int var17 = 0;

            while(!var14.isEmpty()) {
               Point var18 = (Point)var14.removeFirst();
               if (var11 >= var2 || var17 >= var16) {
                  break;
               }

               var6.place(var0, var18.x, var18.y);
               ++var11;
               ++var17;
               var23.add(var18);
               List var19 = Arrays.asList(nextToGetters);
               Collections.shuffle(var19, var1);
               Iterator var20 = var19.iterator();

               while(var20.hasNext()) {
                  Point var21 = (Point)var20.next();
                  Point var22 = new Point(var18.x + var21.x, var18.y + var21.y);
                  if (var22.x >= 0 && var22.y >= 0 && var22.x < var0.width && var22.y < var0.width && var9.contains(var22) && !var23.contains(var22) && !var15.contains(var22)) {
                     if (var1.getChance(0.2F)) {
                        var14.addFirst(var22);
                     } else {
                        var14.addLast(var22);
                     }

                     var15.add(var22);
                  }
               }
            }
         }
      }

      return var11;
   }

   public static void generateRandomObjectVeinsOnTile(Level var0, GameRandom var1, float var2, int var3, int var4, int var5, int var6, float var7, boolean var8) {
      generateRandomVeins(var0, var1, var2, var3, var4, var5, -1, 0.0F, var6, -1, var7, var8, true);
   }

   public static void generateRandomObjectVeins(Level var0, GameRandom var1, float var2, int var3, int var4, int var5, float var6, boolean var7) {
      generateRandomVeins(var0, var1, var2, var3, var4, -1, -1, 0.0F, var5, -1, var6, var7, true);
   }

   public static void generateRandomSmoothVeinsL(Level var0, GameRandom var1, float var2, int var3, float var4, float var5, float var6, float var7, Consumer<LinesGeneration> var8) {
      generateRandomPoints(var0, var1, var2, (var7x) -> {
         var8.accept((new LinesGeneration(var7x.x, var7x.y)).addRandomArms(var1, var3, var4, var5, var6, var7));
      });
   }

   public static void generateRandomSmoothVeinsC(Level var0, GameRandom var1, float var2, int var3, float var4, float var5, float var6, float var7, Consumer<CellAutomaton> var8) {
      generateRandomSmoothVeinsL(var0, var1, var2, var3, var4, var5, var6, var7, (var2x) -> {
         var8.accept(var2x.doCellularAutomaton(var1));
      });
   }

   public static void generateRandomSmoothVeins(Level var0, GameRandom var1, float var2, int var3, float var4, float var5, float var6, float var7, PlaceFunction var8) {
      generateRandomSmoothVeinsC(var0, var1, var2, var3, var4, var5, var6, var7, (var2x) -> {
         var2x.forEachTile(var0, var8);
      });
   }

   public static void generateRandomSmoothTileVeins(Level var0, GameRandom var1, float var2, int var3, float var4, float var5, float var6, float var7, int var8, float var9, boolean var10) {
      GameTile var11 = TileRegistry.getTile(var8);
      generateRandomSmoothVeins(var0, var1, var2, var3, var4, var5, var6, var7, (var4x, var5x, var6x) -> {
         if (var1.getChance(var9) && (var10 || var11.canPlace(var4x, var5x, var6x) == null)) {
            var11.placeTile(var4x, var5x, var6x);
         }

      });
   }

   public static void generateRandomSmoothObjectVeins(Level var0, GameRandom var1, float var2, int var3, float var4, float var5, float var6, float var7, int var8, float var9, boolean var10) {
      GameObject var11 = ObjectRegistry.getObject(var8);
      generateRandomSmoothVeins(var0, var1, var2, var3, var4, var5, var6, var7, (var4x, var5x, var6x) -> {
         if (var1.getChance(var9) && (var10 || var11.canPlace(var4x, var5x, var6x, 0) == null)) {
            var11.placeObject(var4x, var5x, var6x, 0);
         }

      });
   }

   public static void generateFurniture(Level var0, GameRandom var1, int var2, int var3, TicketSystemList<Preset> var4, Function<TilePosition, Boolean> var5) {
      TicketSystemList var6 = new TicketSystemList(var4);

      while(!var6.isEmpty()) {
         Preset var7 = (Preset)var6.getAndRemoveRandomObject(var1);
         ArrayList var8 = new ArrayList(Arrays.asList(() -> {
            return tryPlaceFurniturePreset(PresetUtils.randomizeXMirror(var7, var1), var0, var2, var3, var5);
         }, () -> {
            return tryPlaceFurniturePreset(PresetUtils.randomizeXMirror(var7, var1).tryRotate(PresetRotation.CLOCKWISE), var0, var2, var3, var5);
         }, () -> {
            return tryPlaceFurniturePreset(PresetUtils.randomizeXMirror(var7, var1).tryRotate(PresetRotation.ANTI_CLOCKWISE), var0, var2, var3, var5);
         }, () -> {
            return tryPlaceFurniturePreset(PresetUtils.randomizeXMirror(var7, var1).tryRotate(PresetRotation.HALF_180), var0, var2, var3, var5);
         }));
         boolean var9 = false;

         while(!var8.isEmpty()) {
            int var10 = var1.nextInt(var8.size());
            if ((Boolean)((Supplier)var8.remove(var10)).get()) {
               var9 = true;
               break;
            }
         }

         if (var9) {
            break;
         }
      }

   }

   public static boolean tryPlaceFurniturePreset(Preset var0, Level var1, int var2, int var3, Function<TilePosition, Boolean> var4) {
      if (var4 != null) {
         for(int var5 = 0; var5 < var0.width; ++var5) {
            for(int var6 = 0; var6 < var0.height; ++var6) {
               if (!(Boolean)var4.apply(new TilePosition(var1, var2 + var5, var3 + var6))) {
                  return false;
               }
            }
         }
      }

      if (var0.canApplyToLevel(var1, var2, var3)) {
         var0.applyToLevel(var1, var2, var3);
         return true;
      } else {
         return false;
      }
   }

   public static void fillCellMap(Level var0, boolean[] var1, int var2, int var3) {
      GameTile var4 = null;
      if (var2 != -1) {
         var4 = TileRegistry.getTile(var2);
      }

      GameObject var5 = null;
      if (var3 != -1) {
         var5 = ObjectRegistry.getObject(var3);
      }

      for(int var6 = 0; var6 < var0.width; ++var6) {
         for(int var7 = 0; var7 < var0.height; ++var7) {
            if (var1[var6 + var7 * var0.width]) {
               if (var4 != null) {
                  var4.placeTile(var0, var6, var7);
               }

               if (var5 != null) {
                  var5.placeObject(var0, var6, var7, 0);
               }
            }
         }
      }

   }

   public static void fillMap(Level var0, GameRandom var1, int var2, int var3, float var4, int var5, int var6, float var7, boolean var8, boolean var9) {
      GameObject var10 = null;
      if (var7 > 0.0F) {
         var10 = ObjectRegistry.getObject(var5);
      }

      GameTile var11 = null;
      if (var4 > 0.0F) {
         var11 = TileRegistry.getTile(var2);
      }

      for(int var12 = 0; var12 < var0.width; ++var12) {
         for(int var13 = 0; var13 < var0.height; ++var13) {
            if (var11 != null && (var4 >= 1.0F || var1.nextFloat() < var4) && (var3 == -1 || var0.getTileID(var12, var13) == var3)) {
               var11.placeTile(var0, var12, var13);
            }

            if (var10 != null && (var7 >= 1.0F || var1.nextFloat() < var7) && (!var9 || var0.getTileID(var12, var13) == var2) && (var6 == -1 || var0.getObjectID(var12, var13) == var6) && (var8 || var10.canPlace(var0, var12, var13, 0) == null)) {
               var10.placeObject(var0, var12, var13, 0);
            }
         }
      }

   }

   public static void checkValid(Level var0) {
      int var1;
      int var2;
      for(var1 = 0; var1 < var0.width; ++var1) {
         for(var2 = 0; var2 < var0.height; ++var2) {
            if (var0.getTileID(var1, var2) != 0) {
               var0.getTile(var1, var2).tickValid(var0, var1, var2, true);
            }
         }
      }

      for(var1 = 0; var1 < var0.width; ++var1) {
         for(var2 = 0; var2 < var0.height; ++var2) {
            if (var0.getObjectID(var1, var2) != 0 && !var0.getObject(var1, var2).isValid(var0, var1, var2)) {
               var0.setObject(var1, var2, 0);
            }
         }
      }

      Iterator var3 = var0.entityManager.mobs.iterator();

      while(var3.hasNext()) {
         Mob var4 = (Mob)var3.next();
         if (var4.collidesWith(var0)) {
            var4.remove();
         }
      }

   }

   public static void collectLevelContent(Level var0, HashMap<Integer, Integer> var1, HashMap<Integer, Integer> var2, HashMap<Integer, Integer> var3) {
      for(int var4 = 0; var4 < var0.width; ++var4) {
         for(int var5 = 0; var5 < var0.height; ++var5) {
            int var6;
            if (var1 != null) {
               var6 = var0.getTileID(var4, var5);
               var1.put(var6, (Integer)var1.getOrDefault(var6, 0) + 1);
            }

            if (var2 != null) {
               var6 = var0.getObjectID(var4, var5);
               var2.put(var6, (Integer)var2.getOrDefault(var6, 0) + 1);
            }

            if (var3 != null) {
               ObjectEntity var12 = var0.entityManager.getObjectEntity(var4, var5);
               if (var12 instanceof OEInventory) {
                  Inventory var13 = ((OEInventory)var12).getInventory();

                  for(int var14 = 0; var14 < var13.getSize(); ++var14) {
                     InventoryItem var15 = var13.getItem(var14);
                     if (var15 != null) {
                        var3.put(var15.item.getID(), (Integer)var3.getOrDefault(var15.item.getID(), 1) + var15.getAmount());
                     }
                  }
               } else if (var12 instanceof TrialEntranceObjectEntity) {
                  TrialEntranceObjectEntity var7 = (TrialEntranceObjectEntity)var12;
                  Iterator var8 = var7.lootList.iterator();

                  while(var8.hasNext()) {
                     List var9 = (List)var8.next();
                     Iterator var10 = var9.iterator();

                     while(var10.hasNext()) {
                        InventoryItem var11 = (InventoryItem)var10.next();
                        if (var11 != null) {
                           var3.put(var11.item.getID(), (Integer)var3.getOrDefault(var11.item.getID(), 1) + var11.getAmount());
                        }
                     }
                  }
               }
            }
         }
      }

   }

   public static void printLevelContent(Level var0) {
      HashMap var1 = new HashMap();
      HashMap var2 = new HashMap();
      HashMap var3 = new HashMap();
      collectLevelContent(var0, var1, var2, var3);
      printLevelContent(var1, var2, var3);
   }

   public static void printLevelContent(HashMap<Integer, Integer> var0, HashMap<Integer, Integer> var1, HashMap<Integer, Integer> var2) {
      printLevelContent(var0, (var0x) -> {
         return true;
      }, var1, (var0x) -> {
         return true;
      }, var2, (var0x) -> {
         return true;
      });
   }

   public static void printLevelContent(HashMap<Integer, Integer> var0, Predicate<GameTile> var1, HashMap<Integer, Integer> var2, Predicate<GameObject> var3, HashMap<Integer, Integer> var4, Predicate<Item> var5) {
      if (var0 != null && var0.isEmpty()) {
         var0 = null;
      }

      if (var2 != null && var2.isEmpty()) {
         var2 = null;
      }

      if (var4 != null && var4.isEmpty()) {
         var4 = null;
      }

      Comparator var6 = Comparator.comparingInt(Map.Entry::getValue);
      var6 = var6.reversed();
      int var7 = var0 == null ? 0 : var0.values().stream().mapToInt((var0x) -> {
         return var0x;
      }).sum();
      int var8 = var2 == null ? 0 : var2.values().stream().mapToInt((var0x) -> {
         return var0x;
      }).sum();
      AtomicInteger var9 = new AtomicInteger(0);
      AtomicInteger var10 = new AtomicInteger(0);
      Iterator var11;
      Map.Entry var12;
      if (var0 != null) {
         var11 = var0.entrySet().iterator();

         while(var11.hasNext()) {
            var12 = (Map.Entry)var11.next();
            GameTile var13 = TileRegistry.getTile((Integer)var12.getKey());
            if (var1.test(var13)) {
               var9.updateAndGet((var1x) -> {
                  return Math.max(var13.getStringID().length(), var1x);
               });
               var10.updateAndGet((var1x) -> {
                  return Math.max(((Integer)var12.getValue()).toString().length(), var1x);
               });
            }
         }
      }

      if (var2 != null) {
         var11 = var2.entrySet().iterator();

         while(var11.hasNext()) {
            var12 = (Map.Entry)var11.next();
            GameObject var14 = ObjectRegistry.getObject((Integer)var12.getKey());
            if (var3.test(var14)) {
               var9.updateAndGet((var1x) -> {
                  return Math.max(var14.getStringID().length(), var1x);
               });
               var10.updateAndGet((var1x) -> {
                  return Math.max(((Integer)var12.getValue()).toString().length(), var1x);
               });
            }
         }
      }

      if (var4 != null) {
         var11 = var4.entrySet().iterator();

         while(var11.hasNext()) {
            var12 = (Map.Entry)var11.next();
            Item var15 = ItemRegistry.getItem((Integer)var12.getKey());
            if (var5.test(var15)) {
               var9.updateAndGet((var1x) -> {
                  return Math.max(var15.getStringID().length(), var1x);
               });
               var10.updateAndGet((var1x) -> {
                  return Math.max(((Integer)var12.getValue()).toString().length(), var1x);
               });
            }
         }
      }

      if (var0 != null) {
         if (var2 != null || var4 != null) {
            System.out.println("TILES:");
         }

         var0.entrySet().stream().filter((var1x) -> {
            return var1.test(TileRegistry.getTile((Integer)var1x.getKey()));
         }).sorted(var6).forEach((var3x) -> {
            System.out.println("\t" + GameUtils.padString(TileRegistry.getTile((Integer)var3x.getKey()).getStringID(), var9.get(), '.') + " " + GameUtils.padString(((Integer)var3x.getValue()).toString(), var10.get(), ' ') + " (" + GameMath.toDecimals((double)(Integer)var3x.getValue() / (double)var7 * 100.0, 2) + "%)");
         });
      }

      if (var2 != null) {
         if (var0 != null || var4 != null) {
            System.out.println("OBJECTS:");
         }

         var2.entrySet().stream().filter((var1x) -> {
            return var3.test(ObjectRegistry.getObject((Integer)var1x.getKey()));
         }).sorted(var6).forEach((var3x) -> {
            System.out.println("\t" + GameUtils.padString(ObjectRegistry.getObject((Integer)var3x.getKey()).getStringID(), var9.get(), '.') + " " + GameUtils.padString(((Integer)var3x.getValue()).toString(), var10.get(), ' ') + " (" + GameMath.toDecimals((double)(Integer)var3x.getValue() / (double)var8 * 100.0, 2) + "%)");
         });
      }

      if (var4 != null) {
         if (var0 != null || var2 != null) {
            System.out.println("ITEMS:");
         }

         var4.entrySet().stream().filter((var1x) -> {
            return var5.test(ItemRegistry.getItem((Integer)var1x.getKey()));
         }).sorted(var6).forEach((var2x) -> {
            System.out.println("\t" + GameUtils.padString(ItemRegistry.getItem((Integer)var2x.getKey()).getStringID(), var9.get(), '.') + " " + GameUtils.padString(((Integer)var2x.getValue()).toString(), var10.get(), ' '));
         });
      }

   }

   @FunctionalInterface
   public interface PlaceFunction {
      void place(Level var1, int var2, int var3);
   }

   @FunctionalInterface
   public interface CanPlaceFunction {
      boolean canPlace(Level var1, int var2, int var3);
   }
}
