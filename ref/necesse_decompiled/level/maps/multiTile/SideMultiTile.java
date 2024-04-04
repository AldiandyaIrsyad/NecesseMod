package necesse.level.maps.multiTile;

import java.awt.Point;

public class SideMultiTile extends MultiTile {
   public SideMultiTile(int var1, int var2, int var3, int var4, int var5, boolean var6, int... var7) {
      super(var1, var2, var3, var4, var5, var6, var7);
   }

   public Point getMirrorXPosOffset() {
      Point var1 = this.getCenterTileOffset();
      return this.rotation != 0 && this.rotation != 2 ? new Point(-var1.x, -var1.y) : var1;
   }

   public Point getMirrorYPosOffset() {
      Point var1 = this.getCenterTileOffset();
      return this.rotation != 1 && this.rotation != 3 ? new Point(-var1.x, -var1.y) : var1;
   }

   public int getXMirrorRotation() {
      if (this.rotation == 0) {
         return 2;
      } else {
         return this.rotation == 2 ? 0 : this.rotation;
      }
   }

   public int getYMirrorRotation() {
      if (this.rotation == 1) {
         return 3;
      } else {
         return this.rotation == 3 ? 1 : this.rotation;
      }
   }
}
