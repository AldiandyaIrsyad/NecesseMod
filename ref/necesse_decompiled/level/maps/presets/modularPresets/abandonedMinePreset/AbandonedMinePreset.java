package necesse.level.maps.presets.modularPresets.abandonedMinePreset;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.hostile.SkeletonMinerMob;
import necesse.level.maps.Level;
import necesse.level.maps.generationModules.ModularGeneration;
import necesse.level.maps.generationModules.PlacedPreset;
import necesse.level.maps.presets.modularPresets.ModularPreset;

public class AbandonedMinePreset extends ModularPreset {
   public final int wall;
   public final int floor;
   public final int storagebox;
   public final int door;
   public final int doorOpen;
   public GameRandom random;

   public AbandonedMinePreset(int var1, int var2, int var3, int var4, GameRandom var5) {
      super(var1, var2, 7, var3, var4);
      this.random = var5;
      this.overlap = true;
      this.wall = ObjectRegistry.getObjectID("deepstonewall");
      this.floor = TileRegistry.deepStoneFloorID;
      this.storagebox = ObjectRegistry.getObjectID("storagebox");
      this.door = ObjectRegistry.getObjectID("deepstonedoor");
      this.doorOpen = ObjectRegistry.getObjectID("deepstonedooropen");
   }

   public AbandonedMinePreset(int var1, int var2, GameRandom var3) {
      this(var1, var2, 5, 1, var3);
   }

   public AbandonedMinePreset(int var1, int var2) {
      this(var1, var2, (GameRandom)null);
   }

   protected AbandonedMinePreset newModularObject(int var1, int var2, int var3, int var4, int var5) {
      return new AbandonedMinePreset(var1, var2, var4, var5, this.random);
   }

   protected void fillOpeningRealSuper(Level var1, int var2, int var3, int var4, int var5, int var6) {
      super.fillOpeningReal(var1, var2, var3, var4, var5, var6);
   }

   public void fillOpeningReal(Level var1, int var2, int var3, int var4, int var5, int var6) {
      if (var4 == 0) {
         var1.setObject(var2 + this.openingSize / 2, var3, this.random.nextBoolean() ? this.door : this.doorOpen, var4);
      } else if (var4 == 1) {
         var1.setObject(var2, var3 + this.openingSize / 2, this.random.nextBoolean() ? this.door : this.doorOpen, var4);
      } else if (var4 == 2) {
         var1.setObject(var2 + this.openingSize / 2, var3, this.random.nextBoolean() ? this.door : this.doorOpen, var4);
      } else if (var4 == 3) {
         var1.setObject(var2, var3 + this.openingSize / 2, this.random.nextBoolean() ? this.door : this.doorOpen, var4);
      }

   }

   public void addSkeletonMiner(int var1, int var2, GameRandom var3, float var4) {
      this.addCustomApply(var1, var2, 0, (var2x, var3x, var4x, var5) -> {
         if (var3.getChance(var4)) {
            SkeletonMinerMob var6 = new SkeletonMinerMob();
            var2x.entityManager.addMob(var6, (float)(var3x * 32 + 16), (float)(var4x * 32 + 16));
            var6.canDespawn = false;
            return (var1, var2, var3xx) -> {
               var6.remove();
            };
         } else {
            return null;
         }
      });
   }

