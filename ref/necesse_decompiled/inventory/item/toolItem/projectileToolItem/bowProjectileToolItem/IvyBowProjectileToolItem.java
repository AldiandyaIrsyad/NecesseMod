package necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem;

import necesse.inventory.item.Item;

public class IvyBowProjectileToolItem extends BowProjectileToolItem {
   public IvyBowProjectileToolItem() {
      super(900);
      this.rarity = Item.Rarity.UNCOMMON;
      this.attackAnimTime.setBaseValue(500);
      this.attackDamage.setBaseValue(29.0F).setUpgradedValue(1.0F, 103.0F);
      this.attackRange.setBaseValue(800);
      this.velocity.setBaseValue(180);
      this.attackXOffset = 8;
      this.attackYOffset = 26;
   }
}
