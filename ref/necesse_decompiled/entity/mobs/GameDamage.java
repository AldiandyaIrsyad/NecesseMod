// Source code is decompiled from a .class file using FernFlower decompiler.
package necesse.entity.mobs;

import necesse.engine.modifiers.Modifier;
import necesse.engine.modifiers.ModifierContainer;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.buffs.BuffModifiers;

public class GameDamage {
   public static boolean staticDamage = false;
   public final necesse.entity.mobs.gameDamageType.DamageType type;
   public final float damage;
   public final float armorPen;
   public final float baseCritChance;
   public final float playerDamageMultiplier;
   public final float finalDamageMultiplier;

   public GameDamage(necesse.entity.mobs.gameDamageType.DamageType type, float damage, float armorPen, float baseCritChance, float playerDamageMultiplier, float finalDamageMultiplier) {
      this.type = type;
      this.damage = damage;
      this.armorPen = armorPen;
      this.baseCritChance = baseCritChance;
      this.playerDamageMultiplier = playerDamageMultiplier;
      this.finalDamageMultiplier = finalDamageMultiplier;
   }

   public GameDamage(necesse.entity.mobs.gameDamageType.DamageType type, float damage, float armorPen, float baseCritChance) {
      this(type, damage, armorPen, baseCritChance, 1.0F, 1.0F);
   }

   public GameDamage(necesse.entity.mobs.gameDamageType.DamageType type, float damage, float baseCritChance) {
      this(type, damage, 0.0F, baseCritChance);
   }

   public GameDamage(necesse.entity.mobs.gameDamageType.DamageType type, float damage) {
      this(type, damage, 0.0F);
   }

   public GameDamage(float damage, float armorPen, float baseCritChance, float playerDamageMultiplier, float finalDamageMultiplier) {
      this(DamageTypeRegistry.NORMAL, damage, armorPen, baseCritChance, playerDamageMultiplier, finalDamageMultiplier);
   }

   public GameDamage(float damage, float armorPen, float baseCritChance) {
      this(DamageTypeRegistry.NORMAL, damage, armorPen, baseCritChance);
   }

   public GameDamage(float damage, float armorPen) {
      this(DamageTypeRegistry.NORMAL, damage, armorPen, 0.0F);
   }

   public GameDamage(float damage) {
      this(damage, 0.0F);
   }

   /** @deprecated */
   @Deprecated
   public GameDamage(DamageType type, float damage, float armorPen, float baseCritChance, float playerDamageMultiplier, float finalDamageMultiplier) {
      this((necesse.entity.mobs.gameDamageType.DamageType)type.converter.get(), damage, armorPen, baseCritChance, playerDamageMultiplier, finalDamageMultiplier);
   }

   /** @deprecated */
   @Deprecated
   public GameDamage(DamageType type, float damage, float armorPen, float baseCritChance, float playerDamageMultiplier) {
      this((necesse.entity.mobs.gameDamageType.DamageType)type.converter.get(), damage, armorPen, baseCritChance, playerDamageMultiplier, 1.0F);
   }

   /** @deprecated */
   @Deprecated
   public GameDamage(DamageType type, float damage, float armorPen, float baseCritChance) {
      this(type, damage, armorPen, baseCritChance, 1.0F, 1.0F);
   }

   /** @deprecated */
   @Deprecated
   public GameDamage(DamageType type, float damage, float baseCritChance) {
      this(type, damage, 0.0F, baseCritChance);
   }

   /** @deprecated */
   @Deprecated
   public GameDamage(DamageType type, float damage) {
      this(type, damage, 0.0F);
   }

   private GameDamage(GameDamage copy) {
      this.type = copy.type;
      this.damage = copy.damage;
      this.armorPen = copy.armorPen;
      this.baseCritChance = copy.baseCritChance;
      this.playerDamageMultiplier = copy.playerDamageMultiplier;
      this.finalDamageMultiplier = copy.finalDamageMultiplier;
   }

   public GameDamage enchantedDamage(ModifierContainer container, Modifier<Float> damageModifier, Modifier<Integer> armorPenModifier, Modifier<Float> critModifier) {
      float damage = container == null ? this.damage : this.damage * (Float)container.applyModifierLimited(damageModifier, (Float)damageModifier.defaultBuffManagerValue);
      float armorPen = container == null ? this.armorPen : this.armorPen + (float)(Integer)container.applyModifierLimited(armorPenModifier, (Integer)armorPenModifier.defaultBuffManagerValue);
      float crit = container == null ? this.baseCritChance : this.baseCritChance + (Float)container.applyModifierLimited(critModifier, (Float)critModifier.defaultBuffManagerValue);
      return new GameDamage(this.type, damage, armorPen, crit);
   }

