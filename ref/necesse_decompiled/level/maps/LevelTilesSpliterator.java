package necesse.level.maps;

import java.awt.Shape;

public class LevelTilesSpliterator extends LevelShapeBoundsSpliterator<TilePosition> {
   public LevelTilesSpliterator(Level var1, Shape var2, int var3) {
      super(var1, var2, var3);
   }

   protected int getPosX(int var1) {
      return var1 / 32;
   }

   protected int getPosY(int var1) {
      return var1 / 32;
   }

   protected int getMaxX() {
      return this.level.width - 1;
   }

   protected int getMaxY() {
      return this.level.height - 1;
   }

   protected TilePosition getPos(int var1, int var2) {
      return new TilePosition(this.level, var1, var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected Object getPos(int var1, int var2) {
      return this.getPos(var1, var2);
   }
}
