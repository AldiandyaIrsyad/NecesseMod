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

public class WhisperServerCommand extends ModularChatCommand {
   public WhisperServerCommand(String var1) {
      super(var1, "Whisper a message to another player", PermissionLevel.USER, false, new CmdParameter("player", new ServerClientParameterHandler()), new CmdParameter("message", new RestStringParameterHandler()));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      ServerClient var7 = (ServerClient)var4[0];
      if (var7 != null && var7 != var3) {
         String var8 = (String)var4[1];
         String var9 = var3 == null ? "Server" : var3.getName();
         var6.add("To " + var7.getName() + ": " + var8);
         var6.addClient("From " + var9 + ": " + var8, var7);
      } else {
         var6.add("Cannot whisper self");
      }
   }

   public boolean shouldBeListed() {
      return false;
   }
}
