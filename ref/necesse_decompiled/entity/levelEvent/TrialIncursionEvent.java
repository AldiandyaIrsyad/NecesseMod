package necesse.entity.levelEvent;

import java.util.stream.Stream;
import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.level.maps.levelBuffManager.LevelBuffsEntityComponent;
import necesse.level.maps.levelBuffManager.LevelModifiers;
import necesse.level.maps.levelBuffManager.MobBuffsEntityComponent;

public class TrialIncursionEvent extends IncursionLevelEvent implements MobBuffsEntityComponent, LevelBuffsEntityComponent {
   public TrialIncursionEvent() {
   }

   public TrialIncursionEvent(String var1) {
      super(var1);
   }

   public void init() {
      super.init();
   }

   public Stream<ModifierValue<?>> getLevelModifiers() {
      return !this.isDone && !this.isFighting && !this.bossPortalSpawned ? Stream.empty() : Stream.of(new ModifierValue(LevelModifiers.ENEMIES_RETREATING, true));
   }

   public Stream<ModifierValue<?>> getLevelModifiers(Mob var1) {
      if (!this.isDone && !this.isFighting && !this.bossPortalSpawned) {
         if (var1.isPlayer) {
            return Stream.of(new ModifierValue(BuffModifiers.MOB_SPAWN_RATE, 1.0F), (new ModifierValue(BuffModifiers.MOB_SPAWN_LIGHT_THRESHOLD)).min(150));
         }
      } else if (var1.isPlayer) {
         return Stream.of((new ModifierValue(BuffModifiers.MOB_SPAWN_RATE, 0.0F)).max(0.0F, 1000000));
      }

      return Stream.empty();
   }

   public boolean isObjectiveDone() {
      return true;
   }

   public int getObjectiveCurrent() {
      return 0;
   }

   public int getObjectiveMax() {
      return 0;
   }
}
