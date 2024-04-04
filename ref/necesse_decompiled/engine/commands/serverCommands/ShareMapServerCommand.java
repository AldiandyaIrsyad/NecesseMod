package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.ServerClientParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketMapData;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.level.maps.DiscoveredMap;

public class ShareMapServerCommand extends ModularChatCommand {
   public ShareMapServerCommand() {
      super("sharemap", "Shares your map discoveries with another player", PermissionLevel.ADMIN, true, new CmdParameter("from", new ServerClientParameterHandler(), true, new CmdParameter[0]), new CmdParameter("to", new ServerClientParameterHandler()));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      ServerClient var7 = (ServerClient)var4[0];
      if (var7 == null) {
         var7 = var3;
      }

      ServerClient var8 = (ServerClient)var4[1];
      if (var7 == null) {
         var6.add("Missing from player as server");
      } else if (var8 == null || var8 == var3 && var7 == var3) {
         var6.add("Cannot share map with self");
      } else {
         var8.mapManager.combine(var7.mapManager);
         DiscoveredMap var9 = var8.mapManager.getDiscovery(var3.getLevelIdentifier());
         if (var9 != null) {
            var8.sendPacket(new PacketMapData(var9.getKnownMap()));
         }

         var6.add("Shared " + var7.getName() + "'s map with " + var8.getName());
      }
   }
}
