package necesse.level.maps.liquidManager;

import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import necesse.engine.tickManager.Performance;
import necesse.engine.util.ObjectValue;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;

public class LiquidManager {
   public static final byte maxHeight = 10;
   public static final byte minDepth = -10;
   public static final byte saltWaterDepth = -10;
   private static int range = Math.max(10, Math.abs(-10));
   private byte[][] heights;
   private boolean[][] shores;
   public boolean[][] saltWater;
   private final Level level;
   private boolean calculatedShores;
   private boolean calculatedHeights;
   private static Point[] crossPoints = new Point[]{new Point(0, -1), new Point(-1, 0), new Point(1, 0), new Point(0, 1)};

   public LiquidManager(Level var1) {
      this.level = var1;
      this.calculatedShores = false;
      this.calculatedHeights = false;
      this.heights = new byte[var1.width][var1.height];
      this.shores = new boolean[var1.width][var1.height];
      this.saltWater = new boolean[var1.width][var1.height];
   }

   public int getHeight(int var1, int var2) {
      return var1 >= 0 && var1 < this.level.width && var2 >= 0 && var2 < this.level.height ? this.heights[var1][var2] : -10;
   }

   public boolean isSaltWater(int var1, int var2) {
      return var1 >= 0 && var1 < this.level.width && var2 >= 0 && var2 < this.level.height ? this.saltWater[var1][var2] : true;
   }

   public boolean isFreshWater(int var1, int var2) {
      return !this.isSaltWater(var1, var2);
   }

   public boolean isShore(int var1, int var2) {
      return var1 >= 0 && var1 < this.level.width && var2 >= 0 && var2 < this.level.height && this.shores[var1][var2];
   }

   public void calculateLevel() {
      this.calculateShores();
      this.calculateHeights();
   }

   public void updateLevel(int var1, int var2, int var3, int var4) {
      this.updateHeights(var1, var2, var3, var4, range);
      this.updateAdjacentShores(var1, var2, var3, var4);
   }

   public void calculateHeights() {
      this.updateHeights(0, 0, this.level.width - 1, this.level.height - 1);
      this.calculatedHeights = true;
   }

   public void updateHeights(int var1, int var2) {
      this.updateHeights(var1, var2, range);
   }

   public void updateHeights(int var1, int var2, int var3) {
      this.updateHeights(var1 - var3, var2 - var3, var1 + var3, var2 + var3);
   }

   public void updateHeights(int var1, int var2, int var3, int var4, int var5) {
      this.updateHeights(var1 - var5, var2 - var5, var3 + var5, var4 + var5);
   }

   public void updateHeights(int var1, int var2, int var3, int var4) {
      if (var1 < 0) {
         var1 = 0;
      }

      if (var3 >= this.level.width) {
         var3 = this.level.width - 1;
      }

      if (var2 < 0) {
         var2 = 0;
      }

      if (var4 >= this.level.height) {
         var4 = this.level.height - 1;
      }

      Performance.recordConstant(this.level.debugLoadingPerformance, "updateHeights", () -> {
         LinkedList var5 = new LinkedList();
         LinkedList var6 = new LinkedList();

         for(int var7 = var1; var7 <= var3; ++var7) {
            for(int var8 = var2; var8 <= var4; ++var8) {
               GameTile var9 = this.level.getTile(var7, var8);
               if (var9.isLiquid) {
                  if (var7 - 1 >= 0 && !this.level.getTile(var7 - 1, var8).isLiquid || var7 + 1 < this.level.width && !this.level.getTile(var7 + 1, var8).isLiquid || var8 - 1 >= 0 && !this.level.getTile(var7, var8 - 1).isLiquid || var8 + 1 < this.level.height && !this.level.getTile(var7, var8 + 1).isLiquid) {
                     this.heights[var7][var8] = -1;
                     var5.add(new Point(var7, var8));
                  } else if (var7 != var1 && var7 != var3 && var8 != var2 && var8 != var4) {
                     this.heights[var7][var8] = -10;
                  } else if (var7 != 0 && var7 != this.level.width - 1 && var8 != 0 && var8 != this.level.height - 1) {
                     var5.add(new Point(var7, var8));
                  } else {
                     this.heights[var7][var8] = -10;
                  }
               } else {
                  this.saltWater[var7][var8] = false;
                  if ((var7 - 1 < 0 || !this.level.getTile(var7 - 1, var8).isLiquid) && (var7 + 1 >= this.level.width || !this.level.getTile(var7 + 1, var8).isLiquid) && (var8 - 1 < 0 || !this.level.getTile(var7, var8 - 1).isLiquid) && (var8 + 1 >= this.level.height || !this.level.getTile(var7, var8 + 1).isLiquid)) {
                     if (var7 != var1 && var7 != var3 && var8 != var2 && var8 != var4) {
                        this.heights[var7][var8] = 10;
                     } else if (var7 != 0 && var7 != this.level.width - 1 && var8 != 0 && var8 != this.level.height - 1) {
                        var6.add(new Point(var7, var8));
                     }
                  } else {
                     this.heights[var7][var8] = 0;
                     var6.add(new Point(var7, var8));
                  }
               }
            }
         }

         Performance.recordConstant(this.level.debugLoadingPerformance, "depth", () -> {
            this.updateDepths(var5);
         });
         Performance.recordConstant(this.level.debugLoadingPerformance, "height", () -> {
            this.updateHeights(var6);
         });
         if (!this.level.isCave) {
            Performance.recordConstant(this.level.debugLoadingPerformance, "water", () -> {
               int var5 = Math.abs(-10);
               this.updateSaltWater(var1 - var5, var2 - var5, var3 + var5, var4 + var5);
            });
         }

      });
   }

