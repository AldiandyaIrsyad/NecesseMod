package necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem;

import necesse.inventory.item.Item;

public class TungstenBowProjectileToolItem extends BowProjectileToolItem {
   public TungstenBowProjectileToolItem() {
      super(1000);
      this.rarity = Item.Rarity.UNCOMMON;
      this.attackAnimTime.setBaseValue(500);
      this.attackDamage.setBaseValue(42.0F).setUpgradedValue(1.0F, 98.0F);
      this.attackRange.setBaseValue(800);
      this.velocity.setBaseValue(200);
      this.attackXOffset = 12;
      this.attackYOffset = 28;
   }
}
