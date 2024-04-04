package necesse.level.gameObject;

import java.awt.Rectangle;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;

public class AirObject extends GameObject {
   public AirObject() {
      super(new Rectangle(0, 0));
      this.isLightTransparent = true;
   }

   public String canPlace(Level var1, int var2, int var3, int var4) {
      return null;
   }

   public boolean isValid(Level var1, int var2, int var3) {
      return true;
   }

   public LootTable getLootTable(Level var1, int var2, int var3) {
      return new LootTable();
   }
}
