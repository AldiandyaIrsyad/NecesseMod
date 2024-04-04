package necesse.inventory.item.miscItem;

import java.awt.Color;
import necesse.inventory.item.Item;

public class InfiniteRopeItem extends RopeItem {
   public InfiniteRopeItem() {
      this.setItemCategory(new String[]{"equipment", "tools"});
      this.rarity = Item.Rarity.EPIC;
      this.worldDrawSize = 32;
      this.incinerationTimeMillis = 30000;
   }

   public boolean consumesRope() {
      return false;
   }

   public Color getRopeColor() {
      return new Color(-14739669);
   }
}
