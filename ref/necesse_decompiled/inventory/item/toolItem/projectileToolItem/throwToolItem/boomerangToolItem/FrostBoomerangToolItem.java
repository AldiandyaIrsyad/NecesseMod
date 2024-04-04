package necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem;

import necesse.inventory.item.Item;

public class FrostBoomerangToolItem extends BoomerangToolItem {
   public FrostBoomerangToolItem() {
      super(450, "frostboomerang");
      this.rarity = Item.Rarity.NORMAL;
      this.attackAnimTime.setBaseValue(300);
      this.attackDamage.setBaseValue(24.0F).setUpgradedValue(1.0F, 80.0F);
      this.attackRange.setBaseValue(400);
      this.velocity.setBaseValue(140);
      this.settlerProjectileCanHitWidth = 8.0F;
   }
}
