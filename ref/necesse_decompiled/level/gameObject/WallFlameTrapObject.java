package necesse.level.gameObject;

import necesse.entity.objectEntity.FlameTrapObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.maps.Level;

public class WallFlameTrapObject extends WallTrapObject {
   public WallFlameTrapObject(WallObject var1) {
      super(var1, "flametrap");
   }

   public WallFlameTrapObject(WallObject var1, int var2, ToolType var3) {
      super(var1, "flametrap", var2, var3);
   }

   public ObjectEntity getNewObjectEntity(Level var1, int var2, int var3) {
      return new FlameTrapObjectEntity(var1, var2, var3);
   }
}
