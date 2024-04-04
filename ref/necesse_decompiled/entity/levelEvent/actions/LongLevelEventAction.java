package necesse.entity.levelEvent.actions;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;

public abstract class LongLevelEventAction extends LevelEventAction {
   public LongLevelEventAction() {
   }

   public void runAndSend(long var1) {
      Packet var3 = new Packet();
      PacketWriter var4 = new PacketWriter(var3);
      var4.putNextLong(var1);
      this.runAndSendAction(var3);
   }

   public void executePacket(PacketReader var1) {
      long var2 = var1.getNextLong();
      this.run(var2);
   }

   protected abstract void run(long var1);
}
