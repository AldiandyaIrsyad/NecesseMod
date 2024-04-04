package necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.greatbowProjectileToolItem;

import java.awt.Color;
import necesse.inventory.item.Item;

public class TungstenGreatbowProjectileToolItem extends GreatbowProjectileToolItem {
   public TungstenGreatbowProjectileToolItem() {
      super(1000);
      this.rarity = Item.Rarity.UNCOMMON;
      this.attackAnimTime.setBaseValue(600);
      this.attackDamage.setBaseValue(95.0F).setUpgradedValue(1.0F, 146.0F);
      this.attackRange.setBaseValue(1200);
      this.velocity.setBaseValue(400);
      this.attackXOffset = 10;
      this.attackYOffset = 36;
      this.particleColor = new Color(56, 70, 84);
   }
}
