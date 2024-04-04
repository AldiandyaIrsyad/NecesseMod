package necesse.inventory.item.toolItem;

import necesse.engine.localization.Localization;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.item.Item;

public class MultiToolItem extends ToolDamageItem {
   public MultiToolItem(int var1) {
      super(var1);
      this.setItemCategory(new String[]{"equipment", "tools"});
      this.keyWords.add("pickaxe, axe, shovel");
      this.toolType = ToolType.ALL;
      this.animAttacks = 2;
      this.width = 10.0F;
      this.attackAnimTime.setBaseValue(450);
      this.toolDps.setBaseValue(150);
      this.toolTier.setBaseValue(5);
      this.attackDamage.setBaseValue(25.0F);
      this.attackRange.setBaseValue(50);
      this.knockback.setBaseValue(50);
      this.rarity = Item.Rarity.EPIC;
      this.enchantCost.setUpgradedValue(1.0F, 1400);
   }

   protected void addToolTooltips(ListGameTooltips var1) {
      var1.add(Localization.translate("itemtooltip", "multitooltip"));
   }
}
