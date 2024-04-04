package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.localization.Localization;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.PortalObjectEntity;
import necesse.entity.objectEntity.TrialEntranceObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class TrialEntranceObject extends GameObject {
   public GameTexture texture;

   public TrialEntranceObject() {
      super(new Rectangle(32, 32));
      this.mapColor = new Color(53, 54, 59);
      this.displayMapTooltip = true;
      this.drawDamage = false;
      this.toolType = ToolType.UNBREAKABLE;
      this.isLightTransparent = true;
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/trialentrance");
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4);
      int var11 = var7.getTileDrawY(var5);
      GameTile var12 = var3.getTile(var4, var5);
      Color var13 = var12.getMapColor(var3, var4, var5);
      final TextureDrawOptionsEnd var14 = this.texture.initDraw().sprite(0, 0, 32).color(var13).light(var9).pos(var10, var11);
      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 16;
         }

         public void draw(TickManager var1) {
            var14.draw();
         }
      });
   }

   public Color getMapColor(Level var1, int var2, int var3) {
      return var1.getTile(var2, var3).getMapColor(var1, var2, var3).darker();
   }

   public String getInteractTip(Level var1, int var2, int var3, PlayerMob var4, boolean var5) {
      return Localization.translate("controls", "usetip");
   }

   public boolean canInteract(Level var1, int var2, int var3, PlayerMob var4) {
      return true;
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
      return new TrialEntranceObjectEntity(var1, var2, var3);
   }
}
