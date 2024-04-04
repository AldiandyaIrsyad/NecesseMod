package necesse.engine.networkAction;

import java.util.ArrayList;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;

public abstract class NetworkActionRegistry<R, T extends NetworkAction<R>> {
   public final R registrar;
   public final String actionCallName;
   private boolean registryOpen;
   private int maxActions;
   private ArrayList<T> actions;

   public NetworkActionRegistry(R var1, String var2, int var3) {
      this.registryOpen = true;
      this.actions = new ArrayList();
      if (var2 == null) {
         var2 = "NetworkAction";
      }

      this.registrar = var1;
      this.actionCallName = var2;
      this.maxActions = var3;
   }

   public NetworkActionRegistry(R var1, String var2) {
      this(var1, var2, 32767);
   }

   public void closeRegistry() {
      this.registryOpen = false;
   }

   public final void runAction(int var1, PacketReader var2) {
      if (var1 >= 0 && var1 < this.actions.size()) {
         ((NetworkAction)this.actions.get(var1)).executePacket(var2);
      } else {
         System.err.println("Could not find and run " + this.actionCallName + " " + var1);
      }

   }

   public abstract void runAndSendAction(NetworkAction<R> var1, Packet var2);

   public final <C extends T> C register(C var1) {
      if (!this.registryOpen) {
         throw new IllegalStateException("Cannot register " + this.actionCallName + " after initialization, must be done in constructor.");
      } else if (this.actions.size() >= this.maxActions) {
         throw new IllegalStateException("Cannot register any more " + this.actionCallName);
      } else {
         this.actions.add(var1);
         var1.onRegister(this, this.actions.size() - 1);
         return var1;
      }
   }

   public boolean isEmpty() {
      return this.actions.isEmpty();
   }

   public Iterable<T> getActions() {
      return this.actions;
   }
}
