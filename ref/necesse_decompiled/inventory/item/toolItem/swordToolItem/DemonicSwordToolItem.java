package necesse.inventory.item.toolItem.swordToolItem;

import necesse.inventory.item.Item;

public class DemonicSwordToolItem extends SwordToolItem {
   public DemonicSwordToolItem() {
      super(600);
      this.rarity = Item.Rarity.COMMON;
      this.attackAnimTime.setBaseValue(300);
      this.attackDamage.setBaseValue(30.0F).setUpgradedValue(1.0F, 85.0F);
      this.attackRange.setBaseValue(65);
      this.knockback.setBaseValue(80);
   }
}
