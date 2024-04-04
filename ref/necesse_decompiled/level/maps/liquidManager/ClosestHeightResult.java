package necesse.level.maps.liquidManager;

import java.awt.Point;
import java.util.Collection;

public class ClosestHeightResult {
   public final int startX;
   public final int startY;
   public final Point best;
   public final Point found;
   public final Collection<NextHeightTile> closedTiles;
   public final Collection<NextHeightTile> openTiles;

   public ClosestHeightResult(int var1, int var2, Point var3, Point var4, Collection<NextHeightTile> var5, Collection<NextHeightTile> var6) {
      this.startX = var1;
      this.startY = var2;
      this.best = var3;
      this.found = var4;
      this.closedTiles = var5;
      this.openTiles = var6;
   }
}