   private void updateSaltWater(int var1, int var2, int var3, int var4) {
      if (var1 < 0) {
         var1 = 0;
      }

      if (var3 >= this.level.width) {
         var3 = this.level.width - 1;
      }

      if (var2 < 0) {
         var2 = 0;
      }

      if (var4 >= this.level.height) {
         var4 = this.level.height - 1;
      }

      for(int var5 = var1; var5 <= var3; ++var5) {
         for(int var6 = var2; var6 <= var4; ++var6) {
            GameTile var7 = this.level.getTile(var5, var6);
            if (var7.isLiquid) {
               this.calculateSaltWater(var5, var6);
            }
         }
      }

   }

   private void updateDepths(LinkedList<Point> var1) {
      label16:
      while(true) {
         if (!var1.isEmpty()) {
            Point var2 = (Point)var1.removeFirst();
            byte var3 = this.heights[var2.x][var2.y];
            int var4 = Math.max(var3 - 1, -10);
            Point[] var5 = crossPoints;
            int var6 = var5.length;
            int var7 = 0;

            while(true) {
               if (var7 >= var6) {
                  continue label16;
               }

               Point var8 = var5[var7];
               this.updateDepth(var2.x + var8.x, var2.y + var8.y, var4, var1);
               ++var7;
            }
         }

         return;
      }
   }

   private void updateDepth(int var1, int var2, int var3, LinkedList<Point> var4) {
      if (var1 >= 0 && var1 < this.level.width && var2 >= 0 && var2 < this.level.height) {
         if (this.level.getTile(var1, var2).isLiquid) {
            if (this.heights[var1][var2] < var3) {
               this.heights[var1][var2] = (byte)var3;
               var4.add(new Point(var1, var2));
            }

         }
      }
   }

   private void calculateSaltWater(int var1, int var2) {
      byte var3 = this.heights[var1][var2];
      int var4;
      int var5;
      int var6;
      int var7;
      int var8;
      if (var3 <= -10) {
         this.saltWater[var1][var2] = true;
         var4 = Math.abs(-10);

         for(var5 = -var4; var5 <= var4; ++var5) {
            var6 = var1 + var5;
            var7 = var2 - (var4 - Math.abs(var5));
            var8 = var2 + (var4 - Math.abs(var5));
            if (var7 == var8) {
               if (var6 >= 0 && var6 < this.level.width && var7 >= 0 && var7 < this.level.height && this.heights[var6][var7] < 0) {
                  this.saltWater[var6][var7] = true;
               }
            } else if (var6 >= 0 && var6 < this.level.width) {
               if (var7 >= 0 && var7 < this.level.height && this.heights[var6][var7] < 0) {
                  this.saltWater[var6][var7] = true;
               }

               if (var8 >= 0 && var8 < this.level.height && this.heights[var6][var8] < 0) {
                  this.saltWater[var6][var8] = true;
               }
            }
         }
      } else if (var3 < 0) {
         var4 = Math.abs(-10);

         for(var5 = -var4; var5 <= var4; ++var5) {
            var6 = var1 + var5;
            if (var6 >= 0 && var6 < this.level.width) {
               var7 = Math.max(var2 - (var4 - Math.abs(var5)), 0);
               var8 = Math.min(var2 + (var4 - Math.abs(var5)), this.level.height - 1);

               for(int var9 = var7; var9 <= var8; ++var9) {
                  if (this.heights[var6][var9] <= -10) {
                     this.saltWater[var1][var2] = true;
                     return;
                  }
               }
            }
         }

         this.saltWater[var1][var2] = false;
      }

   }

