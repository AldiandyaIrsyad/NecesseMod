package necesse.level.maps.presets;

import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;

public class HousePreset extends Preset {
   public HousePreset() {
      super(15, 15);
      int var1 = TileRegistry.getTileID("dungeonfloor");
      int var2 = ObjectRegistry.getObjectID("dungeonwall");
      this.fillTile(0, 0, 15, 15, var1);
      this.fillObject(0, 0, 15, 15, 0);
      this.boxObject(0, 0, 15, 15, var2);
      this.boxObject(0, 0, 13, 13, var2);
   }
}
