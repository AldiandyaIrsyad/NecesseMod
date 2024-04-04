package necesse.level.gameObject;

import java.awt.Color;
import java.util.List;
import necesse.engine.localization.Localization;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
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

public class LEDPanelObject extends GameObject {
   public GameTexture texture;

   public LEDPanelObject() {
      this.setItemCategory(new String[]{"wiring"});
      this.mapColor = new Color(200, 200, 200);
      this.displayMapTooltip = true;
      this.showsWire = true;
      this.drawDamage = false;
      this.objectHealth = 1;
      this.toolType = ToolType.ALL;
      this.isLightTransparent = true;
      this.roomProperties.add("lights");
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/ledpanel");
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4);
      int var11 = var7.getTileDrawY(var5);
      byte var12 = 0;
      if (this.isLit(var3, var4, var5)) {
         var12 = 1;
         var9 = new GameLight(150.0F);
      }

      final TextureDrawOptionsEnd var13 = this.texture.initDraw().sprite(var12, 0, 32, this.texture.getHeight()).light(var9).pos(var10, var11 - (this.texture.getHeight() - 32));
      var2.add(new LevelSortedDrawable(this, var4, var5) {
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
      if (this.isLit(var1, var2, var3)) {
         this.texture.initDraw().sprite(1, 0, 32).alpha(var5).draw(var8, var9 - 32);
         this.texture.initDraw().sprite(1, 1, 32).alpha(var5).draw(var8, var9);
      } else {
         this.texture.initDraw().sprite(0, 0, 32).alpha(var5).draw(var8, var9 - 32);
         this.texture.initDraw().sprite(0, 1, 32).alpha(var5).draw(var8, var9);
      }

   }

   public int getLightLevel(Level var1, int var2, int var3) {
      return this.isLit(var1, var2, var3) ? 75 : 0;
   }

   public void onWireUpdate(Level var1, int var2, int var3, int var4, boolean var5) {
      var1.lightManager.updateStaticLight(var2, var3);
   }

   private boolean isLit(Level var1, int var2, int var3) {
      return var1.wireManager.isWireActiveAny(var2, var3);
   }

   public ListGameTooltips getItemTooltips(InventoryItem var1, PlayerMob var2) {
      ListGameTooltips var3 = super.getItemTooltips(var1, var2);
      var3.add(Localization.translate("itemtooltip", "activatedwiretip"));
      return var3;
   }
}