   private void updateHeights(LinkedList<Point> var1) {
      label16:
      while(true) {
         if (!var1.isEmpty()) {
            Point var2 = (Point)var1.removeFirst();
            byte var3 = this.heights[var2.x][var2.y];
            int var4 = Math.min(var3 + 1, 10);
            Point[] var5 = crossPoints;
            int var6 = var5.length;
            int var7 = 0;

            while(true) {
               if (var7 >= var6) {
                  continue label16;
               }

               Point var8 = var5[var7];
               this.updateHeight(var2.x + var8.x, var2.y + var8.y, var4, var1);
               ++var7;
            }
         }

         return;
      }
   }

   private void updateHeight(int var1, int var2, int var3, LinkedList<Point> var4) {
      if (var1 >= 0 && var1 < this.level.width && var2 >= 0 && var2 < this.level.height) {
         if (!this.level.getTile(var1, var2).isLiquid) {
            if (this.heights[var1][var2] > var3) {
               this.heights[var1][var2] = (byte)var3;
               var4.add(new Point(var1, var2));
            }

         }
      }
   }

   public void calculateShores() {
      this.updateShores(0, 0, this.level.width - 1, this.level.height - 1);
      this.calculatedShores = true;
   }

   public void updateAdjacentShores(int var1, int var2, int var3, int var4) {
      if (var1 < 0) {
         var1 = 0;
      }

      if (var3 >= this.level.width) {
         var3 = this.level.width - 1;
      }

      if (var2 < 0) {
         var2 = 0;
      }

      if (var4 >= this.level.height) {
         var4 = this.level.height - 1;
      }

      for(int var5 = var1; var5 <= var3; ++var5) {
         for(int var6 = var2; var6 <= var4; ++var6) {
            this.updateAdjacentShores(var5, var6);
         }
      }

   }

   public void updateAdjacentShores(int var1, int var2) {
      for(int var3 = var1 - 1; var3 <= var1 + 1; ++var3) {
         for(int var4 = var2 - 1; var4 <= var2 + 1; ++var4) {
            this.updateShore(var3, var4);
         }
      }

   }

   public void updateShores(int var1, int var2, int var3, int var4) {
      if (var1 < 0) {
         var1 = 0;
      }

      if (var3 >= this.level.width) {
         var3 = this.level.width - 1;
      }

      if (var2 < 0) {
         var2 = 0;
      }

      if (var4 >= this.level.height) {
         var4 = this.level.height - 1;
      }

      for(int var5 = var1; var5 <= var3; ++var5) {
         for(int var6 = var2; var6 <= var4; ++var6) {
            this.updateShore(var5, var6);
         }
      }

   }

