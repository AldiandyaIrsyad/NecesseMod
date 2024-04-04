package necesse.inventory.container.settlement.data;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.level.maps.levelData.settlementData.LevelSettler;

public class SettlementSettlerRestrictZoneData extends SettlementSettlerData {
   public int restrictZoneUniqueID;

   public SettlementSettlerRestrictZoneData(LevelSettler var1) {
      super(var1);
      this.restrictZoneUniqueID = var1.getRestrictZoneUniqueID();
   }

   public SettlementSettlerRestrictZoneData(PacketReader var1) {
      super(var1);
      this.restrictZoneUniqueID = var1.getNextInt();
   }

   public void writeContentPacket(PacketWriter var1) {
      super.writeContentPacket(var1);
      var1.putNextInt(this.restrictZoneUniqueID);
   }
}
