package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemInventoryItem;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.ArmorBuff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

public abstract class TrinketBuff extends ArmorBuff {
   public TrinketBuff() {
   }

   public final ListGameTooltips getTooltip(ActiveBuff var1, GameBlackboard var2) {
      ListGameTooltips var3 = super.getTooltip(var1, var2);
      GNDItem var4 = var1.getGndData().getItem("trinketItem");
      if (var4 instanceof GNDItemInventoryItem) {
         GNDItemInventoryItem var5 = (GNDItemInventoryItem)var4;
         InventoryItem var6 = var5.invItem;
         if (var6 != null && var6.item instanceof TrinketItem) {
            var3.addAll(this.getTrinketTooltip((TrinketItem)var6.item, var6, var1.owner.isPlayer ? (PlayerMob)var1.owner : null));
         }
      }

      return var3;
   }

   public ListGameTooltips getTrinketTooltip(TrinketItem var1, InventoryItem var2, PlayerMob var3) {
      return new ListGameTooltips();
   }
}
