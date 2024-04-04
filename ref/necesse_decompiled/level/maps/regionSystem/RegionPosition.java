package necesse.level.maps.regionSystem;

import java.awt.Point;
import necesse.level.maps.Level;

public class RegionPosition {
   public final Level level;
   public final int regionX;
   public final int regionY;

   public RegionPosition(Level var1, int var2, int var3) {
      this.level = var1;
      this.regionX = var2;
      this.regionY = var3;
   }

   public RegionPosition(Level var1, Point var2) {
      this(var1, var2.x, var2.y);
   }

   public Region getRegion() {
      return this.level.regionManager.getRegion(this.regionX, this.regionY);
   }

   public Point point() {
      return new Point(this.regionX, this.regionY);
   }

   public boolean isSame(RegionPosition var1) {
      return this.level.getIdentifier().equals(var1.level.getIdentifier()) && this.regionX == var1.regionX && this.regionY == var1.regionY;
   }

   public boolean equals(Object var1) {
      return var1 instanceof RegionPosition ? this.isSame((RegionPosition)var1) : super.equals(var1);
   }
}
