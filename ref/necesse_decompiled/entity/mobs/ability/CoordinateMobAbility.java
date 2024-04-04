package necesse.entity.mobs.ability;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;

public abstract class CoordinateMobAbility extends MobAbility {
   public CoordinateMobAbility() {
   }

   public void runAndSend(int var1, int var2) {
      Packet var3 = new Packet();
      PacketWriter var4 = new PacketWriter(var3);
      var4.putNextInt(var1);
      var4.putNextInt(var2);
      this.runAndSendAbility(var3);
   }

   public void executePacket(PacketReader var1) {
      int var2 = var1.getNextInt();
      int var3 = var1.getNextInt();
      this.run(var2, var3);
   }

   protected abstract void run(int var1, int var2);
}
