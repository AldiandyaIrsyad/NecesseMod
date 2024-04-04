package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.RelativeIntParameterHandler;
import necesse.engine.commands.parameterHandlers.ServerClientParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.LevelIdentifier;

public class SetDimensionServerCommand extends ModularChatCommand {
   public SetDimensionServerCommand() {
      super("setdimension", "Changes the dimension of player", PermissionLevel.ADMIN, true, new CmdParameter("player", new ServerClientParameterHandler(true, false), true, new CmdParameter[0]), new CmdParameter("dimension", new RelativeIntParameterHandler()));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      ServerClient var7 = (ServerClient)var4[0];
      if (var7 == null) {
         var6.add("Must specify <player>");
      } else {
         LevelIdentifier var8 = var7.getLevelIdentifier();
         if (!var8.isIslandPosition()) {
            var6.add(var7.getName() + " is not on an island");
         } else {
            int var9 = RelativeIntParameterHandler.handleRelativeInt(var4[1], var8.getIslandDimension());
            var7.changeIsland(var8.getIslandX(), var8.getIslandY(), var9);
            var6.add("Set " + var7.getName() + " dimension to " + var9);
         }
      }
   }
}
