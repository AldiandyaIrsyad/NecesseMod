package necesse.inventory.item.toolItem.swordToolItem;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.inventory.item.Item;

public class MLG2SwordToolItem extends SwordToolItem {
   public MLG2SwordToolItem() {
      super(2000);
      this.rarity = Item.Rarity.LEGENDARY;
      this.attackAnimTime.setBaseValue(300);
      this.attackDamage.setBaseValue(5000.0F);
      this.attackRange.setBaseValue(400);
      this.knockback.setBaseValue(75);
   }

   public GameMessage getNewLocalization() {
      return new StaticMessage("MLG 2");
   }
}
