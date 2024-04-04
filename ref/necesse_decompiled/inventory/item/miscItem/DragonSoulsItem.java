package necesse.inventory.item.miscItem;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class DragonSoulsItem extends Item {
   public DragonSoulsItem() {
      super(1);
      this.setItemCategory(new String[]{"consumable", "bossitems"});
      this.dropsAsMatDeathPenalty = true;
      this.keyWords.add("boss");
      this.rarity = Item.Rarity.LEGENDARY;
      this.worldDrawSize = 32;
      this.incinerationTimeMillis = 30000;
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "dragonsoulstip"));
      return var4;
   }
}
