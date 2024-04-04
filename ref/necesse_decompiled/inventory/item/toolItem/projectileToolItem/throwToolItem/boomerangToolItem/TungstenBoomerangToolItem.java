package necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem;

import necesse.inventory.item.Item;

public class TungstenBoomerangToolItem extends BoomerangToolItem {
   public TungstenBoomerangToolItem() {
      super(1000, "tungstenboomerang");
      this.rarity = Item.Rarity.UNCOMMON;
      this.attackAnimTime.setBaseValue(300);
      this.attackCooldownTime.setBaseValue(400);
      this.attackDamage.setBaseValue(45.0F).setUpgradedValue(1.0F, 80.0F);
      this.attackRange.setBaseValue(600);
      this.velocity.setBaseValue(180);
      this.stackSize = 4;
      this.resilienceGain.setBaseValue(0.75F);
      this.knockback.setBaseValue(100);
      this.settlerProjectileCanHitWidth = 18.0F;
   }
}
