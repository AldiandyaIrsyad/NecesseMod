package necesse.level.maps.generationModules;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.level.maps.Level;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.modularPresets.ModularPreset;

public class ModularGeneration {
   Level level;
   public GameRandom random;
   private ArrayList<ModularPreset> presets;
   private HashMap<ModularPreset, Integer> presetUsage;
   private HashMap<ModularPreset, Integer> presetMaxUsage;
   private ArrayList<ModularPreset> fillPresets;
   private HashMap<ModularPreset, Integer> fillPresetUsage;
   private HashMap<ModularPreset, Integer> fillPresetMaxUsage;
   protected ModularPreset startPreset;
   public final int cellsWidth;
   public final int cellsHeight;
   public final int cellRes;
   public final int openingSize;
   public final int openingDepth;
   private ArrayList<Point> openCells;
   private HashMap<Point, PlacedPreset> placedPresets;
   public boolean allowRoundTrips;
   private int traceStart;
   public boolean printDebug;

   public ModularGeneration(Level var1, int var2, int var3, int var4) {
      this(var1, getCellsSize(var1.width, var2), getCellsSize(var1.height, var2), var2, var3, var4);
   }

   public ModularGeneration(Level var1, int var2, int var3, int var4, int var5, int var6) {
      this.printDebug = false;
      this.traceStart = Thread.currentThread().getStackTrace().length;
      this.level = var1;
      this.cellsWidth = var2;
      this.cellsHeight = var3;
      this.cellRes = var4;
      this.openingSize = var5;
      this.openingDepth = var6;
      this.random = new GameRandom(var1.getSeed());
      this.presets = new ArrayList();
      this.placedPresets = new HashMap();
      this.presetUsage = new HashMap();
      this.presetMaxUsage = new HashMap();
      this.fillPresets = new ArrayList();
      this.fillPresetUsage = new HashMap();
      this.fillPresetMaxUsage = new HashMap();
      this.allowRoundTrips = true;
   }

   public static int getCellsSize(int var0, int var1) {
      return (var0 - 10) / var1;
   }

   public void generateModularGeneration() {
      this.generateModularGeneration(0, 0);
   }

   public void generateModularGeneration(int var1, int var2) {
      this.initGeneration(var1, var2);

      while(this.canTickGeneration()) {
         this.tickGeneration(var1, var2);
      }

      this.endGeneration();
   }

   public void generateModularGeneration(int var1, int var2, int var3) {
      this.initGeneration(var1, var2);
      this.tickGeneration(var1, var2, var3);
      this.endGeneration();
   }

   public boolean canTickGeneration() {
      return this.openCells.size() != 0;
   }

   public Point initGeneration(int var1, int var2) {
      this.placedPresets.clear();
      this.random = new GameRandom(this.level.getSeed(), false);
      this.openCells = new ArrayList();
      if (this.startPreset == null) {
         throw new NullPointerException("Start preset cannot be null");
      } else {
         Point var3 = this.getStartCell();
         this.debug("Startcell: " + var3.x + ", " + var3.y);
         this.applyPreset(this.startPreset, var3, false, false, var1, var2, (Point)null);
         return var3;
      }
   }

   public void tickGeneration(int var1, int var2, int var3) {
      if (var3 < 0) {
         while(this.canTickGeneration()) {
            this.tickGeneration(var1, var2);
         }
      } else {
         for(int var4 = 0; var4 < var3 && this.canTickGeneration(); ++var4) {
            this.tickGeneration(var1, var2);
         }
      }

   }

   public boolean canTickFillGeneration() {
      return this.fillPresets.size() != 0;
   }

   public void tickFillGeneration(int var1, int var2, int var3) {
      if (var3 < 0) {
         while(this.canTickFillGeneration()) {
            this.tickFillGeneration(var1, var2);
         }
      } else {
         for(int var4 = 0; var4 < var3 && this.canTickFillGeneration(); ++var4) {
            this.tickFillGeneration(var1, var2);
         }
      }

   }

