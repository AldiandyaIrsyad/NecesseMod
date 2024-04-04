package necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem;

import necesse.inventory.item.Item;

public class IronGreatswordToolItem extends GreatswordToolItem {
   public IronGreatswordToolItem() {
      super(500, getThreeChargeLevels(500, 600, 700));
      this.rarity = Item.Rarity.COMMON;
      this.attackDamage.setBaseValue(50.0F).setUpgradedValue(1.0F, 160.0F);
      this.attackRange.setBaseValue(110);
      this.knockback.setBaseValue(150);
   }
}
