package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import java.util.LinkedList;
import necesse.engine.localization.Localization;
import necesse.engine.network.packet.PacketQuartzSetEvent;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.item.ItemStatTip;
import necesse.level.maps.Level;

public class QuartzHelmetSetBonusBuff extends SetBonusBuff {
   public QuartzHelmetSetBonusBuff() {
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
   }

   public void tickEffect(ActiveBuff var1, Mob var2) {
   }

   public void onWasHit(ActiveBuff var1, MobWasHitEvent var2) {
      super.onWasHit(var1, var2);
      if (!var2.wasPrevented) {
         Mob var3 = var1.owner;
         Level var4 = var3.getLevel();
         if (var4.isServer() && !var3.buffManager.hasBuff(BuffRegistry.Debuffs.QUARTZ_SET_COOLDOWN.getID())) {
            var3.buffManager.addBuff(new ActiveBuff(BuffRegistry.MOVE_SPEED_BURST, var3, 2.0F, (Attacker)null), true);
            var3.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.QUARTZ_SET_COOLDOWN, var3, 20.0F, (Attacker)null), true);
            var4.getServer().network.sendToClientsAt(new PacketQuartzSetEvent(var1.owner.getUniqueID()), (Level)var4);
         }
      }

   }

   public ListGameTooltips getTooltip(ActiveBuff var1, GameBlackboard var2) {
      ListGameTooltips var3 = super.getTooltip(var1, var2);
      var3.add(Localization.translate("itemtooltip", "quartzset"));
      return var3;
   }

   public void addStatTooltips(LinkedList<ItemStatTip> var1, ActiveBuff var2, ActiveBuff var3) {
      super.addStatTooltips(var1, var2, var3);
      var2.getModifierTooltipsBuilder(true, true).addLastValues(var3).buildToStatList(var1);
   }
}
