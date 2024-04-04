package necesse.engine.commands.serverCommands.setupCommand;

import java.util.function.Function;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;

public class SimpleTrinketBuild extends SimpleItemBuild {
   public SimpleTrinketBuild(int var1, Function<ServerClient, InventoryItem> var2) {
      super((var1x) -> {
         return new PlayerInventorySlot(var1x.trinkets, var1);
      }, var2);
   }

   public SimpleTrinketBuild(Function<ServerClient, InventoryItem> var1) {
      this(-1, (Function)var1);
   }

   public SimpleTrinketBuild(int var1, String var2) {
      super((var1x) -> {
         return new PlayerInventorySlot(var1x.trinkets, var1);
      }, var2);
   }

   public SimpleTrinketBuild(String var1) {
      this(-1, (String)var1);
   }
}
