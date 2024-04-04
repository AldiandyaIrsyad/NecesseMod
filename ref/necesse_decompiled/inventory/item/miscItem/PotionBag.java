package necesse.inventory.item.miscItem;

import necesse.inventory.item.Item;

public class PotionBag extends PotionPouch {
   public PotionBag() {
      this.rarity = Item.Rarity.RARE;
   }

   public int getInternalInventorySize() {
      return 20;
   }
}
