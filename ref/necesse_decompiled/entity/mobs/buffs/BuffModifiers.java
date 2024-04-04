package necesse.entity.mobs.buffs;

import necesse.engine.modifiers.Modifier;
import necesse.engine.modifiers.ModifierLimiter;
import necesse.engine.modifiers.ModifierList;
import necesse.engine.util.GameMath;

public class BuffModifiers {
   public static final ModifierList LIST = new ModifierList();
   public static final Modifier<Float> ALL_DAMAGE;
   public static final Modifier<Float> MELEE_DAMAGE;
   public static final Modifier<Float> RANGED_DAMAGE;
   public static final Modifier<Float> MAGIC_DAMAGE;
   public static final Modifier<Float> SUMMON_DAMAGE;
   public static final Modifier<Float> TOOL_DAMAGE;
   public static final Modifier<Float> CRIT_CHANCE;
   public static final Modifier<Float> MELEE_CRIT_CHANCE;
   public static final Modifier<Float> RANGED_CRIT_CHANCE;
   public static final Modifier<Float> MAGIC_CRIT_CHANCE;
   public static final Modifier<Float> SUMMON_CRIT_CHANCE;
   public static final Modifier<Float> CRIT_DAMAGE;
   public static final Modifier<Float> MELEE_CRIT_DAMAGE;
   public static final Modifier<Float> RANGED_CRIT_DAMAGE;
   public static final Modifier<Float> MAGIC_CRIT_DAMAGE;
   public static final Modifier<Float> SUMMON_CRIT_DAMAGE;
   public static final Modifier<Float> ARMOR_PEN;
   public static final Modifier<Integer> ARMOR_PEN_FLAT;
   public static final Modifier<Float> KNOCKBACK_OUT;
   public static final Modifier<Float> SPEED;
   public static final Modifier<Float> SPEED_FLAT;
   public static final Modifier<Float> SLOW;
   public static final Modifier<Float> SWIM_SPEED;
   public static final Modifier<Float> FRICTION;
   public static final Modifier<Float> ACCELERATION;
   public static final Modifier<Float> DECELERATION;
   public static final Modifier<Float> ARMOR;
   public static final Modifier<Integer> ARMOR_FLAT;
   public static final Modifier<Float> INCOMING_DAMAGE_MOD;
   public static final Modifier<Float> MAX_HEALTH;
   public static final Modifier<Integer> MAX_HEALTH_FLAT;
   public static final Modifier<Float> MAX_RESILIENCE;
   public static final Modifier<Integer> MAX_RESILIENCE_FLAT;
   public static final Modifier<Float> MAX_MANA;
   public static final Modifier<Integer> MAX_MANA_FLAT;
   public static final Modifier<Float> HEALTH_REGEN;
   public static final Modifier<Float> HEALTH_REGEN_FLAT;
   public static final Modifier<Float> COMBAT_HEALTH_REGEN;
   public static final Modifier<Float> COMBAT_HEALTH_REGEN_FLAT;
   /** @deprecated */
   @Deprecated
   public static final Modifier<Float> REGEN;
   /** @deprecated */
   @Deprecated
   public static final Modifier<Float> REGEN_FLAT;
   /** @deprecated */
   @Deprecated
   public static final Modifier<Float> COMBAT_REGEN;
   /** @deprecated */
   @Deprecated
   public static final Modifier<Float> COMBAT_REGEN_FLAT;
   public static final Modifier<Float> RESILIENCE_GAIN;
   public static final Modifier<Float> RESILIENCE_DECAY;
   public static final Modifier<Float> RESILIENCE_DECAY_FLAT;
   public static final Modifier<Float> RESILIENCE_REGEN;
   public static final Modifier<Float> RESILIENCE_REGEN_FLAT;
   public static final Modifier<Float> MANA_REGEN;
   public static final Modifier<Float> MANA_REGEN_FLAT;
   public static final Modifier<Float> COMBAT_MANA_REGEN;
   public static final Modifier<Float> COMBAT_MANA_REGEN_FLAT;
   public static final Modifier<Float> POISON_DAMAGE;
   public static final Modifier<Float> POISON_DAMAGE_FLAT;
   public static final Modifier<Float> FIRE_DAMAGE;
   public static final Modifier<Float> FIRE_DAMAGE_FLAT;
   public static final Modifier<Float> FROST_DAMAGE;
   public static final Modifier<Float> FROST_DAMAGE_FLAT;
   public static final Modifier<Float> ATTACK_SPEED;
   public static final Modifier<Float> MELEE_ATTACK_SPEED;
   public static final Modifier<Float> RANGED_ATTACK_SPEED;
   public static final Modifier<Float> MAGIC_ATTACK_SPEED;
   public static final Modifier<Float> SUMMON_ATTACK_SPEED;
   public static final Modifier<Float> MINING_SPEED;
   public static final Modifier<Float> BUILDING_SPEED;
   public static final Modifier<Float> MINING_RANGE;
   public static final Modifier<Float> BUILD_RANGE;
   public static float MAX_PICKUP_RANGE_MODIFIER;
   public static final Modifier<Float> ITEM_PICKUP_RANGE;
   public static final Modifier<Float> ARROW_USAGE;
   public static final Modifier<Float> BULLET_USAGE;
   public static final Modifier<Float> MANA_USAGE;
   public static final Modifier<Integer> MAX_SUMMONS;
   public static final Modifier<Float> SUMMONS_SPEED;
   public static final Modifier<Float> SUMMONS_TARGET_RANGE;
   public static final Modifier<Float> ATTACK_MOVEMENT_MOD;
   public static final Modifier<Float> PROJECTILE_VELOCITY;
   public static final Modifier<Float> THROWING_VELOCITY;
   public static final Modifier<Integer> PROJECTILE_BOUNCES;
   public static final Modifier<Integer> DASH_STACKS;
   public static final Modifier<Float> DASH_COOLDOWN;
   public static final Modifier<Float> DASH_COOLDOWN_FLAT;
   public static final Modifier<Float> POTION_DURATION;
   public static final Modifier<Float> STAMINA_CAPACITY;
   public static final Modifier<Float> STAMINA_USAGE;
   public static final Modifier<Float> STAMINA_REGEN;
   public static final Modifier<Integer> TRAVEL_DISTANCE;
   public static final Modifier<Integer> BIOME_VIEW_DISTANCE;
   public static final Modifier<Float> KNOCKBACK_INCOMING_MOD;
   public static final Modifier<Integer> FISHING_POWER;
   public static final Modifier<Integer> FISHING_LINES;
   public static final Modifier<Boolean> WATER_WALKING;
   public static final Modifier<Boolean> EMITS_LIGHT;
   public static final Modifier<Boolean> INVISIBILITY;
   public static final Modifier<Boolean> SPELUNKER;
   public static final Modifier<Boolean> TREASURE_HUNTER;
   public static final Modifier<Boolean> BOUNCY;
   public static final Modifier<Boolean> PARALYZED;
   public static final Modifier<Boolean> UNTARGETABLE;
   public static final Modifier<Boolean> INTIMIDATED;
   public static final Modifier<Boolean> MANA_EXHAUSTED;
   public static final Modifier<Float> MOB_SPAWN_RATE;
   public static final Modifier<Float> MOB_SPAWN_CAP;
   public static final Modifier<Integer> MOB_SPAWN_LIGHT_THRESHOLD;
   public static float MAX_TARGET_RANGE_MODIFIER;
   public static final Modifier<Float> TARGET_RANGE;
   public static final Modifier<Float> CHASER_RANGE;

