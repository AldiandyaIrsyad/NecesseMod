package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.FloatParameterHandler;
import necesse.engine.commands.parameterHandlers.ServerClientParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameUtils;

public class HungerServerCommand extends ModularChatCommand {
   public HungerServerCommand() {
      super("hunger", "Sets the hunger percent of player", PermissionLevel.ADMIN, true, new CmdParameter("player", new ServerClientParameterHandler(true, false), true, new CmdParameter[0]), new CmdParameter("hunger", new FloatParameterHandler()));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      ServerClient var7 = (ServerClient)var4[0];
      float var8 = (Float)var4[1];
      if (var7 == null) {
         var6.add("Must specify <player>");
      } else {
         var7.playerMob.hungerLevel = var8 / 100.0F;
         var7.playerMob.sendHungerPacket();
         var6.add("Set " + var7.getName() + " hunger level to " + GameUtils.formatNumber((double)var8) + "%");
      }
   }
}
