package necesse.inventory.item.matItem;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.inventory.item.Item;
import necesse.inventory.item.ObtainTip;

public class BookMatItem extends MatItem implements ObtainTip {
   public BookMatItem() {
      super(100, Item.Rarity.COMMON, (String[])());
   }

   public GameMessage getObtainTip() {
      return new LocalMessage("itemtooltip", "bookobtain");
   }
}
