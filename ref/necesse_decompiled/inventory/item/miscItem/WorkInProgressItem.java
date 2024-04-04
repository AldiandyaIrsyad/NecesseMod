package necesse.inventory.item.miscItem;

import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class WorkInProgressItem extends Item {
   public WorkInProgressItem() {
      super(100);
      this.rarity = Item.Rarity.UNIQUE;
      this.worldDrawSize = 32;
   }

   public GameMessage getNewLocalization() {
      return new LocalMessage("misc", "workinprogress");
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add(Localization.translate("misc", "workinprogresstip"));
      return var4;
   }
}
