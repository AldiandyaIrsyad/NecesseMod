package necesse.inventory.container.settlement.actions;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.customAction.ContainerCustomAction;
import necesse.inventory.container.settlement.SettlementDependantContainer;
import necesse.inventory.container.settlement.events.SettlementWorkZoneRemovedEvent;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.settlementData.zones.SettlementWorkZone;

public class RenameWorkZoneCustomAction extends ContainerCustomAction {
   public final SettlementDependantContainer container;

   public RenameWorkZoneCustomAction(SettlementDependantContainer var1) {
      this.container = var1;
   }

   public void runAndSend(int var1, GameMessage var2) {
      Packet var3 = new Packet();
      PacketWriter var4 = new PacketWriter(var3);
      var4.putNextInt(var1);
      var2.writePacket(var4);
      this.runAndSendAction(var3);
   }

   public void executePacket(PacketReader var1) {
      int var2 = var1.getNextInt();
      GameMessage var3 = GameMessage.fromPacket(var1);
      if (this.container.client.isServer()) {
         SettlementLevelData var4 = this.container.getLevelData();
         if (var4 != null) {
            SettlementWorkZone var5 = var4.getWorkZones().getZone(var2);
            if (var5 == null) {
               (new SettlementWorkZoneRemovedEvent(var2)).applyAndSendToClient(this.container.client.getServerClient());
            } else {
               var5.setName(var3);
            }
         }
      }

   }
}
