package necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem;

import necesse.inventory.item.Item;

public class SpiderBoomerangToolItem extends BoomerangToolItem {
   public SpiderBoomerangToolItem() {
      super(350, "spiderboomerang");
      this.rarity = Item.Rarity.NORMAL;
      this.attackAnimTime.setBaseValue(300);
      this.attackDamage.setBaseValue(20.0F).setUpgradedValue(1.0F, 85.0F);
      this.attackRange.setBaseValue(400);
      this.velocity.setBaseValue(125);
      this.settlerProjectileCanHitWidth = 8.0F;
   }
}
