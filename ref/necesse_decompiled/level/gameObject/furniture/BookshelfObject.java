package necesse.level.gameObject.furniture;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.Settings;
import necesse.engine.localization.Localization;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.InventoryObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.container.object.OEInventoryContainer;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.ObjectHoverHitbox;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class BookshelfObject extends FurnitureObject {
   protected String textureName;
   public GameTexture texture;

   public BookshelfObject(String var1, ToolType var2, Color var3) {
      super(new Rectangle(32, 32));
      this.textureName = var1;
      this.toolType = var2;
      this.mapColor = var3;
      this.objectHealth = 50;
      this.drawDamage = false;
      this.isLightTransparent = true;
      this.furnitureType = "bookshelf";
   }

   public BookshelfObject(String var1, Color var2) {
      this(var1, ToolType.ALL, var2);
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/" + this.textureName);
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4);
      int var11 = var7.getTileDrawY(var5);
      byte var12 = var3.getObjectRotation(var4, var5);
      float var13 = 1.0F;
      if (var8 != null && !Settings.hideUI) {
         Rectangle var14 = new Rectangle(var4 * 32 - 16, var5 * 32 - 32 - 16, 64, 64);
         if (var8.getCollision().intersects(var14)) {
            var13 = 0.5F;
         } else if (var14.contains(var7.getMouseLevelPosX(), var7.getMouseLevelPosY())) {
            var13 = 0.5F;
         }
      }

      final TextureDrawOptionsEnd var15 = this.texture.initDraw().sprite(var12 % 4, 0, 32, this.texture.getHeight()).alpha(var13).light(var9).pos(var10, var11 - this.texture.getHeight() + 64);
      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 16;
         }

         public void draw(TickManager var1) {
            var15.draw();
         }
      });
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      int var8 = var7.getTileDrawX(var2);
      int var9 = var7.getTileDrawY(var3);
      this.texture.initDraw().sprite(var4 % 4, 0, 32, this.texture.getHeight()).alpha(var5).draw(var8, var9 - this.texture.getHeight() + 64);
   }

   public Rectangle getCollision(Level var1, int var2, int var3, int var4) {
      if (var4 == 0) {
         return new Rectangle(var2 * 32, var3 * 32 + 22, 32, 10);
      } else if (var4 == 1) {
         return new Rectangle(var2 * 32, var3 * 32, 12, 32);
      } else {
         return var4 == 2 ? new Rectangle(var2 * 32, var3 * 32, 32, 10) : new Rectangle(var2 * 32 + 20, var3 * 32, 12, 32);
      }
   }

   public List<ObjectHoverHitbox> getHoverHitboxes(Level var1, int var2, int var3) {
      byte var4 = var1.getObjectRotation(var2, var3);
      List var5 = super.getHoverHitboxes(var1, var2, var3);
      if (var4 == 0) {
         var5.add(new ObjectHoverHitbox(var2, var3, 0, -24, 32, 24));
      } else if (var4 == 1) {
         var5.add(new ObjectHoverHitbox(var2, var3, 0, -44, 12, 44));
      } else if (var4 == 2) {
         var5.add(new ObjectHoverHitbox(var2, var3, 0, -46, 32, 46));
      } else {
         var5.add(new ObjectHoverHitbox(var2, var3, 20, -44, 12, 44));
      }

      return var5;
   }

   public String getInteractTip(Level var1, int var2, int var3, PlayerMob var4, boolean var5) {
      return Localization.translate("controls", "opentip");
   }

   public boolean canInteract(Level var1, int var2, int var3, PlayerMob var4) {
      return true;
   }

   public void interact(Level var1, int var2, int var3, PlayerMob var4) {
      if (var1.isServer()) {
         OEInventoryContainer.openAndSendContainer(ContainerRegistry.OE_INVENTORY_CONTAINER, var4.getServerClient(), var1, var2, var3);
      }

   }

   public ObjectEntity getNewObjectEntity(Level var1, int var2, int var3) {
      return new InventoryObjectEntity(var1, var2, var3, 10);
   }

   public void doExplosionDamage(Level var1, int var2, int var3, int var4, int var5, ServerClient var6) {
      if (!var1.settlementLayer.isActive()) {
         super.doExplosionDamage(var1, var2, var3, var4, var5, var6);
      }

   }
}
