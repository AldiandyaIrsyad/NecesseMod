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

public class SetIslandServerCommand extends ModularChatCommand {
   public SetIslandServerCommand() {
      super("setisland", "Changes the island of the player", PermissionLevel.ADMIN, true, new CmdParameter("player", new ServerClientParameterHandler(true, false), true, new CmdParameter[0]), new CmdParameter("islandX", new RelativeIntParameterHandler()), new CmdParameter("islandY", new RelativeIntParameterHandler()), new CmdParameter("dimension", new RelativeIntParameterHandler(0), true, new CmdParameter[0]));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      ServerClient var7 = (ServerClient)var4[0];
      if (var7 == null) {
         var6.add("Must specify <player>");
      } else {
         LevelIdentifier var8 = var7.getLevelIdentifier();
         int var9 = RelativeIntParameterHandler.handleRelativeInt(var4[1], var8.isIslandPosition() ? var8.getIslandX() : var7.spawnLevelIdentifier.getIslandX());
         int var10 = RelativeIntParameterHandler.handleRelativeInt(var4[2], var8.isIslandPosition() ? var8.getIslandY() : var7.spawnLevelIdentifier.getIslandY());
         int var11 = RelativeIntParameterHandler.handleRelativeInt(var4[3], var8.isIslandPosition() ? var8.getIslandDimension() : var7.spawnLevelIdentifier.getIslandDimension());
         var7.changeIsland(var9, var10, var11);
         var6.add("Set " + var7.getName() + " island to " + var9 + ", " + var10 + " dim " + var11);
      }
   }
}
