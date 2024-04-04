package necesse.engine.commands.serverCommands.setupCommand;

import necesse.engine.network.server.ServerClient;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventoryManager;

public class SimpleToolSetBuild extends CharacterBuild {
   public String pickaxe;
   public String axe;
   public String shovel;

   public SimpleToolSetBuild(String var1, String var2, String var3) {
      super(-1000);
      this.pickaxe = var1;
      this.axe = var2;
      this.shovel = var3;
   }

   public void apply(ServerClient var1) {
      PlayerInventoryManager var2 = var1.playerMob.getInv();
      if (this.pickaxe != null) {
         var2.main.setItem(0, new InventoryItem(this.pickaxe));
      }

      if (this.axe != null) {
         var2.main.setItem(9, new InventoryItem(this.axe));
      }

      if (this.shovel != null) {
         var2.main.setItem(8, new InventoryItem(this.shovel));
      }

   }
}
