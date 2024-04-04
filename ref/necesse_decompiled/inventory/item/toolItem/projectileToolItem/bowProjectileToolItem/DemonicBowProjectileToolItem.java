package necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem;

import necesse.inventory.item.Item;

public class DemonicBowProjectileToolItem extends BowProjectileToolItem {
   public DemonicBowProjectileToolItem() {
      super(800);
      this.rarity = Item.Rarity.COMMON;
      this.attackAnimTime.setBaseValue(500);
      this.attackDamage.setBaseValue(25.0F).setUpgradedValue(1.0F, 105.0F);
      this.attackRange.setBaseValue(750);
      this.velocity.setBaseValue(180);
      this.attackXOffset = 8;
      this.attackYOffset = 26;
   }
}
