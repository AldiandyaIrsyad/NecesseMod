package necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem;

import necesse.inventory.item.Item;

public class HookBoomerangToolItem extends BoomerangToolItem {
   public HookBoomerangToolItem() {
      super(500, "hookboomerang");
      this.rarity = Item.Rarity.RARE;
      this.attackAnimTime.setBaseValue(300);
      this.attackDamage.setBaseValue(35.0F).setUpgradedValue(1.0F, 80.0F);
      this.attackRange.setBaseValue(400);
      this.velocity.setBaseValue(150);
      this.settlerProjectileCanHitWidth = 8.0F;
   }
}