   public void endGeneration() {
      this.presets.clear();
      this.presetUsage.clear();
      this.presetMaxUsage.clear();
      this.fillPresets.clear();
      this.fillPresetUsage.clear();
      this.fillPresetMaxUsage.clear();
      this.startPreset = null;
   }

   public void tickFillGeneration(int var1, int var2) {
      if (this.canTickFillGeneration()) {
         this.debug("Ticking fill with pool of " + this.fillPresets.size() + " presets.");
         ModularPreset var3 = (ModularPreset)this.fillPresets.get(this.random.nextInt(this.fillPresets.size()));
         this.debug("Checking random fill preset " + var3.getClass().getSimpleName());
         if ((Integer)this.fillPresetMaxUsage.get(var3) > 0 && (Integer)this.fillPresetUsage.get(var3) >= (Integer)this.fillPresetMaxUsage.get(var3)) {
            this.removeAllFillPresets(var3);
            this.debug("Preset hit max usage.");
            this.tickFillGeneration(var1, var2);
         } else {
            int var4 = this.cellsWidth - var3.width;
            int var5 = this.cellsHeight - var3.height;
            if (var4 >= 1 && var5 >= 1) {
               this.debug("Found random fill preset " + var3.getClass().getSimpleName());
               boolean var6 = false;
               boolean var7 = this.random.nextBoolean();
               boolean var8 = this.random.nextBoolean();
               int var9 = Math.abs(this.random.nextInt());
               int var10 = Math.abs(this.random.nextInt());

               for(int var11 = 0; var11 < this.cellsWidth - var3.width; ++var11) {
                  int var12 = (var9 + var11) % var4;

                  for(int var13 = 0; var13 < this.cellsWidth - var3.height; ++var13) {
                     int var14 = (var10 + var13) % var5;
                     Point var15 = new Point(var12, var14);
                     if (this.applyFillPreset(var3, var15, var7, var8, var1, var2)) {
                        this.debug("Found place location for " + var3.getClass().getSimpleName() + " at (" + var15.x + ", " + var15.y + ")");
                        this.fillPresetUsage.put(var3, (Integer)this.fillPresetUsage.get(var3) + 1);
                        var6 = true;
                        break;
                     }
                  }

                  if (var6) {
                     break;
                  }
               }

               if (!var6) {
                  this.debug("Could not find any location for " + var3.getClass().getSimpleName());
                  this.removeAllFillPresets(var3);
               }

            } else {
               this.removeAllFillPresets(var3);
               this.debug("Preset too big for the grid.");
               this.tickFillGeneration(var1, var2);
            }
         }
      }
   }

   private void removeAllFillPresets(ModularPreset var1) {
      while(this.fillPresets.remove(var1)) {
      }

   }

