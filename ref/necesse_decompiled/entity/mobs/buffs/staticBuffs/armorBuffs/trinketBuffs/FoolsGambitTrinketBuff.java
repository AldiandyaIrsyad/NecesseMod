package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import necesse.engine.localization.Localization;
import necesse.engine.network.Packet;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffAbility;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

public class FoolsGambitTrinketBuff extends TrinketBuff implements BuffAbility {
   public FoolsGambitTrinketBuff() {
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      var1.setModifier(BuffModifiers.ARMOR_FLAT, 10);
      var1.setModifier(BuffModifiers.ALL_DAMAGE, 0.1F);
      var1.setModifier(BuffModifiers.CRIT_CHANCE, 0.1F);
      var1.setModifier(BuffModifiers.MAX_SUMMONS, 1);
   }

   public void runAbility(PlayerMob var1, ActiveBuff var2, Packet var3) {
   }

   public boolean canRunAbility(PlayerMob var1, ActiveBuff var2, Packet var3) {
      return true;
   }

   public ListGameTooltips getTrinketTooltip(TrinketItem var1, InventoryItem var2, PlayerMob var3) {
      ListGameTooltips var4 = super.getTrinketTooltip(var1, var2, var3);
      var4.add((String)Localization.translate("itemtooltip", "foolsgambittip"), 400);
      return var4;
   }
}
