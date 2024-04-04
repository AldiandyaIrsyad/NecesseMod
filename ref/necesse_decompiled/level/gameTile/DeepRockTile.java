package necesse.level.gameTile;

import java.awt.Color;
import java.awt.Point;
import necesse.engine.util.GameRandom;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.level.maps.Level;

public class DeepRockTile extends TerrainSplatterTile {
   private final GameRandom drawRandom;

   public DeepRockTile() {
      super(false, "deeprocktile");
      this.mapColor = new Color(48, 52, 54);
      this.canBeMined = true;
      this.drawRandom = new GameRandom();
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
