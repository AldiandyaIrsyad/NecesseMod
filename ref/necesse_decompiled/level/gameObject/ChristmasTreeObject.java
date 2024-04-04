package necesse.level.gameObject;

import java.awt.Rectangle;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.localization.Localization;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.furniture.RoomFurniture;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class ChristmasTreeObject extends GameObject implements RoomFurniture {
   protected GameTexture texture;
   protected final GameRandom drawRandom;

   public ChristmasTreeObject() {
      super(new Rectangle(32, 32));
      this.hoverHitbox = new Rectangle(-16, -48, 64, 80);
      this.displayMapTooltip = true;
      this.toolType = ToolType.ALL;
      this.drawRandom = new GameRandom();
      this.rarity = Item.Rarity.RARE;
   }

   public String getFurnitureType() {
      return "christmastree";
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/christmastree");
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      GameLight var10;
      if (var3.wireManager.isWireActiveAny(var4, var5)) {
         var10 = var9;
      } else {
         var10 = var9.minLevelCopy(100.0F);
      }

      int var11 = var7.getTileDrawX(var4);
      int var12 = var7.getTileDrawY(var5);
      float var13 = 1.0F;
      if (var8 != null && !Settings.hideUI) {
         Rectangle var14 = new Rectangle(var4 * 32 - 40 + 16, var5 * 32 - 80 + 16, 80, 80);
         if (var8.getCollision().intersects(var14)) {
            var13 = 0.5F;
         } else if (var14.contains(var7.getX() + Screen.mousePos().sceneX, var7.getY() + Screen.mousePos().sceneY)) {
            var13 = 0.5F;
         }
      }

      boolean var18;
      synchronized(this.drawRandom) {
         this.drawRandom.setSeed(this.getTileSeed(var4, var5));
         var18 = this.drawRandom.nextBoolean();
      }

      final TextureDrawOptionsEnd var15 = this.texture.initDraw().sprite(0, 0, 128).alpha(var13).light(var9).mirror(var18, false).pos(var11 - 48, var12 - 96);
      final TextureDrawOptionsEnd var16 = this.texture.initDraw().sprite(1, 0, 128).alpha(var13).light(var10).mirror(var18, false).pos(var11 - 48, var12 - 96);
      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 16;
         }

         public void draw(TickManager var1) {
            var15.draw();
            var16.draw();
         }
      });
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      GameLight var8 = var1.getLightLevel(var2, var3);
      GameLight var9;
      if (var1.wireManager.isWireActiveAny(var2, var3)) {
         var9 = var8;
      } else {
         var9 = var8.minLevelCopy(100.0F);
      }

      int var10 = var7.getTileDrawX(var2);
      int var11 = var7.getTileDrawY(var3);
      boolean var12;
      synchronized(this.drawRandom) {
         this.drawRandom.setSeed(this.getTileSeed(var2, var3));
         var12 = this.drawRandom.nextBoolean();
      }

      this.texture.initDraw().sprite(0, 0, 128).alpha(var5).light(var8).mirror(var12, false).draw(var10 - 48, var11 - 96);
      this.texture.initDraw().sprite(1, 0, 128).alpha(var5).light(var9).mirror(var12, false).draw(var10 - 48, var11 - 96);
   }

   public ListGameTooltips getItemTooltips(InventoryItem var1, PlayerMob var2) {
      ListGameTooltips var3 = super.getItemTooltips(var1, var2);
      var3.add(Localization.translate("itemtooltip", "christmastreetip"));
      var3.add(Localization.translate("itemtooltip", "biggerthanitlookstip"));
      return var3;
   }
}
