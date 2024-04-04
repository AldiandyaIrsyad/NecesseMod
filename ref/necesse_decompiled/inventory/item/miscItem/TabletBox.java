package necesse.inventory.item.miscItem;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class TabletBox extends PouchItem {
   public TabletBox() {
      this.rarity = Item.Rarity.EPIC;
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "tabletboxtip"));
      var4.add(Localization.translate("itemtooltip", "rclickinvopentip"));
      return var4;
   }

   public boolean isValidPouchItem(InventoryItem var1) {
      return this.isValidRequestItem(var1.item);
   }

   public boolean isValidRequestItem(Item var1) {
      return var1 instanceof GatewayTabletItem;
   }

   public boolean isValidRequestType(Item.Type var1) {
      return false;
   }

   public int getInternalInventorySize() {
      return 40;
   }
}
