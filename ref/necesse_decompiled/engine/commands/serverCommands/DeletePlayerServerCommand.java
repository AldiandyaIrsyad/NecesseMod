package necesse.engine.commands.serverCommands;

import java.io.IOException;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.StoredPlayerParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class DeletePlayerServerCommand extends ModularChatCommand {
   public DeletePlayerServerCommand() {
      super("deleteplayer", "Deletes a players files in the saved players folder", PermissionLevel.ADMIN, false, new CmdParameter("authentication/fullname", new StoredPlayerParameterHandler()));
   }

   public boolean autocompleteOnServer() {
      return true;
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      StoredPlayerParameterHandler.StoredPlayer var7 = (StoredPlayerParameterHandler.StoredPlayer)var4[0];
      boolean var8 = false;
      boolean var9 = false;

      try {
         var8 = var7.file.delete();
         var9 = var7.mapFile.delete();
      } catch (IOException var11) {
         var11.printStackTrace();
      }

      if (var8 && var9) {
         var2.usedNames.remove(var7.authentication);
         var6.add("Deleted player " + var7.authentication + " - \"" + var7.name + "\".");
      } else if (var8) {
         var6.add("Error in deleting player map " + var7.authentication + " - \"" + var7.name + "\".");
      } else {
         var6.add("Error in deleting player " + var7.authentication + " - \"" + var7.name + "\".");
      }

   }
}
