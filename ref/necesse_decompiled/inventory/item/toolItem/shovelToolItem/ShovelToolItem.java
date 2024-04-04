package necesse.inventory.item.toolItem.shovelToolItem;

import necesse.engine.localization.Localization;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.item.toolItem.ToolDamageItem;
import necesse.inventory.item.toolItem.ToolType;

public class ShovelToolItem extends ToolDamageItem {
   public ShovelToolItem(int var1) {
      super(var1);
      this.setItemCategory(new String[]{"equipment", "tools", "shovels"});
      this.keyWords.add("shovel");
      this.toolType = ToolType.SHOVEL;
      this.animInverted = true;
      this.animAttacks = 2;
      this.width = 10.0F;
   }

   protected void addToolTooltips(ListGameTooltips var1) {
      var1.add(Localization.translate("itemtooltip", "shoveltip"));
   }
}
