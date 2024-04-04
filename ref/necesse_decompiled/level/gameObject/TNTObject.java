package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.localization.Localization;
import necesse.engine.network.server.ServerClient;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.TNTObjectEntity;
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

public class TNTObject extends GameObject {
   public GameTexture texture;

   public TNTObject() {
      super(new Rectangle(8, 12, 16, 12));
      this.mapColor = new Color(190, 40, 40);
      this.displayMapTooltip = true;
      this.showsWire = true;
      this.drawDamage = false;
      this.objectHealth = 1;
      this.toolType = ToolType.ALL;
      this.isLightTransparent = true;
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/tnt");
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4);
      int var11 = var7.getTileDrawY(var5);
      final TextureDrawOptionsEnd var12 = this.texture.initDraw().sprite(0, 0, 32).light(var9).pos(var10, var11 - 6);
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
      this.texture.initDraw().sprite(0, 0, 32).alpha(var5).draw(var8, var9 - 6);
   }

   public void onWireUpdate(Level var1, int var2, int var3, int var4, boolean var5) {
      if (var5 && var1.isServer()) {
         ObjectEntity var6 = var1.entityManager.getObjectEntity(var2, var3);
         if (var6 != null) {
            ((TNTObjectEntity)var6).explode();
         }
      }

   }

   public void doExplosionDamage(Level var1, int var2, int var3, int var4, int var5, ServerClient var6) {
      if (var4 > 0) {
         ObjectEntity var7 = var1.entityManager.getObjectEntity(var2, var3);
         if (var7 != null) {
            ((TNTObjectEntity)var7).explode();
         }
      }

   }

   public ObjectEntity getNewObjectEntity(Level var1, int var2, int var3) {
      return new TNTObjectEntity(var1, var2, var3);
   }

   public ListGameTooltips getItemTooltips(InventoryItem var1, PlayerMob var2) {
      ListGameTooltips var3 = new ListGameTooltips();
      var3.add(Localization.translate("itemtooltip", "tnttip"));
      var3.addAll(super.getItemTooltips(var1, var2));
      return var3;
   }
}
