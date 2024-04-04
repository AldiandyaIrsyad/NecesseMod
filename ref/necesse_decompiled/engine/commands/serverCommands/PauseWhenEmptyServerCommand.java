package necesse.engine.commands.serverCommands;

import necesse.engine.Settings;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.BoolParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PauseWhenEmptyServerCommand extends ModularChatCommand {
   public PauseWhenEmptyServerCommand() {
      super("pausewhenempty", "Enable/disable pause when empty setting", PermissionLevel.ADMIN, false, new CmdParameter("0/1", new BoolParameterHandler()));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      Settings.pauseWhenEmpty = (Boolean)var4[0];
      if (!var2.isHosted() && !var2.isSingleplayer()) {
         Settings.saveServerSettings();
      }

      var6.add("Pause when empty set to: " + Settings.pauseWhenEmpty);
   }
}
