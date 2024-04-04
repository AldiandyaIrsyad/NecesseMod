package necesse.engine.commands.clientCommands;

import com.codedisaster.steamworks.SteamNetworkingMessages;
import com.codedisaster.steamworks.SteamNetworkingMessages.SendFlag;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.EnumParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.networkInfo.SteamNetworkMessagesInfo;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class SteamNetworkSendFlagClientCommand extends ModularChatCommand {
   public SteamNetworkSendFlagClientCommand() {
      super("steamnetwork", "Sets steam network send flag", PermissionLevel.USER, false, new CmdParameter("flag", new EnumParameterHandler(SendFlag.values()), false, new CmdParameter[0]));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      SteamNetworkMessagesInfo.sendFlag = (SteamNetworkingMessages.SendFlag)var4[0];
      var6.add("Steam network send tag set to " + SteamNetworkMessagesInfo.sendFlag);
   }
}
