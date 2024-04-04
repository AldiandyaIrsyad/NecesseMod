package necesse.inventory.container.settlement.events;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.inventory.itemFilter.ItemCategoriesFilter;

public class SettlementStorageFullUpdateEvent extends ContainerEvent {
   public final int tileX;
   public final int tileY;
   public final int priority;
   public final Packet filterContent;

   public SettlementStorageFullUpdateEvent(int var1, int var2, ItemCategoriesFilter var3, int var4) {
      this.tileX = var1;
      this.tileY = var2;
      this.priority = var4;
      this.filterContent = new Packet();
      var3.writePacket(new PacketWriter(this.filterContent));
   }

   public SettlementStorageFullUpdateEvent(PacketReader var1) {
      super(var1);
      this.tileX = var1.getNextShortUnsigned();
      this.tileY = var1.getNextShortUnsigned();
      this.priority = var1.getNextInt();
      this.filterContent = var1.getNextContentPacket();
   }

   public void write(PacketWriter var1) {
      var1.putNextShortUnsigned(this.tileX);
      var1.putNextShortUnsigned(this.tileY);
      var1.putNextInt(this.priority);
      var1.putNextContentPacket(this.filterContent);
   }
}
