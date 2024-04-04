package necesse.inventory.container.settlement.actions.workstation;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.settlement.SettlementDependantContainer;
import necesse.inventory.container.settlement.events.SettlementWorkstationEvent;
import necesse.inventory.container.settlement.events.SettlementWorkstationRecipeRemoveEvent;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.settlementData.SettlementWorkstation;

public class RemoveWorkstationRecipeAction extends ConfigureWorkstationAction {
   public RemoveWorkstationRecipeAction(SettlementDependantContainer var1) {
      super(var1);
   }

   public void runAndSend(int var1, int var2, int var3) {
      Packet var4 = new Packet();
      PacketWriter var5 = new PacketWriter(var4);
      var5.putNextInt(var3);
      this.runAndSend(var1, var2, var4);
   }

   public void handleAction(PacketReader var1, SettlementLevelData var2, SettlementWorkstation var3) {
      int var4 = var1.getNextInt();
      if (var3.recipes.removeIf((var1x) -> {
         return var1x.uniqueID == var4;
      })) {
         (new SettlementWorkstationRecipeRemoveEvent(var3.tileX, var3.tileY, var4)).applyAndSendToClientsAt(var2.getLevel());
      } else {
         (new SettlementWorkstationEvent(var3)).applyAndSendToClient(this.container.client.getServerClient());
      }

   }
}
