package necesse.level.gameTile;

import java.awt.Color;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.LevelTileDrawOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.level.maps.Level;

public class LavaPathTile extends PathTiledTile {
   private GameTextureSection lightTexture;

   public LavaPathTile() {
      super("lavapath", new Color(100, 20, 20));
      this.lightHue = 0.0F;
      this.lightSat = 0.6F;
      this.lightLevel = 50;
   }

   protected void loadTextures() {
      super.loadTextures();
      this.lightTexture = tileTextures.addTexture(GameTexture.fromFile("tiles/lavapath_light"));
   }

   public void addDrawables(LevelTileDrawOptions var1, List<LevelSortedDrawable> var2, Level var3, int var4, int var5, GameCamera var6, TickManager var7) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7);
      int var8 = var4 % this.textures.length;
      int var9 = var5 % this.textures[0].length;
      final TextureDrawOptionsEnd var10 = this.lightTexture.sprite(2 + var8, var9, 32).initDraw().light(var3.getLightLevel(var4, var5).minLevelCopy(100.0F)).pos(var6.getTileDrawX(var4), var6.getTileDrawY(var5));
      var2.add(new LevelSortedDrawable(this) {
         public int getSortY() {
            return 0;
         }

         public void draw(TickManager var1) {
            var10.draw();
         }
      });
   }
}
