package necesse.entity.mobs.networkField;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;

public class BooleanNetworkField extends NetworkField<Boolean> {
   public BooleanNetworkField(boolean var1) {
      super(var1);
   }

   public void writePacket(Boolean var1, PacketWriter var2) {
      var2.putNextBoolean(var1);
   }

   public Boolean readPacket(PacketReader var1) {
      return var1.getNextBoolean();
   }

   // $FF: synthetic method
   // $FF: bridge method
   public Object readPacket(PacketReader var1) {
      return this.readPacket(var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public void writePacket(Object var1, PacketWriter var2) {
      this.writePacket((Boolean)var1, var2);
   }
}
