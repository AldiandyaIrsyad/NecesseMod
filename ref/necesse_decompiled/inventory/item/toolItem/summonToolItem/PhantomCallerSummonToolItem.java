package necesse.inventory.item.toolItem.summonToolItem;

import necesse.engine.network.server.FollowPosition;
import necesse.inventory.item.Item;

public class PhantomCallerSummonToolItem extends SummonToolItem {
   public PhantomCallerSummonToolItem() {
      super("playerchargingphantom", FollowPosition.FLYING_CIRCLE_FAST, 1.0F, 1600);
      this.rarity = Item.Rarity.EPIC;
      this.attackDamage.setBaseValue(27.0F).setUpgradedValue(1.0F, 30.0F);
   }
}
