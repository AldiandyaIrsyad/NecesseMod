package necesse.inventory.item.toolItem.pickaxeToolItem;

import necesse.engine.localization.Localization;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.item.toolItem.ToolDamageItem;
import necesse.inventory.item.toolItem.ToolType;

public class PickaxeToolItem extends ToolDamageItem {
   public PickaxeToolItem(int var1) {
      super(var1);
      this.setItemCategory(new String[]{"equipment", "tools", "pickaxes"});
      this.keyWords.add("pickaxe");
      this.toolType = ToolType.PICKAXE;
      this.animAttacks = 2;
      this.width = 10.0F;
   }

   protected void addToolTooltips(ListGameTooltips var1) {
      var1.add(Localization.translate("itemtooltip", "pickaxetip"));
   }
}
