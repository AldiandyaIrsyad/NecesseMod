package necesse.inventory.item.toolItem.swordToolItem;

import necesse.inventory.item.Item;

public class SpiderClawSwordToolItem extends SwordToolItem {
   public SpiderClawSwordToolItem() {
      super(700);
      this.rarity = Item.Rarity.UNCOMMON;
      this.attackAnimTime.setBaseValue(100);
      this.attackDamage.setBaseValue(16.0F).setUpgradedValue(1.0F, 55.0F);
      this.attackRange.setBaseValue(45);
      this.knockback.setBaseValue(30);
      this.resilienceGain.setBaseValue(0.5F);
   }
}
