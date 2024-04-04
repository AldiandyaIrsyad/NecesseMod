package necesse.inventory.item.toolItem.swordToolItem;

import necesse.inventory.item.Item;

public class IvySwordToolItem extends SwordToolItem {
   public IvySwordToolItem() {
      super(800);
      this.rarity = Item.Rarity.UNCOMMON;
      this.attackAnimTime.setBaseValue(300);
      this.attackDamage.setBaseValue(34.0F).setUpgradedValue(1.0F, 82.0F);
      this.attackRange.setBaseValue(65);
      this.knockback.setBaseValue(80);
   }
}
