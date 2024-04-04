package necesse.inventory.item.miscItem;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class BinocularsItem extends Item {
   public BinocularsItem() {
      super(1);
      this.setItemCategory(new String[]{"equipment", "tools"});
      this.rarity = Item.Rarity.UNCOMMON;
      this.worldDrawSize = 32;
      this.incinerationTimeMillis = 30000;
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "binocularstip"));
      return var4;
   }

   public float zoomAmount() {
      return 600.0F;
   }
}
