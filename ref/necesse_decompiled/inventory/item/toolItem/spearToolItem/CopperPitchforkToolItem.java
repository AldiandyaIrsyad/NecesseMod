package necesse.inventory.item.toolItem.spearToolItem;

import necesse.inventory.item.Item;

public class CopperPitchforkToolItem extends SpearToolItem {
   public CopperPitchforkToolItem() {
      super(200);
      this.rarity = Item.Rarity.COMMON;
      this.attackAnimTime.setBaseValue(500);
      this.attackDamage.setBaseValue(18.0F).setUpgradedValue(1.0F, 66.0F);
      this.attackRange.setBaseValue(80);
      this.knockback.setBaseValue(25);
      this.width = 80.0F;
   }
}
