package necesse.level.gameObject.furniture;

import java.awt.Color;
import necesse.inventory.item.toolItem.ToolType;

public class ToiletObject extends ChairObject {
   public ToiletObject(String var1, ToolType var2, Color var3) {
      super(var1, var2, var3);
      this.furnitureType = "toilet";
   }

   public ToiletObject(String var1, Color var2) {
      super(var1, var2);
      this.furnitureType = "toilet";
   }
}
