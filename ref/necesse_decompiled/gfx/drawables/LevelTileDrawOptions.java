package necesse.gfx.drawables;

import necesse.engine.Settings;
import necesse.engine.tickManager.Performance;
import necesse.engine.tickManager.PerformanceWrapper;
import necesse.engine.tickManager.TickManager;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class LevelTileDrawOptions extends SharedTextureDrawOptions {
   protected SharedTextureDrawOptions lightOverlays;

   public LevelTileDrawOptions() {
      super(GameTile.generatedTileTexture);
      this.lightOverlays = new SharedTextureDrawOptions(GameTile.generatedTileTexture);
   }

   public void addLight(TickManager var1, Level var2, int var3, int var4, GameCamera var5) {
      int var6 = var5.getTileDrawX(var3);
      int var7 = var5.getTileDrawY(var4);
      if (Settings.smoothLighting) {
         this.lightOverlays.add(GameTile.tileBlankTexture).size(32, 32).advColor(getSmoothLight(var1, var2, var3, var4)).pos(var6 + 16, var7 + 16);
      } else {
         this.lightOverlays.add(GameTile.tileBlankTexture).size(32, 32).advColor(getTiledLight(var1, var2, var3, var4)).pos(var6, var7);
      }

   }

   public static float[] getTiledLight(TickManager var0, Level var1, int var2, int var3) {
      PerformanceWrapper var4 = Performance.wrapTimer(var0, "getLight");

      float[] var6;
      try {
         GameLight var5 = var1.getLightLevel(var2, var3);
         var6 = var5.getAdvColor();
      } finally {
         var4.end();
      }

      return var6;
   }

   public static float[] getSmoothLight(TickManager var0, Level var1, int var2, int var3) {
      PerformanceWrapper var4 = Performance.wrapTimer(var0, "getLight");

      float[] var9;
      try {
         GameLight var5 = var1.getLightLevel(var2, var3);
         GameLight var6 = var1.getLightLevel(var2 + 1, var3);
         GameLight var7 = var1.getLightLevel(var2 + 1, var3 + 1);
         GameLight var8 = var1.getLightLevel(var2, var3 + 1);
         var9 = var5.getAdvColor(var6, var7, var8);
      } finally {
         var4.end();
      }

      return var9;
   }
}
