package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.IntParameterHandler;
import necesse.engine.commands.parameterHandlers.ItemParameterHandler;
import necesse.engine.commands.parameterHandlers.ServerClientParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.InventoryAddConsumer;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class GiveServerCommand extends ModularChatCommand {
   public GiveServerCommand() {
      super("give", "Gives item to player", PermissionLevel.ADMIN, true, new CmdParameter("player", new ServerClientParameterHandler(true, false), true, new CmdParameter[0]), new CmdParameter("item", new ItemParameterHandler()), new CmdParameter("amount", new IntParameterHandler(1), true, new CmdParameter[0]));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      ServerClient var7 = (ServerClient)var4[0];
      Item var8 = (Item)var4[1];
      int var9 = (Integer)var4[2];
      if (var7 == null) {
         var6.add("Must specify <player>");
      } else {
         InventoryItem var10 = var8.getDefaultItem(var7.playerMob, var9);
         var7.playerMob.getInv().addItem(var10, true, "give", (InventoryAddConsumer)null);
         var6.add("Gave item " + var10.getItemDisplayName() + " x" + var9 + " to " + var7.getName());
      }
   }
}
