package necesse.level.gameObject;

import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.SawTrapObjectEntity;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.maps.Level;

public class WallSawTrapObject extends WallTrapObject {
   public WallSawTrapObject(WallObject var1) {
      super(var1, "sawtrap");
   }

   public WallSawTrapObject(WallObject var1, int var2, ToolType var3) {
      super(var1, "sawtrap", var2, var3);
   }

   public ObjectEntity getNewObjectEntity(Level var1, int var2, int var3) {
      return new SawTrapObjectEntity(var1, var2, var3);
   }
}
