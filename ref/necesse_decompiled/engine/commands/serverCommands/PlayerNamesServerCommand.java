package necesse.engine.commands.serverCommands;

import java.util.Iterator;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PlayerNamesServerCommand extends ModularChatCommand {
   public PlayerNamesServerCommand() {
      super("playernames", "Lists all authentications and their names", PermissionLevel.MODERATOR, false);
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      var6.add("Total players stored: " + var2.usedNames.size());
      Iterator var7 = var2.usedNames.keySet().iterator();

      while(var7.hasNext()) {
         long var8 = (Long)var7.next();
         var6.add(var8 + " - " + (String)var2.usedNames.get(var8));
      }

   }
}
