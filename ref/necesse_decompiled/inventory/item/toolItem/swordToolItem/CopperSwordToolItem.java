package necesse.inventory.item.toolItem.swordToolItem;

import necesse.inventory.item.Item;

public class CopperSwordToolItem extends SwordToolItem {
   public CopperSwordToolItem() {
      super(200);
      this.rarity = Item.Rarity.NORMAL;
      this.attackAnimTime.setBaseValue(300);
      this.attackDamage.setBaseValue(17.0F).setUpgradedValue(1.0F, 93.0F);
      this.attackRange.setBaseValue(50);
      this.knockback.setBaseValue(75);
   }
}
