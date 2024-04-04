package necesse.entity.mobs.networkField;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;

public class LongNetworkField extends NetworkField<Long> {
   public LongNetworkField(long var1) {
      super(var1);
   }

   public void writePacket(Long var1, PacketWriter var2) {
      var2.putNextLong(var1);
   }

   public Long readPacket(PacketReader var1) {
      return var1.getNextLong();
   }

   // $FF: synthetic method
   // $FF: bridge method
   public Object readPacket(PacketReader var1) {
      return this.readPacket(var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public void writePacket(Object var1, PacketWriter var2) {
      this.writePacket((Long)var1, var2);
   }
}
