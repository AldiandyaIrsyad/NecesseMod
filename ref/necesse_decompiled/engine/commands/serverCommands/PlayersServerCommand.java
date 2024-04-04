package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PlayersServerCommand extends ModularChatCommand {
   public PlayersServerCommand() {
      super("players", "Lists players currently online", PermissionLevel.MODERATOR, false);
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      int var7 = 0;

      int var8;
      for(var8 = 0; var8 < var2.getSlots(); ++var8) {
         if (var2.getClient(var8) != null) {
            ++var7;
         }
      }

      var6.add("Players online: " + var7 + "/" + var2.getSlots());

      for(var8 = 0; var8 < var2.getSlots(); ++var8) {
         if (var2.getClient(var8) != null) {
            String var9 = var2.getClient(var8).networkInfo == null ? "LOCAL" : var2.getClient(var8).networkInfo.getDisplayName();
            var6.add("Slot " + (var8 + 1) + ": " + var2.getClient(var8).authentication + " \"" + var2.getClient(var8).getName() + "\", latency: " + var2.getClient(var8).latency + ", level: " + var2.getClient(var8).getLevelIdentifier() + ",conn: " + var9);
         }
      }

   }
}
