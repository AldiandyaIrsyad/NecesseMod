package necesse.engine.commands.serverCommands;

import java.util.Iterator;
import necesse.engine.Settings;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class BansServerCommand extends ModularChatCommand {
   public BansServerCommand() {
      super("bans", "Lists all current bans", PermissionLevel.ADMIN, false);
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      if (Settings.banned.size() == 0) {
         var6.add("There are no listed bans.");
      } else {
         var6.add(Settings.banned.size() + " total bans:");
         Iterator var7 = Settings.banned.iterator();

         while(var7.hasNext()) {
            String var8 = (String)var7.next();
            var6.add(var8);
         }
      }

   }
}
