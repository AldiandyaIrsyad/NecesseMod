package necesse.engine.commands.serverCommands;

import necesse.engine.Settings;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.IntParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class MaxLatencyServerCommand extends ModularChatCommand {
   public MaxLatencyServerCommand() {
      super("maxlatency", "Sets the max latency before client timeout", PermissionLevel.ADMIN, false, new CmdParameter("seconds", new IntParameterHandler()));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      int var7 = (Integer)var4[0];
      if (var7 >= 1 && var7 <= 300) {
         Settings.maxClientLatencySeconds = var7;
         if (!var2.isHosted() && !var2.isSingleplayer()) {
            Settings.saveServerSettings();
         }

         var6.add("Max latency set to: " + Settings.maxClientLatencySeconds);
      } else {
         var6.add("Max latency must be between 1 and 300 seconds");
      }
   }
}
