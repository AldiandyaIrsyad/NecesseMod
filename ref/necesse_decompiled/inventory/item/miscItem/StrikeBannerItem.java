package necesse.inventory.item.miscItem;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.inventory.item.Item;

public class StrikeBannerItem extends Item {
   public StrikeBannerItem() {
      super(1);
   }

   public GameMessage getNewLocalization() {
      return new StaticMessage("STRIKE_BANNER");
   }
}
