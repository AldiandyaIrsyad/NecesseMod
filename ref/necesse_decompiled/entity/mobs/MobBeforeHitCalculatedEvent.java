// Source code is decompiled from a .class file using FernFlower decompiler.
package necesse.entity.mobs;

import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.gameDamageType.DamageType;

public class MobBeforeHitCalculatedEvent {
   public final Mob target;
   public final Attacker attacker;
   public final DamageType damageType;
   public final int damage;
   public final float knockbackX;
   public final float knockbackY;
   public final float knockbackAmount;
   public final boolean isCrit;
   public final GNDItemMap gndData;
   public boolean showDamageTip;
   public boolean playHitSound;
   private boolean prevented;

   public MobBeforeHitCalculatedEvent(MobBeforeHitEvent beforeHitEvent) {
      this.target = beforeHitEvent.target;
      this.attacker = beforeHitEvent.attacker;
      GameDamage damage = beforeHitEvent.damage;
      boolean isCrit = GameRandom.globalRandom.getChance(damage == null ? 0.0F : damage.getBuffedCritChance(this.attacker));
      float critMod = 1.0F;
      if (isCrit) {
         Mob attackOwner = this.attacker.getAttackOwner();
         if (attackOwner != null) {
            critMod = attackOwner.getCritDamageModifier();
         } else {
            critMod = (Float)BuffModifiers.CRIT_DAMAGE.defaultBuffManagerValue;
         }

         critMod += damage == null ? 0.0F : damage.type.getTypeCritDamageModifier(this.attacker);
      }

      this.damageType = beforeHitEvent.damage.type;
      this.damage = damage == null ? 0 : damage.getTotalDamage(this.target, this.attacker, critMod);
      this.knockbackX = beforeHitEvent.knockbackX;
      this.knockbackY = beforeHitEvent.knockbackY;
      this.knockbackAmount = beforeHitEvent.knockbackAmount;
      this.isCrit = isCrit;
      this.showDamageTip = beforeHitEvent.showDamageTip;
      this.playHitSound = beforeHitEvent.playHitSound;
      this.prevented = beforeHitEvent.isPrevented();
      this.gndData = beforeHitEvent.gndData;
   }

   public void prevent() {
      this.prevented = true;
   }

   public boolean isPrevented() {
      return this.prevented;
   }

   public int getExpectedHealth() {
      return this.prevented ? this.target.getHealth() : Math.max(this.target.getHealth() - this.damage, 0);
   }
}