   public void tickGeneration(int var1, int var2) {
      if (this.openCells.size() != 0) {
         Point var3 = (Point)this.openCells.get(this.openCells.size() - 1);
         this.debug("Current cell: (" + var3.x + ", " + var3.y + ") out of " + this.openCells.size());
         boolean var4 = false;
         boolean var5 = true;
         int var6 = Math.abs(this.random.nextInt(Integer.MAX_VALUE) % Math.max(this.presets.size(), 1));
         boolean var7 = true;

         for(int var8 = 0; var8 < this.presets.size(); ++var8) {
            int var9 = (var6 + var8) % this.presets.size();
            ModularPreset var10 = (ModularPreset)this.presets.get(var9);
            if ((Integer)this.presetMaxUsage.get(var10) > 0 && (Integer)this.presetUsage.get(var10) >= (Integer)this.presetMaxUsage.get(var10)) {
               while(this.presets.remove(var10)) {
               }

               var8 = 0;
            } else {
               int var11;
               if (!var7) {
                  var11 = (var6 + var8 - 1) % this.presets.size();
                  if (var10 == this.presets.get(var11)) {
                     continue;
                  }
               }

               var7 = false;
               this.debug("Checking preset " + var10.getClass().getSimpleName());
               var11 = this.random.nextInt(4);

               for(int var12 = 0; var12 < 4; ++var12) {
                  int var13 = (var12 + var11) % 4;
                  this.debug("Checking dir " + var13);
                  Point var14 = this.getNextCell(var3, var13);
                  if (!this.placedPresets.containsKey(var14) && this.isCellOpen(var3, var13)) {
                     this.debug("Found opening: (" + var14.x + ", " + var14.y + ")");
                     int var15 = var10.getRandomOpenDir(this.random.nextInt(), var13);
                     int var16 = var10.getRandomOpenDir(this.random.nextInt(), var13 + 2);
                     if (var15 != -1 || var16 != -1) {
                        this.debug("Found opening in preset " + var15 + ", " + var16);
                        Point var17;
                        boolean var18;
                        if (var13 != 0 && var13 != 2) {
                           if (var13 == 1 || var13 == 3) {
                              var17 = new Point(var14.x, var14.y);
                              var18 = var15 != -1;
                              if (var18 && var16 != -1) {
                                 var18 = this.random.nextBoolean();
                              }

                              if (var13 == 3) {
                                 var17.x -= var10.sectionWidth - 1;
                              }

                              var17.y -= !var18 ? var16 : var15;
                              if (this.applyPreset(var10, var17, var18, false, var1, var2, var3)) {
                                 this.getPlacedPreset(var14).openDirs(this.level, var1 + 5, var2 + 5, this.random);
                                 var5 = false;
                                 var4 = true;
                              }
                           }
                        } else {
                           var17 = new Point(var14.x, var14.y);
                           var18 = var15 != -1;
                           if (var18 && var16 != -1) {
                              var18 = this.random.nextBoolean();
                           }

                           if (var13 == 0) {
                              var17.y -= var10.sectionHeight - 1;
                           }

                           var17.x -= !var18 ? var16 : var15;
                           if (this.applyPreset(var10, var17, false, var18, var1, var2, var3)) {
                              this.getPlacedPreset(var14).openDirs(this.level, var1 + 5, var2 + 5, this.random);
                              var5 = false;
                              var4 = true;
                           }
                        }
                     }

                     if (var4) {
                        break;
                     }
                  }
               }

               if (var4) {
                  break;
               }
            }
         }

         if (var5) {
            this.debug("Closed cell: (" + var3.x + ", " + var3.y + ")");
            this.openCells.remove(var3);
         }

      }
   }

   public void openCell(Point var1) {
      if (!this.openCells.contains(var1)) {
         this.openCells.add(var1);
      }
   }

   public void removePlacedPreset(PlacedPreset var1) {
      this.placedPresets.remove(var1.cell);
   }

   public final void addPreset(ModularPreset var1, int var2) {
      this.addPreset(var1, var2, -1);
   }

   public void addPreset(ModularPreset var1, int var2, int var3) {
      for(int var4 = 0; var4 < var2; ++var4) {
         this.presets.add(var1);
      }

      this.presetUsage.put(var1, 0);
      this.presetMaxUsage.put(var1, var3);
   }

   public void removeAllPresets(ModularPreset var1) {
      while(this.presets.remove(var1)) {
      }

   }

   public final void addFillPreset(ModularPreset var1, int var2) {
      this.addFillPreset(var1, var2, -1);
   }

   public void addFillPreset(ModularPreset var1, int var2, int var3) {
      for(int var4 = 0; var4 < var2; ++var4) {
         this.fillPresets.add(var1);
      }

      this.fillPresetUsage.put(var1, 0);
      this.fillPresetMaxUsage.put(var1, var3);
   }

   public void clearPresets() {
      this.presets.clear();
   }

   public void setStartPreset(ModularPreset var1) {
      this.startPreset = var1;
   }

   public boolean applyPreset(ModularPreset var1, Point var2, boolean var3, boolean var4, int var5, int var6, Point var7) {
      return this.applyPreset(var1, var2, var3, var4, var5, var6, var7, true);
   }

