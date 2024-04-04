package necesse.level.maps.presets.modularPresets.dungeonPresets;

import necesse.engine.util.GameRandom;

public class DungeonTestPreset extends DungeonPreset {
   public DungeonTestPreset(GameRandom var1) {
      super(1, 1, var1);
      this.open(0, 0, 0);
      this.open(0, 0, 1);
      this.open(0, 0, 2);
      this.open(0, 0, 3);
   }
}
