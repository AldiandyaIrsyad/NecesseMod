package necesse.inventory.item.toolItem.spearToolItem;

import necesse.inventory.item.Item;

public class DemonicSpearToolItem extends SpearToolItem {
   public DemonicSpearToolItem() {
      super(600);
      this.rarity = Item.Rarity.COMMON;
      this.attackAnimTime.setBaseValue(450);
      this.attackDamage.setBaseValue(24.0F).setUpgradedValue(1.0F, 60.0F);
      this.attackRange.setBaseValue(130);
      this.knockback.setBaseValue(25);
   }
}
