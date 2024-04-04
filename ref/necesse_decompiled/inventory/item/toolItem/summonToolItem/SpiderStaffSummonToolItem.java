package necesse.inventory.item.toolItem.summonToolItem;

import necesse.engine.network.server.FollowPosition;
import necesse.inventory.item.Item;

public class SpiderStaffSummonToolItem extends SummonToolItem {
   public SpiderStaffSummonToolItem() {
      super("babyspider", FollowPosition.PYRAMID, 1.0F, 400);
      this.rarity = Item.Rarity.UNCOMMON;
      this.attackDamage.setBaseValue(12.0F).setUpgradedValue(1.0F, 29.0F);
   }
}
