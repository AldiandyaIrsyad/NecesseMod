package necesse.engine.events.worldGeneration;

import necesse.engine.events.GameEvent;
import necesse.level.maps.Level;

public class GeneratedIslandFeatureEvent extends GameEvent {
   public final Level level;
   public final float islandSize;

   public GeneratedIslandFeatureEvent(Level var1, float var2) {
      this.level = var1;
      this.islandSize = var2;
   }
}
