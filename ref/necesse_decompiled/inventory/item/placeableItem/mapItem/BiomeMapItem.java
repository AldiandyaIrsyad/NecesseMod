package necesse.inventory.item.placeableItem.mapItem;

import java.awt.Point;
import necesse.engine.AreaFinder;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.LevelIdentifier;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.travel.TravelDir;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class BiomeMapItem extends MapItem {
   public String[] targetBiomes;
   public int maxDistance;

   public BiomeMapItem(Item.Rarity var1, int var2, String... var3) {
      super(1);
      this.rarity = var1;
      this.targetBiomes = var3;
      this.maxDistance = var2;
   }

   public BiomeMapItem(String var1, int var2) {
      this(Item.Rarity.NORMAL, var2, var1);
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "biomemaptip"));
      return var4;
   }

   public String canPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      return !var1.getIdentifier().isIslandPosition() ? "notisland" : null;
   }

   public InventoryItem onPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      if (var1.isServer()) {
         final Server var7 = var1.getServer();
         final ServerClient var8 = var4.getServerClient();
         LevelIdentifier var9 = var8.getLevelIdentifier();
         if (var9.isIslandPosition()) {
            int var10 = var9.getIslandX();
            int var11 = var9.getIslandY();
            AreaFinder var12 = new AreaFinder(var10, var11, this.maxDistance, true) {
               public boolean checkPoint(int var1, int var2) {
                  return !var7.world.worldEntity.isWithinWorldBorder(var1, var2) ? false : BiomeMapItem.this.checkIsland(var1, var2, var8, var7);
               }
            };
            var12.runFinder();
            Point var13 = var12.getFirstFind();
            if (var13 == null) {
               var8.sendChatMessage((GameMessage)(new LocalMessage("itemtooltip", "mapfail")));
               return var5;
            }

            var5 = super.onPlace(var1, var2, var3, var4, var5, var6);
         }
      }

      var5 = super.onPlace(var1, var2, var3, var4, var5, var6);
      return var5;
   }

   public boolean checkIsland(int var1, int var2, ServerClient var3, Server var4) {
      if (var3.hasDiscoveredIsland(var1, var2)) {
         return false;
      } else {
         String[] var5 = this.targetBiomes;
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            String var8 = var5[var7];
            int var9 = BiomeRegistry.getBiomeID(var8);
            if (var4.levelCache.getBiomeID(var1, var2) == var9) {
               this.discoverIsland(var1, var2, var3, var4);
               return true;
            }
         }

         return false;
      }
   }

   public void discoverIsland(int var1, int var2, ServerClient var3, Server var4) {
      var3.addDiscoveredIsland(var1, var2);
      LevelIdentifier var5 = var3.getLevelIdentifier();
      TravelDir var6 = TravelDir.getDeltaDir(var5.getIslandX(), var5.getIslandY(), var1, var2);
      LocalMessage var7 = new LocalMessage("itemtooltip", "mapresult");
      var7.addReplacement("dir", var6.dirMessage);
      var7.addReplacement("coord", var1 - var5.getIslandX() + "," + (var2 - var5.getIslandY()));
      var3.sendChatMessage((GameMessage)var7);
   }
}
