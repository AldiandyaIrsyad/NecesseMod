package necesse.inventory.item.miscItem;

import necesse.inventory.item.Item;

public class AmmoBag extends AmmoPouch {
   public AmmoBag() {
      this.rarity = Item.Rarity.RARE;
   }

   public int getInternalInventorySize() {
      return 20;
   }
}
