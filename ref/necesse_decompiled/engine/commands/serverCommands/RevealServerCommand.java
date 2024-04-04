package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.ServerClientParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class RevealServerCommand extends ModularChatCommand {
   public RevealServerCommand() {
      super("reveal", "Reveals entire clients current level", PermissionLevel.ADMIN, true, new CmdParameter("player", new ServerClientParameterHandler(true, false), true, new CmdParameter[0]));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      ServerClient var7 = (ServerClient)var4[0];
      if (var7 == null) {
         var6.add("Must specify <player>");
      } else {
         var7.discoverEntireMap();
         var6.add("Revealed " + var7.getName() + " entire map.");
      }
   }
}
