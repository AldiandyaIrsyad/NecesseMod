package necesse.inventory.item.toolItem.spearToolItem;

import necesse.inventory.item.Item;

public class WoodSpearToolItem extends SpearToolItem {
   public WoodSpearToolItem() {
      super(100);
      this.rarity = Item.Rarity.NORMAL;
      this.attackAnimTime.setBaseValue(500);
      this.attackDamage.setBaseValue(14.0F).setUpgradedValue(1.0F, 70.0F);
      this.attackRange.setBaseValue(100);
      this.knockback.setBaseValue(25);
   }
}
