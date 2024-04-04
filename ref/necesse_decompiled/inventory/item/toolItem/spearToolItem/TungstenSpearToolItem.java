package necesse.inventory.item.toolItem.spearToolItem;

import necesse.inventory.item.Item;

public class TungstenSpearToolItem extends SpearToolItem {
   public TungstenSpearToolItem() {
      super(1100);
      this.rarity = Item.Rarity.UNCOMMON;
      this.attackAnimTime.setBaseValue(400);
      this.attackDamage.setBaseValue(38.0F).setUpgradedValue(1.0F, 60.0F);
      this.attackRange.setBaseValue(140);
      this.knockback.setBaseValue(25);
   }
}
