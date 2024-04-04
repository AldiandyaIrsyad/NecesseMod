package necesse.entity.mobs.gameDamageType;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.Modifier;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.inventory.item.DoubleItemStatTip;
import necesse.inventory.item.LocalMessageDoubleItemStatTip;

public class TrueDamageType extends DamageType {
   public TrueDamageType() {
   }

   public Modifier<Float> getBuffDamageModifier() {
      return null;
   }

   public Modifier<Float> getBuffAttackSpeedModifier(Attacker var1) {
      return null;
   }

   public Modifier<Float> getBuffCritChanceModifier() {
      return null;
   }

   public Modifier<Float> getBuffCritDamageModifier() {
      return null;
   }

   public GameMessage getStatsText() {
      return new LocalMessage("stats", "true_damage");
   }

   public DoubleItemStatTip getDamageTip(int var1) {
      return new LocalMessageDoubleItemStatTip("itemtooltip", "truedamagetip", "value", (double)var1, 0);
   }

   public float getDamageReduction(Mob var1, Attacker var2, GameDamage var3) {
      return 0.0F;
   }

   public String getSteamStatKey() {
      return "true_damage_dealt";
   }
}
