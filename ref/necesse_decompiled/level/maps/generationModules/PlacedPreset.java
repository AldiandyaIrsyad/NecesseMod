package necesse.level.maps.generationModules;

import java.awt.Point;
import necesse.engine.util.GameRandom;
import necesse.level.maps.Level;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.modularPresets.ModularPreset;

public class PlacedPreset {
   public final ModularGeneration mg;
   public final Point cell;
   public final boolean xMirror;
   public final boolean yMirror;
   public final ModularPreset preset;
   public final int x;
   public final int y;
   public final int realX;
   public final int realY;
   public final PlacedPreset from;
   public int heuristic;
   private boolean[] open;
   public final Preset replacedLevel;

   public PlacedPreset(ModularGeneration var1, Point var2, ModularPreset var3, int var4, int var5, boolean var6, boolean var7, PlacedPreset var8, Preset var9) {
      this.mg = var1;
      this.cell = var2;
      this.preset = var3;
      this.realX = var4;
      this.realY = var5;
      if (var6) {
         var4 = Math.abs(var4 - (var3.sectionWidth - 1));
      }

      if (var7) {
         var5 = Math.abs(var5 - (var3.sectionHeight - 1));
      }

      this.x = var4;
      this.y = var5;
      this.xMirror = var6;
      this.yMirror = var7;
      this.from = var8;
      if (var8 != null) {
         this.heuristic = var8.heuristic + 1;
      } else {
         this.heuristic = 0;
      }

      this.open = new boolean[4];
      this.replacedLevel = var9;
   }

   public boolean isPresetOpenTop() {
      int var1 = this.yMirror ? 2 : 0;
      return this.preset.isOpen(this.x, this.y, var1);
   }

   public boolean isPresetOpenBot() {
      int var1 = this.yMirror ? 0 : 2;
      return this.preset.isOpen(this.x, this.y, var1);
   }

   public boolean isPresetOpenRight() {
      int var1 = this.xMirror ? 3 : 1;
      return this.preset.isOpen(this.x, this.y, var1);
   }

   public boolean isPresetOpenLeft() {
      int var1 = this.xMirror ? 1 : 3;
      return this.preset.isOpen(this.x, this.y, var1);
   }

   public boolean isPresetOpenDir(int var1) {
      var1 %= 4;
      if (var1 == 0) {
         return this.isPresetOpenTop();
      } else if (var1 == 1) {
         return this.isPresetOpenRight();
      } else if (var1 == 2) {
         return this.isPresetOpenBot();
      } else {
         return var1 == 3 ? this.isPresetOpenLeft() : false;
      }
   }

   public static int getCellsDir(Point var0, Point var1) {
      if (var0.x != var1.x && var0.y != var1.y) {
         throw new IllegalArgumentException("Cells aren't adjacent");
      } else if (var0.x != var1.x) {
         return var0.x < var1.x ? 1 : 3;
      } else {
         return var0.y < var1.y ? 2 : 0;
      }
   }

   public void openDirs(Level var1, PlacedPreset var2, int var3, int var4, GameRandom var5) {
      if (var2 != null) {
         if (this.heuristic > var2.heuristic + 1) {
            this.heuristic = var2.heuristic + 1;
            if (this.from != null) {
               this.from.fixHeuristic(this.heuristic + 1);
            }
         }

         int var6 = getCellsDir(var2.cell, this.cell);
         if (!var2.dirOpen(var6)) {
            var2.openDir(var1, var3, var4, var6, var5);
         }

         int var7 = getCellsDir(this.cell, var2.cell);
         if (!this.dirOpen(var7)) {
            this.openDir(var1, var3, var4, var7, var5);
         }
      }

   }

   public void fixHeuristic(int var1) {
      if (this.heuristic > var1) {
         this.heuristic = var1;
         if (this.from != null) {
            this.from.fixHeuristic(var1 + 1);
         }
      }

   }

   public void openDirs(Level var1, int var2, int var3, GameRandom var4) {
      this.openDirs(var1, this.from, var2, var3, var4);
   }

   private void openDir(Level var1, int var2, int var3, int var4, GameRandom var5) {
      this.preset.openLevel(var1, this.cell.x, this.cell.y, var2, var3, var4, var5, this.mg.cellRes);
      this.open[var4] = true;
   }

   private boolean dirOpen(int var1) {
      return this.open[var1];
   }
}
