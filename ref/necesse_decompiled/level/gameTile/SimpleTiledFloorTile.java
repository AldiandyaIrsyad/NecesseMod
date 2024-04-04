package necesse.level.gameTile;

import java.awt.Color;
import java.awt.Point;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.level.maps.Level;

public class SimpleTiledFloorTile extends TerrainSplatterTile {
   public SimpleTiledFloorTile(String var1, Color var2) {
      super(true, var1);
      this.mapColor = var2;
      this.canBeMined = true;
   }

   public Point getTerrainSprite(GameTextureSection var1, Level var2, int var3, int var4) {
      int var5 = var3 % (var1.getWidth() / 32);
      int var6 = var4 % (var1.getHeight() / 32);
      return new Point(var5, var6);
   }

   public int getTerrainPriority() {
      return 400;
   }
}
