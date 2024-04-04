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

public class PrintServerCommand extends ModularChatCommand {
   public PrintServerCommand() {
      super("print", "Prints a message in the chat", PermissionLevel.ADMIN, false, new CmdParameter("message", new RestStringParameterHandler()));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      String var7 = (String)var4[0];
      var2.network.sendToAllClients(new PacketChatMessage(var7));
      var6.addConsole("(Print): " + var7);
   }
}
