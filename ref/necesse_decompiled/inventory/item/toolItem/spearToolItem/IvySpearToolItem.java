package necesse.inventory.item.toolItem.spearToolItem;

import necesse.inventory.item.Item;

public class IvySpearToolItem extends SpearToolItem {
   public IvySpearToolItem() {
      super(800);
      this.rarity = Item.Rarity.UNCOMMON;
      this.attackAnimTime.setBaseValue(400);
      this.attackDamage.setBaseValue(32.0F).setUpgradedValue(1.0F, 60.0F);
      this.attackRange.setBaseValue(120);
      this.knockback.setBaseValue(25);
   }
}
