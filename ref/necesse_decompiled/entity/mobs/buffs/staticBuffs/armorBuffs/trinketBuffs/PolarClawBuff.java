package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import necesse.engine.localization.Localization;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

public class PolarClawBuff extends TrinketBuff {
   public PolarClawBuff() {
   }

   public ListGameTooltips getTrinketTooltip(TrinketItem var1, InventoryItem var2, PlayerMob var3) {
      ListGameTooltips var4 = super.getTrinketTooltip(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "polarclawtip"));
      return var4;
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
   }

   public void onHasAttacked(ActiveBuff var1, MobWasHitEvent var2) {
      super.onHasAttacked(var1, var2);
      if (!var2.wasPrevented) {
         var2.target.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.CHILLED, var2.target, 2.0F, var2.attacker), var2.target.isServer());
      }

   }
}
