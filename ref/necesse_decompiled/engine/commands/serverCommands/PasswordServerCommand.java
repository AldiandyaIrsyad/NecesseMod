package necesse.engine.commands.serverCommands;

import necesse.engine.Settings;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.StringParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PasswordServerCommand extends ModularChatCommand {
   public PasswordServerCommand() {
      super("password", "Set a password of the server, blank will be no password", PermissionLevel.OWNER, false, new CmdParameter("password", new StringParameterHandler(), true, new CmdParameter[0]));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      String var7 = (String)var4[0];
      if (var7 != null && var7.length() != 0) {
         Settings.serverPassword = var7;
         var6.add("Password set to: " + Settings.serverPassword);
      } else {
         Settings.serverPassword = "";
         var6.add("Removed password from server");
      }

      if (!var2.isHosted() && !var2.isSingleplayer()) {
         Settings.saveServerSettings();
      }

      var2.settings.password = Settings.serverPassword;
   }
}
