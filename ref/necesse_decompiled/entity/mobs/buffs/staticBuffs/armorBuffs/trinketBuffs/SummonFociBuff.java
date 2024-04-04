package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import necesse.engine.localization.Localization;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

public class SummonFociBuff extends TrinketBuff {
   public SummonFociBuff() {
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      var1.setModifier(BuffModifiers.SUMMON_DAMAGE, 0.2F);
      var1.setModifier(BuffModifiers.MELEE_DAMAGE, -0.2F);
      var1.setModifier(BuffModifiers.RANGED_DAMAGE, -0.2F);
      var1.setModifier(BuffModifiers.MAGIC_DAMAGE, -0.2F);
   }

   public ListGameTooltips getTrinketTooltip(TrinketItem var1, InventoryItem var2, PlayerMob var3) {
      ListGameTooltips var4 = super.getTrinketTooltip(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "summonfoci1"));
      var4.add(Localization.translate("itemtooltip", "summonfoci2"));
      return var4;
   }
}
