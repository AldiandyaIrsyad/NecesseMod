package necesse.level.maps.biomes;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldEntity;
import necesse.level.maps.Level;
import necesse.level.maps.generationModules.IslandGeneration;

public class BasicSurfaceLevel extends Level {
   public BasicSurfaceLevel(LevelIdentifier var1, int var2, int var3, WorldEntity var4) {
      super(var1, var2, var3, var4);
   }

   public BasicSurfaceLevel(int var1, int var2, float var3, WorldEntity var4) {
      super(new LevelIdentifier(var1, var2, 0), 300, 300, var4);
      this.generateLevel(var3);
   }

   public void generateLevel(float var1) {
      int var2 = (int)(var1 * 90.0F) + 40;
      IslandGeneration var3 = new IslandGeneration(this, var2);
      int var4 = TileRegistry.getTileID("watertile");
      int var5 = TileRegistry.getTileID("sandtile");
      int var6 = TileRegistry.grassID;
      if (var3.random.getChance(0.05F)) {
         var3.generateSimpleIsland(this.width / 2, this.height / 2, var4, var6, var5);
      } else {
         var3.generateShapedIsland(var4, var6, var5);
      }

   }

   public GameMessage getLocationMessage() {
      return new LocalMessage("biome", "surface", "biome", this.biome.getLocalization());
   }
}
