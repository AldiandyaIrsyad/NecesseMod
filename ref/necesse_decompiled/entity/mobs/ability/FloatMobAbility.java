package necesse.entity.mobs.ability;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;

public abstract class FloatMobAbility extends MobAbility {
   public FloatMobAbility() {
   }

   public void runAndSend(float var1) {
      Packet var2 = new Packet();
      PacketWriter var3 = new PacketWriter(var2);
      var3.putNextFloat(var1);
      this.runAndSendAbility(var2);
   }

   public void executePacket(PacketReader var1) {
      float var2 = var1.getNextFloat();
      this.run(var2);
   }

   protected abstract void run(float var1);
}
