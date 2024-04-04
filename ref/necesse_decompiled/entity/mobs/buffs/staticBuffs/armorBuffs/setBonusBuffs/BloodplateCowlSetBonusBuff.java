package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import java.util.LinkedList;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.MobHealthChangedEvent;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.inventory.item.ItemStatTip;

public class BloodplateCowlSetBonusBuff extends BloodPlateSetBonusBuff {
   public BloodplateCowlSetBonusBuff() {
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      super.init(var1, var2);
      var2.subscribeEvent(MobHealthChangedEvent.class, (var1x) -> {
         if (var1x.currentHealth < var1x.lastHealth && !var1x.fromUpdatePacket) {
            ActiveBuff var2 = new ActiveBuff(BuffRegistry.BLOODPLATE_COWL_ACTIVE, var1.owner, 4.0F, (Attacker)null);
            var1.owner.buffManager.addBuff(var2, false);
         }

      });
   }

   public void serverTick(ActiveBuff var1) {
      super.serverTick(var1);
   }

   public void addStatTooltips(LinkedList<ItemStatTip> var1, ActiveBuff var2, ActiveBuff var3) {
      super.addStatTooltips(var1, var2, var3);
      var2.getModifierTooltipsBuilder(true, true).addLastValues(var3).buildToStatList(var1);
   }
}
