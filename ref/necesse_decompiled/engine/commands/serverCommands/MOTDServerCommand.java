package necesse.engine.commands.serverCommands;

import necesse.engine.Settings;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.RestStringParameterHandler;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class MOTDServerCommand extends ModularChatCommand {
   public MOTDServerCommand() {
      super("motd", "Sets or clears the message of the day. Use \\n for new line", PermissionLevel.ADMIN, false, new CmdParameter("clear/get/message", new RestStringParameterHandler()));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      String var7 = (String)var4[0];
      var7 = var7.trim();
      if (var7.equals("clear")) {
         Settings.serverMOTD = "";
         var6.add("Cleared message of the day");
         if (!var2.isHosted() && !var2.isSingleplayer()) {
            Settings.saveServerSettings();
         }
      } else if (var7.equals("get")) {
         if (Settings.serverMOTD.isEmpty()) {
            var6.add("There are currently no message of the day set");
         } else {
            GameMessageBuilder var8 = (new GameMessageBuilder()).append("misc", "motd").append("\n").append(Settings.serverMOTD);
            var6.add((GameMessage)var8);
         }
      } else {
         var7 = var7.replace("\\n", "\n");
         Settings.serverMOTD = var7;
         var6.add("Set message of the day to:");
         var6.add(var7);
         if (!var2.isHosted() && !var2.isSingleplayer()) {
            Settings.saveServerSettings();
         }
      }

   }
}
