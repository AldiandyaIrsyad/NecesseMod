package necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.greatbowProjectileToolItem;

import java.awt.Color;
import necesse.inventory.item.Item;

public class VoidGreatbowProjectileToolItem extends GreatbowProjectileToolItem {
   public VoidGreatbowProjectileToolItem() {
      super(800);
      this.rarity = Item.Rarity.UNCOMMON;
      this.attackAnimTime.setBaseValue(600);
      this.attackDamage.setBaseValue(55.0F).setUpgradedValue(1.0F, 150.0F);
      this.attackRange.setBaseValue(1000);
      this.velocity.setBaseValue(350);
      this.attackXOffset = 10;
      this.attackYOffset = 36;
      this.particleColor = new Color(50, 0, 102);
   }
}
