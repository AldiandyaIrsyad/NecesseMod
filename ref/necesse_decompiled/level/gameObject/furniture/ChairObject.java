package necesse.level.gameObject.furniture;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.ObjectHoverHitbox;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class ChairObject extends FurnitureObject {
   protected String textureName;
   public GameTexture texture;

   public ChairObject(String var1, ToolType var2, Color var3) {
      super(new Rectangle(32, 32));
      this.textureName = var1;
      this.toolType = var2;
      this.mapColor = var3;
      this.objectHealth = 50;
      this.drawDamage = false;
      this.isLightTransparent = true;
      this.furnitureType = "chair";
   }

   public ChairObject(String var1, Color var2) {
      this(var1, ToolType.ALL, var2);
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/" + this.textureName);
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      int var9 = var7.getTileDrawX(var4);
      int var10 = var7.getTileDrawY(var5);
      byte var11 = var3.getObjectRotation(var4, var5);
      GameLight var12 = var3.getLightLevel(var4, var5);
      final TextureDrawOptionsEnd var13 = this.texture.initDraw().sprite(var11 % 4, 0, 32, this.texture.getHeight()).light(var12).pos(var9, var10 - this.texture.getHeight() + 32);
      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 16;
         }

         public void draw(TickManager var1) {
            var13.draw();
         }
      });
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      int var8 = var7.getTileDrawX(var2);
      int var9 = var7.getTileDrawY(var3);
      this.texture.initDraw().sprite(var4 % 4, 0, 32, this.texture.getHeight()).alpha(var5).draw(var8, var9 - this.texture.getHeight() + 32);
   }

   public Rectangle getCollision(Level var1, int var2, int var3, int var4) {
      if (var4 == 0) {
         return new Rectangle(var2 * 32 + 8, var3 * 32 + 18, 16, 6);
      } else if (var4 == 1) {
         return new Rectangle(var2 * 32 + 8, var3 * 32 + 10, 6, 14);
      } else if (var4 == 2) {
         return new Rectangle(var2 * 32 + 8, var3 * 32 + 12, 16, 6);
      } else {
         return var4 == 3 ? new Rectangle(var2 * 32 + 18, var3 * 32 + 10, 6, 14) : new Rectangle(0, 0);
      }
   }

   public List<ObjectHoverHitbox> getHoverHitboxes(Level var1, int var2, int var3) {
      List var4 = super.getHoverHitboxes(var1, var2, var3);
      var4.add(new ObjectHoverHitbox(var2, var3, 0, -16, 32, 16));
      return var4;
   }
}
