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

public class FlowerPotObject extends GameObject {
   public GameTexture texture;

   public FlowerPotObject() {
      super(new Rectangle(0, 0));
      this.setItemCategory(new String[]{"objects", "furniture"});
      this.mapColor = new Color(115, 65, 50);
      this.displayMapTooltip = true;
      this.isFlowerpot = true;
      this.drawDamage = false;
      this.objectHealth = 50;
      this.toolType = ToolType.ALL;
      this.isLightTransparent = true;
      this.replaceCategories.add("furniture");
      this.canReplaceCategories.add("furniture");
      this.canReplaceCategories.add("column");
      this.replaceRotations = false;
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/flowerpot");
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4);
      int var11 = var7.getTileDrawY(var5) - 8;
      final TextureDrawOptionsEnd var12 = this.texture.initDraw().sprite(0, 0, 32).light(var9).pos(var10, var11);
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
      int var9 = var7.getTileDrawY(var3) - 8;
      this.texture.initDraw().sprite(0, 0, 32).alpha(var5).draw(var8, var9);
   }
}
