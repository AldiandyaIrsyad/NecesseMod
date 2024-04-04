package necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem;

import necesse.inventory.item.Item;

public class VoidBoomerangToolItem extends BoomerangToolItem {
   public VoidBoomerangToolItem() {
      super(700, "voidboomerang");
      this.rarity = Item.Rarity.RARE;
      this.attackAnimTime.setBaseValue(300);
      this.attackDamage.setBaseValue(25.0F).setUpgradedValue(1.0F, 65.0F);
      this.attackRange.setBaseValue(400);
      this.velocity.setBaseValue(150);
      this.stackSize = 3;
      this.resilienceGain.setBaseValue(0.5F);
      this.settlerProjectileCanHitWidth = 10.0F;
   }
}
