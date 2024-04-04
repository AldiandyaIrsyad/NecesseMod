package necesse.entity.mobs.ability;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;

public abstract class IntMobAbility extends MobAbility {
   public IntMobAbility() {
   }

   public void runAndSend(int var1) {
      Packet var2 = new Packet();
      PacketWriter var3 = new PacketWriter(var2);
      var3.putNextInt(var1);
      this.runAndSendAbility(var2);
   }

   public void executePacket(PacketReader var1) {
      int var2 = var1.getNextInt();
      this.run(var2);
   }

   protected abstract void run(int var1);
}
