package necesse.inventory.container.settlement.events;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class SettlementSingleWorkstationsEvent extends ContainerEvent {
   public final int tileX;
   public final int tileY;
   public final boolean exists;

   public SettlementSingleWorkstationsEvent(SettlementLevelData var1, int var2, int var3) {
      this.tileX = var2;
      this.tileY = var3;
      this.exists = var1.getWorkstation(var2, var3) != null;
   }

   public SettlementSingleWorkstationsEvent(PacketReader var1) {
      super(var1);
      this.tileX = var1.getNextShortUnsigned();
      this.tileY = var1.getNextShortUnsigned();
      this.exists = var1.getNextBoolean();
   }

   public void write(PacketWriter var1) {
      var1.putNextShortUnsigned(this.tileX);
      var1.putNextShortUnsigned(this.tileY);
      var1.putNextBoolean(this.exists);
   }
}
