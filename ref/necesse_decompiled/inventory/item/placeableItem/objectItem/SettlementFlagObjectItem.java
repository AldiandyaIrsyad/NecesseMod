package necesse.inventory.item.placeableItem.objectItem;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.InventoryItem;
import necesse.level.gameObject.SettlementFlagObject;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class SettlementFlagObjectItem extends ObjectItem {
   public SettlementFlagObjectItem(SettlementFlagObject var1) {
      super(var1);
   }

   public String canPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      if (var1.isServer() && var4 != null) {
         SettlementLevelData var7 = SettlementLevelData.getSettlementData(var1);
         ServerClient var8 = var4.getServerClient();
         if (var7 == null || var1.settlementLayer.getOwnerAuth() == var8.authentication && var7.getObjectEntityPos() == null) {
            Server var9 = var4.getLevel().getServer();
            int var10 = var9.world.settings.maxSettlementsPerPlayer;
            if (var10 >= 0) {
               long var11 = var9.levelCache.getSettlements().stream().filter((var1x) -> {
                  return var1x.active && var1x.ownerAuth == var8.authentication;
               }).count();
               if (var11 >= (long)var10) {
                  return "maxsettlements";
               }
            }
         }
      }

      return super.canPlace(var1, var2, var3, var4, var5, var6);
   }

   public InventoryItem onAttemptPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6, String var7) {
      if (var7.equals("maxsettlements")) {
         Server var8 = var1.getServer();
         var4.getServerClient().sendChatMessage((GameMessage)(new LocalMessage("misc", "maxsettlementsreached", new Object[]{"count", var8.world.settings.maxSettlementsPerPlayer})));
         return var5;
      } else {
         return super.onAttemptPlace(var1, var2, var3, var4, var5, var6, var7);
      }
   }

   public InventoryItem onPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      ServerClient var8;
      if (var1.isServer()) {
         SettlementLevelData var7 = SettlementLevelData.getSettlementData(var1);
         var8 = var4.getServerClient();
         if (var7 == null || var1.settlementLayer.getOwnerAuth() != var8.authentication || var7.getObjectEntityPos() == null) {
            Server var9 = var1.getServer();
            if (var9.isSingleplayer() || var9.isHosted()) {
               long var10 = var9.levelCache.getSettlements().stream().filter((var1x) -> {
                  return var1x.ownerAuth == var4.getServerClient().authentication;
               }).count();
               if (var10 > 0L) {
                  var8.sendChatMessage((GameMessage)(new LocalMessage("misc", "multisettlementstip")));
               }
            }
         }

         if (var8 != null) {
            var1.settlementLayer.tryChangeOwner(var8);
            if (var8.achievementsLoaded()) {
               var8.achievements().START_SETTLEMENT.markCompleted(var8);
            }
         }
      }

      InventoryItem var12 = super.onPlace(var1, var2, var3, var4, var5, var6);
      if (var1.isServer()) {
         var8 = var4.getServerClient();
         if (var8 != null && !var1.settlementLayer.isSettlementNameSet()) {
            ContainerRegistry.openAndSendContainer(var8, new PacketOpenContainer(ContainerRegistry.SETTLEMENT_NAME_CONTAINER));
         }
      }

      return var12;
   }
}
