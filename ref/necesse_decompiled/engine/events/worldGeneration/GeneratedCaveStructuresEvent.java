package necesse.engine.events.worldGeneration;

import necesse.engine.events.GameEvent;
import necesse.level.maps.Level;
import necesse.level.maps.generationModules.CaveGeneration;
import necesse.level.maps.generationModules.PresetGeneration;

public class GeneratedCaveStructuresEvent extends GameEvent {
   public final Level level;
   public final CaveGeneration caveGeneration;
   public final PresetGeneration presetGeneration;

   public GeneratedCaveStructuresEvent(Level var1, CaveGeneration var2, PresetGeneration var3) {
      this.level = var1;
      this.caveGeneration = var2;
      this.presetGeneration = var3;
   }
}
