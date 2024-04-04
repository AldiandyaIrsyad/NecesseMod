package necesse.inventory.item.miscItem;

import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class TelescopeItem extends Item {
   public TelescopeItem() {
      super(1);
      this.setItemCategory(new String[]{"equipment", "tools"});
      this.rarity = Item.Rarity.LEGENDARY;
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add("NOT OBTAINABLE");
      return var4;
   }

   public float zoomAmount() {
      return 3000.0F;
   }
}
