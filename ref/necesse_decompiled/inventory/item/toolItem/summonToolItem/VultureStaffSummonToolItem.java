package necesse.inventory.item.toolItem.summonToolItem;

import necesse.engine.network.server.FollowPosition;
import necesse.inventory.item.Item;

public class VultureStaffSummonToolItem extends SummonToolItem {
   public VultureStaffSummonToolItem() {
      super("playervulturehatchling", FollowPosition.FLYING, 1.0F, 800);
      this.rarity = Item.Rarity.RARE;
      this.attackDamage.setBaseValue(18.0F).setUpgradedValue(1.0F, 26.0F);
   }
}
