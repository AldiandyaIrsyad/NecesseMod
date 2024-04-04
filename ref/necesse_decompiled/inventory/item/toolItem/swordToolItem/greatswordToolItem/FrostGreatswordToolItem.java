package necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem;

import necesse.inventory.item.Item;

public class FrostGreatswordToolItem extends GreatswordToolItem {
   public FrostGreatswordToolItem() {
      super(600, getThreeChargeLevels(500, 600, 700));
      this.rarity = Item.Rarity.COMMON;
      this.attackDamage.setBaseValue(65.0F).setUpgradedValue(1.0F, 158.0F);
      this.attackRange.setBaseValue(114);
      this.knockback.setBaseValue(150);
   }
}
