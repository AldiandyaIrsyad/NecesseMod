package necesse.engine.network;

import necesse.engine.util.ByteIterator;

public class PacketIterator extends ByteIterator {
   protected Packet packet;

   protected PacketIterator(Packet var1, int var2) {
      this.packet = var1;
      this.resetIndex(var2);
   }

   protected PacketIterator(Packet var1) {
      this(var1, 0);
   }

   public PacketIterator(PacketIterator var1) {
      super(var1);
      this.packet = var1.packet;
   }

   public int getSizeOfData() {
      return this.packet.getSize();
   }

   public Packet getPacket() {
      return this.packet;
   }
}
