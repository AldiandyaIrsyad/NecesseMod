package necesse.inventory.item.toolItem.glaiveToolItem;

import necesse.inventory.item.Item;

public class GoldGlaiveToolItem extends GlaiveToolItem {
   public GoldGlaiveToolItem() {
      super(450);
      this.rarity = Item.Rarity.NORMAL;
      this.attackAnimTime.setBaseValue(500);
      this.attackDamage.setBaseValue(18.0F).setUpgradedValue(1.0F, 70.0F);
      this.attackRange.setBaseValue(140);
      this.knockback.setBaseValue(75);
      this.width = 20.0F;
      this.attackXOffset = 40;
      this.attackYOffset = 40;
   }
}
