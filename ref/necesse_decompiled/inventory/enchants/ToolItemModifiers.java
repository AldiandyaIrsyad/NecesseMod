package necesse.inventory.enchants;

import necesse.engine.modifiers.Modifier;
import necesse.engine.modifiers.ModifierLimiter;
import necesse.engine.modifiers.ModifierList;

public class ToolItemModifiers {
   public static final ModifierList LIST = new ModifierList();
   public static final Modifier<Float> DAMAGE;
   public static final Modifier<Float> CRIT_CHANCE;
   public static final Modifier<Integer> ARMOR_PEN;
   public static final Modifier<Float> KNOCKBACK;
   public static final Modifier<Float> RESILIENCE_GAIN;
   public static final Modifier<Float> RANGE;
   public static final Modifier<Float> SUMMONS_TARGET_RANGE;
   public static final Modifier<Float> ATTACK_SPEED;
   public static final Modifier<Float> SUMMONS_SPEED;
   public static final Modifier<Float> VELOCITY;
   public static final Modifier<Float> MANA_USAGE;
   public static final Modifier<Float> TOOL_DAMAGE;
   public static final Modifier<Float> MINING_SPEED;

   public ToolItemModifiers() {
   }

   static {
      DAMAGE = new Modifier(LIST, "damage", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.NORMAL_PERC_PARSER("damage"), ModifierLimiter.NORMAL_PERC_LIMITER("damage"));
      CRIT_CHANCE = new Modifier(LIST, "critchance", 0.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.NORMAL_PERC_PARSER("critchance"), ModifierLimiter.NORMAL_PERC_LIMITER("critchance"));
      ARMOR_PEN = new Modifier(LIST, "armorpen", 0, 0, Modifier.INT_ADD_APPEND, Modifier.NORMAL_FLAT_INT_PARSER("armorpenflat"), ModifierLimiter.NORMAL_FLAT_INT_LIMITER("armorpenflat"));
      KNOCKBACK = new Modifier(LIST, "knockback", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.NORMAL_PERC_PARSER("toolknockback"), ModifierLimiter.NORMAL_PERC_LIMITER("toolknockback"));
      RESILIENCE_GAIN = new Modifier(LIST, "resilience", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.NORMAL_PERC_PARSER("resiliencegain"), ModifierLimiter.NORMAL_PERC_LIMITER("resiliencegain"));
      RANGE = new Modifier(LIST, "range", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.NORMAL_PERC_PARSER("toolrange"), ModifierLimiter.NORMAL_PERC_LIMITER("toolrange"));
      SUMMONS_TARGET_RANGE = new Modifier(LIST, "summontargetrange", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.NORMAL_PERC_PARSER("summonstargetrange"), ModifierLimiter.NORMAL_PERC_LIMITER("summonstargetrange"));
      ATTACK_SPEED = new Modifier(LIST, "attackspeed", 0.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return var0;
      }, Modifier.NORMAL_PERC_PARSER("attackspeed"), ModifierLimiter.NORMAL_PERC_LIMITER("attackspeed"));
      SUMMONS_SPEED = new Modifier(LIST, "summonsspeed", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.NORMAL_PERC_PARSER("summonsspeed"), ModifierLimiter.NORMAL_PERC_LIMITER("summonsspeed"));
      VELOCITY = new Modifier(LIST, "velocity", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.NORMAL_PERC_PARSER("projectilevel"), ModifierLimiter.NORMAL_PERC_LIMITER("projectilevel"));
      MANA_USAGE = new Modifier(LIST, "manausage", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.INVERSE_PERC_PARSER("manausage"), ModifierLimiter.NORMAL_PERC_LIMITER("manausage"));
      TOOL_DAMAGE = new Modifier(LIST, "tooldamage", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.NORMAL_PERC_PARSER("tooldamage"), ModifierLimiter.NORMAL_PERC_LIMITER("tooldamage"));
      MINING_SPEED = new Modifier(LIST, "miningspeed", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.NORMAL_PERC_PARSER("miningspeed"), ModifierLimiter.NORMAL_PERC_LIMITER("miningspeed"));
   }
}
