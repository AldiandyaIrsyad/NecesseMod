package necesse.level.gameTile;

import java.awt.Color;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.LevelTileDrawOptions;
import necesse.level.maps.Level;

public class EmptyTile extends GameTile {
   public EmptyTile() {
      super(false);
      this.mapColor = new Color(0, 0, 0);
      this.canBeMined = false;
   }

   public void addDrawables(LevelTileDrawOptions var1, List<LevelSortedDrawable> var2, Level var3, int var4, int var5, GameCamera var6, TickManager var7) {
      int var8 = var6.getTileDrawX(var4);
      int var9 = var6.getTileDrawY(var5);
      var1.add(tileBlankTexture).size(32, 32).color(0.0F, 0.0F, 0.0F).pos(var8, var9);
   }

   public String canPlace(Level var1, int var2, int var3) {
      return "neverplace";
   }
}
