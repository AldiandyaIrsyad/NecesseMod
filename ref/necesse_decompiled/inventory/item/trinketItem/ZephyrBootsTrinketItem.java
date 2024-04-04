package necesse.inventory.item.trinketItem;

import necesse.engine.localization.Localization;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class ZephyrBootsTrinketItem extends TrinketItem {
   public ZephyrBootsTrinketItem() {
      super(Item.Rarity.RARE, 400);
   }

   public ListGameTooltips getPreEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getPreEnchantmentTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "sprinttip"));
      var4.add(Localization.translate("itemtooltip", "staminausertip"));
      var4.add(Localization.translate("itemtooltip", "zephyrbootstip"));
      return var4;
   }

   public TrinketBuff[] getBuffs(InventoryItem var1) {
      return new TrinketBuff[]{(TrinketBuff)BuffRegistry.getBuff("zephyrbootstrinket")};
   }
}
