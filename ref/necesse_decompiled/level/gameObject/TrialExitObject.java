package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.DungeonExitObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class TrialExitObject extends DungeonExitObject {
   public TrialExitObject() {
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/trialexit");
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4);
      int var11 = var7.getTileDrawY(var5);
      GameTile var12 = var3.getTile(var4, var5);
      Color var13 = var12.getMapColor(var3, var4, var5);
      final TextureDrawOptionsEnd var14 = this.texture.initDraw().sprite(0, 0, 32, 64).color(var13).light(var9).pos(var10, var11 - 32);
      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 16;
         }

         public void draw(TickManager var1) {
            var14.draw();
         }
      });
   }

   public ObjectEntity getNewObjectEntity(Level var1, final int var2, final int var3) {
      return new DungeonExitObjectEntity(var1, var2, var3, 10, 10) {
         public boolean shouldDrawOnMap() {
            return true;
         }

         public Rectangle drawOnMapBox() {
            return new Rectangle(-8, -24, 16, 32);
         }

         public void drawOnMap(TickManager var1, int var2x, int var3x) {
            GameTile var4 = this.getLevel().getTile(var2, var3);
            Color var5 = var4.getMapColor(this.getLevel(), var2, var3);
            TrialExitObject.this.texture.initDraw().size(16, 32).color(var5).draw(var2x - 8, var3x - 24);
         }

         public GameTooltips getMapTooltips() {
            return new StringTooltips(TrialExitObject.this.getDisplayName());
         }
      };
   }
}
