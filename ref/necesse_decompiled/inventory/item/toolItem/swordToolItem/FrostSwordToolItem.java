package necesse.inventory.item.toolItem.swordToolItem;

import necesse.inventory.item.Item;

public class FrostSwordToolItem extends SwordToolItem {
   public FrostSwordToolItem() {
      super(500);
      this.rarity = Item.Rarity.NORMAL;
      this.attackAnimTime.setBaseValue(300);
      this.attackDamage.setBaseValue(25.0F).setUpgradedValue(1.0F, 87.0F);
      this.attackRange.setBaseValue(60);
      this.knockback.setBaseValue(75);
   }
}