   public static Rectangle generateAbandonedMineOnLevel(Level var0, GameRandom var1, List<Rectangle> var2) {
      ModularGeneration var3 = new ModularGeneration(var0, var1.getIntBetween(6, 10), var1.getIntBetween(6, 10), 6, 5, 1) {
         public Point getStartCell() {
            return new Point(this.cellsWidth / 2, this.cellsHeight / 2);
         }
      };
      int var4 = 10 + var1.nextInt(var0.width - var3.cellRes * var3.cellsWidth - 20);
      int var5 = 10 + var1.nextInt(var0.height - var3.cellRes * var3.cellsHeight - 20);
      Rectangle var6 = new Rectangle(var4, var5, var3.cellRes * var3.cellsWidth, var3.cellRes * var3.cellsHeight);
      if (var2 != null && var2.stream().anyMatch((var1x) -> {
         return var1x.intersects(var6);
      })) {
         return null;
      } else {
         AbandonedMineHallwayPreset var7 = new AbandonedMineHallwayPreset(var3.random, true, true, true, true);
         var3.setStartPreset(var7);
         var3.initGeneration(var4, var5);
         Point var8 = var3.getStartCell();
         float var9 = 0.1F;
         ArrayList var10 = new ArrayList();
         ArrayList var11 = new ArrayList();

         int var12;
         for(var12 = 0; var12 < 4; ++var12) {
            var10.add(new HallwayCell(new Point(var8), var12));
         }

         HallwayCell var13;
         for(var12 = 0; !var10.isEmpty(); var11.add(var13)) {
            var13 = (HallwayCell)var10.remove(var3.random.nextInt(var10.size()));
            Point var14 = var13.cell;

            while(true) {
               Point var15 = var3.getNextCell(var14, var13.dir);
               Point var16 = var3.getNextCell(var15, var13.dir);
               if (var11.stream().anyMatch((var1x) -> {
                  return var1x.cell.equals(var16);
               }) || var11.stream().anyMatch((var1x) -> {
                  return var1x.cell.equals(var15);
               }) || var15.x < 0 || var15.x >= var3.cellsWidth || var15.y < 0 || var15.y >= var3.cellsHeight) {
                  break;
               }

               int var17;
               if (var13.dir != 0 && var13.dir != 2) {
                  var17 = Math.abs(var15.x - var8.x);
                  if (var17 % 2 == 0) {
                     if (var3.random.getChance(var9)) {
                        var10.add(new HallwayCell(var15, 0));
                     }

                     if (var3.random.getChance(var9)) {
                        var10.add(new HallwayCell(var15, 2));
                     }
                  }
               } else {
                  var17 = Math.abs(var15.y - var8.y);
                  if (var17 % 2 == 0) {
                     if (var3.random.getChance(var9)) {
                        var10.add(new HallwayCell(var15, 1));
                     }

                     if (var3.random.getChance(var9)) {
                        var10.add(new HallwayCell(var15, 3));
                     }
                  }
               }

               var3.applyPreset(var7, var15, false, false, var4, var5, var14);
               ++var12;
               var14 = var15;
            }
         }

         int[] var18 = new int[]{TileRegistry.deepStoneBrickFloorID};
         int var20 = var18.length;

         for(int var22 = 0; var22 < var20; ++var22) {
            int var23 = var18[var22];
            var3.addPreset(new AbandonedMineBedroomPreset(var3.random, var23), 100);
            var3.addPreset(new AbandonedMineBedroomRPreset(var3.random, var23), 100);
            var3.addPreset(new AbandonedMineBlacksmithPreset(var3.random, var23), 100);
            var3.addPreset(new AbandonedMineBlacksmithRPreset(var3.random, var23), 100);
            var3.addPreset(new AbandonedMineCrateRoomPreset(var3.random, var23), 150);
            var3.addPreset(new AbandonedMineDiningPreset(var3.random, var23), 100);
            var3.addPreset(new AbandonedMineDiningRPreset(var3.random, var23), 100);
            var3.addPreset(new AbandonedMineLibraryPreset(var3.random, var23), 100);
            var3.addPreset(new AbandonedMineLibraryRPreset(var3.random, var23), 100);
         }

         var3.tickGeneration(var4, var5, (int)((float)var12 * 1.5F));
         Iterator var19 = var3.getPlacedPresets().iterator();

         while(var19.hasNext()) {
            PlacedPreset var21 = (PlacedPreset)var19.next();
            if (var21.preset instanceof AbandonedMineHallwayPreset) {
               fixHallway(var0, var3, var4, var5, var21);
            }
         }

         var3.endGeneration();
         return var6;
      }
   }

   private static void fixHallway(Level var0, ModularGeneration var1, int var2, int var3, PlacedPreset var4) {
      for(int var5 = 0; var5 < 4; ++var5) {
         Point var6 = var1.getNextCell(var4.cell, var5);
         PlacedPreset var7 = var1.getPlacedPreset(var6);
         if (var7 == null) {
            var4.preset.closeLevel(var0, 0, 0, var1.getCellRealX(var4.cell.x) + var2, var1.getCellRealY(var4.cell.y) + var3, var5, var1.cellRes);
         }
      }

   }

   // $FF: synthetic method
   // $FF: bridge method
   protected ModularPreset newModularObject(int var1, int var2, int var3, int var4, int var5) {
      return this.newModularObject(var1, var2, var3, var4, var5);
   }

   private static class HallwayCell {
      public final Point cell;
      public final int dir;

      public HallwayCell(Point var1, int var2) {
         this.cell = var1;
         this.dir = var2;
      }
   }
}
