package necesse.inventory.item.toolItem.spearToolItem;

import necesse.inventory.item.Item;

public class VulturesTalonToolItem extends SpearToolItem {
   public VulturesTalonToolItem() {
      super(1000);
      this.rarity = Item.Rarity.RARE;
      this.attackAnimTime.setBaseValue(400);
      this.attackDamage.setBaseValue(35.0F).setUpgradedValue(1.0F, 60.0F);
      this.attackRange.setBaseValue(150);
      this.knockback.setBaseValue(50);
      this.width = 12.0F;
   }
}
