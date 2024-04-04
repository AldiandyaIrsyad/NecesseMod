package necesse.engine.commands.serverCommands;

import necesse.engine.Settings;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.LanguageParameterHandler;
import necesse.engine.localization.Language;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class SetLanguageServerCommand extends ModularChatCommand {
   public SetLanguageServerCommand() {
      super("language", "Sets server language settings", PermissionLevel.SERVER, false, new CmdParameter("language", new LanguageParameterHandler(), false, new CmdParameter[0]));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      Language var7 = (Language)var4[0];
      if (Settings.language.equals(var7.stringID)) {
         var6.add("Server language already set to " + var7.stringID);
      } else {
         Settings.language = var7.stringID;
         Settings.saveServerSettings();
         var7.setCurrent();
         var6.add("Changed server language to " + var7.stringID);
      }

   }
}
