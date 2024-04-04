package necesse.inventory.item.placeableItem.objectItem;

import necesse.engine.GlobalData;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;

public class TravelstoneObjectItem extends ObjectItem {
   public TravelstoneObjectItem(GameObject var1) {
      super(var1);
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "travelstonetip"));
      var4.add(Localization.translate("itemtooltip", "placeinsettlement"));
      GNDItemMap var5 = var1.getGndData();
      if (GlobalData.debugActive()) {
         var4.add("Island coords: (" + var5.getInt("islandX") + ", " + var5.getInt("islandY") + ")");
      }

      return var4;
   }

   public String canPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      String var7 = super.canPlace(var1, var2, var3, var4, var5, var6);
      if (var7 != null) {
         return var7;
      } else if (!var1.isIslandPosition()) {
         return "notisland";
      } else {
         GNDItemMap var8 = var5.getGndData();
         int var9 = var8.getInt("islandX");
         int var10 = var8.getInt("islandY");
         return var1.getIslandX() == var9 && var1.getIslandY() == var10 ? null : "wrongisland";
      }
   }

   public boolean canCombineItem(Level var1, PlayerMob var2, InventoryItem var3, InventoryItem var4, String var5) {
      return !super.canCombineItem(var1, var2, var3, var4, var5) ? false : this.isSameGNDData(var1, var3, var4, var5);
   }

   public boolean isSameGNDData(Level var1, InventoryItem var2, InventoryItem var3, String var4) {
      return var2.getGndData().sameKeys(var3.getGndData(), "islandX", "islandY");
   }

   public static InventoryItem setupTravelstoneItem(InventoryItem var0, Level var1) {
      return setupTravelstoneItem(var0, var1.getIslandX(), var1.getIslandY());
   }

   public static InventoryItem setupTravelstoneItem(InventoryItem var0, int var1, int var2) {
      GNDItemMap var3 = var0.getGndData();
      setupTravelstoneGNDData(var3, var1, var2);
      return var0;
   }

   public static GNDItemMap travelstoneGNDData(int var0, int var1) {
      GNDItemMap var2 = new GNDItemMap();
      setupTravelstoneGNDData(var2, var0, var1);
      return var2;
   }

   public static void setupTravelstoneGNDData(GNDItemMap var0, int var1, int var2) {
      var0.setInt("islandX", var1);
      var0.setInt("islandY", var2);
   }
}
