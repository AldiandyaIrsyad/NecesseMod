package necesse.engine.commands.serverCommands;

import necesse.engine.GameAuth;
import necesse.engine.Settings;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.MultiParameterHandler;
import necesse.engine.commands.parameterHandlers.ParameterHandler;
import necesse.engine.commands.parameterHandlers.ServerClientParameterHandler;
import necesse.engine.commands.parameterHandlers.StringParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketDisconnect;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class BanServerCommand extends ModularChatCommand {
   public BanServerCommand() {
      super("ban", "Bans a player", PermissionLevel.ADMIN, false, new CmdParameter("authentication/name", new MultiParameterHandler(new ParameterHandler[]{new ServerClientParameterHandler(false, true), new StringParameterHandler()})));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      Object[] var7 = (Object[])var4[0];
      ServerClient var8 = (ServerClient)var7[0];
      String var9 = (String)var7[1];
      if (var8 != null) {
         if (var8.authentication == GameAuth.getAuthentication()) {
            var6.add("Cannot ban yourself");
         } else {
            Settings.addBanned(var8.getName());
            var6.add("Banned " + var8.getName() + ".");
            var2.disconnectClient(var8, PacketDisconnect.kickPacket(var8.slot, "You have been banned."));
         }
      } else if (String.valueOf(GameAuth.getAuthentication()).equals(var9)) {
         var6.add("Cannot ban your own authentication");
      } else {
         if (!Settings.isBanned(var9)) {
            Settings.addBanned(var9);
            var6.add("Banned " + var9 + ".");
         } else {
            var6.add(var9 + " is already banned.");
         }

      }
   }
}
