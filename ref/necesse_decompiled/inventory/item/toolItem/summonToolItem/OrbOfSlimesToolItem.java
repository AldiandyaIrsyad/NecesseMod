package necesse.inventory.item.toolItem.summonToolItem;

import necesse.engine.localization.Localization;
import necesse.engine.network.server.FollowPosition;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class OrbOfSlimesToolItem extends SummonToolItem {
   public OrbOfSlimesToolItem() {
      super("orbofslimesslime", FollowPosition.SLIME_CIRCLE_MOVEMENT, 1.0F, 1600);
      this.rarity = Item.Rarity.EPIC;
      this.attackDamage.setBaseValue(41.0F).setUpgradedValue(1.0F, 44.0F);
      this.attackXOffset = 15;
      this.attackYOffset = 10;
   }

   public ListGameTooltips getPreEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getPreEnchantmentTooltips(var1, var2, var3);
      var4.add((String)Localization.translate("itemtooltip", "orbofslimestip"), 400);
      return var4;
   }
}