   public boolean applyPreset(ModularPreset var1, Point var2, boolean var3, boolean var4, int var5, int var6, Point var7, boolean var8) {
      this.debug("Attempting to apply preset " + var1.getClass().getSimpleName() + " to (" + var2.x + ", " + var2.y + ")");
      if (!var1.canPlace(this.level, this.getCellRealX(var2.x) + var5, this.getCellRealY(var2.y) + var6)) {
         this.debug("Preset would not allow to be there");
         return false;
      } else if (this.isCellsOutOfMap(var1, var2)) {
         this.debug("Preset out of map");
         return false;
      } else if (this.isCellsOccupied(var1, var2)) {
         this.debug("Cells occupied");
         return false;
      } else if (!this.canApplyPreset(var1, var2, var3, var4, var7)) {
         this.debug("Level did not allow preset to be placed");
         return false;
      } else {
         this.debug("Applied preset " + var1.getClass().getSimpleName() + " to (" + var2.x + ", " + var2.y + ")");
         Object var9 = var1;
         if (var3) {
            var9 = var1.tryMirrorX();
         }

         if (var4) {
            var9 = ((Preset)var9).tryMirrorY();
         }

         Preset var10 = Preset.copyFromLevel(this.level, this.getCellRealX(var2.x) + var5, this.getCellRealY(var2.y) + var6, var1.width, var1.height);
         ((Preset)var9).applyToLevel(this.level, this.getCellRealX(var2.x) + var5, this.getCellRealY(var2.y) + var6);

         for(int var11 = 0; var11 < var1.sectionWidth; ++var11) {
            for(int var12 = 0; var12 < var1.sectionHeight; ++var12) {
               Point var13 = new Point(var2.x + var11, var2.y + var12);
               Preset var14 = var10.subPreset(var11 * var1.sectionRes, var12 * var1.sectionRes, var1.sectionRes, var1.sectionRes);
               PlacedPreset var15 = new PlacedPreset(this, var13, var1, var11, var12, var3, var4, var7 == null ? null : this.getPlacedPreset(var7), var14);
               this.placedPresets.put(var13, var15);
               this.openCells.add(var13);
               if (this.allowRoundTrips) {
                  this.openAround(var15, var5, var6);
               }
            }
         }

         if (var8) {
            if (!this.presetUsage.containsKey(var1)) {
               this.presetUsage.put(var1, 1);
            } else {
               this.presetUsage.put(var1, (Integer)this.presetUsage.get(var1) + 1);
            }
         }

         return true;
      }
   }

   private boolean applyFillPreset(ModularPreset var1, Point var2, boolean var3, boolean var4, int var5, int var6) {
      this.debug("Attempting to apply fill preset " + var1.getClass().getSimpleName() + " to (" + var2.x + ", " + var2.y + ")");
      if (!var1.canPlace(this.level, this.getCellRealX(var2.x) + var5, this.getCellRealY(var2.y) + var6)) {
         this.debug("Preset would not allow to be there");
         return false;
      } else if (this.isCellsOutOfMap(var1, var2)) {
         this.debug("Preset out of map");
         return false;
      } else if (this.isCellsOccupied(var1, var2)) {
         this.debug("Cells occupied");
         return false;
      } else {
         this.debug("Applied preset " + var1.getClass().getSimpleName() + " to (" + var2.x + ", " + var2.y + ")");
         Object var7 = var1;
         if (var3) {
            var7 = var1.tryMirrorX();
         }

         if (var4) {
            var7 = ((Preset)var7).tryMirrorY();
         }

         Preset var8 = Preset.copyFromLevel(this.level, this.getCellRealX(var2.x) + var5, this.getCellRealY(var2.y) + var6, var1.width, var1.height);
         ((Preset)var7).applyToLevel(this.level, this.getCellRealX(var2.x) + var5, this.getCellRealY(var2.y) + var6);

         for(int var9 = 0; var9 < var1.sectionWidth; ++var9) {
            for(int var10 = 0; var10 < var1.sectionHeight; ++var10) {
               Point var11 = new Point(var2.x + var9, var2.y + var10);
               Preset var12 = var8.subPreset(var9 * var1.sectionRes, var10 * var1.sectionRes, var1.sectionRes, var1.sectionRes);
               this.placedPresets.put(var11, new PlacedPreset(this, var11, var1, var9, var10, var3, var4, (PlacedPreset)null, var12));
            }
         }

         return true;
      }
   }

