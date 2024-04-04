package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.ServerClient;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.TeleportResult;
import necesse.engine.world.GameClock;
import necesse.entity.levelEvent.TeleportEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.WaystoneObjectEntity;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.object.HomestoneUpdateEvent;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.objectItem.WaystoneObjectItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.settlementData.Waystone;
import necesse.level.maps.light.GameLight;

public class WaystoneObject extends GameObject {
   public GameTexture texture;

   public WaystoneObject() {
      super(new Rectangle(0, 4, 32, 22));
      this.mapColor = new Color(143, 136, 142);
      this.displayMapTooltip = true;
      this.objectHealth = 1;
      this.drawDamage = false;
      this.toolType = ToolType.ALL;
      this.stackSize = 10;
      this.lightLevel = 0;
      this.lightHue = 50.0F;
      this.lightSat = 0.2F;
      this.rarity = Item.Rarity.RARE;
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/waystone");
   }

   public ListGameTooltips getItemTooltips(InventoryItem var1, PlayerMob var2) {
      ListGameTooltips var3 = super.getItemTooltips(var1, var2);
      var3.add(Localization.translate("itemtooltip", "waystonetip1"));
      var3.add(Localization.translate("itemtooltip", "waystonetip2"));
      return var3;
   }

   public LootTable getLootTable(Level var1, int var2, int var3) {
      return new LootTable();
   }

   public Item generateNewObjectItem() {
      return new WaystoneObjectItem(this);
   }

   public ObjectEntity getNewObjectEntity(Level var1, int var2, int var3) {
      return new WaystoneObjectEntity(var1, var2, var3);
   }

   public void onDestroyed(Level var1, int var2, int var3, ServerClient var4, ArrayList<ItemPickupEntity> var5) {
      if (var1.isServer()) {
         ObjectEntity var6 = var1.entityManager.getObjectEntity(var2, var3);
         if (var6 instanceof WaystoneObjectEntity) {
            WaystoneObjectEntity var7 = (WaystoneObjectEntity)var6;
            if (var7.homeIsland != null) {
               Level var8 = var1.getServer().world.getLevel(new LevelIdentifier(var7.homeIsland.x, var7.homeIsland.y, 0));
               var7.homeIsland = null;
               SettlementLevelData var9 = SettlementLevelData.getSettlementData(var8);
               if (var9 != null) {
                  ArrayList var10 = var9.getWaystones();

                  for(int var11 = 0; var11 < var10.size(); ++var11) {
                     Waystone var12 = (Waystone)var10.get(var11);
                     if (var12.matches(var1, var2, var3)) {
                        var10.remove(var11);
                        --var11;
                        var9.sendEvent(HomestoneUpdateEvent.class);
                     }
                  }
               }
            }
         }
      }

      super.onDestroyed(var1, var2, var3, var4, var5);
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4) + 1;
      int var11 = var7.getTileDrawY(var5) - 32;
      long var12 = 1500L;
      float var14 = Math.abs(var9.getFloatLevel() - 1.0F) * 0.2F + 0.1F;
      if (var3.getWorldEntity() == null) {
         long var10000 = 0L;
      } else {
         var3.getWorldEntity().getTime();
      }

      final SharedTextureDrawOptions var17 = new SharedTextureDrawOptions(this.texture);
      var17.addSprite(0, 0, 32, this.texture.getHeight()).light(var9).pos(var10, var11);
      var17.addSprite(1, 0, 32, this.texture.getHeight()).spelunkerLight(var9, true, this.getTileSeed(var4, var5), var3, var12, var14, 50).pos(var10, var11);
      var17.addSprite(2, 0, 32, this.texture.getHeight()).spelunkerLight(var9, true, this.getTileSeed(var4, var5), GameClock.offsetClock(var3, var12 / 3L), var12, var14, 50).pos(var10, var11);
      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 16;
         }

         public void draw(TickManager var1) {
            var17.draw();
         }
      });
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      int var8 = var7.getTileDrawX(var2) + 1;
      int var9 = var7.getTileDrawY(var3) - 32;
      long var10 = 1500L;
      float var12 = 0.1F;
      SharedTextureDrawOptions var13 = new SharedTextureDrawOptions(this.texture);
      var13.addSprite(0, 0, 32, this.texture.getHeight()).alpha(var5).pos(var8, var9);
      var13.addSprite(1, 0, 32, this.texture.getHeight()).spelunkerLight(new GameLight(150.0F), true, this.getTileSeed(var2, var3), var1, var10, var12, 50).alpha(var5).pos(var8, var9);
      var13.addSprite(2, 0, 32, this.texture.getHeight()).spelunkerLight(new GameLight(150.0F), true, this.getTileSeed(var2, var3), GameClock.offsetClock(var1, var10 / 3L), var10, var12, 50).alpha(var5).pos(var8, var9);
      var13.draw();
   }

   public List<ObjectHoverHitbox> getHoverHitboxes(Level var1, int var2, int var3) {
      List var4 = super.getHoverHitboxes(var1, var2, var3);
      var4.add(new ObjectHoverHitbox(var2, var3, 0, -16, 32, 16));
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
         ObjectEntity var5 = var1.entityManager.getObjectEntity(var2, var3);
         if (var5 instanceof WaystoneObjectEntity) {
            WaystoneObjectEntity var6 = (WaystoneObjectEntity)var5;
            ServerClient var7 = var4.getServerClient();
            if (var6.homeIsland != null) {
               TeleportEvent var8 = new TeleportEvent(var7, 0, new LevelIdentifier(var6.homeIsland, 0), 0.0F, (Function)null, (var5x) -> {
                  SettlementLevelData var6 = SettlementLevelData.getSettlementData(var5x);
                  if (var6 != null) {
                     Point var7x = var6.getHomestonePos();
                     if (var7x != null) {
                        if (var6.getWaystones().stream().anyMatch((var3x) -> {
                           return var3x.matches(var1, var2, var3);
                        })) {
                           Point var8 = Waystone.findTeleportLocation(var5x, var7x.x, var7x.y, var4);
                           var7.newStats.waystones_used.increment(1);
                           return new TeleportResult(true, var8);
                        }

                        var7.sendChatMessage((GameMessage)(new LocalMessage("ui", "waystoneinvalidhome")));
                     } else {
                        var7.sendChatMessage((GameMessage)(new LocalMessage("ui", "waystoneinvalidhome")));
                     }
                  } else {
                     var7.sendChatMessage((GameMessage)(new LocalMessage("ui", "waystoneinvalidhome")));
                  }

                  return new TeleportResult(false, (Point)null);
               });
               var7.getLevel().entityManager.addLevelEventHidden(var8);
            } else {
               var7.sendChatMessage((GameMessage)(new LocalMessage("ui", "waystoneinvalidhome")));
            }
         }
      }

   }
}
