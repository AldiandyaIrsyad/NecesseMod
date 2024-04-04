package necesse.inventory.item.toolItem.swordToolItem;

import necesse.inventory.item.Item;

public class AntiqueSwordSwordToolItem extends SwordToolItem {
   public AntiqueSwordSwordToolItem() {
      super(1400);
      this.rarity = Item.Rarity.RARE;
      this.attackAnimTime.setBaseValue(300);
      this.attackDamage.setBaseValue(80.0F).setUpgradedValue(1.0F, 82.0F);
      this.attackRange.setBaseValue(90);
      this.knockback.setBaseValue(125);
   }
}
