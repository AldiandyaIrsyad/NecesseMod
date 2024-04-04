package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.localization.Localization;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.DungeonExitObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.PortalObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class DungeonExitObject extends GameObject {
   public GameTexture texture;

   public DungeonExitObject() {
      super(new Rectangle(32, 32));
      this.mapColor = new Color(53, 54, 59);
      this.drawDamage = false;
      this.lightLevel = 50;
      this.toolType = ToolType.UNBREAKABLE;
      this.isLightTransparent = true;
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/dungeonexit");
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

   public boolean canInteract(Level var1, int var2, int var3, PlayerMob var4) {
      return true;
   }

   public String getInteractTip(Level var1, int var2, int var3, PlayerMob var4, boolean var5) {
      return Localization.translate("controls", "usetip");
   }

   public void interact(Level var1, int var2, int var3, PlayerMob var4) {
      if (var1.isServer() && var4.isServerClient()) {
         ObjectEntity var5 = var1.entityManager.getObjectEntity(var2, var3);
         if (var5 instanceof PortalObjectEntity) {
            ((PortalObjectEntity)var5).use(var1.getServer(), var4.getServerClient());
         }
      }

      super.interact(var1, var2, var3, var4);
   }

   public ObjectEntity getNewObjectEntity(Level var1, int var2, int var3) {
      return new DungeonExitObjectEntity(var1, var2, var3, 10, 10) {
         public boolean shouldDrawOnMap() {
            return true;
         }

         public Rectangle drawOnMapBox() {
            return new Rectangle(-8, -24, 16, 32);
         }

         public void drawOnMap(TickManager var1, int var2, int var3) {
            DungeonExitObject.this.texture.initDraw().size(16, 32).draw(var2 - 8, var3 - 24);
         }

         public GameTooltips getMapTooltips() {
            return new StringTooltips(DungeonExitObject.this.getDisplayName());
         }
      };
   }
}
