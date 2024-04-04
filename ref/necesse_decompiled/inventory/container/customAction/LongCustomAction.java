package necesse.inventory.container.customAction;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;

public abstract class LongCustomAction extends ContainerCustomAction {
   public LongCustomAction() {
   }

   public void runAndSend(long var1) {
      Packet var3 = new Packet();
      PacketWriter var4 = new PacketWriter(var3);
      var4.putNextLong(var1);
      this.runAndSendAction(var3);
   }

   public void executePacket(PacketReader var1) {
      this.run(var1.getNextLong());
   }

   protected abstract void run(long var1);
}
