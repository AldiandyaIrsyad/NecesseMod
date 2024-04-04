package necesse.inventory.item.toolItem.swordToolItem;

import necesse.inventory.item.Item;

public class CutlassSwordToolItem extends SwordToolItem {
   public CutlassSwordToolItem() {
      super(900);
      this.rarity = Item.Rarity.RARE;
      this.attackAnimTime.setBaseValue(300);
      this.attackDamage.setBaseValue(40.0F).setUpgradedValue(1.0F, 80.0F);
      this.attackRange.setBaseValue(65);
      this.knockback.setBaseValue(80);
      this.attackXOffset = 8;
      this.attackYOffset = 8;
   }
}
