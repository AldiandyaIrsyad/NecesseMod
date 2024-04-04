package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.localization.Localization;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.CartographerTableObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class CartographerTableObject extends GameObject {
   public GameTexture texture;

   public CartographerTableObject() {
      super(new Rectangle(32, 32));
      this.mapColor = new Color(150, 119, 70);
      this.toolType = ToolType.ALL;
      this.objectHealth = 50;
      this.drawDamage = false;
      this.isLightTransparent = true;
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/cartographertable");
   }

   public Rectangle getCollision(Level var1, int var2, int var3, int var4) {
      return var4 % 2 == 0 ? new Rectangle(var2 * 32 + 2, var3 * 32 + 6, 28, 20) : new Rectangle(var2 * 32 + 4, var3 * 32 + 2, 24, 28);
   }

   public List<ObjectHoverHitbox> getHoverHitboxes(Level var1, int var2, int var3) {
      List var4 = super.getHoverHitboxes(var1, var2, var3);
      var4.add(new ObjectHoverHitbox(var2, var3, 0, -16, 32, 16));
      return var4;
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4);
      int var11 = var7.getTileDrawY(var5);
      int var12 = var3.getObjectRotation(var4, var5) % 4;
      final TextureDrawOptionsEnd var13 = this.texture.initDraw().sprite(var12 % 4, 0, 32, this.texture.getHeight()).light(var9).pos(var10, var11 - this.texture.getHeight() + 32);
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

   public CartographerTableObjectEntity getCartographerTableObjectEntity(Level var1, int var2, int var3) {
      ObjectEntity var4 = var1.entityManager.getObjectEntity(var2, var3);
      return var4 instanceof CartographerTableObjectEntity ? (CartographerTableObjectEntity)var4 : null;
   }

   public String getInteractTip(Level var1, int var2, int var3, PlayerMob var4, boolean var5) {
      return Localization.translate("controls", "cartographertip");
   }

   public boolean canInteract(Level var1, int var2, int var3, PlayerMob var4) {
      return true;
   }

   public void interact(Level var1, int var2, int var3, PlayerMob var4) {
      CartographerTableObjectEntity var5 = this.getCartographerTableObjectEntity(var1, var2, var3);
      if (var5 != null) {
         var5.interact(var4);
      }

   }

   public ListGameTooltips getItemTooltips(InventoryItem var1, PlayerMob var2) {
      ListGameTooltips var3 = super.getItemTooltips(var1, var2);
      var3.add(Localization.translate("itemtooltip", "cartographertabletip"));
      return var3;
   }

   public ObjectEntity getNewObjectEntity(Level var1, int var2, int var3) {
      return new CartographerTableObjectEntity(var1, "cartographertable", var2, var3);
   }
}
