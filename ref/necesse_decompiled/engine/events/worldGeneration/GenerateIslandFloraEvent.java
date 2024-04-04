package necesse.engine.events.worldGeneration;

import necesse.engine.events.PreventableGameEvent;
import necesse.level.maps.Level;
import necesse.level.maps.generationModules.IslandGeneration;

public class GenerateIslandFloraEvent extends PreventableGameEvent {
   public final Level level;
   public final float islandSize;
   public final IslandGeneration islandGeneration;

   public GenerateIslandFloraEvent(Level var1, float var2, IslandGeneration var3) {
      this.level = var1;
      this.islandSize = var2;
      this.islandGeneration = var3;
   }
}
