package necesse.level.gameObject.furniture;

import java.awt.Color;
import java.awt.Rectangle;

public class TableObject extends FurnitureObject {
   public TableObject(Rectangle var1, Color var2) {
      super(var1);
      this.mapColor = var2;
      this.furnitureType = "table";
   }
}
