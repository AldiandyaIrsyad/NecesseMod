package necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.greatbowProjectileToolItem;

import java.awt.Color;
import necesse.inventory.item.Item;

public class GoldGreatbowProjectileToolItem extends GreatbowProjectileToolItem {
   public GoldGreatbowProjectileToolItem() {
      super(500);
      this.rarity = Item.Rarity.COMMON;
      this.attackAnimTime.setBaseValue(700);
      this.attackDamage.setBaseValue(45.0F).setUpgradedValue(1.0F, 155.0F);
      this.attackRange.setBaseValue(800);
      this.velocity.setBaseValue(300);
      this.attackXOffset = 10;
      this.attackYOffset = 34;
      this.particleColor = new Color(228, 176, 77);
   }
}
