package necesse.inventory.item.toolItem.spearToolItem;

import necesse.inventory.item.Item;

public class IronSpearToolItem extends SpearToolItem {
   public IronSpearToolItem() {
      super(400);
      this.rarity = Item.Rarity.NORMAL;
      this.attackAnimTime.setBaseValue(500);
      this.attackDamage.setBaseValue(18.0F).setUpgradedValue(1.0F, 66.0F);
      this.attackRange.setBaseValue(100);
      this.knockback.setBaseValue(25);
   }
}
