package necesse.inventory.container.settlement.actions;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.customAction.ContainerCustomAction;
import necesse.inventory.container.settlement.SettlementDependantContainer;
import necesse.inventory.container.settlement.events.SettlementWorkZoneChangedEvent;
import necesse.inventory.container.settlement.events.SettlementWorkZoneRemovedEvent;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.settlementData.zones.SettlementHusbandryZone;
import necesse.level.maps.levelData.settlementData.zones.SettlementWorkZone;

public class HusbandryZoneConfigCustomAction extends ContainerCustomAction {
   public final SettlementDependantContainer container;

   public HusbandryZoneConfigCustomAction(SettlementDependantContainer var1) {
      this.container = var1;
   }

   public void runAndSendSetMaxAnimals(int var1, int var2) {
      Packet var3 = new Packet();
      PacketWriter var4 = new PacketWriter(var3);
      var4.putNextInt(var1);
      var4.putNextMaxValue(0, 1);
      var4.putNextInt(var2);
      this.runAndSendAction(var3);
   }

   public void executePacket(PacketReader var1) {
      int var2 = var1.getNextInt();
      int var3 = var1.getNextMaxValue(1);
      if (this.container.client.isServer()) {
         SettlementLevelData var4 = this.container.getLevelData();
         if (var4 != null) {
            SettlementWorkZone var5 = var4.getWorkZones().getZone(var2);
            if (var5 == null) {
               (new SettlementWorkZoneRemovedEvent(var2)).applyAndSendToClient(this.container.client.getServerClient());
            } else if (var5 instanceof SettlementHusbandryZone) {
               if (var3 == 0) {
                  int var6 = var1.getNextInt();
                  ((SettlementHusbandryZone)var5).setMaxAnimalsBeforeSlaughter(var6);
               }
            } else {
               (new SettlementWorkZoneChangedEvent(var5)).applyAndSendToClient(this.container.client.getServerClient());
            }
         }
      }

   }
}
