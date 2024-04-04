package necesse.engine.events.worldGeneration;

import necesse.engine.events.PreventableGameEvent;
import necesse.level.maps.Level;
import necesse.level.maps.generationModules.CaveGeneration;

public class GenerateCaveOresEvent extends PreventableGameEvent {
   public final Level level;
   public final CaveGeneration caveGeneration;

   public GenerateCaveOresEvent(Level var1, CaveGeneration var2) {
      this.level = var1;
      this.caveGeneration = var2;
   }
}
