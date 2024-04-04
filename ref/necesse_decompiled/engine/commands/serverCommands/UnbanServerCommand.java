package necesse.engine.commands.serverCommands;

import necesse.engine.Settings;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.UnbanParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class UnbanServerCommand extends ModularChatCommand {
   public UnbanServerCommand() {
      super("unban", "Removes a ban", PermissionLevel.ADMIN, false, new CmdParameter("authentication/name", new UnbanParameterHandler()));
   }

   public boolean autocompleteOnServer() {
      return true;
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      String var7 = (String)var4[0];
      if (Settings.removeBanned(var7)) {
         var6.add(var7 + " is no longer banned.");
      } else {
         var6.add(var7 + " is not banned.");
      }

   }
}
