package necesse.level.gameObject.furniture;

import java.awt.Color;
import java.awt.Rectangle;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.maps.Level;

public class StorageBoxInventoryObject extends InventoryObject {
   public StorageBoxInventoryObject(String var1, int var2, ToolType var3, Color var4) {
      super(var1, var2, new Rectangle(32, 32), var3, var4);
   }

   public StorageBoxInventoryObject(String var1, int var2, Color var3) {
      super(var1, var2, new Rectangle(32, 32), var3);
   }

   public Rectangle getCollision(Level var1, int var2, int var3, int var4) {
      return var4 % 2 == 0 ? new Rectangle(var2 * 32 + 3, var3 * 32 + 6, 26, 20) : new Rectangle(var2 * 32 + 6, var3 * 32 + 4, 20, 24);
   }
}