   public BuffModifiers() {
   }

   static {
      ALL_DAMAGE = new Modifier(LIST, "alldamage", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.NORMAL_PERC_PARSER("damage"), ModifierLimiter.NORMAL_PERC_LIMITER("damage"));
      MELEE_DAMAGE = new Modifier(LIST, "meleedamage", 0.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return var0;
      }, Modifier.NORMAL_PERC_PARSER("meleedamage"), ModifierLimiter.NORMAL_PERC_LIMITER("meleedamage"));
      RANGED_DAMAGE = new Modifier(LIST, "rangedamage", 0.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return var0;
      }, Modifier.NORMAL_PERC_PARSER("rangeddamage"), ModifierLimiter.NORMAL_PERC_LIMITER("rangeddamage"));
      MAGIC_DAMAGE = new Modifier(LIST, "magicdamage", 0.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return var0;
      }, Modifier.NORMAL_PERC_PARSER("magicdamage"), ModifierLimiter.NORMAL_PERC_LIMITER("magicdamage"));
      SUMMON_DAMAGE = new Modifier(LIST, "summondamage", 0.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return var0;
      }, Modifier.NORMAL_PERC_PARSER("summondamage"), ModifierLimiter.NORMAL_PERC_LIMITER("summondamage"));
      TOOL_DAMAGE = new Modifier(LIST, "tooldamage", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.NORMAL_PERC_PARSER("tooldamage"), ModifierLimiter.NORMAL_PERC_LIMITER("tooldamage"));
      CRIT_CHANCE = new Modifier(LIST, "critchance", 0.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.NORMAL_PERC_PARSER("critchance"), ModifierLimiter.NORMAL_PERC_LIMITER("critchance"));
      MELEE_CRIT_CHANCE = new Modifier(LIST, "meleecritchance", 0.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return var0;
      }, Modifier.NORMAL_PERC_PARSER("meleecritchance"), ModifierLimiter.NORMAL_PERC_LIMITER("meleecritchance"));
      RANGED_CRIT_CHANCE = new Modifier(LIST, "rangedcritchance", 0.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return var0;
      }, Modifier.NORMAL_PERC_PARSER("rangedcritchance"), ModifierLimiter.NORMAL_PERC_LIMITER("rangedcritchance"));
      MAGIC_CRIT_CHANCE = new Modifier(LIST, "magiccritchance", 0.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return var0;
      }, Modifier.NORMAL_PERC_PARSER("magiccritchance"), ModifierLimiter.NORMAL_PERC_LIMITER("magiccritchance"));
      SUMMON_CRIT_CHANCE = new Modifier(LIST, "summoncritchance", 0.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return var0;
      }, Modifier.NORMAL_PERC_PARSER("summoncritchance"), ModifierLimiter.NORMAL_PERC_LIMITER("summoncritchance"));
      CRIT_DAMAGE = new Modifier(LIST, "critdamage", 2.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(1.0F, var0);
      }, Modifier.NORMAL_PERC_PARSER("critdamage"), ModifierLimiter.NORMAL_PERC_LIMITER("critdamage"));
      MELEE_CRIT_DAMAGE = new Modifier(LIST, "meleecritdamage", 0.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return var0;
      }, Modifier.NORMAL_PERC_PARSER("meleecritdamage"), ModifierLimiter.NORMAL_PERC_LIMITER("meleecritdamage"));
      RANGED_CRIT_DAMAGE = new Modifier(LIST, "rangedcritdamage", 0.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return var0;
      }, Modifier.NORMAL_PERC_PARSER("rangedcritdamage"), ModifierLimiter.NORMAL_PERC_LIMITER("rangedcritdamage"));
      MAGIC_CRIT_DAMAGE = new Modifier(LIST, "magiccritdamage", 0.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return var0;
      }, Modifier.NORMAL_PERC_PARSER("magiccritdamage"), ModifierLimiter.NORMAL_PERC_LIMITER("magiccritdamage"));
      SUMMON_CRIT_DAMAGE = new Modifier(LIST, "summoncritdamage", 0.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return var0;
      }, Modifier.NORMAL_PERC_PARSER("summoncritdamage"), ModifierLimiter.NORMAL_PERC_LIMITER("summoncritdamage"));
      ARMOR_PEN = new Modifier(LIST, "armorpen", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.NORMAL_PERC_PARSER("armorpen"), ModifierLimiter.NORMAL_PERC_LIMITER("armorpen"));
      ARMOR_PEN_FLAT = new Modifier(LIST, "armorpenflat", 0, 0, Modifier.INT_ADD_APPEND, Modifier.NORMAL_FLAT_INT_PARSER("armorpenflat"), ModifierLimiter.NORMAL_FLAT_INT_LIMITER("armorpenflat"));
      KNOCKBACK_OUT = new Modifier(LIST, "knockbackout", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.NORMAL_PERC_PARSER("knockbackout"), ModifierLimiter.NORMAL_PERC_LIMITER("knockbackout"));
      SPEED = new Modifier(LIST, "speed", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.NORMAL_PERC_PARSER("speed"), ModifierLimiter.NORMAL_PERC_LIMITER("speed"));
      SPEED_FLAT = new Modifier(LIST, "speedflat", 0.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, Modifier.NORMAL_FLAT_FLOAT_PARSER("speedflat"), ModifierLimiter.NORMAL_FLAT_FLOAT_LIMITER("speedflat"));
      SLOW = new Modifier(LIST, "slow", 0.0F, 0.0F, (var0, var1, var2) -> {
         return Math.max(var0, var1);
      }, (var0) -> {
         return GameMath.limit(var0, 0.0F, 1.0F);
      }, Modifier.BAD_PERCENT_MODIFIER("slow"), ModifierLimiter.INVERSE_PERC_LIMITER("slow"));
      SWIM_SPEED = new Modifier(LIST, "swimspeed", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.NORMAL_PERC_PARSER("swimspeed"), ModifierLimiter.NORMAL_PERC_LIMITER("swimspeed"));
      FRICTION = new Modifier(LIST, "friction", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.NORMAL_PERC_PARSER("friction"), ModifierLimiter.NORMAL_PERC_LIMITER("friction"));
      ACCELERATION = new Modifier(LIST, "acceleration", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.NORMAL_PERC_PARSER("acceleration"), ModifierLimiter.NORMAL_PERC_LIMITER("acceleration"));
      DECELERATION = new Modifier(LIST, "deceleration", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.NORMAL_PERC_PARSER("deceleration"), ModifierLimiter.NORMAL_PERC_LIMITER("deceleration"));
      ARMOR = new Modifier(LIST, "armor", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.NORMAL_PERC_PARSER("armor"), ModifierLimiter.NORMAL_PERC_LIMITER("armor"));
      ARMOR_FLAT = new Modifier(LIST, "armorflat", 0, 0, Modifier.INT_ADD_APPEND, Modifier.NORMAL_FLAT_INT_PARSER("armorflat"), ModifierLimiter.NORMAL_FLAT_INT_LIMITER("armorflat"));
      INCOMING_DAMAGE_MOD = new Modifier(LIST, "incomingdamagemod", 1.0F, 1.0F, Modifier.FLOAT_MULT_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.LESS_GOOD_PERCENT_MODIFIER("incdamage"), ModifierLimiter.NORMAL_PERC_LIMITER("incdamage"));
      MAX_HEALTH = new Modifier(LIST, "maxhealth", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.NORMAL_PERC_PARSER("maxhealth"), ModifierLimiter.NORMAL_PERC_LIMITER("maxhealth"));
      MAX_HEALTH_FLAT = new Modifier(LIST, "maxhealthflat", 0, 0, Modifier.INT_ADD_APPEND, Modifier.NORMAL_FLAT_INT_PARSER("maxhealthflat"), ModifierLimiter.NORMAL_FLAT_INT_LIMITER("maxhealthflat"));
      MAX_RESILIENCE = new Modifier(LIST, "maxresilience", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.NORMAL_PERC_PARSER("maxresilience"), ModifierLimiter.NORMAL_PERC_LIMITER("maxresilience"));
      MAX_RESILIENCE_FLAT = new Modifier(LIST, "maxresilienceflat", 0, 0, Modifier.INT_ADD_APPEND, Modifier.NORMAL_FLAT_INT_PARSER("maxresilienceflat"), ModifierLimiter.NORMAL_FLAT_INT_LIMITER("maxresilienceflat"));
      MAX_MANA = new Modifier(LIST, "maxmana", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.NORMAL_PERC_PARSER("maxmana"), ModifierLimiter.NORMAL_PERC_LIMITER("maxmana"));
      MAX_MANA_FLAT = new Modifier(LIST, "maxmanaflat", 0, 0, Modifier.INT_ADD_APPEND, Modifier.NORMAL_FLAT_INT_PARSER("maxmanaflat"), ModifierLimiter.NORMAL_FLAT_INT_LIMITER("maxmanaflat"));
      HEALTH_REGEN = new Modifier(LIST, "basehealthregen", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.NORMAL_PERC_PARSER("basehealthregen"), ModifierLimiter.NORMAL_PERC_LIMITER("basehealthregen"));
      HEALTH_REGEN_FLAT = new Modifier(LIST, "basehealthregenflat", 0.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, Modifier.NORMAL_FLAT_FLOAT_PARSER("basehealthregenflat"), ModifierLimiter.NORMAL_FLAT_FLOAT_LIMITER("basehealthregenflat"));
      COMBAT_HEALTH_REGEN = new Modifier(LIST, "combathealthregen", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.NORMAL_PERC_PARSER("combathealthregen"), ModifierLimiter.NORMAL_PERC_LIMITER("combathealthregen"));
      COMBAT_HEALTH_REGEN_FLAT = new Modifier(LIST, "combathealthregenflat", 0.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, Modifier.NORMAL_FLAT_FLOAT_PARSER("combathealthregenflat"), ModifierLimiter.NORMAL_FLAT_FLOAT_LIMITER("combathealthregenflat"));
      REGEN = HEALTH_REGEN;
      REGEN_FLAT = HEALTH_REGEN_FLAT;
      COMBAT_REGEN = COMBAT_HEALTH_REGEN;
      COMBAT_REGEN_FLAT = COMBAT_HEALTH_REGEN_FLAT;
      RESILIENCE_GAIN = new Modifier(LIST, "resiliencegain", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.NORMAL_PERC_PARSER("resiliencegain"), ModifierLimiter.NORMAL_PERC_LIMITER("resiliencegain"));
      RESILIENCE_DECAY = new Modifier(LIST, "resiliencedecay", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.INVERSE_PERC_PARSER("resiliencedecay"), ModifierLimiter.INVERSE_PERC_LIMITER("resiliencedecay"));
      RESILIENCE_DECAY_FLAT = new Modifier(LIST, "resiliencedecayflat", 0.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.INVERSE_FLAT_FLOAT_PARSER("resiliencedecayflat"), ModifierLimiter.INVERSE_FLAT_FLOAT_LIMITER("resiliencedecayflat"));
      RESILIENCE_REGEN = new Modifier(LIST, "resilienceregen", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.NORMAL_PERC_PARSER("resilienceregen"), ModifierLimiter.NORMAL_PERC_LIMITER("resilienceregen"));
      RESILIENCE_REGEN_FLAT = new Modifier(LIST, "resilienceregenflat", 0.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.NORMAL_FLAT_FLOAT_PARSER("resilienceregenflat"), ModifierLimiter.NORMAL_FLAT_FLOAT_LIMITER("resilienceregenflat"));
      MANA_REGEN = new Modifier(LIST, "basemanaregen", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.NORMAL_PERC_PARSER("basemanaregen"), ModifierLimiter.NORMAL_PERC_LIMITER("basemanaregen"));
      MANA_REGEN_FLAT = new Modifier(LIST, "basemanaregenflat", 0.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, Modifier.NORMAL_FLAT_FLOAT_PARSER("basemanaregenflat"), ModifierLimiter.NORMAL_FLAT_FLOAT_LIMITER("basemanaregenflat"));
      COMBAT_MANA_REGEN = new Modifier(LIST, "combatmanaregen", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.NORMAL_PERC_PARSER("combatmanaregen"), ModifierLimiter.NORMAL_PERC_LIMITER("combatmanaregen"));
      COMBAT_MANA_REGEN_FLAT = new Modifier(LIST, "combatmanaregenflat", 0.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, Modifier.NORMAL_FLAT_FLOAT_PARSER("combatmanaregenflat"), ModifierLimiter.NORMAL_FLAT_FLOAT_LIMITER("combatmanaregenflat"));
      POISON_DAMAGE = new Modifier(LIST, "poisondamage", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.INVERSE_PERC_PARSER("poisondamage"), ModifierLimiter.INVERSE_PERC_LIMITER("poisondamage"));
      POISON_DAMAGE_FLAT = new Modifier(LIST, "poisondamageflat", 0.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.INVERSE_FLAT_FLOAT_PARSER("poisondamageflat"), ModifierLimiter.INVERSE_FLAT_FLOAT_LIMITER("poisondamageflat"));
      FIRE_DAMAGE = new Modifier(LIST, "firedamage", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.INVERSE_PERC_PARSER("firedamage"), ModifierLimiter.INVERSE_PERC_LIMITER("firedamage"));
      FIRE_DAMAGE_FLAT = new Modifier(LIST, "firedamageflat", 0.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.INVERSE_FLAT_FLOAT_PARSER("firedamageflat"), ModifierLimiter.INVERSE_FLAT_FLOAT_LIMITER("firedamageflat"));
      FROST_DAMAGE = new Modifier(LIST, "frostdamage", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.INVERSE_PERC_PARSER("frostdamage"), ModifierLimiter.INVERSE_PERC_LIMITER("frostdamage"));
      FROST_DAMAGE_FLAT = new Modifier(LIST, "frostdamageflat", 0.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.INVERSE_FLAT_FLOAT_PARSER("frostdamageflat"), ModifierLimiter.INVERSE_FLAT_FLOAT_LIMITER("frostdamageflat"));
      ATTACK_SPEED = new Modifier(LIST, "attackspeed", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.NORMAL_PERC_PARSER("attackspeed"), ModifierLimiter.NORMAL_PERC_LIMITER("attackspeed"));
      MELEE_ATTACK_SPEED = new Modifier(LIST, "meleeattackspeed", 0.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return var0;
      }, Modifier.NORMAL_PERC_PARSER("meleeattackspeed"), ModifierLimiter.NORMAL_PERC_LIMITER("meleeattackspeed"));
      RANGED_ATTACK_SPEED = new Modifier(LIST, "rangedattackspeed", 0.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return var0;
      }, Modifier.NORMAL_PERC_PARSER("rangedattackspeed"), ModifierLimiter.NORMAL_PERC_LIMITER("rangedattackspeed"));
      MAGIC_ATTACK_SPEED = new Modifier(LIST, "magicattackspeed", 0.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return var0;
      }, Modifier.NORMAL_PERC_PARSER("magicattackspeed"), ModifierLimiter.NORMAL_PERC_LIMITER("magicattackspeed"));
      SUMMON_ATTACK_SPEED = new Modifier(LIST, "summonattackspeed", 0.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return var0;
      }, Modifier.NORMAL_PERC_PARSER("summonattackspeed"), ModifierLimiter.NORMAL_PERC_LIMITER("summonattackspeed"));
      MINING_SPEED = new Modifier(LIST, "miningspeed", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.NORMAL_PERC_PARSER("miningspeed"), ModifierLimiter.NORMAL_PERC_LIMITER("miningspeed"));
      BUILDING_SPEED = new Modifier(LIST, "buildingspeed", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.NORMAL_PERC_PARSER("buildingspeed"), ModifierLimiter.NORMAL_PERC_LIMITER("buildingspeed"));
      MINING_RANGE = new Modifier(LIST, "miningrange", 0.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, Modifier.NORMAL_FLAT_FLOAT_PARSER("miningrange"), ModifierLimiter.NORMAL_FLAT_FLOAT_LIMITER("miningrange"));
      BUILD_RANGE = new Modifier(LIST, "buildrange", 0.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, Modifier.NORMAL_FLAT_FLOAT_PARSER("buildingrange"), ModifierLimiter.NORMAL_FLAT_FLOAT_LIMITER("buildingrange"));
      MAX_PICKUP_RANGE_MODIFIER = 50.0F;
      ITEM_PICKUP_RANGE = new Modifier(LIST, "itempickuprange", 0.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.min(var0, MAX_PICKUP_RANGE_MODIFIER);
      }, Modifier.NORMAL_FLAT_FLOAT_PARSER("itempickuprange"), ModifierLimiter.NORMAL_FLAT_FLOAT_LIMITER("itempickuprange"));
      ARROW_USAGE = new Modifier(LIST, "arrowusage", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return GameMath.limit(var0, 0.0F, 1.0F);
      }, Modifier.INVERSE_PERC_PARSER("arrowusage"), ModifierLimiter.INVERSE_PERC_LIMITER("arrowusage"));
      BULLET_USAGE = new Modifier(LIST, "bulletusage", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return GameMath.limit(var0, 0.0F, 1.0F);
      }, Modifier.INVERSE_PERC_PARSER("bulletusage"), ModifierLimiter.INVERSE_PERC_LIMITER("bulletusage"));
      MANA_USAGE = new Modifier(LIST, "manausage", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.INVERSE_PERC_PARSER("manausage"), ModifierLimiter.INVERSE_PERC_LIMITER("manausage"));
      MAX_SUMMONS = new Modifier(LIST, "maxsummons", 1, 0, Modifier.INT_ADD_APPEND, (var0) -> {
         return Math.max(0, var0);
      }, Modifier.NORMAL_FLAT_INT_PARSER("maxsummons"), ModifierLimiter.NORMAL_FLAT_INT_LIMITER("maxsummons"));
      SUMMONS_SPEED = new Modifier(LIST, "summonsspeed", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.NORMAL_PERC_PARSER("summonsspeed"), ModifierLimiter.NORMAL_PERC_LIMITER("summonsspeed"));
      SUMMONS_TARGET_RANGE = new Modifier(LIST, "summonstargetrange", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.NORMAL_PERC_PARSER("summonstargetrange"), ModifierLimiter.NORMAL_PERC_LIMITER("summonstargetrange"));
      ATTACK_MOVEMENT_MOD = new Modifier(LIST, "attackmovementmod", 1.0F, 1.0F, Modifier.FLOAT_MULT_APPEND, (var0) -> {
         return GameMath.limit(var0, 0.0F, 1.0F);
      }, Modifier.INVERSE_PERC_PARSER("attackmovement"), ModifierLimiter.INVERSE_PERC_LIMITER("attackmovement"));
      PROJECTILE_VELOCITY = new Modifier(LIST, "projectilevelocity", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.NORMAL_PERC_PARSER("projectilevel"), ModifierLimiter.NORMAL_PERC_LIMITER("projectilevel"));
      THROWING_VELOCITY = new Modifier(LIST, "throwingvelocity", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.NORMAL_PERC_PARSER("throwingvel"), ModifierLimiter.NORMAL_PERC_LIMITER("throwingvel"));
      PROJECTILE_BOUNCES = new Modifier(LIST, "projectilebounces", 0, 0, Modifier.INT_ADD_APPEND, (var0) -> {
         return Math.max(0, var0);
      }, Modifier.NORMAL_FLAT_INT_PARSER("projectilebounce"), ModifierLimiter.NORMAL_FLAT_INT_LIMITER("projectilebounce"));
      DASH_STACKS = new Modifier(LIST, "dashstacks", 1, 0, Modifier.INT_ADD_APPEND, (var0) -> {
         return GameMath.limit(var0, 1, 100);
      }, Modifier.NORMAL_FLAT_INT_PARSER("dashstacks"), ModifierLimiter.NORMAL_FLAT_INT_LIMITER("dashstacks"));
      DASH_COOLDOWN = new Modifier(LIST, "dashcooldown", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.INVERSE_PERC_PARSER("dashcooldown"), ModifierLimiter.INVERSE_PERC_LIMITER("dashcooldown"));
      DASH_COOLDOWN_FLAT = new Modifier(LIST, "dashcooldownflat", 0.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return var0;
      }, Modifier.INVERSE_FLAT_FLOAT_PARSER("dashcooldownflat"), ModifierLimiter.INVERSE_FLAT_FLOAT_LIMITER("dashcooldownflat"));
      POTION_DURATION = new Modifier(LIST, "potionduration", 1.0F, 1.0F, (var0, var1, var2) -> {
         return var0 * var1;
      }, (var0) -> {
         return Math.max(var0, 0.0F);
      }, Modifier.NORMAL_PERC_PARSER("potionduration"), ModifierLimiter.NORMAL_PERC_LIMITER("potionduration"));
      STAMINA_CAPACITY = new Modifier(LIST, "staminacapacity", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.NORMAL_PERC_PARSER("staminacap"), ModifierLimiter.NORMAL_PERC_LIMITER("staminacap"));
      STAMINA_USAGE = new Modifier(LIST, "staminausage", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.INVERSE_PERC_PARSER("staminausage"), ModifierLimiter.INVERSE_PERC_LIMITER("staminausage"));
      STAMINA_REGEN = new Modifier(LIST, "staminaregen", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.NORMAL_PERC_PARSER("staminaregen"), ModifierLimiter.NORMAL_PERC_LIMITER("staminaregen"));
      TRAVEL_DISTANCE = new Modifier(LIST, "traveldistance", 3, 0, Modifier.INT_ADD_APPEND, (var0) -> {
         return GameMath.limit(var0, 1, 100);
      }, Modifier.NORMAL_FLAT_INT_PARSER("traveldistance"), ModifierLimiter.NORMAL_FLAT_INT_LIMITER("traveldistance"));
      BIOME_VIEW_DISTANCE = new Modifier(LIST, "biomeviewdistance", 1, 0, Modifier.INT_ADD_APPEND, (var0) -> {
         return Math.max(0, var0);
      }, Modifier.NORMAL_FLAT_INT_PARSER("biomeviewdistance"), ModifierLimiter.NORMAL_FLAT_INT_LIMITER("biomeviewdistance"));
      KNOCKBACK_INCOMING_MOD = new Modifier(LIST, "knockbackin", 1.0F, 1.0F, Modifier.FLOAT_MULT_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.LESS_GOOD_PERCENT_MODIFIER("knockbackin"), ModifierLimiter.INVERSE_PERC_LIMITER("knockbackin"));
      FISHING_POWER = new Modifier(LIST, "fishingpower", 0, 0, Modifier.INT_ADD_APPEND, Modifier.NORMAL_FLAT_INT_PARSER("fishingpower"), ModifierLimiter.NORMAL_FLAT_INT_LIMITER("fishingpower"));
      FISHING_LINES = new Modifier(LIST, "fishinglines", 0, 0, Modifier.INT_ADD_APPEND, Modifier.NORMAL_FLAT_INT_PARSER("fishinglines"), ModifierLimiter.NORMAL_FLAT_INT_LIMITER("fishinglines"));
      WATER_WALKING = new Modifier(LIST, "waterwalking", false, false, Modifier.OR_APPEND, Modifier.BOOL_PARSER("waterwalking"), (ModifierLimiter)null);
      EMITS_LIGHT = new Modifier(LIST, "emitlight", false, false, Modifier.OR_APPEND, Modifier.BOOL_PARSER("emitslight"), (ModifierLimiter)null);
      INVISIBILITY = new Modifier(LIST, "invisibility", false, false, Modifier.OR_APPEND, Modifier.BOOL_PARSER("invisibility"), (ModifierLimiter)null);
      SPELUNKER = new Modifier(LIST, "spelunker", false, false, Modifier.OR_APPEND, Modifier.BOOL_PARSER("spelunker"), (ModifierLimiter)null);
      TREASURE_HUNTER = new Modifier(LIST, "treasurehunter", false, false, Modifier.OR_APPEND, Modifier.BOOL_PARSER("treasurehunter"), (ModifierLimiter)null);
      BOUNCY = new Modifier(LIST, "bouncy", false, false, Modifier.OR_APPEND, Modifier.BOOL_PARSER("bouncy"), (ModifierLimiter)null);
      PARALYZED = new Modifier(LIST, "paralyzed", false, false, Modifier.OR_APPEND, Modifier.BOOL_PARSER("paralyzed"), (ModifierLimiter)null);
      UNTARGETABLE = new Modifier(LIST, "untargetable", false, false, Modifier.OR_APPEND, Modifier.BOOL_PARSER("untargetable"), (ModifierLimiter)null);
      INTIMIDATED = new Modifier(LIST, "intimidated", false, false, Modifier.OR_APPEND, Modifier.BOOL_PARSER("intimidated"), (ModifierLimiter)null);
      MANA_EXHAUSTED = new Modifier(LIST, "manaexhausted", false, false, Modifier.OR_APPEND, Modifier.INVERSE_BOOL_PARSER("manaexhausted"), (ModifierLimiter)null);
      MOB_SPAWN_RATE = new Modifier(LIST, "mobspawnrate", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.INVERSE_PERC_PARSER("mobspawnrate"), ModifierLimiter.INVERSE_PERC_LIMITER("mobspawnrate"));
      MOB_SPAWN_CAP = new Modifier(LIST, "mobspawncap", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.INVERSE_PERC_PARSER("mobspawncap"), ModifierLimiter.INVERSE_PERC_LIMITER("mobspawncap"));
      MOB_SPAWN_LIGHT_THRESHOLD = new Modifier(LIST, "mobspawnlightthreshold", 50, 0, Modifier.INT_ADD_APPEND, (var0) -> {
         return GameMath.limit(var0, 0, 150);
      }, Modifier.NORMAL_FLAT_INT_PARSER("mobspawnlightthreshold"), ModifierLimiter.NORMAL_FLAT_INT_LIMITER("mobspawnlightthreshold"));
      MAX_TARGET_RANGE_MODIFIER = 2.0F;
      TARGET_RANGE = new Modifier(LIST, "targetrange", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return GameMath.limit(var0, 0.0F, MAX_TARGET_RANGE_MODIFIER);
      }, Modifier.INVERSE_PERC_PARSER("targetrange"), ModifierLimiter.INVERSE_PERC_LIMITER("targetrange"));
      CHASER_RANGE = new Modifier(LIST, "chaserrange", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.NORMAL_PERC_PARSER("chaserrange"), ModifierLimiter.NORMAL_PERC_LIMITER("chaserrange"));
   }
}
