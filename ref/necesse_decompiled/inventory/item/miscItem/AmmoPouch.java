package necesse.inventory.item.miscItem;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class AmmoPouch extends PouchItem {
   public AmmoPouch() {
      this.rarity = Item.Rarity.UNCOMMON;
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "ammopouchtip1"));
      var4.add(Localization.translate("itemtooltip", "ammopouchtip2"));
      var4.add(Localization.translate("itemtooltip", "rclickinvopentip"));
      var4.add(Localization.translate("itemtooltip", "storedammo", "items", (Object)this.getStoredItemAmounts(var1)));
      return var4;
   }

   public boolean isValidPouchItem(InventoryItem var1) {
      return this.isValidRequestType(var1.item.type);
   }

   public boolean isValidRequestItem(Item var1) {
      return this.isValidRequestType(var1.type);
   }

   public boolean isValidRequestType(Item.Type var1) {
      return var1 == Item.Type.ARROW || var1 == Item.Type.BULLET;
   }

   public int getInternalInventorySize() {
      return 10;
   }
}
