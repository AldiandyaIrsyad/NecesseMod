package necesse.entity.mobs.networkField;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;

public class ByteNetworkField extends NetworkField<Byte> {
   public ByteNetworkField(byte var1) {
      super(var1);
   }

   public void writePacket(Byte var1, PacketWriter var2) {
      var2.putNextByte(var1);
   }

   public Byte readPacket(PacketReader var1) {
      return var1.getNextByte();
   }

   // $FF: synthetic method
   // $FF: bridge method
   public Object readPacket(PacketReader var1) {
      return this.readPacket(var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public void writePacket(Object var1, PacketWriter var2) {
      this.writePacket((Byte)var1, var2);
   }
}
