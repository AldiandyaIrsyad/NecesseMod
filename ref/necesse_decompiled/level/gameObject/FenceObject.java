package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.regionSystem.SemiRegion;

public class FenceObject extends GameObject implements FenceObjectInterface {
   protected String textureName;
   protected GameTexture texture;
   protected LinkedList<Integer> connections = new LinkedList();

   public FenceObject(String var1, Color var2, int var3, int var4) {
      super(new Rectangle((32 - var3) / 2, (32 - var4) / 2, var3, var4));
      this.setItemCategory(new String[]{"objects", "fencesandgates"});
      this.textureName = var1;
      this.mapColor = var2;
      this.isFence = true;
      this.toolType = ToolType.ALL;
      this.regionType = SemiRegion.RegionType.FENCE;
      this.isLightTransparent = true;
      this.canPlaceOnShore = true;
      this.replaceCategories.add("fencegate");
      this.canReplaceCategories.add("fencegate");
      this.canReplaceCategories.add("fence");
      this.canReplaceCategories.add("wall");
      this.canReplaceCategories.add("door");
      this.replaceRotations = false;
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/" + this.textureName);
   }

   public boolean attachesToObject(GameObject var1, Level var2, int var3, int var4, LevelObject var5) {
      return var5.object.isWall || var5.object.getID() == var1.getID() || this.connections.contains(var5.object.getID());
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4);
      int var11 = var7.getTileDrawY(var5) - this.texture.getHeight() + 32;
      final SharedTextureDrawOptions var12 = new SharedTextureDrawOptions(this.texture);
      LevelObject var13 = var3.getLevelObject(var4, var5 - 1);
      LevelObject var14 = var3.getLevelObject(var4, var5 + 1);
      LevelObject var15 = var3.getLevelObject(var4 - 1, var5);
      LevelObject var16 = var3.getLevelObject(var4 + 1, var5);
      if (this.attachesToObject(this, var3, var4, var5, var13)) {
         var12.addSprite(1, 0, 32, this.texture.getHeight()).light(var9).pos(var10, var11);
         if (!var13.object.isFence || !((FenceObjectInterface)var13.object).attachesToObject(var13.object, var13.level, var13.tileX, var13.tileY, var3.getLevelObject(var4, var5))) {
            var12.addSprite(2, 0, 32, this.texture.getHeight()).light(var9).pos(var10, var11 - 32 + 8);
         }
      }

      var12.addSprite(0, 0, 32, this.texture.getHeight()).light(var9).pos(var10, var11);
      if (this.attachesToObject(this, var3, var4, var5, var14)) {
         var12.addSprite(2, 0, 32, this.texture.getHeight()).light(var9).pos(var10, var11);
      }

      if (this.attachesToObject(this, var3, var4, var5, var15)) {
         var12.addSprite(3, 0, 32, this.texture.getHeight()).light(var9).pos(var10, var11);
      }

      if (this.attachesToObject(this, var3, var4, var5, var16)) {
         var12.addSprite(4, 0, 32, this.texture.getHeight()).light(var9).pos(var10, var11);
      }

      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 14;
         }

         public void draw(TickManager var1) {
            var12.draw();
         }
      });
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      int var8 = var7.getTileDrawX(var2);
      int var9 = var7.getTileDrawY(var3) - this.texture.getHeight() + 32;
      LevelObject var10 = var1.getLevelObject(var2, var3 - 1);
      LevelObject var11 = var1.getLevelObject(var2, var3 + 1);
      LevelObject var12 = var1.getLevelObject(var2 - 1, var3);
      LevelObject var13 = var1.getLevelObject(var2 + 1, var3);
      this.texture.initDraw().sprite(0, 0, 32, this.texture.getHeight()).alpha(var5).draw(var8, var9);
      if (this.attachesToObject(this, var1, var2, var3, var10)) {
         this.texture.initDraw().sprite(1, 0, 32, this.texture.getHeight()).alpha(var5).draw(var8, var9);
         if (!var10.object.isFence || !((FenceObjectInterface)var10.object).attachesToObject(var10.object, var10.level, var10.tileX, var10.tileY, var1.getLevelObject(var2, var3))) {
            this.texture.initDraw().sprite(2, 0, 32, this.texture.getHeight()).alpha(var5).draw(var8, var9 - 32 + 8);
         }
      }

      if (this.attachesToObject(this, var1, var2, var3, var11)) {
         this.texture.initDraw().sprite(2, 0, 32, this.texture.getHeight()).alpha(var5).draw(var8, var9);
      }

      if (this.attachesToObject(this, var1, var2, var3, var12)) {
         this.texture.initDraw().sprite(3, 0, 32, this.texture.getHeight()).alpha(var5).draw(var8, var9);
      }

      if (this.attachesToObject(this, var1, var2, var3, var13)) {
         this.texture.initDraw().sprite(4, 0, 32, this.texture.getHeight()).alpha(var5).draw(var8, var9);
      }

   }

   public List<Rectangle> getCollisions(Level var1, int var2, int var3, int var4) {
      LevelObject var5 = var1.getLevelObject(var2, var3 - 1);
      LevelObject var6 = var1.getLevelObject(var2, var3 + 1);
      LevelObject var7 = var1.getLevelObject(var2 - 1, var3);
      LevelObject var8 = var1.getLevelObject(var2 + 1, var3);
      LinkedList var9 = new LinkedList();
      var9.add(new Rectangle(var2 * 32 + this.collision.x, var3 * 32 + this.collision.y, this.collision.width, this.collision.height));
      if (this.attachesToObject(this, var1, var2, var3, var5)) {
         var9.add(new Rectangle(var2 * 32 + this.collision.x, var3 * 32, this.collision.width, 16 - this.collision.height / 2));
      }

      if (this.attachesToObject(this, var1, var2, var3, var6)) {
         var9.add(new Rectangle(var2 * 32 + this.collision.x, var3 * 32 + 16 + this.collision.height / 2, this.collision.width, 16 - this.collision.height / 2));
      }

      if (this.attachesToObject(this, var1, var2, var3, var7)) {
         var9.add(new Rectangle(var2 * 32, var3 * 32 + this.collision.y, 16 - this.collision.width / 2, this.collision.height));
      }

      if (this.attachesToObject(this, var1, var2, var3, var8)) {
         var9.add(new Rectangle(var2 * 32 + 16 + this.collision.width / 2, var3 * 32 + this.collision.y, 16 - this.collision.width / 2, this.collision.height));
      }

      return var9;
   }

   public List<ObjectHoverHitbox> getHoverHitboxes(Level var1, int var2, int var3) {
      List var4 = super.getHoverHitboxes(var1, var2, var3);
      var4.add(new ObjectHoverHitbox(var2, var3, 0, -10, 32, 10));
      return var4;
   }
}
