package necesse.level.maps;

import java.awt.Rectangle;
import java.awt.Shape;
import necesse.engine.util.AreaSpliterator;

public abstract class LevelShapeBoundsSpliterator<T> extends AreaSpliterator<T> {
   public final Level level;

   public LevelShapeBoundsSpliterator(Level var1, Shape var2, int var3) {
      this.level = var1;
      Rectangle var4 = var2.getBounds();
      int var5 = Math.max(this.getMinX(), this.getPosX(var4.x) - var3);
      int var6 = Math.max(this.getMinY(), this.getPosY(var4.y) - var3);
      int var7 = Math.min(this.getMaxX(), this.getPosX(var4.x + var4.width) + var3);
      int var8 = Math.min(this.getMaxY(), this.getPosY(var4.y + var4.height) + var3);
      this.reset(var5, var6, var7 + 1, var8 + 1);
   }

   protected abstract int getPosX(int var1);

   protected abstract int getPosY(int var1);

   protected int getMinX() {
      return 0;
   }

   protected int getMinY() {
      return 0;
   }

   protected abstract int getMaxX();

   protected abstract int getMaxY();
}
