package necesse.inventory.container.customAction;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;

public abstract class StringCustomAction extends ContainerCustomAction {
   public StringCustomAction() {
   }

   public void runAndSend(String var1) {
      Packet var2 = new Packet();
      PacketWriter var3 = new PacketWriter(var2);
      var3.putNextString(var1);
      this.runAndSendAction(var2);
   }

   public void executePacket(PacketReader var1) {
      this.run(var1.getNextString());
   }

   protected abstract void run(String var1);
}
