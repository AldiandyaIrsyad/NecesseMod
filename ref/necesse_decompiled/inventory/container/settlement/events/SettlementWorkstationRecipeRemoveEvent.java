package necesse.inventory.container.settlement.events;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;

public class SettlementWorkstationRecipeRemoveEvent extends ContainerEvent {
   public final int tileX;
   public final int tileY;
   public final int uniqueID;

   public SettlementWorkstationRecipeRemoveEvent(int var1, int var2, int var3) {
      this.tileX = var1;
      this.tileY = var2;
      this.uniqueID = var3;
   }

   public SettlementWorkstationRecipeRemoveEvent(PacketReader var1) {
      super(var1);
      this.tileX = var1.getNextShortUnsigned();
      this.tileY = var1.getNextShortUnsigned();
      this.uniqueID = var1.getNextInt();
   }

   public void write(PacketWriter var1) {
      var1.putNextShortUnsigned(this.tileX);
      var1.putNextShortUnsigned(this.tileY);
      var1.putNextInt(this.uniqueID);
   }
}
