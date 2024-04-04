package necesse.level.maps;

import necesse.engine.registries.TileRegistry;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldEntity;

public class DebugLevel extends Level {
   public DebugLevel(LevelIdentifier var1, int var2, int var3, WorldEntity var4) {
      super(var1, var2, var3, var4);
   }

   public DebugLevel(int var1, int var2, int var3, WorldEntity var4) {
      super(new LevelIdentifier(var1, var2, var3), 300, 300, var4);
      int var5 = TileRegistry.grassID;

      for(int var6 = 0; var6 < this.width; ++var6) {
         for(int var7 = 0; var7 < this.height; ++var7) {
            this.setTile(var6, var7, var5);
         }
      }

   }
}
