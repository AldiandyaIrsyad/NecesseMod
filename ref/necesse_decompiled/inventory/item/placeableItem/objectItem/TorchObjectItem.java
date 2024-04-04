package necesse.inventory.item.placeableItem.objectItem;

import java.awt.Point;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.TorchItem;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;

public class TorchObjectItem extends ObjectItem implements TorchItem {
   public TorchObjectItem(GameObject var1, boolean var2) {
      super(var1, var2);
   }

   public TorchObjectItem(GameObject var1) {
      super(var1);
   }

   public boolean canPlaceTorch(Level var1, int var2, int var3, InventoryItem var4, PlayerMob var5) {
      int var6 = var5.isAttacking ? var5.beforeAttackDir : var5.dir;
      GameObject var7 = this.getObject();
      int var8 = var7.getPlaceRotation(var1, var2, var3, var5, var6);
      Point var9 = var7.getPlaceOffset(var1, var2, var3, var5, var8);
      int var10 = (var2 + var9.x) / 32;
      int var11 = (var3 + var9.y) / 32;
      if (var5.getPositionPoint().distance((double)(var10 * 32 + 16 - var9.x), (double)(var11 * 32 + 16 - var9.y)) > (double)this.getPlaceRange(var4, var5)) {
         return false;
      } else {
         return var7.canPlace(var1, var10, var11, var8) == null;
      }
   }

   public int getTorchPlaceRange(Level var1, InventoryItem var2, PlayerMob var3) {
      return this.getPlaceRange(var2, var3);
   }
}
