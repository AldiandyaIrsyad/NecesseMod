package necesse.level.maps.regionSystem;

import java.awt.Shape;
import necesse.level.maps.Level;
import necesse.level.maps.LevelShapeBoundsSpliterator;

public class LevelRegionsSpliterator extends LevelShapeBoundsSpliterator<RegionPosition> {
   public LevelRegionsSpliterator(Level var1, Shape var2, int var3) {
      super(var1, var2, var3);
   }

   protected int getPosX(int var1) {
      return this.level.regionManager.getRegionXByTile(var1 / 32);
   }

   protected int getPosY(int var1) {
      return this.level.regionManager.getRegionYByTile(var1 / 32);
   }

   protected int getMaxX() {
      return this.level.regionManager.getRegionsWidth() - 1;
   }

   protected int getMaxY() {
      return this.level.regionManager.getRegionsHeight() - 1;
   }

   protected RegionPosition getPos(int var1, int var2) {
      return new RegionPosition(this.level, var1, var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected Object getPos(int var1, int var2) {
      return this.getPos(var1, var2);
   }
}
