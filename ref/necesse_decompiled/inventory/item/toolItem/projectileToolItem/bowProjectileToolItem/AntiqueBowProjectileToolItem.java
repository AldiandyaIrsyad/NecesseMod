package necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem;

import necesse.inventory.item.Item;

public class AntiqueBowProjectileToolItem extends BowProjectileToolItem {
   public AntiqueBowProjectileToolItem() {
      super(1400);
      this.rarity = Item.Rarity.RARE;
      this.attackAnimTime.setBaseValue(450);
      this.attackDamage.setBaseValue(90.0F).setUpgradedValue(1.0F, 95.0F);
      this.attackRange.setBaseValue(1000);
      this.velocity.setBaseValue(240);
      this.attackXOffset = 12;
      this.attackYOffset = 12;
   }
}
