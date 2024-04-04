package necesse.entity.mobs.networkField;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;

public class FloatNetworkField extends NetworkField<Float> {
   public FloatNetworkField(float var1) {
      super(var1);
   }

   public void writePacket(Float var1, PacketWriter var2) {
      var2.putNextFloat(var1);
   }

   public Float readPacket(PacketReader var1) {
      return var1.getNextFloat();
   }

   // $FF: synthetic method
   // $FF: bridge method
   public Object readPacket(PacketReader var1) {
      return this.readPacket(var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public void writePacket(Object var1, PacketWriter var2) {
      this.writePacket((Float)var1, var2);
   }
}
