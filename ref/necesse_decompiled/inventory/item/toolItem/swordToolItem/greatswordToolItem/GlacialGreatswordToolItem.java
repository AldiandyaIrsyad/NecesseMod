package necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem;

import necesse.inventory.item.Item;

public class GlacialGreatswordToolItem extends GreatswordToolItem {
   public GlacialGreatswordToolItem() {
      super(900, getThreeChargeLevels(500, 600, 700));
      this.rarity = Item.Rarity.UNCOMMON;
      this.attackDamage.setBaseValue(125.0F).setUpgradedValue(1.0F, 150.0F);
      this.attackRange.setBaseValue(130);
      this.knockback.setBaseValue(150);
      this.attackXOffset = 12;
      this.attackYOffset = 14;
   }
}
