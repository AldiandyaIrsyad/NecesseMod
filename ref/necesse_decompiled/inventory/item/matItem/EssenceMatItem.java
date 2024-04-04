package necesse.inventory.item.matItem;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class EssenceMatItem extends MatItem {
   private int tier;

   public EssenceMatItem(int var1, Item.Rarity var2, int var3) {
      super(var1, var2);
      this.tier = var3;
      this.addGlobalIngredient(new String[]{"anytier" + var3 + "essence"});
      this.setItemCategory(new String[]{"materials", "essences"});
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "tieressence", "tier", (Object)this.tier));
      var4.add(Localization.translate("itemtooltip", "gatewaymaterial"));
      return var4;
   }
}
