package necesse.level.gameTile;

import java.awt.Color;
import java.awt.Point;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.snow.SnowBiome;

public class SnowTile extends TerrainSplatterTile {
   public static double snowChance = GameMath.getAverageSuccessRuns(2800.0);
   private final GameRandom drawRandom;

   public SnowTile() {
      super(false, "snow");
      this.mapColor = new Color(240, 240, 240);
      this.canBeMined = true;
      this.drawRandom = new GameRandom();
   }

   public void tick(Level var1, int var2, int var3) {
      if (var1.isServer()) {
         if (var1.biome instanceof SnowBiome && var1.rainingLayer.isRaining() && var1.getObjectID(var2, var3) == 0 && GameRandom.globalRandom.getChance(snowChance)) {
            GameObject var4 = ObjectRegistry.getObject(ObjectRegistry.getObjectID("snowpile0"));
            if (var4.canPlace(var1, var2, var3, 0) == null) {
               var4.placeObject(var1, var2, var3, 0);
               var1.sendObjectUpdatePacket(var2, var3);
            }
         }

      }
   }

   public Point getTerrainSprite(GameTextureSection var1, Level var2, int var3, int var4) {
      int var5;
      synchronized(this.drawRandom) {
         var5 = this.drawRandom.seeded(this.getTileSeed(var3, var4)).nextInt(var1.getHeight() / 32);
      }

      return new Point(0, var5);
   }

   public int getTerrainPriority() {
      return 100;
   }
}
