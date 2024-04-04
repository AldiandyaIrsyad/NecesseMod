package necesse.inventory.item.toolItem.miscToolItem;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.ToolDamageItem;
import necesse.inventory.item.toolItem.ToolType;

public class TestToolItem extends ToolDamageItem {
   public TestToolItem() {
      super(0);
      this.setItemCategory(new String[]{"equipment", "tools"});
      this.toolType = ToolType.ALL;
      this.attackAnimTime.setBaseValue(100);
      this.toolDps.setBaseValue(10000);
      this.toolTier.setBaseValue(100000000);
      this.attackDamage.setBaseValue(0.0F);
      this.attackRange.setBaseValue(200);
      this.knockback.setBaseValue(500);
      this.animInverted = true;
   }

   public GameMessage getNewLocalization() {
      return new StaticMessage("Dev Cheat Tool");
   }

   protected void addToolTooltips(ListGameTooltips var1) {
   }

   public ListGameTooltips getPreEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getPreEnchantmentTooltips(var1, var2, var3);
      var4.add("NOT OBTAINABLE");
      return var4;
   }
}
