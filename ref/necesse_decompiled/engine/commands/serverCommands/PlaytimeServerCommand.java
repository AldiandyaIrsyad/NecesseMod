package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameUtils;

public class PlaytimeServerCommand extends ModularChatCommand {
   public PlaytimeServerCommand() {
      super("playtime", "Shows your current playtime on the server", PermissionLevel.USER, false);
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      if (var3 == null) {
         var6.add("Command cannot be run from server.");
      } else {
         var6.add("Total playtime: " + GameUtils.formatSeconds((long)var3.characterStats().time_played.get()));
         var6.add("Current session: " + GameUtils.formatSeconds(var3.getSessionTime()));
      }

   }
}
