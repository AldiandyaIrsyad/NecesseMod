package necesse.level.maps.levelBuffManager;

import java.util.Collections;
import java.util.stream.Stream;
import necesse.engine.modifiers.ModifierManager;
import necesse.engine.modifiers.ModifierValue;
import necesse.level.maps.Level;

public class LevelBuffManager extends ModifierManager<ActiveLevelBuff> {
   public final Level level;
   private int updateTimer;
   private boolean updateModifiers;

   public LevelBuffManager(Level var1) {
      super(LevelModifiers.LIST);
      this.level = var1;
      super.updateModifiers();
   }

   public void clientTick() {
      boolean var1 = false;
      if (this.updateModifiers || ++this.updateTimer > 20) {
         var1 = true;
      }

      if (var1) {
         this.updateModifiers();
      }

   }

   public void serverTick() {
      boolean var1 = false;
      if (this.updateModifiers || ++this.updateTimer > 20) {
         var1 = true;
      }

      if (var1) {
         this.updateModifiers();
      }

   }

   protected void updateModifiers() {
      this.updateTimer = 0;
      this.updateModifiers = false;
      super.updateModifiers();
   }

   public void updateBuffs() {
      this.updateModifiers = true;
   }

   public void forceUpdateBuffs() {
      this.updateModifiers();
   }

   protected Iterable<? extends ActiveLevelBuff> getModifierContainers() {
      return Collections.emptyList();
   }

   public Stream<ModifierValue<?>> getDefaultModifiers() {
      return this.level.getDefaultLevelModifiers();
   }
}
