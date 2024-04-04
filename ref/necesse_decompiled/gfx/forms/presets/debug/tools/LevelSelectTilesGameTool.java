package necesse.gfx.forms.presets.debug.tools;

import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;

public abstract class LevelSelectTilesGameTool extends LevelSelectGameTool {
   public LevelSelectTilesGameTool(int var1) {
      super(var1);
   }

   public LevelSelectTilesGameTool() {
   }

   public void onSelection(int var1, int var2, int var3, int var4) {
      int var5 = var1 / 32;
      int var6 = var2 / 32;
      int var7 = var3 / 32;
      int var8 = var4 / 32;
      this.onTileSelection(var5, var6, var7, var8);
   }

   public void drawSelection(GameCamera var1, PlayerMob var2, int var3, int var4, int var5, int var6) {
      int var7 = var3 / 32;
      int var8 = var4 / 32;
      int var9 = var5 / 32;
      int var10 = var6 / 32;
      this.drawTileSelection(var1, var2, var7, var8, var9, var10);
   }

   public abstract void onTileSelection(int var1, int var2, int var3, int var4);

   public abstract void drawTileSelection(GameCamera var1, PlayerMob var2, int var3, int var4, int var5, int var6);
}
