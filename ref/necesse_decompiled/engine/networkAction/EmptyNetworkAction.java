package necesse.engine.networkAction;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;

public abstract class EmptyNetworkAction<R> extends NetworkAction<R> {
   public EmptyNetworkAction() {
   }

   public void runAndSend() {
      this.runAndSendAction(new Packet());
   }

   public void executePacket(PacketReader var1) {
      this.run();
   }

   protected abstract void run();
}
