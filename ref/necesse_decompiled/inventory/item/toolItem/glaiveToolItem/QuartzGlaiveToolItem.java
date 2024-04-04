package necesse.inventory.item.toolItem.glaiveToolItem;

import necesse.inventory.item.Item;

public class QuartzGlaiveToolItem extends GlaiveToolItem {
   public QuartzGlaiveToolItem() {
      super(600);
      this.rarity = Item.Rarity.UNCOMMON;
      this.attackAnimTime.setBaseValue(500);
      this.attackDamage.setBaseValue(27.0F).setUpgradedValue(1.0F, 70.0F);
      this.attackRange.setBaseValue(140);
      this.knockback.setBaseValue(100);
      this.width = 20.0F;
      this.attackXOffset = 54;
      this.attackYOffset = 54;
   }
}
