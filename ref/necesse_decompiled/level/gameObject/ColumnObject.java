package necesse.level.gameObject;

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
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class ColumnObject extends GameObject {
   protected GameTexture texture;
   protected String textureName;
   protected boolean hasRotation;
   protected int yOffset;

   public ColumnObject(String var1, Color var2, ToolType var3, int var4, int var5) {
      super(new Rectangle((32 - var4) / 2, (32 - var5) / 2, var4, var5));
      this.yOffset = -3;
      this.textureName = var1;
      this.mapColor = var2;
      this.toolType = var3;
      this.setItemCategory(new String[]{"objects", "furniture"});
      this.drawDamage = false;
      this.isLightTransparent = true;
      this.replaceCategories.add("column");
      this.canReplaceCategories.add("furniture");
      this.canReplaceCategories.add("column");
      this.canReplaceCategories.add("torch");
      this.replaceRotations = false;
   }

   public ColumnObject(String var1, Color var2, ToolType var3) {
      this(var1, var2, var3, 28, 22);
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/" + this.textureName);
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4);
      int var11 = var7.getTileDrawY(var5) - this.texture.getHeight() + 32;
      final TextureDrawOptionsEnd var12;
      int var13;
      if (this.hasRotation) {
         var13 = var3.getObjectRotation(var4, var5);
         int var14 = this.texture.getWidth() / 4;
         int var15 = (var14 - 32) / 2;
         var12 = this.texture.initDraw().sprite(var13 % 4, 0, var14, this.texture.getHeight()).light(var9).pos(var10 - var15, var11 + this.yOffset);
      } else {
         var13 = (this.texture.getWidth() - 32) / 2;
         var12 = this.texture.initDraw().light(var9).pos(var10 - var13, var11 + this.yOffset);
      }

      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 16;
         }

         public void draw(TickManager var1) {
            var12.draw();
         }
      });
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      int var8 = var7.getTileDrawX(var2);
      int var9 = var7.getTileDrawY(var3) - this.texture.getHeight() + 32;
      int var10;
      if (this.hasRotation) {
         var10 = this.texture.getWidth() / 4;
         int var11 = (var10 - 32) / 2;
         this.texture.initDraw().sprite(var4 % 4, 0, var10, this.texture.getHeight()).alpha(var5).draw(var8 - var11, var9 + this.yOffset);
      } else {
         var10 = (this.texture.getWidth() - 32) / 2;
         this.texture.initDraw().alpha(var5).draw(var8 - var10, var9 + this.yOffset);
      }

   }

   public List<ObjectHoverHitbox> getHoverHitboxes(Level var1, int var2, int var3) {
      List var4 = super.getHoverHitboxes(var1, var2, var3);
      var4.add(new ObjectHoverHitbox(var2, var3, 0, -32, 32, 32));
      return var4;
   }
}
