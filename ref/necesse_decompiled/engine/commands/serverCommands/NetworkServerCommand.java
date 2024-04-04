package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class NetworkServerCommand extends ModularChatCommand {
   public NetworkServerCommand() {
      super("network", "Shows network usage this session", PermissionLevel.MODERATOR, false);
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      var6.add("Server received: " + var2.packetManager.getAverageIn() + "/s (" + var2.packetManager.getAverageInPackets() + "), Total: " + var2.packetManager.getTotalIn() + " (" + var2.packetManager.getTotalInPackets() + ")");
      var6.add("Server sent: " + var2.packetManager.getAverageOut() + "/s (" + var2.packetManager.getAverageOutPackets() + "), Total: " + var2.packetManager.getTotalOut() + " (" + var2.packetManager.getTotalOutPackets() + ")");
   }
}
