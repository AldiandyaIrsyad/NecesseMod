package necesse.engine.networkAction;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;

public abstract class NetworkAction<R> {
   private NetworkActionRegistry<R, ?> registry;
   private int id;

   public NetworkAction() {
   }

   public void onRegister(NetworkActionRegistry<R, ?> var1, int var2) {
      if (this.registry != null) {
         throw new IllegalStateException(this.registry.actionCallName + " already registered");
      } else {
         this.registry = var1;
         this.id = var2;
      }
   }

   public R getRegistrar() {
      return this.registry.registrar;
   }

   public int getID() {
      return this.id;
   }

   public abstract void executePacket(PacketReader var1);

   protected void runAndSendAction(Packet var1) {
      if (this.registry != null) {
         this.registry.runAndSendAction(this, var1);
      } else {
         System.err.println("Cannot run action that's not registered");
      }

   }
}
