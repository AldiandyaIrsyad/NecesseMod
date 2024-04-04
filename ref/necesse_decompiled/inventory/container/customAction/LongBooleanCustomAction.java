package necesse.inventory.container.customAction;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;

public abstract class LongBooleanCustomAction extends ContainerCustomAction {
   public LongBooleanCustomAction() {
   }

   public void runAndSend(long var1, boolean var3) {
      Packet var4 = new Packet();
      PacketWriter var5 = new PacketWriter(var4);
      var5.putNextLong(var1);
      var5.putNextBoolean(var3);
      this.runAndSendAction(var4);
   }

   public void executePacket(PacketReader var1) {
      long var2 = var1.getNextLong();
      boolean var4 = var1.getNextBoolean();
      this.run(var2, var4);
   }

   protected abstract void run(long var1, boolean var3);
}
