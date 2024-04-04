package necesse.level.maps.generationModules;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedList;
import necesse.engine.util.GameRandom;
import necesse.level.maps.Level;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetUtils;

public class PresetGeneration {
   public final Level level;
   private LinkedList<Rectangle> occupiedSpace = new LinkedList();

   public PresetGeneration(Level var1) {
      this.level = var1;
   }

   public void addOccupiedSpace(Rectangle var1) {
      this.occupiedSpace.add(var1);
   }

   public LinkedList<Rectangle> getOccupiedSpace() {
      return this.occupiedSpace;
   }

   private Rectangle getPresetRectangle(Preset var1, int var2, int var3) {
      return new Rectangle(var2, var3, var1.width, var1.height);
   }

   public void applyPreset(Preset var1, int var2, int var3) {
      var1.applyToLevel(this.level, var2, var3);
      this.addOccupiedSpace(this.getPresetRectangle(var1, var2, var3));
   }

   public void applyPreset(Preset var1, Point var2) {
      this.applyPreset(var1, var2.x, var2.y);
   }

   public boolean canPlacePreset(Preset var1, int var2, int var3) {
      Rectangle var4 = this.getPresetRectangle(var1, var2, var3);
      return this.occupiedSpace.stream().noneMatch((var1x) -> {
         return var1x.intersects(var4);
      });
   }

   public boolean canPlacePreset(Preset var1, Point var2) {
      return this.canPlacePreset(var1, var2.x, var2.y);
   }

   public Point getRandomPresetPosition(GameRandom var1, Preset var2, int var3) {
      int var4 = var1.getIntBetween(var3, this.level.width - var2.width - var3 - 1);
      int var5 = var1.getIntBetween(var3, this.level.height - var2.height - var3 - 1);
      return new Point(var4, var5);
   }

   public void findRandomValidPositionAndApply(GameRandom var1, int var2, Preset var3, int var4, boolean var5, boolean var6) {
      if (var5) {
         var3 = PresetUtils.randomizeXMirror(var3, var1);
         var3 = PresetUtils.randomizeYMirror(var3, var1);
      }

      if (var6) {
         var3 = PresetUtils.randomizeRotation(var3, var1);
      }

      for(int var7 = 0; var7 < var2; ++var7) {
         Point var8 = this.getRandomPresetPosition(var1, var3, var4);
         if (this.canPlacePreset(var3, var8)) {
            this.applyPreset(var3, var8);
            break;
         }
      }

   }

   public void findRandomValidPositionAndApply(GameRandom var1, Preset var2, int var3, boolean var4, boolean var5) {
      this.findRandomValidPositionAndApply(var1, 1, var2, var3, var4, var5);
   }
}
