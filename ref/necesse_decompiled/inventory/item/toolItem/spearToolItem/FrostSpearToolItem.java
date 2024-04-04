package necesse.inventory.item.toolItem.spearToolItem;

import necesse.inventory.item.Item;

public class FrostSpearToolItem extends SpearToolItem {
   public FrostSpearToolItem() {
      super(500);
      this.rarity = Item.Rarity.NORMAL;
      this.attackAnimTime.setBaseValue(500);
      this.attackDamage.setBaseValue(22.0F).setUpgradedValue(1.0F, 62.0F);
      this.attackRange.setBaseValue(100);
      this.knockback.setBaseValue(25);
   }
}
