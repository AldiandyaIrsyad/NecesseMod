package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.IntParameterHandler;
import necesse.engine.commands.parameterHandlers.PresetStringParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.LevelIdentifier;
import necesse.level.maps.Level;

public class RainServerCommand extends ModularChatCommand {
   public RainServerCommand() {
      super("rain", "Sets the rain on the level", PermissionLevel.ADMIN, true, new CmdParameter("islandX", new IntParameterHandler(Integer.MIN_VALUE), true, new CmdParameter[]{new CmdParameter("islandY", new IntParameterHandler()), new CmdParameter("dimension", new IntParameterHandler())}), new CmdParameter("start/clear", new PresetStringParameterHandler(new String[]{"start", "clear"})));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      LevelIdentifier var7 = new LevelIdentifier((Integer)var4[0], (Integer)var4[1], (Integer)var4[2]);
      String var8 = (String)var4[3];
      boolean var9 = var8.equals("start");
      if (var7.getIslandX() == Integer.MIN_VALUE) {
         if (var3 == null) {
            var6.add("Please specify island coordinates and dimension");
            return;
         }

         var7 = var3.getLevelIdentifier();
      }

      if (var2.world.levelManager.isLoaded(var7)) {
         Level var10 = var2.world.getLevel(var7);
         if (var9 && !var10.biome.canRain(var10)) {
            var6.add("Level does not allow rain");
         } else {
            var10.rainingLayer.setRaining(var9);
            var10.rainingLayer.resetRainTimer();
            if (var10.rainingLayer.isRaining()) {
               var6.add("Started rain session");
            } else {
               var6.add("Cleared rain session");
            }
         }
      } else {
         var6.add("Specified level is not loaded");
      }

   }
}
