package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class StopHelpServerCommand extends ModularChatCommand {
   public StopHelpServerCommand() {
      super("stophelp", "Saves and stops the server", PermissionLevel.OWNER, false);
   }

   public boolean onlyForHelp() {
      return true;
   }

   public String getFullHelp(boolean var1) {
      String var2 = var1 ? "/" : "";
      return var2 + "stop, " + var2 + "exit or " + var2 + "quit " + this.getUsage();
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
   }
}
