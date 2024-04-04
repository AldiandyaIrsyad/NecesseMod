package necesse.entity.mobs.buffs.staticBuffs;

import necesse.engine.localization.message.LocalMessage;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;

public class ThornsPotionBuff extends Buff {
   public ThornsPotionBuff() {
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
   }

   public void onWasHit(ActiveBuff var1, MobWasHitEvent var2) {
      super.onWasHit(var1, var2);
      if (!var2.wasPrevented && var1.owner.isServer()) {
         Mob var3 = var2.attacker != null ? var2.attacker.getAttackOwner() : null;
         boolean var4 = var2.attacker != null && var2.attacker.isInAttackOwnerChain(var1.owner);
         if (var3 != null && !var4) {
            float var5 = (float)(var3.getX() - var1.owner.getX());
            float var6 = (float)(var3.getY() - var1.owner.getY());
            float var7 = (float)var2.damage;
            if (var3.isPlayer) {
               var7 /= 2.0F;
            }

            var3.isServerHit(new GameDamage(var7, 0.0F), var5, var6, 50.0F, var1.owner);
         }
      }

   }

   public void updateLocalDisplayName() {
      this.displayName = new LocalMessage("item", this.getStringID());
   }
}
