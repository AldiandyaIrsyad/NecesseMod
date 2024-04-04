package necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem;

import necesse.inventory.item.Item;

public class QuartzGreatswordToolItem extends GreatswordToolItem {
   public QuartzGreatswordToolItem() {
      super(900, getThreeChargeLevels(500, 600, 700));
      this.rarity = Item.Rarity.UNCOMMON;
      this.attackDamage.setBaseValue(90.0F).setUpgradedValue(1.0F, 154.0F);
      this.attackRange.setBaseValue(126);
      this.knockback.setBaseValue(150);
   }
}
