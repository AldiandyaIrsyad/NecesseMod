package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class SaveServerCommand extends ModularChatCommand {
   public SaveServerCommand() {
      super("save", "Saves all data", PermissionLevel.MODERATOR, false);
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      var2.saveAll();
      var6.add("Saved all data.");
   }
}
