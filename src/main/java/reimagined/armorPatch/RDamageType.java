package reimagined.armorPatch;

import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.gameDamageType.DamageType;
import net.bytebuddy.asm.Advice;

// necesse.entity.mobs.gameDamageType.DamageType
// Class: public abstract class DamageType
// Method: public float getDamageReduction(Mob target, Attacker attacker, GameDamage damage)

// public int getTotalDamage(Mob target, Attacker attacker, float critModifier) {
//     float damage = this.getBuffedDamage(attacker);
//     if (target != null) {
//        if (target.isPlayer) {
//           float damageTakenModifier = this.playerDamageMultiplier;
//           if (target.isServer()) {
//              damageTakenModifier *= target.getLevel().getServer().world.settings.difficulty.damageTakenModifier;
//           } else if (target.isClient()) {
//              damageTakenModifier *= target.getLevel().getClient().worldSettings.difficulty.damageTakenModifier;
//           }

//           damage *= damageTakenModifier;
//        }

//        damage = Math.max(0.0F, damage - this.getDamageReduction(target, attacker));
//     }

//     damage *= critModifier;
//     if (target != null) {
//        damage *= target.getIncomingDamageModifier();
//     }

//     damage *= this.finalDamageMultiplier;
//     return (int)Math.max(1.0F, applyRandomizer(damage));
//  }

//  public float getDamageReduction(Mob target, Attacker attacker) {
//     return this.type.getDamageReduction(target, attacker, this);
//  }

//  public static float getDamageReduction(float armor) {
//     return armor * 0.5F;
//  }

@ModMethodPatch(target = DamageType.class, name = "getDamageReduction", arguments = {
        Mob.class, Attacker.class,
        GameDamage.class
})
public class RDamageType {

    // Skip the original method's execution
    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    static boolean onEnter() {
        return true;
    }

    // change the return value of the method
    @Advice.OnMethodExit
    static void onExit(
            @Advice.Return(readOnly = false) float returnValue,
            @Advice.Argument(0) Mob target,
            @Advice.Argument(1) Attacker attacker,
            @Advice.Argument(2) GameDamage damage) {
        float armorPen = damage.armorPen;
        if (attacker != null) {
            Mob attackOwner = attacker.getAttackOwner();
            if (attackOwner != null) {
                armorPen = (float) ((int) ((armorPen
                        + (float) (Integer) attackOwner.buffManager.getModifier(BuffModifiers.ARMOR_PEN_FLAT))
                        * (Float) attackOwner.buffManager.getModifier(BuffModifiers.ARMOR_PEN)));
            }
        }

        float defense = target.getArmorAfterPen(armorPen);
        float damageReduction = defense / (defense + 100);

        // return the damage reduction
        System.out.println("Damage Reduction: " + damageReduction);

        returnValue = damageReduction;
    }
}
