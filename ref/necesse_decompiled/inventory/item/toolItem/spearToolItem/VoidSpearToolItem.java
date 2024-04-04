package necesse.inventory.item.toolItem.spearToolItem;

import necesse.inventory.item.Item;

public class VoidSpearToolItem extends SpearToolItem {
   public VoidSpearToolItem() {
      super(700);
      this.rarity = Item.Rarity.RARE;
      this.attackAnimTime.setBaseValue(400);
      this.attackDamage.setBaseValue(28.0F).setUpgradedValue(1.0F, 60.0F);
      this.attackRange.setBaseValue(140);
      this.knockback.setBaseValue(25);
   }
}
