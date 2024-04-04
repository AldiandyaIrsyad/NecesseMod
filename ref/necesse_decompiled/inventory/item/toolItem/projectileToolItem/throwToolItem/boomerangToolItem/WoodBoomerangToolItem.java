package necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem;

import necesse.inventory.item.Item;

public class WoodBoomerangToolItem extends BoomerangToolItem {
   public WoodBoomerangToolItem() {
      super(200, "woodboomerang");
      this.rarity = Item.Rarity.NORMAL;
      this.attackAnimTime.setBaseValue(300);
      this.attackDamage.setBaseValue(15.0F).setUpgradedValue(1.0F, 90.0F);
      this.attackRange.setBaseValue(200);
      this.velocity.setBaseValue(100);
      this.settlerProjectileCanHitWidth = 10.0F;
   }
}
