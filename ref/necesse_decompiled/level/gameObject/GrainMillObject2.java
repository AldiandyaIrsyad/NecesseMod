package necesse.level.gameObject;

import java.awt.Rectangle;
import java.util.List;
import necesse.level.maps.Level;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.multiTile.StaticMultiTile;

class GrainMillObject2 extends GrainMillExtraObject {
   protected int counterIDTopLeft;
   protected int counterIDBotLeft;
   protected int counterIDBotRight;

   public GrainMillObject2() {
   }

   protected Rectangle getCollision(Level var1, int var2, int var3, int var4) {
      if (var4 == 0) {
         return new Rectangle(var2 * 32, var3 * 32 + 12, 27, 20);
      } else if (var4 == 1) {
         return new Rectangle(var2 * 32, var3 * 32, 27, 22);
      } else {
         return var4 == 2 ? new Rectangle(var2 * 32 + 5, var3 * 32, 27, 22) : new Rectangle(var2 * 32 + 5, var3 * 32 + 12, 27, 20);
      }
   }

   public List<ObjectHoverHitbox> getHoverHitboxes(Level var1, int var2, int var3) {
      List var4 = super.getHoverHitboxes(var1, var2, var3);
      var4.add(new ObjectHoverHitbox(var2, var3, 0, -32, 32, 32));
      return var4;
   }

   protected void setCounterIDs(int var1, int var2, int var3, int var4) {
      this.counterIDTopLeft = var1;
      this.counterIDBotLeft = var3;
      this.counterIDBotRight = var4;
   }

   public MultiTile getMultiTile(int var1) {
      return new StaticMultiTile(1, 0, 2, 2, var1, false, new int[]{this.counterIDTopLeft, this.getID(), this.counterIDBotLeft, this.counterIDBotRight});
   }
}