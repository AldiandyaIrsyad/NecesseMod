package necesse.inventory.item.toolItem.swordToolItem;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.inventory.item.Item;

public class MLG1SwordToolItem extends SwordToolItem {
   public MLG1SwordToolItem() {
      super(2000);
      this.rarity = Item.Rarity.LEGENDARY;
      this.attackAnimTime.setBaseValue(300);
      this.attackDamage.setBaseValue(1000.0F);
      this.attackRange.setBaseValue(200);
      this.knockback.setBaseValue(75);
   }

   public GameMessage getNewLocalization() {
      return new StaticMessage("MLG 1");
   }
}
