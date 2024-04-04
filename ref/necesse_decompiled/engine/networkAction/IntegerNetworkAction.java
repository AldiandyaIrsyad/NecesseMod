package necesse.engine.networkAction;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;

public abstract class IntegerNetworkAction<R> extends NetworkAction<R> {
   public IntegerNetworkAction() {
   }

   public void runAndSend(int var1) {
      Packet var2 = new Packet();
      PacketWriter var3 = new PacketWriter(var2);
      var3.putNextInt(var1);
      this.runAndSendAction(var2);
   }

   public void executePacket(PacketReader var1) {
      this.run(var1.getNextInt());
   }

   protected abstract void run(int var1);
}