   public GameDamage add(float damage, int armorPen, float critChance) {
      return new GameDamage(this.type, this.damage + damage, this.armorPen + (float)armorPen, this.baseCritChance + critChance, this.playerDamageMultiplier, this.finalDamageMultiplier);
   }

   public GameDamage setDamage(float damage) {
      return new GameDamage(this.type, damage, this.armorPen, this.baseCritChance, this.playerDamageMultiplier, this.finalDamageMultiplier);
   }

   public GameDamage setArmorPen(float armorPen) {
      return new GameDamage(this.type, this.damage, armorPen, this.baseCritChance, this.playerDamageMultiplier, this.finalDamageMultiplier);
   }

   public GameDamage setCritChance(float critChance) {
      return new GameDamage(this.type, this.damage, this.armorPen, critChance, this.playerDamageMultiplier, this.finalDamageMultiplier);
   }

   public GameDamage setPlayerMultiplier(float multiplier) {
      return new GameDamage(this.type, this.damage, this.armorPen, this.baseCritChance, multiplier, this.finalDamageMultiplier);
   }

   public GameDamage setFinalMultiplier(float multiplier) {
      return new GameDamage(this.type, this.damage, this.armorPen, this.baseCritChance, this.playerDamageMultiplier, multiplier);
   }

   public GameDamage addDamage(float damage) {
      return this.setDamage(this.damage + damage);
   }

   public GameDamage addArmorPen(float armorPen) {
      return this.setArmorPen(this.armorPen + armorPen);
   }

   public GameDamage addCritChance(float baseCritChance) {
      return this.setCritChance(this.baseCritChance + baseCritChance);
   }

   public GameDamage modDamage(float multiplier) {
      return this.setDamage(this.damage * multiplier);
   }

   public GameDamage modArmorPen(float multiplier) {
      return this.setArmorPen(this.armorPen * multiplier);
   }

   public GameDamage modCritChance(float multiplier) {
      return this.setCritChance(this.baseCritChance * multiplier);
   }

   public GameDamage modPlayerMultiplier(float multiplier) {
      return this.setPlayerMultiplier(this.playerDamageMultiplier * multiplier);
   }

   public GameDamage modFinalMultiplier(float multiplier) {
      return this.setFinalMultiplier(this.finalDamageMultiplier * multiplier);
   }

   public static float getDamageModifier(Attacker attacker, necesse.entity.mobs.gameDamageType.DamageType type) {
      float damageMod = 1.0F;
      if (attacker != null) {
         Mob owner = attacker.getAttackOwner();
         if (owner != null) {
            damageMod = owner.getOutgoingDamageModifier();
         }
      }

      damageMod += type.getDamageModifier(attacker);
      damageMod = (Float)BuffModifiers.ALL_DAMAGE.finalLimit(damageMod);
      return damageMod;
   }

   public float getBuffedDamage(Attacker attacker) {
      return this.damage * getDamageModifier(attacker, this.type);
   }

   public float getCritChanceModifier(Attacker attacker, necesse.entity.mobs.gameDamageType.DamageType type) {
      float chance = 0.0F;
      if (attacker != null) {
         Mob owner = attacker.getAttackOwner();
         if (owner != null) {
            chance = owner.getCritChance();
         }
      }

      chance += type.getTypeCritChanceModifier(attacker);
      return GameMath.limit(chance, 0.0F, 1.0F);
   }

   public float getBuffedCritChance(Attacker attacker) {
      return GameMath.limit(this.baseCritChance + this.getCritChanceModifier(attacker, this.type), 0.0F, 1.0F);
   }

   private static float applyRandomizer(float damage) {
      return staticDamage ? damage : damage * GameRandom.globalRandom.getFloatBetween(0.8F, 1.2F);
   }

   public int getTotalDamage(Mob target, Attacker attacker, float critModifier) {
      float damage = this.getBuffedDamage(attacker);
      if (target != null) {
         if (target.isPlayer) {
            float damageTakenModifier = this.playerDamageMultiplier;
            if (target.isServer()) {
               damageTakenModifier *= target.getLevel().getServer().world.settings.difficulty.damageTakenModifier;
            } else if (target.isClient()) {
               damageTakenModifier *= target.getLevel().getClient().worldSettings.difficulty.damageTakenModifier;
            }

            damage *= damageTakenModifier;
         }

         damage = Math.max(0.0F, damage - this.getDamageReduction(target, attacker));
      }

      damage *= critModifier;
      if (target != null) {
         damage *= target.getIncomingDamageModifier();
      }

      damage *= this.finalDamageMultiplier;
      return (int)Math.max(1.0F, applyRandomizer(damage));
   }

