package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.BoolParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.GameDamage;

public class StaticDamageServerCommand extends ModularChatCommand {
   public StaticDamageServerCommand() {
      super("staticdamage", "Makes all damage not be randomized", PermissionLevel.OWNER, true, new CmdParameter("value", new BoolParameterHandler(true)));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      GameDamage.staticDamage = (Boolean)var4[0];
      var6.add("Static damage set to " + GameDamage.staticDamage);
   }
}
