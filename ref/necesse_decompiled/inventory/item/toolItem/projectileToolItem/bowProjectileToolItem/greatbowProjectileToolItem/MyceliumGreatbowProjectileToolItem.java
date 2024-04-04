package necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.greatbowProjectileToolItem;

import java.awt.Color;
import necesse.inventory.item.Item;

public class MyceliumGreatbowProjectileToolItem extends GreatbowProjectileToolItem {
   public MyceliumGreatbowProjectileToolItem() {
      super(1400);
      this.rarity = Item.Rarity.UNCOMMON;
      this.attackAnimTime.setBaseValue(600);
      this.attackDamage.setBaseValue(110.0F).setUpgradedValue(1.0F, 144.0F);
      this.attackRange.setBaseValue(1400);
      this.velocity.setBaseValue(425);
      this.attackXOffset = 10;
      this.attackYOffset = 36;
      this.particleColor = new Color(230, 108, 14);
   }
}
