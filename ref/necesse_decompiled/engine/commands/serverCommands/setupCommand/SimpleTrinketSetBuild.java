package necesse.engine.commands.serverCommands.setupCommand;

import necesse.engine.network.packet.PacketUpdateTrinketSlots;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventoryManager;

public class SimpleTrinketSetBuild extends CharacterBuild {
   public String[] trinkets;

   public SimpleTrinketSetBuild(String... var1) {
      this.trinkets = var1;
   }

   public void apply(ServerClient var1) {
      var1.playerMob.getInv().trinkets.changeSize(Math.max(this.trinkets.length, 4));
      var1.playerMob.equipmentBuffManager.updateTrinketBuffs();
      var1.closeContainer(false);
      var1.updateInventoryContainer();
      var1.getServer().network.sendToAllClients(new PacketUpdateTrinketSlots(var1));
      PlayerInventoryManager var2 = var1.playerMob.getInv();

      for(int var3 = 0; var3 < this.trinkets.length && var3 < var2.trinkets.getSize(); ++var3) {
         var2.trinkets.setItem(var3, new InventoryItem(this.trinkets[var3]));
      }

   }
}
