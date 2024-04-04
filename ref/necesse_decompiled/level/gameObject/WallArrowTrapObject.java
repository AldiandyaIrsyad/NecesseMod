package necesse.level.gameObject;

import necesse.entity.objectEntity.ArrowTrapObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.maps.Level;

public class WallArrowTrapObject extends WallTrapObject {
   public WallArrowTrapObject(WallObject var1) {
      super(var1, "arrowtrap");
   }

   public WallArrowTrapObject(WallObject var1, int var2, ToolType var3) {
      super(var1, "arrowtrap", var2, var3);
   }

   public ObjectEntity getNewObjectEntity(Level var1, int var2, int var3) {
      return new ArrowTrapObjectEntity(var1, var2, var3);
   }
}