   public float getDamageReduction(Mob target, Attacker attacker) {
      return this.type.getDamageReduction(target, attacker, this);
   }

   public static float getDamageReduction(float armor) {
      return armor * 0.5F;
   }

   public boolean equals(GameDamage other) {
      if (other == null) {
         return false;
      } else {
         return this.type == other.type && this.damage == other.damage && this.armorPen == other.armorPen && this.baseCritChance == other.baseCritChance && this.playerDamageMultiplier == other.playerDamageMultiplier && this.finalDamageMultiplier == other.finalDamageMultiplier;
      }
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return obj instanceof GameDamage ? this.equals((GameDamage)obj) : false;
      }
   }

   public static GameDamage fromLoadData(LoadData save) {
      String typeString = save.getUnsafeString("type");
      int id = DamageTypeRegistry.getDamageTypeID(typeString);
      if (id == -1) {
         id = DamageTypeRegistry.getDamageTypeID(typeString.toLowerCase());
      }

      if (id == -1) {
         throw new LoadDataException("Could not load damage type: " + typeString);
      } else {
         necesse.entity.mobs.gameDamageType.DamageType type = DamageTypeRegistry.getDamageType(id);
         float damage = save.getFloat("damage");
         float armorPen = save.getFloat("armorPen", 0.0F, false);
         float baseCritChance = save.getFloat("baseCritChance", 0.0F, false);
         float playerDamageMultiplier = save.getFloat("playerDamageMultiplier", 1.0F, false);
         float finalDamageMultiplier = save.getFloat("finalDamageMultiplier", 1.0F, false);
         return new GameDamage(type, damage, armorPen, baseCritChance, playerDamageMultiplier, finalDamageMultiplier);
      }
   }

   public void addSaveData(SaveData save) {
      save.addUnsafeString("type", this.type.getStringID());
      save.addFloat("damage", this.damage);
      if (this.armorPen != 0.0F) {
         save.addFloat("armorPen", this.armorPen);
      }

      if (this.baseCritChance != 0.0F) {
         save.addFloat("baseCritChance", this.baseCritChance);
      }

      if (this.playerDamageMultiplier != 1.0F) {
         save.addFloat("playerDamageMultiplier", this.playerDamageMultiplier);
      }

      if (this.finalDamageMultiplier != 1.0F) {
         save.addFloat("finalDamageMultiplier", this.finalDamageMultiplier);
      }

   }

   public Packet getPacket() {
      Packet out = new Packet();
      this.writePacket(new PacketWriter(out));
      return out;
   }

   public void writePacket(PacketWriter writer) {
      writer.putNextByteUnsigned(this.type.getID());
      writer.putNextFloat(this.damage);
      writer.putNextFloat(this.armorPen);
      writer.putNextFloat(this.baseCritChance);
      writer.putNextBoolean(this.playerDamageMultiplier != 1.0F);
      if (this.playerDamageMultiplier != 1.0F) {
         writer.putNextFloat(this.playerDamageMultiplier);
      }

      writer.putNextBoolean(this.finalDamageMultiplier != 1.0F);
      if (this.finalDamageMultiplier != 1.0F) {
         writer.putNextFloat(this.finalDamageMultiplier);
      }

   }

   public static GameDamage fromPacket(Packet packet) {
      return fromReader(new PacketReader(packet));
   }

   public static GameDamage fromReader(PacketReader reader) {
      necesse.entity.mobs.gameDamageType.DamageType type = DamageTypeRegistry.getDamageType(reader.getNextByteUnsigned());
      if (type == null) {
         type = DamageTypeRegistry.NORMAL;
      }

      float damage = reader.getNextFloat();
      int armorPen = reader.getNextInt();
      float baseCritChance = reader.getNextFloat();
      boolean hasPlayerDamageMultiplier = reader.getNextBoolean();
      float playerDamageMultiplier = 1.0F;
      if (hasPlayerDamageMultiplier) {
         playerDamageMultiplier = reader.getNextFloat();
      }

      boolean hasFinalDamageMultiplier = reader.getNextBoolean();
      float finalDamageMultiplier = 1.0F;
      if (hasFinalDamageMultiplier) {
         finalDamageMultiplier = reader.getNextFloat();
      }

      return new GameDamage(type, damage, (float)armorPen, baseCritChance, playerDamageMultiplier, finalDamageMultiplier);
   }
}
