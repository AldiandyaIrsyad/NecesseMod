package necesse.inventory.item.matItem;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class MatItem extends Item {
   private String tooltipKey;

   public MatItem(int var1, String... var2) {
      super(var1);
      this.tooltipKey = null;
      this.dropsAsMatDeathPenalty = true;
      this.setItemCategory(new String[]{"materials"});
      this.keyWords.add("material");
      this.addGlobalIngredient(var2);
   }

   public MatItem(int var1, Item.Rarity var2, String... var3) {
      this(var1, var3);
      this.rarity = var2;
   }

   public MatItem(int var1, Item.Rarity var2, String var3) {
      this(var1, var2);
      this.tooltipKey = var3;
   }

   public MatItem(int var1, Item.Rarity var2, String var3, String... var4) {
      this(var1, var2, var4);
      this.tooltipKey = var3;
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      if (this.tooltipKey != null) {
         var4.add(Localization.translate("itemtooltip", this.tooltipKey));
      }

      return var4;
   }
}
