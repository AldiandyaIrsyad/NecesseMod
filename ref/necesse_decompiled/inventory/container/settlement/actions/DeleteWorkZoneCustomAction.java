package necesse.inventory.container.settlement.actions;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.customAction.ContainerCustomAction;
import necesse.inventory.container.settlement.SettlementDependantContainer;
import necesse.inventory.container.settlement.events.SettlementWorkZoneRemovedEvent;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.settlementData.zones.SettlementWorkZone;

public class DeleteWorkZoneCustomAction extends ContainerCustomAction {
   public final SettlementDependantContainer container;

   public DeleteWorkZoneCustomAction(SettlementDependantContainer var1) {
      this.container = var1;
   }

   public void runAndSend(int var1) {
      Packet var2 = new Packet();
      PacketWriter var3 = new PacketWriter(var2);
      var3.putNextInt(var1);
      this.runAndSendAction(var2);
   }

   public void executePacket(PacketReader var1) {
      int var2 = var1.getNextInt();
      if (this.container.client.isServer()) {
         SettlementLevelData var3 = this.container.getLevelData();
         if (var3 != null) {
            SettlementWorkZone var4 = var3.getWorkZones().getZone(var2);
            if (var4 == null) {
               (new SettlementWorkZoneRemovedEvent(var2)).applyAndSendToClient(this.container.client.getServerClient());
            } else {
               var3.getWorkZones().removeZone(var2);
            }
         }
      }

   }
}
