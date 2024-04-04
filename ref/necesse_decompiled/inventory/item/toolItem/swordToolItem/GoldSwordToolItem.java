package necesse.inventory.item.toolItem.swordToolItem;

import necesse.inventory.item.Item;

public class GoldSwordToolItem extends SwordToolItem {
   public GoldSwordToolItem() {
      super(450);
      this.rarity = Item.Rarity.NORMAL;
      this.attackAnimTime.setBaseValue(300);
      this.attackDamage.setBaseValue(22.0F).setUpgradedValue(1.0F, 90.0F);
      this.attackRange.setBaseValue(55);
      this.knockback.setBaseValue(75);
   }
}
