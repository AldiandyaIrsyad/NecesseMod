package necesse.entity.mobs.networkField;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;

public class IntNetworkField extends NetworkField<Integer> {
   public IntNetworkField(int var1) {
      super(var1);
   }

   public void writePacket(Integer var1, PacketWriter var2) {
      var2.putNextInt(var1);
   }

   public Integer readPacket(PacketReader var1) {
      return var1.getNextInt();
   }

   // $FF: synthetic method
   // $FF: bridge method
   public Object readPacket(PacketReader var1) {
      return this.readPacket(var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public void writePacket(Object var1, PacketWriter var2) {
      this.writePacket((Integer)var1, var2);
   }
}
