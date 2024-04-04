package necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.greatbowProjectileToolItem;

import java.awt.Color;
import necesse.inventory.item.Item;

public class IvyGreatbowProjectileToolItem extends GreatbowProjectileToolItem {
   public IvyGreatbowProjectileToolItem() {
      super(900);
      this.rarity = Item.Rarity.UNCOMMON;
      this.attackAnimTime.setBaseValue(600);
      this.attackDamage.setBaseValue(65.0F).setUpgradedValue(1.0F, 148.0F);
      this.attackRange.setBaseValue(1100);
      this.velocity.setBaseValue(375);
      this.attackXOffset = 10;
      this.attackYOffset = 34;
      this.particleColor = new Color(91, 130, 36);
   }
}
