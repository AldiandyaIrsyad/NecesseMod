package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import necesse.engine.localization.Localization;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

public class ClockworkHeartBuff extends TrinketBuff {
   public ClockworkHeartBuff() {
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      this.updateModifiers(var1);
   }

   public ListGameTooltips getTrinketTooltip(TrinketItem var1, InventoryItem var2, PlayerMob var3) {
      ListGameTooltips var4 = super.getTrinketTooltip(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "clockworkhearttip"));
      return var4;
   }

   public void clientTick(ActiveBuff var1) {
      this.updateModifiers(var1);
   }

   public void serverTick(ActiveBuff var1) {
      this.updateModifiers(var1);
   }

   private void updateModifiers(ActiveBuff var1) {
      int var2 = var1.getGndData().getInt("lastMaxHealth");
      int var3 = var1.owner.getMaxHealth() + var2 / 2;
      if (var2 != var3) {
         var1.setModifier(BuffModifiers.MAX_HEALTH_FLAT, -var3 / 2);
         var1.setModifier(BuffModifiers.MAX_RESILIENCE_FLAT, var3 / 2);
         var1.getGndData().setInt("lastMaxHealth", var3);
         var1.owner.buffManager.forceUpdateBuffs();
      }

      Mob var4 = var1.owner;
      if (var4.getHealth() < var4.getMaxHealth()) {
         var1.setModifier(BuffModifiers.RESILIENCE_REGEN_FLAT, 0.0F);
      } else {
         var1.setModifier(BuffModifiers.RESILIENCE_REGEN_FLAT, var4.isInCombat() ? 1.0F + var4.getCombatRegen() : var4.getRegen());
      }

   }
}
