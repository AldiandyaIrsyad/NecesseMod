package necesse.level.maps.levelBuffManager;

import necesse.engine.modifiers.Modifier;
import necesse.engine.modifiers.ModifierLimiter;
import necesse.engine.modifiers.ModifierList;

public class LevelModifiers {
   public static final ModifierList LIST = new ModifierList();
   public static final Modifier<Float> LOOT;
   public static final Modifier<Float> ENEMY_DAMAGE;
   public static final Modifier<Float> ENEMY_SPEED;
   public static final Modifier<Float> ENEMY_MAX_HEALTH;
   public static final Modifier<Boolean> ENEMIES_RETREATING;

   public LevelModifiers() {
   }

   static {
      LOOT = new Modifier(LIST, "loot", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.NORMAL_PERC_PARSER("loot"), ModifierLimiter.NORMAL_PERC_LIMITER("loot"));
      ENEMY_DAMAGE = new Modifier(LIST, "enemydamage", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.INVERSE_PERC_PARSER("enemydamage"), ModifierLimiter.INVERSE_PERC_LIMITER("enemydamage"));
      ENEMY_SPEED = new Modifier(LIST, "enemyspeed", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.INVERSE_PERC_PARSER("enemyspeed"), ModifierLimiter.INVERSE_PERC_LIMITER("enemyspeed"));
      ENEMY_MAX_HEALTH = new Modifier(LIST, "enemymaxhealth", 1.0F, 0.0F, Modifier.FLOAT_ADD_APPEND, (var0) -> {
         return Math.max(0.0F, var0);
      }, Modifier.INVERSE_PERC_PARSER("enemymaxhealth"), ModifierLimiter.INVERSE_PERC_LIMITER("enemymaxhealth"));
      ENEMIES_RETREATING = new Modifier(LIST, "enemiesretreating", false, false, Modifier.OR_APPEND, Modifier.BOOL_PARSER("enemiesretreating"), (ModifierLimiter)null);
   }
}
