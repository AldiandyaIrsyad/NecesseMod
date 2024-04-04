package necesse.inventory.item.toolItem.spearToolItem;

import necesse.inventory.item.Item;

public class CopperSpearToolItem extends SpearToolItem {
   public CopperSpearToolItem() {
      super(200);
      this.rarity = Item.Rarity.NORMAL;
      this.attackAnimTime.setBaseValue(500);
      this.attackDamage.setBaseValue(16.0F).setUpgradedValue(1.0F, 68.0F);
      this.attackRange.setBaseValue(100);
      this.knockback.setBaseValue(25);
   }
}
