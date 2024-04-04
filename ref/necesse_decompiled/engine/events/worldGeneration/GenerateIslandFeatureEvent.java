package necesse.engine.events.worldGeneration;

import necesse.engine.events.PreventableGameEvent;
import necesse.level.maps.Level;

public class GenerateIslandFeatureEvent extends PreventableGameEvent {
   public final Level level;
   public final float islandSize;

   public GenerateIslandFeatureEvent(Level var1, float var2) {
      this.level = var1;
      this.islandSize = var2;
   }
}
