package necesse.inventory.item.toolItem.swordToolItem;

import necesse.inventory.item.Item;

public class TungstenSwordToolItem extends SwordToolItem {
   public TungstenSwordToolItem() {
      super(1000);
      this.rarity = Item.Rarity.UNCOMMON;
      this.attackAnimTime.setBaseValue(300);
      this.attackDamage.setBaseValue(45.0F).setUpgradedValue(1.0F, 80.0F);
      this.attackRange.setBaseValue(80);
      this.knockback.setBaseValue(100);
   }
}
