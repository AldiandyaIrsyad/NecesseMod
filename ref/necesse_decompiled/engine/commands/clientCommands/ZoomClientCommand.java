package necesse.engine.commands.clientCommands;

import necesse.engine.GlobalData;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.IntParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class ZoomClientCommand extends ModularChatCommand {
   public ZoomClientCommand() {
      super("zoom", "Sets zoom level", PermissionLevel.USER, false, new CmdParameter("percent", new IntParameterHandler(), false, new CmdParameter[0]));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      int var7 = (Integer)var4[0];
      if (GlobalData.isDevMode() || var7 >= 100 && var7 <= 200) {
         Settings.sceneSize = (float)var7 / 100.0F;
         Screen.updateSceneSize();
         Settings.saveClientSettings();
         var6.add("Changed zoom level to " + var7 + "%");
      } else {
         var6.add("Zoom percent must be between 100 and 200");
      }
   }
}
