package necesse.inventory.item.baitItem;

import necesse.engine.localization.Localization;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class BaitItem extends Item {
   public boolean sinks;
   public int fishingPower;

   public BaitItem(boolean var1, int var2) {
      super(250);
      this.sinks = var1;
      this.fishingPower = var2;
      this.setItemCategory(new String[]{"equipment", "bait"});
      this.keyWords.add("bait");
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "baittip"));
      var4.add(Localization.translate("itemtooltip", "fishingpower", "value", this.fishingPower + "%"));
      return var4;
   }

   public float getSinkingRate(ItemPickupEntity var1, float var2) {
      return this.sinks ? Math.max(super.getSinkingRate(var1, var2), TickManager.getTickDelta(60.0F)) : super.getSinkingRate(var1, var2);
   }

   public float getMaxSinking(ItemPickupEntity var1) {
      return this.sinks ? 1.0F : super.getMaxSinking(var1);
   }
}
