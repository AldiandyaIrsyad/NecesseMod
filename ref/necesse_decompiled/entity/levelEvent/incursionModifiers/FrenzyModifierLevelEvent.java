package necesse.entity.levelEvent.incursionModifiers;

import necesse.engine.registries.BuffRegistry;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.manager.MobHealthChangeListenerEntityComponent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;

public class FrenzyModifierLevelEvent extends LevelEvent implements MobHealthChangeListenerEntityComponent {
   public FrenzyModifierLevelEvent() {
      super(true);
      this.shouldSave = true;
   }

   public void onLevelMobHealthChanged(Mob var1, int var2, int var3, float var4, float var5, Attacker var6) {
      if (!var1.isPlayer) {
         float var7 = (float)var1.getMaxHealth() * 0.4F;
         if ((float)var3 <= var7) {
            var1.buffManager.addBuff(new ActiveBuff(BuffRegistry.FRENZY, var1, 9999.0F, (Attacker)null), true);
         } else if (var1.buffManager.hasBuff(BuffRegistry.FRENZY) && (float)var3 > var7) {
            var1.buffManager.removeBuff(BuffRegistry.FRENZY, true);
         }
      }

   }
}
