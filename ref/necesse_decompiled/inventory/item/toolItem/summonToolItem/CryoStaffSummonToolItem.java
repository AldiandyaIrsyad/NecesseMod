package necesse.inventory.item.toolItem.summonToolItem;

import necesse.engine.network.server.FollowPosition;
import necesse.inventory.item.Item;

public class CryoStaffSummonToolItem extends SummonToolItem {
   public CryoStaffSummonToolItem() {
      super("playercryoflake", FollowPosition.FLYING_CIRCLE, 1.0F, 1200);
      this.rarity = Item.Rarity.UNCOMMON;
      this.attackDamage.setBaseValue(22.0F).setUpgradedValue(1.0F, 32.0F);
   }
}