   public boolean replacePreset(Point var1, ModularPreset var2, boolean var3, boolean var4, int var5, int var6) {
      PlacedPreset var7 = this.getPlacedPreset(var1);
      if (var7 == null) {
         this.debug("Could not find old preset to replace");
         return false;
      } else if (var7.preset.sectionWidth == var2.sectionWidth && var7.preset.sectionHeight == var2.sectionHeight) {
         if (var7.realX != 0) {
            var1.x -= var7.realX;
         }

         if (var7.realY != 0) {
            var1.y -= var7.realY;
         }

         Object var8 = var2;
         if (var3) {
            var8 = var2.tryMirrorX();
         }

         if (var4) {
            var8 = ((Preset)var8).tryMirrorY();
         }

         this.debug("Replaced preset " + var2.getClass().getSimpleName() + " to (" + var1.x + ", " + var1.y + ")");
         ((Preset)var8).applyToLevel(this.level, this.getCellRealX(var1.x) + var5, this.getCellRealY(var1.y) + var6);

         for(int var9 = 0; var9 < var2.sectionWidth; ++var9) {
            for(int var10 = 0; var10 < var2.sectionHeight; ++var10) {
               Point var11 = new Point(var1.x + var9, var1.y + var10);
               PlacedPreset var12 = this.getPlacedPreset(var11);
               PlacedPreset var13 = new PlacedPreset(this, var11, var2, var9, var10, var3, var4, var12.from, var12.replacedLevel);
               this.placedPresets.put(var11, var13);
               var13.openDirs(this.level, var5 + 5, var6 + 5, this.random);
               if (this.allowRoundTrips) {
                  this.openAround(var13, var5, var6);
               }
            }
         }

         return true;
      } else {
         this.debug("Replace preset are not the same dimensions as old one");
         return false;
      }
   }

   private void openAround(PlacedPreset var1, int var2, int var3) {
      for(int var4 = 0; var4 < 4; ++var4) {
         if (var1.isPresetOpenDir(var4)) {
            PlacedPreset var5 = this.getPlacedPreset(this.getNextCell(var1.cell, var4));
            if (var5 != null && var5.isPresetOpenDir(var4 + 2)) {
               this.debug("Opened from " + var1.cell.x + "," + var1.cell.y + " to " + var5.cell.x + "," + var5.cell.y);
               var1.openDirs(this.level, var5, var2 + 5, var3 + 5, this.random);
            }
         }
      }

   }

   public void fixHeuristic() {
      Iterator var1 = this.getPlacedPresets().iterator();

      while(var1.hasNext()) {
         PlacedPreset var2 = (PlacedPreset)var1.next();
         if (var2.from != null && var2.heuristic > var2.from.heuristic + 1) {
            var2.fixHeuristic(var2.from.heuristic + 1);
            this.fixHeuristic();
         }
      }

   }

   private boolean isCellsOccupied(ModularPreset var1, Point var2) {
      for(int var3 = 0; var3 < var1.sectionWidth; ++var3) {
         for(int var4 = 0; var4 < var1.sectionHeight; ++var4) {
            if (this.placedPresets.containsKey(new Point(var2.x + var3, var2.y + var4))) {
               return true;
            }
         }
      }

      return false;
   }

   private boolean isCellsOutOfMap(ModularPreset var1, Point var2) {
      return var2.x < 0 || var2.x + var1.sectionWidth > this.cellsWidth || var2.y < 0 || var2.y + var1.sectionHeight > this.cellsHeight;
   }

