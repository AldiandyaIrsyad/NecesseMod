package necesse.inventory.container.settlement.actions.workstation;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.settlement.SettlementDependantContainer;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.settlementData.SettlementWorkstation;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationRecipe;

public class UpdateWorkstationRecipeAction extends ConfigureWorkstationAction {
   public UpdateWorkstationRecipeAction(SettlementDependantContainer var1) {
      super(var1);
   }

   public void runAndSend(int var1, int var2, int var3, SettlementWorkstationRecipe var4) {
      Packet var5 = new Packet();
      PacketWriter var6 = new PacketWriter(var5);
      var6.putNextShortUnsigned(var3);
      var6.putNextInt(var4.uniqueID);
      var4.writePacket(var6);
      this.runAndSend(var1, var2, var5);
   }

   public void handleAction(PacketReader var1, SettlementLevelData var2, SettlementWorkstation var3) {
      int var4 = var1.getNextShortUnsigned();
      int var5 = var1.getNextInt();
      var3.updateRecipe(var4, var5, var1);
   }
}
