package necesse.inventory.item.toolItem.summonToolItem;

import necesse.engine.network.server.FollowPosition;
import necesse.inventory.item.Item;

public class MagicBranchSummonToolItem extends SummonToolItem {
   public MagicBranchSummonToolItem() {
      super("babysnowman", FollowPosition.PYRAMID, 1.0F, 550);
      this.rarity = Item.Rarity.UNCOMMON;
      this.attackDamage.setBaseValue(16.0F).setUpgradedValue(1.0F, 28.0F);
   }
}
