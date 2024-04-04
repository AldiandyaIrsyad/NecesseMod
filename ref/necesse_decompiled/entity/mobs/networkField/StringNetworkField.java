package necesse.entity.mobs.networkField;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;

public class StringNetworkField extends NetworkField<String> {
   public StringNetworkField(String var1) {
      super(var1);
   }

   public void writePacket(String var1, PacketWriter var2) {
      var2.putNextString(var1);
   }

   public String readPacket(PacketReader var1) {
      return var1.getNextString();
   }

   // $FF: synthetic method
   // $FF: bridge method
   public Object readPacket(PacketReader var1) {
      return this.readPacket(var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public void writePacket(Object var1, PacketWriter var2) {
      this.writePacket((String)var1, var2);
   }
}