   public boolean canApplyPreset(ModularPreset var1, Point var2, boolean var3, boolean var4, Point var5) {
      return true;
   }

   public static Point getStartCell(Level var0, int var1, int var2) {
      GameRandom var3 = new GameRandom(var0.getSeed());
      int var4 = GameMath.limit(var1 - 2, 0, 4);
      int var5 = GameMath.limit(var2 - 2, 0, 4);
      int var6 = var3.nextInt(var1 - var4) + var4 / 2;
      int var7 = var3.nextInt(var2 - var5) + var5 / 2;
      return new Point(var6, var7);
   }

   public Point getStartCell() {
      return getStartCell(this.level, this.cellsWidth - this.startPreset.sectionWidth, this.cellsHeight - this.startPreset.sectionHeight);
   }

   public static int getCellRealPos(int var0, int var1) {
      return 5 + var0 * var1;
   }

   public int getCellRealX(int var1) {
      return getCellRealPos(var1, this.cellRes);
   }

   public int getCellRealY(int var1) {
      return getCellRealPos(var1, this.cellRes);
   }

   private boolean isCellOpen(Point var1, int var2) {
      var2 %= 4;
      if (this.placedPresets.containsKey(var1)) {
         return ((PlacedPreset)this.placedPresets.get(var1)).isPresetOpenDir(var2);
      } else {
         this.debug("Did not find placedPreset at " + var1.toString());
         return false;
      }
   }

   public Point getNextCell(Point var1, int var2) {
      var2 %= 4;
      if (var2 == 0) {
         return new Point(var1.x, var1.y - 1);
      } else if (var2 == 1) {
         return new Point(var1.x + 1, var1.y);
      } else if (var2 == 2) {
         return new Point(var1.x, var1.y + 1);
      } else {
         return var2 == 3 ? new Point(var1.x - 1, var1.y) : new Point(var1.x, var1.y);
      }
   }

   public void addRandomMobs(int var1, int var2, String[] var3, int var4, int var5) {
      this.addRandomMobs(var1, var2, var3, var4, var5, true);
   }

   public void addRandomMobs(int var1, int var2, String[] var3, int var4, int var5, boolean var6) {
      Object[] var7 = null;
      if (var6) {
         var7 = this.placedPresets.keySet().toArray();
      }

      int var8 = 0;

      for(int var9 = 0; var9 < var4 && var8 <= var5; ++var9) {
         Point var10;
         int var11;
         int var12;
         if (var6) {
            var10 = (Point)var7[this.random.nextInt(var7.length)];
         } else {
            var11 = this.random.nextInt(this.cellsWidth);
            var12 = this.random.nextInt(this.cellsHeight);
            var10 = new Point(var11, var12);
         }

         if (var6 || this.placedPresets.containsKey(var10)) {
            var11 = var1 + this.getCellRealX(var10.x) + this.random.nextInt(this.cellRes);
            var12 = var2 + this.getCellRealY(var10.y) + this.random.nextInt(this.cellRes);
            String var13 = var3[this.random.nextInt(var3.length)];
            Mob var14 = MobRegistry.getMob(var13, this.level);
            var14.resetUniqueID(this.random);
            var14.setPos((float)(var11 * 32 + 16), (float)(var12 * 32 + 16), true);
            if (!var14.collidesWith(this.level)) {
               this.level.entityManager.mobs.add(var14);
               ++var8;
            }
         }
      }

   }

   private boolean printDebug() {
      return this.printDebug;
   }

   private void debug(String var1) {
      if (this.printDebug()) {
         int var2 = Thread.currentThread().getStackTrace().length - this.traceStart;

         for(int var3 = 0; var3 < var2; ++var3) {
            var1 = "\t" + var1;
         }

         System.out.println(var1);
      }

   }

   public PlacedPreset getPlacedPreset(Point var1) {
      return (PlacedPreset)this.placedPresets.get(var1);
   }

   public Collection<PlacedPreset> getPlacedPresets() {
      return this.placedPresets.values();
   }
}
