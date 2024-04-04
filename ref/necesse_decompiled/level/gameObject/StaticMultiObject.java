package necesse.level.gameObject;

import java.awt.Rectangle;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.multiTile.StaticMultiTile;

public class StaticMultiObject extends GameObject {
   protected int multiX;
   protected int multiY;
   protected int multiWidth;
   protected int multiHeight;
   protected int[] multiIDs;
   protected String texturePath;
   public GameTexture texture;

   public StaticMultiObject(int var1, int var2, int var3, int var4, int[] var5, Rectangle var6, String var7) {
      super(var6.intersection(new Rectangle(var1 * 32, var2 * 32, 32, 32)));
      Rectangle var10000 = this.collision;
      var10000.x -= var1 * 32;
      var10000 = this.collision;
      var10000.y -= var2 * 32;
      this.multiX = var1;
      this.multiY = var2;
      this.multiWidth = var3;
      this.multiHeight = var4;
      this.multiIDs = var5;
      this.texturePath = var7;
   }

   public MultiTile getMultiTile(int var1) {
      return new StaticMultiTile(this.multiX, this.multiY, this.multiWidth, this.multiHeight, this.multiX == 0 && this.multiY == 0, this.multiIDs);
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/" + this.texturePath);
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      final DrawOptions var9 = this.getMultiTextureDrawOptions(this.texture, var3, var4, var5, var7);
      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 16;
         }

         public void draw(TickManager var1) {
            var9.draw();
         }
      });
   }

   protected DrawOptions getMultiTextureDrawOptions(GameTexture var1, Level var2, int var3, int var4, GameCamera var5) {
      GameLight var6 = var2.getLightLevel(var3, var4);
      int var7 = var5.getTileDrawX(var3);
      int var8 = var5.getTileDrawY(var4);
      int var9 = this.multiX * 32;
      int var10 = var1.getHeight() - this.multiHeight * 32;
      if (this.multiY == 0) {
         return var1.initDraw().section(var9, var9 + 32, 0, 32 + var10).light(var6).pos(var7, var8 - var10);
      } else {
         int var11 = this.multiY * 32 + var10;
         return var1.initDraw().section(var9, var9 + 32, var11, var11 + 32).light(var6).pos(var7, var8);
      }
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      int var8 = var7.getTileDrawX(var2 - this.multiX);
      int var9 = var7.getTileDrawY(var3 - this.multiY);
      int var10 = this.texture.getHeight() - this.multiHeight * 32;
      this.texture.initDraw().alpha(var5).draw(var8, var9 - var10);
   }
}
