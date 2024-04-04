package reimagined.armorPatch;

import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import net.bytebuddy.asm.Advice;

// necesse.entity.mobs.GameDamage
// Class: GameDamage

// public int getTotalDamage(Mob target, Attacker attacker, float critModifier) {
//   float damage = this.getBuffedDamage(attacker);
//   if (target != null) {
//      if (target.isPlayer) {
//         float damageTakenModifier = this.playerDamageMultiplier;
//         if (target.isServer()) {
//            damageTakenModifier *= target.getLevel().getServer().world.settings.difficulty.damageTakenModifier;
//         } else if (target.isClient()) {
//            damageTakenModifier *= target.getLevel().getClient().worldSettings.difficulty.damageTakenModifier;
//         }

//         damage *= damageTakenModifier;
//      }

//      damage = Math.max(0.0F, damage - this.getDamageReduction(target, attacker));
//   }

//   damage *= critModifier;
//   if (target != null) {
//      damage *= target.getIncomingDamageModifier();
//   }

//   damage *= this.finalDamageMultiplier;
//   return (int)Math.max(1.0F, applyRandomizer(damage));
// }

@ModMethodPatch(target = GameDamage.class, name = "getTotalDamage", arguments = {
    Mob.class, Attacker.class, float.class
})
public class RGameDamage {

  // Skip the original method's execution
  @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
  static boolean onEnter() {
    return true;
  }

  @Advice.OnMethodExit
  static void onExit(
      @Advice.Return(readOnly = false) int returnValue,
      @Advice.Argument(0) Mob target,
      @Advice.Argument(1) Attacker attacker,
      @Advice.Argument(2) float critModifier,
      @Advice.This GameDamage instance) {
    float damage = instance.getBuffedDamage(attacker);
    if (target != null) {
      if (target.isPlayer) {
        float damageTakenModifier = instance.playerDamageMultiplier;
        if (target.isServer()) {
          damageTakenModifier *= target.getLevel().getServer().world.settings.difficulty.damageTakenModifier;
        } else if (target.isClient()) {
          damageTakenModifier *= target.getLevel().getClient().worldSettings.difficulty.damageTakenModifier;
        }

        damage *= damageTakenModifier;
      }

      damage = Math.max(0.0F, damage * instance.getDamageReduction(target, attacker));
    }

    // This skip private static float applyRandomizer
    // sadly I'm not able to use the method here
    // 1 day java modder experience moment.
    int endDamage = (int) (GameDamage.staticDamage ? damage
        : damage * GameRandom.globalRandom.getFloatBetween(0.8F, 1.2F));
    returnValue = endDamage;
  }

}
