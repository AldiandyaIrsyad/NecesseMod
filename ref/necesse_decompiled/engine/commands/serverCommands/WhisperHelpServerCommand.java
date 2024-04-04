package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.RestStringParameterHandler;
import necesse.engine.commands.parameterHandlers.ServerClientParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class WhisperHelpServerCommand extends ModularChatCommand {
   public WhisperHelpServerCommand() {
      super("whisperhelp", "Whisper a message to another player", PermissionLevel.USER, false, new CmdParameter("player", new ServerClientParameterHandler()), new CmdParameter("message", new RestStringParameterHandler()));
   }

   public boolean onlyForHelp() {
      return true;
   }

   public String getFullHelp(boolean var1) {
      String var2 = var1 ? "/" : "";
      return var2 + "w, " + var2 + "whisper or " + var2 + "pm " + this.getUsage();
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
   }
}
