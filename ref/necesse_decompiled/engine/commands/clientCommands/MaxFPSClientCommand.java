package necesse.engine.commands.clientCommands;

import necesse.engine.Settings;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.IntParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class MaxFPSClientCommand extends ModularChatCommand {
   public MaxFPSClientCommand() {
      super("maxfps", "Sets max fps", PermissionLevel.USER, false, new CmdParameter("fps", new IntParameterHandler(), false, new CmdParameter[0]));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      int var7 = (Integer)var4[0];
      Settings.maxFPS = var7;
      Settings.saveClientSettings();
      var6.add("Set max fps to " + var7);
   }
}
