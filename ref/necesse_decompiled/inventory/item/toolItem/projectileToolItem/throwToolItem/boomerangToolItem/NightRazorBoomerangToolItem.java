package necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class NightRazorBoomerangToolItem extends BoomerangToolItem {
   public NightRazorBoomerangToolItem() {
      super(1500, "nightrazorboomerang");
      this.rarity = Item.Rarity.RARE;
      this.attackAnimTime.setBaseValue(300);
      this.attackDamage.setBaseValue(70.0F).setUpgradedValue(1.0F, 70.0F);
      this.attackRange.setBaseValue(600);
      this.velocity.setBaseValue(220);
      this.stackSize = 4;
      this.resilienceGain.setBaseValue(0.5F);
      this.knockback.setBaseValue(50);
      this.settlerProjectileCanHitWidth = 18.0F;
   }

   public ListGameTooltips getPreEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getPreEnchantmentTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "nightrazorboomerangtip"));
      return var4;
   }
}
