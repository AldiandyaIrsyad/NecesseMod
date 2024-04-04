package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.RestStringParameterHandler;
import necesse.engine.commands.parameterHandlers.ServerClientParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketDisconnect;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class KickServerCommand extends ModularChatCommand {
   public KickServerCommand() {
      super("kick", "Kicks player from the server", PermissionLevel.MODERATOR, false, new CmdParameter("player", new ServerClientParameterHandler()), new CmdParameter("message/reason", new RestStringParameterHandler(), true, new CmdParameter[0]));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      ServerClient var7 = (ServerClient)var4[0];
      String var8 = (String)var4[1];
      if (var8 == null) {
         var8 = "No message";
      }

      var2.disconnectClient(var7, PacketDisconnect.kickPacket(var7.slot, var8));
   }
}
