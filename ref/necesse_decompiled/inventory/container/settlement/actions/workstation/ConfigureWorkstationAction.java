package necesse.inventory.container.settlement.actions.workstation;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.customAction.ContainerCustomAction;
import necesse.inventory.container.settlement.SettlementDependantContainer;
import necesse.inventory.container.settlement.events.SettlementBasicsEvent;
import necesse.inventory.container.settlement.events.SettlementSingleWorkstationsEvent;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.settlementData.SettlementWorkstation;

public abstract class ConfigureWorkstationAction extends ContainerCustomAction {
   public final SettlementDependantContainer container;

   public ConfigureWorkstationAction(SettlementDependantContainer var1) {
      this.container = var1;
   }

   protected void runAndSend(int var1, int var2, Packet var3) {
      Packet var4 = new Packet();
      PacketWriter var5 = new PacketWriter(var4);
      var5.putNextInt(var1);
      var5.putNextInt(var2);
      var5.putNextContentPacket(var3);
      this.runAndSendAction(var4);
   }

   public void executePacket(PacketReader var1) {
      int var2 = var1.getNextInt();
      int var3 = var1.getNextInt();
      Packet var4 = var1.getNextContentPacket();
      if (this.container.client.isServer()) {
         SettlementLevelData var5 = this.container.getLevelData();
         if (var5 != null) {
            ServerClient var6 = this.container.client.getServerClient();
            if (!this.container.getLevelLayer().doesClientHaveAccess(var6)) {
               (new SettlementBasicsEvent(var5)).applyAndSendToClient(var6);
               return;
            }

            SettlementWorkstation var7 = var5.getWorkstation(var2, var3);
            if (var7 != null) {
               this.handleAction(new PacketReader(var4), var5, var7);
            } else {
               (new SettlementSingleWorkstationsEvent(var5, var2, var3)).applyAndSendToClient(var6);
            }
         }
      }

   }

   public abstract void handleAction(PacketReader var1, SettlementLevelData var2, SettlementWorkstation var3);
}
