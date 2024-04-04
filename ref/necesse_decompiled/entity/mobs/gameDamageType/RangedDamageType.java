package necesse.entity.mobs.gameDamageType;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.Modifier;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.item.DoubleItemStatTip;
import necesse.inventory.item.LocalMessageDoubleItemStatTip;

public class RangedDamageType extends DamageType {
   public RangedDamageType() {
   }

   public Modifier<Float> getBuffDamageModifier() {
      return BuffModifiers.RANGED_DAMAGE;
   }

   public Modifier<Float> getBuffAttackSpeedModifier(Attacker var1) {
      return BuffModifiers.RANGED_ATTACK_SPEED;
   }

   public Modifier<Float> getBuffCritChanceModifier() {
      return BuffModifiers.RANGED_CRIT_CHANCE;
   }

   public Modifier<Float> getBuffCritDamageModifier() {
      return BuffModifiers.RANGED_CRIT_DAMAGE;
   }

   public GameMessage getStatsText() {
      return new LocalMessage("stats", "ranged_damage");
   }

   public DoubleItemStatTip getDamageTip(int var1) {
      return new LocalMessageDoubleItemStatTip("itemtooltip", "rangeddamagetip", "value", (double)var1, 0);
   }

   public String getSteamStatKey() {
      return "ranged_damage_dealt";
   }
}
