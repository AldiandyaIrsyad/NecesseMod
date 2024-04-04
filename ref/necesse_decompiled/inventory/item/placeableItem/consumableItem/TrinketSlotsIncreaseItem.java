package necesse.inventory.item.placeableItem.consumableItem;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class TrinketSlotsIncreaseItem extends ChangeTrinketSlotsItem {
   public TrinketSlotsIncreaseItem(int var1) {
      super(var1);
      this.rarity = Item.Rarity.UNIQUE;
      this.allowRightClickToConsume = true;
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "trinketinctip", "number", (Object)this.trinketSlots));
      return var4;
   }

   public float getMaxSinking(ItemPickupEntity var1) {
      return Math.min(super.getMaxSinking(var1), 0.25F);
   }
}
