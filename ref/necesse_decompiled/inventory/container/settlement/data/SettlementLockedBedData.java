package necesse.inventory.container.settlement.data;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;

public class SettlementLockedBedData {
   public final int tileX;
   public final int tileY;

   public SettlementLockedBedData(int var1, int var2) {
      this.tileX = var1;
      this.tileY = var2;
   }

   public SettlementLockedBedData(PacketReader var1) {
      this.tileX = var1.getNextShortUnsigned();
      this.tileY = var1.getNextShortUnsigned();
   }

   public void writeContentPacket(PacketWriter var1) {
      var1.putNextShortUnsigned(this.tileX);
      var1.putNextShortUnsigned(this.tileY);
   }
}
