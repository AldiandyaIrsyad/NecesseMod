package necesse.inventory.item.toolItem.swordToolItem;

import necesse.inventory.item.Item;

public class IronSwordToolItem extends SwordToolItem {
   public IronSwordToolItem() {
      super(400);
      this.rarity = Item.Rarity.NORMAL;
      this.attackAnimTime.setBaseValue(300);
      this.attackDamage.setBaseValue(20.0F).setUpgradedValue(1.0F, 92.0F);
      this.attackRange.setBaseValue(50);
      this.knockback.setBaseValue(75);
   }
}
