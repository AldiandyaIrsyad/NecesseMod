package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.localization.Localization;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.PressurePlateObjectEntity;
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

public class PressurePlateObject extends GameObject {
   public GameTexture texture;

   public PressurePlateObject(Color var1) {
      super(new Rectangle(0, 0));
      this.mapColor = var1;
      this.setItemCategory(new String[]{"wiring"});
      this.showsWire = true;
      this.drawDamage = false;
      this.objectHealth = 1;
      this.toolType = ToolType.ALL;
      this.isLightTransparent = true;
      this.replaceCategories.add("pressureplate");
      this.canReplaceCategories.add("pressureplate");
      this.canReplaceCategories.add("lever");
      this.replaceRotations = false;
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/" + this.getStringID());
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4);
      int var11 = var7.getTileDrawY(var5);
      ObjectEntity var12 = var3.entityManager.getObjectEntity(var4, var5);
      TextureDrawOptionsEnd var13;
      if (var12 != null && ((PressurePlateObjectEntity)var12).isDown()) {
         var13 = this.texture.initDraw().sprite(1, 0, 32).light(var9).pos(var10, var11);
      } else {
         var13 = this.texture.initDraw().sprite(0, 0, 32).light(var9).pos(var10, var11);
      }

      var2.add((var1x) -> {
         var13.draw();
      });
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      int var8 = var7.getTileDrawX(var2);
      int var9 = var7.getTileDrawY(var3);
      this.texture.initDraw().sprite(0, 0, 32).alpha(var5).draw(var8, var9);
   }

   public boolean isWireActive(Level var1, int var2, int var3, int var4) {
      ObjectEntity var5 = var1.entityManager.getObjectEntity(var2, var3);
      return var5 != null ? ((PressurePlateObjectEntity)var5).isDown() : false;
   }

   public ObjectEntity getNewObjectEntity(Level var1, int var2, int var3) {
      return new PressurePlateObjectEntity(var1, var2, var3, new Rectangle(4, 4, 24, 24));
   }

   public ListGameTooltips getItemTooltips(InventoryItem var1, PlayerMob var2) {
      ListGameTooltips var3 = new ListGameTooltips();
      var3.add(Localization.translate("itemtooltip", "activewiretip"));
      var3.addAll(super.getItemTooltips(var1, var2));
      return var3;
   }
}
