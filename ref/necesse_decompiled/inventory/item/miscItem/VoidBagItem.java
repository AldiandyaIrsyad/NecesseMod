package necesse.inventory.item.miscItem;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class VoidBagItem extends CloudInventoryOpenItem {
   public VoidBagItem() {
      super(false, 0, 39);
      this.setItemCategory(new String[]{"misc", "pouches"});
      this.rarity = Item.Rarity.LEGENDARY;
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "voidbagtip"));
      var4.add(Localization.translate("itemtooltip", "rclickopentip"));
      return var4;
   }
}
