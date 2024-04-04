package necesse.engine.network.packet;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.entity.objectEntity.SettlementFlagObjectEntity;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class PacketSettlementOpen extends Packet {
   public PacketSettlementOpen(byte[] var1) {
      super(var1);
   }

   public PacketSettlementOpen() {
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      Level var4 = var2.world.getLevel(var3);
      if (var4.isIslandPosition() && var4.getIslandDimension() == 0) {
         SettlementLevelData var5 = SettlementLevelData.getSettlementData(var4);
         if (var5 != null) {
            SettlementFlagObjectEntity var6 = var5.getObjectEntity();
            if (var6 != null) {
               PacketOpenContainer var7 = PacketOpenContainer.ObjectEntity(ContainerRegistry.SETTLEMENT_CONTAINER, var6, var6.getContainerContentPacket(var3));
               ContainerRegistry.openAndSendContainer(var3, var7);
            } else {
               var3.sendChatMessage((GameMessage)(new LocalMessage("ui", "settlementnone")));
            }
         } else {
            var3.sendChatMessage((GameMessage)(new LocalMessage("ui", "settlementnone")));
         }

      } else {
         var3.sendChatMessage((GameMessage)(new LocalMessage("ui", "settlementsurface")));
      }
   }
}
