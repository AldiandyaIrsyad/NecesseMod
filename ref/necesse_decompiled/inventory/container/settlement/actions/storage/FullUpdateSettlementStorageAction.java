package necesse.inventory.container.settlement.actions.storage;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.customAction.ContainerCustomAction;
import necesse.inventory.container.settlement.SettlementDependantContainer;
import necesse.inventory.container.settlement.events.SettlementBasicsEvent;
import necesse.inventory.container.settlement.events.SettlementSingleStorageEvent;
import necesse.inventory.container.settlement.events.SettlementStorageFullUpdateEvent;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.level.maps.levelData.settlementData.SettlementInventory;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class FullUpdateSettlementStorageAction extends ContainerCustomAction {
   public final SettlementDependantContainer container;

   public FullUpdateSettlementStorageAction(SettlementDependantContainer var1) {
      this.container = var1;
   }

   public void runAndSend(int var1, int var2, ItemCategoriesFilter var3, int var4) {
      Packet var5 = new Packet();
      PacketWriter var6 = new PacketWriter(var5);
      var6.putNextShortUnsigned(var1);
      var6.putNextShortUnsigned(var2);
      var6.putNextInt(var4);
      var3.writePacket(var6);
      this.runAndSendAction(var5);
   }

   public void executePacket(PacketReader var1) {
      int var2 = var1.getNextShortUnsigned();
      int var3 = var1.getNextShortUnsigned();
      int var4 = var1.getNextInt();
      if (this.container.client.isServer()) {
         SettlementLevelData var5 = this.container.getLevelData();
         if (var5 != null) {
            ServerClient var6 = this.container.client.getServerClient();
            if (!this.container.getLevelLayer().doesClientHaveAccess(var6)) {
               (new SettlementBasicsEvent(var5)).applyAndSendToClient(var6);
               return;
            }

            SettlementInventory var7 = var5.getStorage(var2, var3);
            if (var7 != null) {
               var7.priority = var4;
               var7.filter.readPacket(var1);
               (new SettlementStorageFullUpdateEvent(var2, var3, var7.filter, var7.priority)).applyAndSendToClientsAtExcept(var6);
            } else {
               (new SettlementSingleStorageEvent(var5, var2, var3)).applyAndSendToClient(var6);
            }
         }
      }

   }
}
