package necesse.entity.mobs.gameDamageType;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.Modifier;
import necesse.entity.mobs.Attacker;
import necesse.inventory.item.DoubleItemStatTip;
import necesse.inventory.item.LocalMessageDoubleItemStatTip;

public class NormalDamageType extends DamageType {
   public NormalDamageType() {
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
      return new LocalMessage("stats", "normal_damage");
   }

   public DoubleItemStatTip getDamageTip(int var1) {
      return new LocalMessageDoubleItemStatTip("itemtooltip", "damagetip", "value", (double)var1, 0);
   }

   public String getSteamStatKey() {
      return "normal_damage_dealt";
   }
}
