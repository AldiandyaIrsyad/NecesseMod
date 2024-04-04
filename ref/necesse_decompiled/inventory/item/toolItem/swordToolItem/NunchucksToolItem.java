package necesse.inventory.item.toolItem.swordToolItem;

import necesse.inventory.item.Item;

public class NunchucksToolItem extends SwordToolItem {
   public NunchucksToolItem() {
      super(400);
      this.rarity = Item.Rarity.COMMON;
      this.attackAnimTime.setBaseValue(300);
      this.attackDamage.setBaseValue(16.0F).setUpgradedValue(1.0F, 80.0F);
      this.attackRange.setBaseValue(30);
      this.knockback.setBaseValue(150);
   }
}
