package necesse.entity.mobs.buffs.staticBuffs;

import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;

public class SleepingBuff extends Buff {
   public SleepingBuff() {
      this.isVisible = false;
      this.shouldSave = false;
      this.canCancel = false;
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      var1.setModifier(BuffModifiers.PARALYZED, true);
      GNDItemMap var3 = var1.getGndData();
      float var4 = var3.getFloat("regen");
      if (var4 == 0.0F) {
         var4 = Math.max((float)var1.owner.getMaxHealth() / 20.0F, 5.0F);
         var3.setFloat("regen", var4);
      }

      float var5 = var3.getFloat("manaRegen");
      if (var5 == 0.0F) {
         var5 = Math.max((float)var1.owner.getMaxMana() / 20.0F, 5.0F);
         var3.setFloat("manaRegen", var5);
      }

      var1.setModifier(BuffModifiers.HEALTH_REGEN_FLAT, var4);
      var1.setModifier(BuffModifiers.MANA_REGEN_FLAT, var5);
   }

   public void onWasHit(ActiveBuff var1, MobWasHitEvent var2) {
      super.onWasHit(var1, var2);
      if (!var2.wasPrevented) {
         if (var1.owner.isPlayer && var1.owner.isServer()) {
            PlayerMob var3 = (PlayerMob)var1.owner;
            ServerClient var4 = var3.getServerClient();
            if (var4 != null) {
               var4.closeContainer(true);
            }
         }

         var1.remove();
      }

   }
}
