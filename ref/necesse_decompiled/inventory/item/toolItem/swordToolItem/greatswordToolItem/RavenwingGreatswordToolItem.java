package necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class RavenwingGreatswordToolItem extends GreatswordToolItem {
   public RavenwingGreatswordToolItem() {
      super(1950, getThreeChargeLevels(150, 300, 450));
      this.rarity = Item.Rarity.EPIC;
      this.attackDamage.setBaseValue(150.0F).setUpgradedValue(1.0F, 160.0F);
      this.attackRange.setBaseValue(100);
      this.knockback.setBaseValue(150);
   }

   public ListGameTooltips getPreEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getPreEnchantmentTooltips(var1, var2, var3);
      var4.add((String)Localization.translate("itemtooltip", "ravenwinggreatswordtip"), 400);
      return var4;
   }

   public float getFinalAttackMovementMod(InventoryItem var1, PlayerMob var2) {
      return 1.0F;
   }
}
