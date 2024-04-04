package necesse.engine.events.worldGeneration;

import necesse.engine.events.GameEvent;
import necesse.level.maps.Level;
import necesse.level.maps.generationModules.CaveGeneration;

public class GeneratedCaveLayoutEvent extends GameEvent {
   public final Level level;
   public final CaveGeneration caveGeneration;

   public GeneratedCaveLayoutEvent(Level var1, CaveGeneration var2) {
      this.level = var1;
      this.caveGeneration = var2;
   }
}
