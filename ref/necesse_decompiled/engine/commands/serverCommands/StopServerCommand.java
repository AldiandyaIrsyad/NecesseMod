package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class StopServerCommand extends ModularChatCommand {
   public StopServerCommand(String var1) {
      super(var1, "Saves and stops the server", PermissionLevel.OWNER, false);
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      var2.stop((var0) -> {
         Thread.currentThread().interrupt();
      });
   }

   public boolean shouldBeListed() {
      return false;
   }
}
