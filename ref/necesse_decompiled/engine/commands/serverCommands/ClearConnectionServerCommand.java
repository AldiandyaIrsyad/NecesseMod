package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.ServerClientParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.networkInfo.InvalidNetworkInfo;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class ClearConnectionServerCommand extends ModularChatCommand {
   public ClearConnectionServerCommand() {
      super("clearconnection", "Clears the connection info of a client, making them try to reconnect to session", PermissionLevel.OWNER, true, new CmdParameter("player", new ServerClientParameterHandler(true, false)));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      ServerClient var7 = (ServerClient)var4[0];
      var7.networkInfo = new InvalidNetworkInfo();
      var6.add("Cleared connection info of " + var7.getName());
   }
}