   public boolean updateShore(int var1, int var2) {
      if (var1 >= 0 && var1 < this.level.width && var2 >= 0 && var2 < this.level.height) {
         this.shores[var1][var2] = false;
         if (!this.level.getTile(var1, var2).isLiquid) {
            GameTile[] var3 = this.level.getAdjacentTiles(var1, var2);
            GameTile[] var4 = var3;
            int var5 = var3.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               GameTile var7 = var4[var6];
               if (var7.isLiquid) {
                  this.shores[var1][var2] = true;
                  break;
               }
            }
         }

         return this.shores[var1][var2];
      } else {
         return false;
      }
   }

   public void tileUpdated(int var1, int var2, GameTile var3, GameTile var4) {
      if (var3.isLiquid != var4.isLiquid) {
         if (this.calculatedShores) {
            this.updateAdjacentShores(var1, var2);
         }

         if (this.calculatedHeights) {
            this.updateHeights(var1, var2);
         }
      }

   }

   public ClosestHeightResult findClosestHeightTile(int var1, int var2, int var3, Predicate<Point> var4) {
      return this.findClosestHeightTile(var1, var2, var3, 0, var4);
   }

   public ClosestHeightResult findClosestHeightTile(int var1, int var2, int var3, int var4, Predicate<Point> var5) {
      int var6 = this.level.liquidManager.getHeight(var1, var2);
      if (var6 == var3) {
         return new ClosestHeightResult(var1, var2, new Point(var1, var2), new Point(var1, var2), new HashSet(), new LinkedList());
      } else {
         HashMap var7 = new HashMap();
         HashMap var8 = new HashMap();
         LinkedList var9 = new LinkedList();
         NextHeightTile var10 = new NextHeightTile(var1, var2, var6, 0);
         var9.add(var10);
         var8.put(var10.x * this.level.width + var10.y, var10);
         AtomicReference var11 = new AtomicReference(new ObjectValue(new Point(var1, var2), var6));

         while(!var9.isEmpty()) {
            NextHeightTile var12 = (NextHeightTile)var9.removeLast();
            var7.put(var12.x * this.level.width + var12.y, var12);
            Point[] var13 = crossPoints;
            int var14 = var13.length;

            for(int var15 = 0; var15 < var14; ++var15) {
               Point var16 = var13[var15];
               Point var17 = this.checkNextHeight(var12.x + var16.x, var12.y + var16.y, var7, var9, var8, var12, var4, var3, var11, var5);
               if (var17 != null) {
                  return new ClosestHeightResult(var1, var2, var17, var17, var7.values(), var9);
               }
            }
         }

         return new ClosestHeightResult(var1, var2, (Point)((ObjectValue)var11.get()).object, (Point)null, var7.values(), var9);
      }
   }

   private Point checkNextHeight(int var1, int var2, HashMap<Integer, NextHeightTile> var3, LinkedList<NextHeightTile> var4, HashMap<Integer, NextHeightTile> var5, NextHeightTile var6, int var7, int var8, AtomicReference<ObjectValue<Point, Integer>> var9, Predicate<Point> var10) {
      if (var1 >= 0 && var2 >= 0 && var1 < this.level.width && var2 < this.level.height) {
         int var11 = var1 * this.level.width + var2;
         NextHeightTile var12 = (NextHeightTile)var3.get(var11);
         if (var12 == null) {
            int var13 = this.level.liquidManager.getHeight(var1, var2);
            NextHeightTile var14 = new NextHeightTile(var1, var2, var13, var13 == var6.height ? var6.sameHeightTraveled + 1 : 0);
            if (var13 == var8 && var10.test(var14)) {
               return var14;
            }

            boolean var15;
            label94: {
               var15 = var8 < var6.height;
               if (var15) {
                  if (var13 < var6.height) {
                     break label94;
                  }
               } else if (var13 > var6.height) {
                  break label94;
               }

               if (var13 == var6.height && var6.sameHeightTraveled + 1 <= var7) {
                  label53: {
                     if (var15) {
                        if (var13 >= (Integer)((ObjectValue)var9.get()).value) {
                           break label53;
                        }
                     } else if (var13 <= (Integer)((ObjectValue)var9.get()).value) {
                        break label53;
                     }

                     if (var10.test(var14)) {
                        var9.set(new ObjectValue(var14, var13));
                     }
                  }

                  var5.compute(var11, (var2x, var3x) -> {
                     if (var3x == null) {
                        var4.addFirst(var14);
                        return var14;
                     } else {
                        var3x.sameHeightTraveled = Math.min(var3x.sameHeightTraveled, var14.sameHeightTraveled);
                        return var3x;
                     }
                  });
               }

               return null;
            }

            label86: {
               if (var15) {
                  if (var13 <= var8) {
                     break label86;
                  }
               } else if (var13 >= var8) {
                  break label86;
               }

               if (var15) {
                  if (var13 >= (Integer)((ObjectValue)var9.get()).value) {
                     break label86;
                  }
               } else if (var13 <= (Integer)((ObjectValue)var9.get()).value) {
                  break label86;
               }

               if (var10.test(var14)) {
                  var9.set(new ObjectValue(var14, var13));
               }
            }

            var5.compute(var11, (var2x, var3x) -> {
               if (var3x == null) {
                  var4.addLast(var14);
                  return var14;
               } else {
                  var3x.sameHeightTraveled = Math.min(var3x.sameHeightTraveled, var14.sameHeightTraveled);
                  return var3x;
               }
            });
         }

         return null;
      } else {
         return null;
      }
   }
}
