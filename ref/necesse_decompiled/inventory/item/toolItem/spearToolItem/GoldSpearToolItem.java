package necesse.inventory.item.toolItem.spearToolItem;

import necesse.inventory.item.Item;

public class GoldSpearToolItem extends SpearToolItem {
   public GoldSpearToolItem() {
      super(450);
      this.rarity = Item.Rarity.NORMAL;
      this.attackAnimTime.setBaseValue(500);
      this.attackDamage.setBaseValue(20.0F).setUpgradedValue(1.0F, 64.0F);
      this.attackRange.setBaseValue(110);
      this.knockback.setBaseValue(25);
   }
}
