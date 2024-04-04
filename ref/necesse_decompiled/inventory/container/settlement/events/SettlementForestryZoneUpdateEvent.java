package necesse.inventory.container.settlement.events;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.level.maps.levelData.settlementData.zones.SettlementForestryZone;

public class SettlementForestryZoneUpdateEvent extends ContainerEvent {
   public final int uniqueID;
   public final boolean choppingAllowed;
   public final boolean replantChoppedDownTrees;
   public final int autoPlantSaplingID;

   public SettlementForestryZoneUpdateEvent(SettlementForestryZone var1) {
      this.uniqueID = var1.getUniqueID();
      this.choppingAllowed = var1.isChoppingAllowed();
      this.replantChoppedDownTrees = var1.replantChoppedDownTrees();
      this.autoPlantSaplingID = var1.getAutoPlantSaplingID();
   }

   public SettlementForestryZoneUpdateEvent(PacketReader var1) {
      super(var1);
      this.uniqueID = var1.getNextInt();
      this.choppingAllowed = var1.getNextBoolean();
      this.replantChoppedDownTrees = var1.getNextBoolean();
      this.autoPlantSaplingID = var1.getNextInt();
   }

   public void write(PacketWriter var1) {
      var1.putNextInt(this.uniqueID);
      var1.putNextBoolean(this.choppingAllowed);
      var1.putNextBoolean(this.replantChoppedDownTrees);
      var1.putNextInt(this.autoPlantSaplingID);
   }
}
