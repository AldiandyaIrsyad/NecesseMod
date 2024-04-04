package necesse.entity.mobs.ability;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;

public abstract class FloatCoordinateMobAbility extends MobAbility {
   public FloatCoordinateMobAbility() {
   }

   public void runAndSend(float var1, float var2) {
      Packet var3 = new Packet();
      PacketWriter var4 = new PacketWriter(var3);
      var4.putNextFloat(var1);
      var4.putNextFloat(var2);
      this.runAndSendAbility(var3);
   }

   public void executePacket(PacketReader var1) {
      float var2 = var1.getNextFloat();
      float var3 = var1.getNextFloat();
      this.run(var2, var3);
   }

   protected abstract void run(float var1, float var2);
}
