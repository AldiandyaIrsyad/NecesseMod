package necesse.inventory.container.settlement.actions;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.customAction.ContainerCustomAction;
import necesse.inventory.container.settlement.SettlementDependantContainer;
import necesse.inventory.container.settlement.events.SettlementWorkZoneChangedEvent;
import necesse.inventory.container.settlement.events.SettlementWorkZoneRemovedEvent;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.settlementData.zones.SettlementForestryZone;
import necesse.level.maps.levelData.settlementData.zones.SettlementWorkZone;

public class ForestryZoneConfigCustomAction extends ContainerCustomAction {
   public final SettlementDependantContainer container;

   public ForestryZoneConfigCustomAction(SettlementDependantContainer var1) {
      this.container = var1;
   }

   public void runAndSendSetAllowChopping(int var1, boolean var2) {
      Packet var3 = new Packet();
      PacketWriter var4 = new PacketWriter(var3);
      var4.putNextInt(var1);
      var4.putNextMaxValue(0, 3);
      var4.putNextBoolean(var2);
      this.runAndSendAction(var3);
   }

   public void runAndSendSetReplantTrees(int var1, boolean var2) {
      Packet var3 = new Packet();
      PacketWriter var4 = new PacketWriter(var3);
      var4.putNextInt(var1);
      var4.putNextMaxValue(1, 3);
      var4.putNextBoolean(var2);
      this.runAndSendAction(var3);
   }

   public void runAndSendSetAutoPlantSapling(int var1, int var2) {
      Packet var3 = new Packet();
      PacketWriter var4 = new PacketWriter(var3);
      var4.putNextInt(var1);
      var4.putNextMaxValue(2, 3);
      var4.putNextInt(var2);
      this.runAndSendAction(var3);
   }

   public void executePacket(PacketReader var1) {
      int var2 = var1.getNextInt();
      int var3 = var1.getNextMaxValue(3);
      if (this.container.client.isServer()) {
         SettlementLevelData var4 = this.container.getLevelData();
         if (var4 != null) {
            SettlementWorkZone var5 = var4.getWorkZones().getZone(var2);
            if (var5 == null) {
               (new SettlementWorkZoneRemovedEvent(var2)).applyAndSendToClient(this.container.client.getServerClient());
            } else if (var5 instanceof SettlementForestryZone) {
               SettlementForestryZone var6 = (SettlementForestryZone)var5;
               boolean var7;
               if (var3 == 0) {
                  var7 = var1.getNextBoolean();
                  var6.setChoppingAllowed(var7);
               } else if (var3 == 1) {
                  var7 = var1.getNextBoolean();
                  var6.setReplantChoppedDownTrees(var7);
               } else if (var3 == 2) {
                  int var8 = var1.getNextInt();
                  var6.setAutoPlantSaplingID(var8);
               }
            } else {
               (new SettlementWorkZoneChangedEvent(var5)).applyAndSendToClient(this.container.client.getServerClient());
            }
         }
      }

   }
}
