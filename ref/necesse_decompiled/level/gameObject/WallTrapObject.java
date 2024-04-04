package necesse.level.gameObject;

import java.awt.Rectangle;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import necesse.engine.Settings;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.TrapObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.MergeFunction;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.regionSystem.SemiRegion;

public class WallTrapObject extends GameObject {
   protected String textureName;
   public WallObject wallObject;
   public GameTexture texture;

   public WallTrapObject(WallObject var1, String var2) {
      this(var1, var2, var1.toolTier, var1.toolType);
   }

   public WallTrapObject(WallObject var1, String var2, int var3, ToolType var4) {
      super(new Rectangle(32, 32));
      var1.connectedWalls.add(this);
      this.wallObject = var1;
      this.textureName = var2;
      this.toolTier = var3;
      this.toolType = var4;
      this.regionType = SemiRegion.RegionType.WALL;
      this.setItemCategory(new String[]{"objects", "traps"});
      this.displayMapTooltip = true;
      this.mapColor = var1.mapColor;
      this.isWall = true;
      this.showsWire = true;
      this.replaceCategories.add("wall");
      this.canReplaceCategories.add("wall");
      this.canReplaceCategories.add("door");
      this.canReplaceCategories.add("fence");
      this.canReplaceCategories.add("fencegate");
   }

   public GameMessage getNewLocalization() {
      return this.wallObject.getNewTrapLocalization(new LocalMessage("object", this.textureName));
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      final ArrayList var9 = new ArrayList();
      this.wallObject.addDrawables(var9, var2, var3, var4, var5, var6, var7, var8);
      final SharedTextureDrawOptions var10 = new SharedTextureDrawOptions(this.texture);
      GameLight var11 = var3.getLightLevel(var4, var5);
      int var12 = var7.getTileDrawX(var4);
      int var13 = var7.getTileDrawY(var5);
      byte var14 = var3.getObjectRotation(var4, var5);
      float var15 = 1.0F;
      if (var8 != null && !Settings.hideUI) {
         Rectangle var16 = new Rectangle(var4 * 32 - 16, var5 * 32 - 48, 64, 48);
         if (var8.getCollision().intersects(var16)) {
            var15 = 0.5F;
         } else if (var16.contains(var7.getMouseLevelPosX(), var7.getMouseLevelPosY())) {
            var15 = 0.5F;
         }
      }

      int var17 = var14 % 4;
      if (!var3.getObject(var4, var5 - 1).isWall) {
         var10.addSprite(var17, 0, 32, 64).alpha(var15).light(var11).pos(var12, var13 - 32);
      } else {
         var10.addSprite(var17, 0, 32, 64).light(var11).pos(var12, var13 - 32);
      }

      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 20;
         }

         public void draw(TickManager var1) {
            var9.forEach((var1x) -> {
               var1x.draw(var1);
            });
            var10.draw();
         }
      });
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      this.wallObject.drawPreview(var1, var2, var3, var4, var5, var6, var7);
      int var8 = var7.getTileDrawX(var2);
      int var9 = var7.getTileDrawY(var3);
      int var10 = var4 % 4;
      this.texture.initDraw().sprite(var10, 0, 32).alpha(var5).draw(var8, var9 - 32);
      this.texture.initDraw().sprite(var10, 1, 32).alpha(var5).draw(var8, var9);
   }

   public List<ObjectHoverHitbox> getHoverHitboxes(Level var1, int var2, int var3) {
      return this.wallObject.getHoverHitboxes(var1, var2, var3);
   }

   public void onWireUpdate(Level var1, int var2, int var3, int var4, boolean var5) {
      if (var5) {
         ObjectEntity var6 = var1.entityManager.getObjectEntity(var2, var3);
         if (var6 != null) {
            ((TrapObjectEntity)var6).triggerTrap(var4, var1.getObjectRotation(var2, var3));
         }
      }

   }

   public boolean stopsTerrainSplatting() {
      return true;
   }

   public ObjectEntity getNewObjectEntity(Level var1, int var2, int var3) {
      return new TrapObjectEntity(var1, var2, var3, 10000L);
   }

   public void loadTextures() {
      super.loadTextures();

      GameTexture var1;
      try {
         var1 = GameTexture.fromFileRaw("objects/" + this.textureName + "_short");
      } catch (FileNotFoundException var3) {
         var1 = GameTexture.fromFile("objects/" + this.textureName);
      }

      this.texture = var1;
   }

   public GameTexture generateItemTexture() {
      GameTexture var1 = new GameTexture(this.wallObject.generateItemTexture());
      var1.merge(this.texture, 0, 0, 128, 0, 32, 32, MergeFunction.NORMAL);
      var1.makeFinal();
      return var1;
   }

   public ListGameTooltips getItemTooltips(InventoryItem var1, PlayerMob var2) {
      ListGameTooltips var3 = super.getItemTooltips(var1, var2);
      var3.add(Localization.translate("itemtooltip", "activatedwiretip"));
      return var3;
   }
}
