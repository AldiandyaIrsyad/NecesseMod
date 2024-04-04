package necesse.inventory.item.toolItem.axeToolItem;

import necesse.engine.localization.Localization;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.item.toolItem.ToolDamageItem;
import necesse.inventory.item.toolItem.ToolType;

public class AxeToolItem extends ToolDamageItem {
   public AxeToolItem(int var1) {
      super(var1);
      this.setItemCategory(new String[]{"equipment", "tools", "axes"});
      this.keyWords.add("axe");
      this.toolType = ToolType.AXE;
      this.animAttacks = 2;
      this.width = 10.0F;
   }

   protected void addToolTooltips(ListGameTooltips var1) {
      var1.add(Localization.translate("itemtooltip", "axetip"));
   }
}
