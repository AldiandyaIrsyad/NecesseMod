package necesse.level.gameObject;

import java.awt.Rectangle;

public class ObjectHoverHitbox extends Rectangle {
   public final int tileX;
   public final int tileY;
   public final int sortY;

   public ObjectHoverHitbox(int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      super(var1 * 32 + var3, var2 * 32 + var4, var5, var6);
      this.tileX = var1;
      this.tileY = var2;
      this.sortY = var2 * 32 + var7;
   }

   public ObjectHoverHitbox(int var1, int var2, int var3, int var4, int var5, int var6) {
      this(var1, var2, var3, var4, var5, var6, 16);
   }
}
