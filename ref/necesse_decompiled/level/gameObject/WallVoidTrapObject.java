package necesse.level.gameObject;

import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.VoidTrapObjectEntity;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.maps.Level;

public class WallVoidTrapObject extends WallTrapObject {
   public WallVoidTrapObject(WallObject var1) {
      super(var1, "voidtrap");
   }

   public WallVoidTrapObject(WallObject var1, int var2, ToolType var3) {
      super(var1, "voidtrap", var2, var3);
   }

   public ObjectEntity getNewObjectEntity(Level var1, int var2, int var3) {
      return new VoidTrapObjectEntity(var1, var2, var3);
   }
}
