package necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class RazorBladeBoomerangToolItem extends BoomerangToolItem {
   public RazorBladeBoomerangToolItem() {
      super(800, "razorbladeboomerang");
      this.rarity = Item.Rarity.RARE;
      this.attackAnimTime.setBaseValue(180);
      this.attackDamage.setBaseValue(20.0F).setUpgradedValue(1.0F, 40.0F);
      this.attackRange.setBaseValue(250);
      this.velocity.setBaseValue(125);
      this.resilienceGain.setBaseValue(0.2F);
      this.knockback.setBaseValue(50);
      this.settlerProjectileCanHitWidth = 10.0F;
   }

   public ListGameTooltips getPreEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getPreEnchantmentTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "razorbladeboomerangtip"));
      return var4;
   }

   public String canAttack(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5) {
      return var4.getBoomerangsUsage() < 5 ? null : "";
   }

   protected boolean canSettlerBoomerangAttack(HumanMob var1, Mob var2, InventoryItem var3) {
      return var1.getBoomerangsUsage() < 5;
   }
}
