// Source code is decompiled from a .class file using FernFlower decompiler.
package necesse.entity.mobs.gameDamageType;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.modifiers.Modifier;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.IDData;
import necesse.engine.registries.RegistryClosedException;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.item.DoubleItemStatTip;

public abstract class DamageType {
   public final IDData idData = new IDData();

   public final int getID() {
      return this.idData.getID();
   }

   public String getStringID() {
      return this.idData.getStringID();
   }

   public DamageType() {
      if (DamageTypeRegistry.instance.isClosed()) {
         throw new RegistryClosedException("Cannot construct GameDamageType objects when damage type registry is closed, since they are a static registered objects. Use DamageTypeRegistry.getDamageType(...) to get damage types.");
      }
   }

   public void onDamageTypeRegistryClosed() {
   }

   public abstract Modifier<Float> getBuffDamageModifier();

   public float getDamageModifier(Attacker attacker) {
      Modifier<Float> modifier = this.getBuffDamageModifier();
      if (modifier != null) {
         Mob attackOwner = attacker != null ? attacker.getAttackOwner() : null;
         return attackOwner != null ? (Float)attackOwner.buffManager.getModifier(modifier) : 0.0F;
      } else {
         return 0.0F;
      }
   }

   public abstract Modifier<Float> getBuffAttackSpeedModifier(Attacker var1);

   public float getTypeAttackSpeedModifier(Attacker attacker) {
      Modifier<Float> modifier = this.getBuffAttackSpeedModifier(attacker);
      if (modifier != null) {
         Mob attackOwner = attacker != null ? attacker.getAttackOwner() : null;
         return attackOwner != null ? (Float)attackOwner.buffManager.getModifier(modifier) : 0.0F;
      } else {
         return 0.0F;
      }
   }

   public float getTypeFinalAttackSpeedModifier(Attacker attacker) {
      return 1.0F;
   }

   public float calculateTotalAttackSpeedModifier(Attacker attacker, float appendAttackSpeed) {
      Mob owner = attacker == null ? null : attacker.getAttackOwner();
      float totalModifier = owner == null ? (Float)BuffModifiers.ATTACK_SPEED.defaultBuffManagerValue : (Float)owner.buffManager.getModifier(BuffModifiers.ATTACK_SPEED);
      totalModifier += appendAttackSpeed;
      totalModifier += this.getTypeAttackSpeedModifier(attacker);
      totalModifier *= this.getTypeFinalAttackSpeedModifier(attacker);
      totalModifier = (Float)BuffModifiers.ATTACK_SPEED.finalLimit(totalModifier);
      return totalModifier;
   }

   public float calculateTotalAttackSpeedModifier(Attacker attacker) {
      return this.calculateTotalAttackSpeedModifier(attacker, 0.0F);
   }

   public abstract Modifier<Float> getBuffCritChanceModifier();

   public float getTypeCritChanceModifier(Attacker attacker) {
      Modifier<Float> modifier = this.getBuffCritChanceModifier();
      if (modifier != null) {
         Mob attackOwner = attacker != null ? attacker.getAttackOwner() : null;
         return attackOwner != null ? (Float)attackOwner.buffManager.getModifier(modifier) : 0.0F;
      } else {
         return 0.0F;
      }
   }

   public abstract Modifier<Float> getBuffCritDamageModifier();

   public float getTypeCritDamageModifier(Attacker attacker) {
      Modifier<Float> modifier = this.getBuffCritDamageModifier();
      if (modifier != null) {
         Mob attackOwner = attacker != null ? attacker.getAttackOwner() : null;
         return attackOwner != null ? (Float)attackOwner.buffManager.getModifier(modifier) : 0.0F;
      } else {
         return 0.0F;
      }
   }

   public float getDamageReduction(Mob target, Attacker attacker, GameDamage damage) {
      float armorPen = damage.armorPen;
      if (attacker != null) {
         Mob attackOwner = attacker.getAttackOwner();
         if (attackOwner != null) {
            armorPen = (float)((int)((armorPen + (float)(Integer)attackOwner.buffManager.getModifier(BuffModifiers.ARMOR_PEN_FLAT)) * (Float)attackOwner.buffManager.getModifier(BuffModifiers.ARMOR_PEN)));
         }
      }

      return getDamageReduction(target.getArmorAfterPen(armorPen));
   }

   public abstract GameMessage getStatsText();

   public abstract DoubleItemStatTip getDamageTip(int var1);

   public String getSteamStatKey() {
      return null;
   }

   public static float getDamageReduction(float armor) {
      return armor * 0.5F;
   }
}
