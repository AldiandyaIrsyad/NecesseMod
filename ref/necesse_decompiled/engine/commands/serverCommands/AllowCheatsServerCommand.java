package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class AllowCheatsServerCommand extends ModularChatCommand {
   public AllowCheatsServerCommand() {
      super("allowcheats", "Enables/allows cheats on this world (NOT REVERSIBLE)", PermissionLevel.OWNER, true);
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      if (var2.world.settings.allowCheats) {
         var6.add("Cheats are already allowed");
      } else {
         var2.world.settings.enableCheats();
         var6.add("Cheats are now allowed");
      }

   }
}
