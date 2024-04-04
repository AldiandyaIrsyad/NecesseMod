package necesse.engine.events.worldGeneration;

import necesse.engine.events.GameEvent;
import necesse.level.maps.Level;

public class GeneratedLevelEvent extends GameEvent {
   public final Level level;

   public GeneratedLevelEvent(Level var1) {
      this.level = var1;
   }
}
