package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.localization.Localization;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.FireworkDispenserObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.object.OEInventoryContainer;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class FireworkDispenserObject extends GameObject {
   public GameTexture texture;

   public FireworkDispenserObject() {
      super(new Rectangle(5, 5, 20, 20));
      this.toolType = ToolType.ALL;
      this.mapColor = new Color(38, 80, 39);
      this.displayMapTooltip = true;
      this.objectHealth = 50;
      this.drawDamage = false;
      this.isLightTransparent = true;
      this.showsWire = true;
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/fireworkdispenser");
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4);
      int var11 = var7.getTileDrawY(var5);
      final TextureDrawOptionsEnd var12 = this.texture.initDraw().sprite(0, 0, 32, this.texture.getHeight()).light(var9).pos(var10, var11 - this.texture.getHeight() + 32);
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
      int var9 = var7.getTileDrawY(var3);
      this.texture.initDraw().sprite(0, 0, 32, this.texture.getHeight()).alpha(var5).draw(var8, var9 - this.texture.getHeight() + 32);
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

   public void onWireUpdate(Level var1, int var2, int var3, int var4, boolean var5) {
      if (var5) {
         ObjectEntity var6 = var1.entityManager.getObjectEntity(var2, var3);
         if (var6 != null) {
            ((FireworkDispenserObjectEntity)var6).fire();
         }
      }

   }

   public ObjectEntity getNewObjectEntity(Level var1, int var2, int var3) {
      return new FireworkDispenserObjectEntity(var1, var2, var3);
   }

   public ListGameTooltips getItemTooltips(InventoryItem var1, PlayerMob var2) {
      ListGameTooltips var3 = super.getItemTooltips(var1, var2);
      var3.add(Localization.translate("itemtooltip", "fireworkdispensertip"));
      return var3;
   }
}
