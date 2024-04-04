package necesse.inventory.container.customAction;

import java.util.ArrayList;
import necesse.engine.network.PacketReader;
import necesse.inventory.container.Container;

public class ContainerCustomActionRegistry {
   private final Container container;
   private boolean registryOpen = true;
   private ArrayList<ContainerCustomAction> actions = new ArrayList();

   public ContainerCustomActionRegistry(Container var1) {
      this.container = var1;
   }

   public void closeRegistry() {
      this.registryOpen = false;
   }

   public final void runAction(int var1, PacketReader var2) {
      if (var1 >= 0 && var1 < this.actions.size()) {
         ((ContainerCustomAction)this.actions.get(var1)).executePacket(var2);
      } else {
         System.err.println("Could not find and run container custom action " + var1);
      }

   }

   public final <T extends ContainerCustomAction> T registerAction(T var1) {
      if (!this.registryOpen) {
         throw new IllegalStateException("Cannot register container custom actions after initialization, must be done in constructor.");
      } else if (this.actions.size() >= 32767) {
         throw new IllegalStateException("Cannot register any more container custom actions");
      } else {
         this.actions.add(var1);
         var1.onRegister(this.container, this.actions.size() - 1);
         return var1;
      }
   }
}
