package necesse.entity.levelEvent.actions;

import java.util.ArrayList;
import necesse.engine.network.PacketReader;
import necesse.entity.levelEvent.LevelEvent;

public class LevelEventActionRegistry {
   private final LevelEvent event;
   private boolean registryOpen = true;
   private ArrayList<LevelEventAction> actions = new ArrayList();

   public LevelEventActionRegistry(LevelEvent var1) {
      this.event = var1;
   }

   public void closeRegistry() {
      this.registryOpen = false;
   }

   public final void runAction(int var1, PacketReader var2) {
      if (var1 >= 0 && var1 < this.actions.size()) {
         ((LevelEventAction)this.actions.get(var1)).executePacket(var2);
      } else {
         System.err.println("Could not find and run level event action " + var1 + " for " + this.event.toString());
      }

   }

   public final <T extends LevelEventAction> T registerAction(T var1) {
      if (!this.registryOpen) {
         throw new IllegalStateException("Cannot register level event actions after initialization, must be done in constructor");
      } else if (this.actions.size() >= 32767) {
         throw new IllegalStateException("Cannot register any more level event actions for " + var1.toString());
      } else {
         this.actions.add(var1);
         var1.onRegister(this.event, this.actions.size() - 1);
         return var1;
      }
   }
}
