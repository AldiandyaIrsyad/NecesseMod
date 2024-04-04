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
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.objectItem.TorchObjectItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class LampObject extends FurnitureObject {
   protected String textureName;
   public GameTexture texture;
   public GameTexture texture_off;

   public LampObject(String var1, Rectangle var2, ToolType var3, Color var4, float var5, float var6) {
      super(var2);
      this.textureName = var1;
      this.toolType = var3;
      this.mapColor = var4;
      this.objectHealth = 50;
      this.lightLevel = 150;
      this.lightHue = var5;
      this.lightSat = var6;
      this.drawDamage = false;
      this.isLightTransparent = true;
      this.roomProperties.add("lights");
      this.furnitureType = "lamp";
   }

   public LampObject(String var1, Rectangle var2, Color var3, float var4, float var5) {
      this(var1, var2, ToolType.ALL, var3, var4, var5);
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/" + this.textureName);
      this.texture_off = GameTexture.fromFile("objects/" + this.textureName + "_off");
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4);
      int var11 = var7.getTileDrawY(var5);
      GameTexture var12 = this.isActive(var3, var4, var5) ? this.texture : this.texture_off;
      int var13 = var3.getObjectRotation(var4, var5) % (var12.getWidth() / 32);
      final TextureDrawOptionsEnd var14 = var12.initDraw().sprite(var13, 0, 32, var12.getHeight()).light(var9).pos(var10, var11 - var12.getHeight() + 32);
      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 16;
         }

         public void draw(TickManager var1) {
            var14.draw();
         }
      });
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      int var8 = var7.getTileDrawX(var2);
      int var9 = var7.getTileDrawY(var3);
      byte var10 = (byte)(var4 % (this.texture.getWidth() / 32));
      this.texture.initDraw().sprite(var10, 0, 32, this.texture.getHeight()).alpha(var5).draw(var8, var9 - this.texture.getHeight() + 32);
   }

   public Item generateNewObjectItem() {
      return new TorchObjectItem(this);
   }

   public int getLightLevel(Level var1, int var2, int var3) {
      return this.isActive(var1, var2, var3) ? this.lightLevel : 0;
   }

   public boolean isActive(Level var1, int var2, int var3) {
      byte var4 = var1.getObjectRotation(var2, var3);
      return this.getMultiTile(var4).streamIDs(var2, var3).noneMatch((var1x) -> {
         return var1.wireManager.isWireActiveAny(var1x.tileX, var1x.tileY);
      });
   }

   public void onWireUpdate(Level var1, int var2, int var3, int var4, boolean var5) {
      byte var6 = var1.getObjectRotation(var2, var3);
      Rectangle var7 = this.getMultiTile(var6).getTileRectangle(var2, var3);
      var1.lightManager.updateStaticLight(var7.x, var7.y, var7.x + var7.width - 1, var7.y + var7.height - 1, true);
   }
}
