package necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem;

import necesse.inventory.item.Item;

public class CopperBowProjectileToolItem extends BowProjectileToolItem {
   public CopperBowProjectileToolItem() {
      super(200);
      this.rarity = Item.Rarity.NORMAL;
      this.attackAnimTime.setBaseValue(700);
      this.attackDamage.setBaseValue(15.0F).setUpgradedValue(1.0F, 125.0F);
      this.attackRange.setBaseValue(650);
      this.velocity.setBaseValue(120);
      this.attackXOffset = 8;
      this.attackYOffset = 20;
   }
}
