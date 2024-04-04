package necesse.inventory.item.toolItem.swordToolItem;

import necesse.inventory.item.Item;

public class WoodSwordToolItem extends SwordToolItem {
   public WoodSwordToolItem() {
      super(100);
      this.rarity = Item.Rarity.NORMAL;
      this.attackAnimTime.setBaseValue(300);
      this.attackDamage.setBaseValue(15.0F).setUpgradedValue(1.0F, 95.0F);
      this.attackRange.setBaseValue(50);
      this.knockback.setBaseValue(75);
   }
}
