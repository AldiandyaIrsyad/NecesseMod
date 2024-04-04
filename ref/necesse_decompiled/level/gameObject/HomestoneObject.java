package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import necesse.engine.localization.Localization;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.world.GameClock;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.HomestoneObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.container.object.HomestoneContainer;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.objectItem.HomestoneObjectItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.light.GameLight;

public class HomestoneObject extends GameObject {
   public GameTexture texture;

   public HomestoneObject() {
      super(new Rectangle(0, 4, 32, 22));
      this.mapColor = new Color(143, 136, 142);
      this.displayMapTooltip = true;
      this.objectHealth = 100;
      this.drawDamage = false;
      this.toolType = ToolType.ALL;
      this.stackSize = 1;
      this.lightLevel = 0;
      this.lightHue = 50.0F;
      this.lightSat = 0.2F;
      this.rarity = Item.Rarity.EPIC;
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/homestone");
   }

   public LootTable getLootTable(Level var1, int var2, int var3) {
      return new LootTable(new LootItemInterface[]{(new LootItem(this.getStringID(), HomestoneObjectItem.homestoneGNDData(var1.getIslandX(), var1.getIslandY()))).preventLootMultiplier()});
   }

   public String canPlace(Level var1, int var2, int var3, int var4) {
      String var5 = super.canPlace(var1, var2, var3, var4);
      if (var5 != null) {
         return var5;
      } else if (!var1.isIslandPosition()) {
         return "notisland";
      } else {
         return var1.getIslandDimension() != 0 ? "notsurface" : null;
      }
   }

   public Item generateNewObjectItem() {
      return new HomestoneObjectItem(this);
   }

   public ObjectEntity getNewObjectEntity(Level var1, int var2, int var3) {
      return new HomestoneObjectEntity(var1, var2, var3);
   }

   public void onDestroyed(Level var1, int var2, int var3, ServerClient var4, ArrayList<ItemPickupEntity> var5) {
      if (var1.isServer()) {
         SettlementLevelData var6 = SettlementLevelData.getSettlementData(var1);
         if (var6 != null) {
            Point var7 = var6.getHomestonePos();
            if (var7 != null && var7.x == var2 && var7.y == var3) {
               var6.setHomestonePos((Point)null);
            }
         }
      }

      super.onDestroyed(var1, var2, var3, var4, var5);
   }

   public void placeObject(Level var1, int var2, int var3, int var4) {
      super.placeObject(var1, var2, var3, var4);
      if (var1.isServer()) {
         SettlementLevelData var5 = SettlementLevelData.getSettlementData(var1);
         if (var5 != null) {
            var5.setHomestonePos(new Point(var2, var3));
         }
      }

   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4) - 16;
      int var11 = var7.getTileDrawY(var5) - 96;
      long var12 = 1500L;
      float var14 = Math.abs(var9.getFloatLevel() - 1.0F) * 0.2F + 0.1F;
      final SharedTextureDrawOptions var15 = new SharedTextureDrawOptions(this.texture);
      var15.addSprite(0, 0, 64, this.texture.getHeight()).light(var9).pos(var10, var11);
      var15.addSprite(1, 0, 64, this.texture.getHeight()).spelunkerLight(var9, true, this.getTileSeed(var4, var5), var3, var12, var14, 50).pos(var10, var11);
      var15.addSprite(2, 0, 64, this.texture.getHeight()).spelunkerLight(var9, true, this.getTileSeed(var4, var5), GameClock.offsetClock(var3, var12 / 3L), var12, var14, 50).pos(var10, var11);
      var15.addSprite(3, 0, 64, this.texture.getHeight()).spelunkerLight(var9, true, this.getTileSeed(var4, var5), GameClock.offsetClock(var3, var12 / 3L * 2L), var12, var14, 50).pos(var10, var11);
      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 16;
         }

         public void draw(TickManager var1) {
            var15.draw();
         }
      });
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      int var8 = var7.getTileDrawX(var2) - 16;
      int var9 = var7.getTileDrawY(var3) - 96;
      long var10 = 1500L;
      float var12 = 0.1F;
      SharedTextureDrawOptions var13 = new SharedTextureDrawOptions(this.texture);
      var13.addSprite(0, 0, 64, this.texture.getHeight()).alpha(var5).pos(var8, var9);
      var13.addSprite(1, 0, 64, this.texture.getHeight()).spelunkerLight(new GameLight(150.0F), true, this.getTileSeed(var2, var3), var1, var10, var12, 50).alpha(var5).pos(var8, var9);
      var13.addSprite(2, 0, 64, this.texture.getHeight()).spelunkerLight(new GameLight(150.0F), true, this.getTileSeed(var2, var3), GameClock.offsetClock(var1, var10 / 3L), var10, var12, 50).alpha(var5).pos(var8, var9);
      var13.addSprite(3, 0, 64, this.texture.getHeight()).spelunkerLight(new GameLight(150.0F), true, this.getTileSeed(var2, var3), GameClock.offsetClock(var1, var10 / 3L * 2L), var10, var12, 50).alpha(var5).pos(var8, var9);
      var13.draw();
   }

   public List<ObjectHoverHitbox> getHoverHitboxes(Level var1, int var2, int var3) {
      List var4 = super.getHoverHitboxes(var1, var2, var3);
      var4.add(new ObjectHoverHitbox(var2, var3, 0, -44, 32, 44));
      return var4;
   }

   public String getInteractTip(Level var1, int var2, int var3, PlayerMob var4, boolean var5) {
      return Localization.translate("controls", "usetip");
   }

   public boolean canInteract(Level var1, int var2, int var3, PlayerMob var4) {
      return true;
   }

   public void interact(Level var1, int var2, int var3, PlayerMob var4) {
      if (var1.isServer()) {
         PacketOpenContainer var5 = PacketOpenContainer.LevelObject(ContainerRegistry.HOMESTONE_CONTAINER, var2, var3, HomestoneContainer.getContainerContent(var1));
         ContainerRegistry.openAndSendContainer(var4.getServerClient(), var5);
      }

   }
}
