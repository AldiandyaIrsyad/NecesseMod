package necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem;

import necesse.inventory.item.Item;

public class WoodBowProjectileToolItem extends BowProjectileToolItem {
   public WoodBowProjectileToolItem() {
      super(100);
      this.rarity = Item.Rarity.NORMAL;
      this.attackAnimTime.setBaseValue(800);
      this.attackDamage.setBaseValue(12.0F).setUpgradedValue(1.0F, 130.0F);
      this.attackRange.setBaseValue(600);
      this.velocity.setBaseValue(100);
      this.attackXOffset = 8;
      this.attackYOffset = 20;
   }
}
