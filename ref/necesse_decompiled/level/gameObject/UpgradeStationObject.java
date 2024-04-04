package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.localization.Localization;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.UpgradeStationObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.object.UpgradeStationContainer;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class UpgradeStationObject extends GameObject {
   public GameTexture texture;

   public UpgradeStationObject() {
      super(new Rectangle(32, 32));
      this.stackSize = 1;
      this.rarity = Item.Rarity.RARE;
      this.displayMapTooltip = true;
      this.mapColor = new Color(75, 63, 81);
      this.drawDamage = false;
      this.toolType = ToolType.ALL;
      this.isLightTransparent = true;
      this.setItemCategory(new String[]{"objects", "craftingstations"});
      this.replaceCategories.add("workstation");
      this.canReplaceCategories.add("workstation");
      this.canReplaceCategories.add("wall");
      this.canReplaceCategories.add("furniture");
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/upgradestation");
   }

   public List<ObjectHoverHitbox> getHoverHitboxes(Level var1, int var2, int var3) {
      List var4 = super.getHoverHitboxes(var1, var2, var3);
      var4.add(new ObjectHoverHitbox(var2, var3, 0, -10, 32, 10));
      return var4;
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4);
      int var11 = var7.getTileDrawY(var5);
      final TextureDrawOptionsEnd var12 = this.texture.initDraw().light(var9).pos(var10 + 16 - this.texture.getWidth() / 2, var11 - this.texture.getHeight() + 32 + 4);
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
      this.texture.initDraw().alpha(var5).draw(var8 + 16 - this.texture.getWidth() / 2, var9 - this.texture.getHeight() + 32 + 4);
   }

   public ListGameTooltips getItemTooltips(InventoryItem var1, PlayerMob var2) {
      ListGameTooltips var3 = super.getItemTooltips(var1, var2);
      var3.add((String)Localization.translate("itemtooltip", "upgradestationtip"), 400);
      return var3;
   }

   public String getInteractTip(Level var1, int var2, int var3, PlayerMob var4, boolean var5) {
      return Localization.translate("controls", "usetip");
   }

   public boolean canInteract(Level var1, int var2, int var3, PlayerMob var4) {
      return true;
   }

   public void interact(Level var1, int var2, int var3, PlayerMob var4) {
      if (var1.isServer()) {
         UpgradeStationContainer.openAndSendContainer(ContainerRegistry.UPGRADE_STATION_CONTAINER, var4.getServerClient(), var1, var2, var3);
      }

   }

   public ObjectEntity getNewObjectEntity(Level var1, int var2, int var3) {
      return new UpgradeStationObjectEntity(var1, var2, var3);
   }
}
