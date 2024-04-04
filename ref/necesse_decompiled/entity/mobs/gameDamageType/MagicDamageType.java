package necesse.entity.mobs.gameDamageType;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.Modifier;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.item.DoubleItemStatTip;
import necesse.inventory.item.LocalMessageDoubleItemStatTip;

public class MagicDamageType extends DamageType {
   public MagicDamageType() {
   }

   public Modifier<Float> getBuffDamageModifier() {
      return BuffModifiers.MAGIC_DAMAGE;
   }

   public Modifier<Float> getBuffAttackSpeedModifier(Attacker var1) {
      return BuffModifiers.MAGIC_ATTACK_SPEED;
   }

   public float getTypeFinalAttackSpeedModifier(Attacker var1) {
      Mob var2 = var1 != null ? var1.getAttackOwner() : null;
      return var2 != null && (Boolean)var2.buffManager.getModifier(BuffModifiers.MANA_EXHAUSTED) ? 0.5F : super.getTypeFinalAttackSpeedModifier(var1);
   }

   public Modifier<Float> getBuffCritChanceModifier() {
      return BuffModifiers.MAGIC_CRIT_CHANCE;
   }

   public Modifier<Float> getBuffCritDamageModifier() {
      return BuffModifiers.MAGIC_CRIT_DAMAGE;
   }

   public GameMessage getStatsText() {
      return new LocalMessage("stats", "magic_damage");
   }

   public DoubleItemStatTip getDamageTip(int var1) {
      return new LocalMessageDoubleItemStatTip("itemtooltip", "magicdamagetip", "value", (double)var1, 0);
   }

   public String getSteamStatKey() {
      return "magic_damage_dealt";
   }
}
