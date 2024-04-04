package necesse.level.gameObject.furniture;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.localization.Localization;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.DisplayStandObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
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

public class DisplayStandObject extends FurnitureObject {
   protected String textureName;
   public GameTexture texture;
   protected int itemHeight;

   public DisplayStandObject(String var1, ToolType var2, Color var3, int var4) {
      super(new Rectangle(6, 10, 20, 20));
      this.textureName = var1;
      this.toolType = var2;
      this.mapColor = var3;
      this.itemHeight = var4;
      this.objectHealth = 50;
      this.drawDamage = false;
      this.isLightTransparent = true;
      this.furnitureType = "table";
   }

   public DisplayStandObject(String var1, Color var2, int var3) {
      this(var1, ToolType.ALL, var2, var3);
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
      final TextureDrawOptionsEnd var13 = this.texture.initDraw().sprite(var12 % 4, 0, 32, this.texture.getHeight()).light(var9).pos(var10, var11 - (this.texture.getHeight() - 32));
      ObjectEntity var14 = var3.entityManager.getObjectEntity(var4, var5);
      final DrawOptions var15;
      if (var14 != null && var14.implementsOEInventory()) {
         InventoryItem var16 = ((OEInventory)var14).getInventory().getItem(0);
         var15 = var16 != null ? var16.getWorldDrawOptions(var8, var10 + 16, var11 + 32 - this.itemHeight, var9, 0.0F, 32) : () -> {
         };
      } else {
         var15 = () -> {
         };
      }

      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 16;
         }

         public void draw(TickManager var1) {
            var13.draw();
            var15.draw();
         }
      });
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      int var8 = var7.getTileDrawX(var2);
      int var9 = var7.getTileDrawY(var3);
      this.texture.initDraw().sprite(var4 % 4, 0, 32, this.texture.getHeight()).alpha(var5).draw(var8, var9 - (this.texture.getHeight() - 32));
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
      return new DisplayStandObjectEntity(var1, var2, var3);
   }

   public ListGameTooltips getItemTooltips(InventoryItem var1, PlayerMob var2) {
      ListGameTooltips var3 = super.getItemTooltips(var1, var2);
      var3.add(Localization.translate("itemtooltip", "displaytip"));
      return var3;
   }
}
