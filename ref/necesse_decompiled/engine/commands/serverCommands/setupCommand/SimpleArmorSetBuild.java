package necesse.engine.commands.serverCommands.setupCommand;

import necesse.engine.network.server.ServerClient;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventoryManager;

public class SimpleArmorSetBuild extends CharacterBuild {
   public String head;
   public String chest;
   public String feet;

   public SimpleArmorSetBuild(String var1, String var2, String var3) {
      this.head = var1;
      this.chest = var2;
      this.feet = var3;
   }

   public void apply(ServerClient var1) {
      PlayerInventoryManager var2 = var1.playerMob.getInv();
      if (this.head != null) {
         var2.armor.setItem(0, new InventoryItem(this.head));
      }

      if (this.head != null) {
         var2.armor.setItem(1, new InventoryItem(this.chest));
      }

      if (this.head != null) {
         var2.armor.setItem(2, new InventoryItem(this.feet));
      }

   }
}
