package necesse.inventory.item.placeableItem.objectItem;

import java.awt.Point;
import java.util.ArrayList;
import necesse.engine.GlobalData;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.LevelIdentifier;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.WaystoneObjectEntity;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.object.HomestoneUpdateEvent;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.settlementData.Waystone;

public class WaystoneObjectItem extends ObjectItem {
   public WaystoneObjectItem(GameObject var1) {
      super(var1);
      this.itemCooldownTime.setBaseValue(2000);
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      GNDItemMap var5 = var1.getGndData();
      if (GlobalData.debugActive()) {
         var4.add("Home coords: (" + var5.getInt("homeX") + ", " + var5.getInt("homeY") + ")");
      }

      return var4;
   }

   public String canPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      return super.canPlace(var1, var2, var3, var4, var5, var6);
   }

   public boolean onPlaceObject(GameObject var1, Level var2, int var3, int var4, int var5, ServerClient var6, InventoryItem var7) {
      GNDItemMap var8 = var7.getGndData();
      int var9 = var8.getInt("homeX");
      int var10 = var8.getInt("homeY");
      Level var11 = var2.getServer().world.getLevel(new LevelIdentifier(var9, var10, 0));
      SettlementLevelData var12 = SettlementLevelData.getSettlementData(var11);
      LocalMessage var13 = new LocalMessage("ui", "waystoneinvalidhome");
      if (var12 != null) {
         int var14 = var12.getMaxWaystones();
         ArrayList var15 = var12.getWaystones();
         var13 = new LocalMessage("ui", "waystonenoslots");
         if (var15.size() < var14) {
            Waystone var16 = new Waystone(var2.getIdentifier(), var3, var4);
            var16.name = var2.biome.getDisplayName() + " waystone";
            var15.add(var16);
            var12.sendEvent(HomestoneUpdateEvent.class);
            var13 = null;
         }
      }

      if (var13 == null) {
         var1.placeObject(var2, var3, var4, var5);
         ObjectEntity var17 = var2.entityManager.getObjectEntity(var3, var4);
         if (var17 instanceof WaystoneObjectEntity) {
            WaystoneObjectEntity var18 = (WaystoneObjectEntity)var17;
            var18.homeIsland = new Point(var9, var10);
         }

         return true;
      } else {
         if (var6 != null) {
            var6.sendChatMessage((GameMessage)var13);
         }

         return false;
      }
   }

   public boolean canCombineItem(Level var1, PlayerMob var2, InventoryItem var3, InventoryItem var4, String var5) {
      return !super.canCombineItem(var1, var2, var3, var4, var5) ? false : this.isSameGNDData(var1, var3, var4, var5);
   }

   public boolean isSameGNDData(Level var1, InventoryItem var2, InventoryItem var3, String var4) {
      return var2.getGndData().sameKeys(var3.getGndData(), "homeX", "homeY");
   }

   public static InventoryItem setupWaystoneItem(InventoryItem var0, Level var1) {
      return setupWaystoneItem(var0, var1.getIslandX(), var1.getIslandY());
   }

   public static InventoryItem setupWaystoneItem(InventoryItem var0, int var1, int var2) {
      GNDItemMap var3 = var0.getGndData();
      setupWaystoneGNDData(var3, var1, var2);
      return var0;
   }

   public static GNDItemMap waystoneGNDData(int var0, int var1) {
      GNDItemMap var2 = new GNDItemMap();
      setupWaystoneGNDData(var2, var0, var1);
      return var2;
   }

   public static void setupWaystoneGNDData(GNDItemMap var0, int var1, int var2) {
      var0.setInt("homeX", var1);
      var0.setInt("homeY", var2);
   }
}
