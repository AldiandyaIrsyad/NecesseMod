package necesse.inventory.item.toolItem.swordToolItem;

import necesse.engine.localization.Localization;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.levelEvent.toolItemEvent.ToolItemEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class SandKnifeToolItem extends SwordToolItem {
   public SandKnifeToolItem() {
      super(1200);
      this.rarity = Item.Rarity.UNCOMMON;
      this.attackAnimTime.setBaseValue(200);
      this.attackDamage.setBaseValue(5.0F).setUpgradedValue(1.0F, 10.0F);
      this.attackRange.setBaseValue(30);
      this.knockback.setBaseValue(25);
      this.attackXOffset = 10;
      this.attackYOffset = 10;
   }

   public ListGameTooltips getPreEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getPreEnchantmentTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "sandknifetip"));
      return var4;
   }

   public void hitMob(InventoryItem var1, ToolItemEvent var2, Level var3, Mob var4, Mob var5) {
      super.hitMob(var1, var2, var3, var4, var5);
      var4.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.SAND_KNIFE_WOUND_BUFF, var4, 5000, var5), true);
   }
}
