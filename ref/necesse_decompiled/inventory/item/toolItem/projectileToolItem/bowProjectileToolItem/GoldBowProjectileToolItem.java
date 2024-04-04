package necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem;

import necesse.inventory.item.Item;

public class GoldBowProjectileToolItem extends BowProjectileToolItem {
   public GoldBowProjectileToolItem() {
      super(450);
      this.rarity = Item.Rarity.NORMAL;
      this.attackAnimTime.setBaseValue(600);
      this.attackDamage.setBaseValue(20.0F).setUpgradedValue(1.0F, 115.0F);
      this.attackRange.setBaseValue(700);
      this.velocity.setBaseValue(140);
      this.attackXOffset = 8;
      this.attackYOffset = 24;
   }
}
