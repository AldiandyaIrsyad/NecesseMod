package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CommandLog;
import necesse.engine.commands.CommandsManager;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class MyPermissionsServerCommand extends ModularChatCommand {
   public MyPermissionsServerCommand() {
      super("mypermissions", "Shows your permission level", PermissionLevel.USER, false);
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      var6.add("Your permission level: " + CommandsManager.getPermissionLevel(var1, var3).name.translate());
      if (var3 != null) {
         var3.setPermissionLevel(var3.getPermissionLevel(), false);
      }

   }
}
