package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.RestStringParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class MeServerCommand extends ModularChatCommand {
   public MeServerCommand() {
      super("me", "Declare an action to the entire server", PermissionLevel.USER, false, new CmdParameter("action", new RestStringParameterHandler()));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      if (var3 == null) {
         var6.add("Command cannot be run from server.");
      } else if (var4[0] != null && ((String)var4[0]).length() != 0) {
         String var7 = var3.getName() + " " + var4[0];
         var2.network.sendToAllClients(new PacketChatMessage(var7));
      } else {
         var6.add("Missing action");
      }

   }
}
