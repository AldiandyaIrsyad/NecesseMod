package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import necesse.engine.localization.Localization;
import necesse.engine.network.packet.PacketLifelineEvent;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.MobBeforeHitCalculatedEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;
import necesse.level.maps.Level;

public class LifelineBuff extends TrinketBuff {
   public LifelineBuff() {
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
   }

   public ListGameTooltips getTrinketTooltip(TrinketItem var1, InventoryItem var2, PlayerMob var3) {
      ListGameTooltips var4 = super.getTrinketTooltip(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "lifeline"));
      return var4;
   }

   public void onBeforeHitCalculated(ActiveBuff var1, MobBeforeHitCalculatedEvent var2) {
      super.onBeforeHitCalculated(var1, var2);
      Level var3 = var1.owner.getLevel();
      if (var3.isServer() && !var1.owner.buffManager.hasBuff(BuffRegistry.Debuffs.LIFELINE_COOLDOWN.getID()) && var2.getExpectedHealth() <= 0) {
         var2.prevent();
         var1.owner.setHealth(Math.max(10, var1.owner.getMaxHealth() / 4));
         var1.owner.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.LIFELINE_COOLDOWN, var1.owner, 300.0F, (Attacker)null), true);
         var3.getServer().network.sendToClientsAt(new PacketLifelineEvent(var1.owner.getUniqueID()), (Level)var3);
      }

   }
}
