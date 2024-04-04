package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.FloatParameterHandler;
import necesse.engine.commands.parameterHandlers.IntParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameRandom;
import necesse.level.maps.Level;

public class MowServerCommand extends ModularChatCommand {
   public MowServerCommand() {
      super("mow", "Mows ground of grass in range with percent chance", PermissionLevel.ADMIN, true, new CmdParameter("range", new IntParameterHandler()), new CmdParameter("chance", new FloatParameterHandler(100.0F), true, new CmdParameter[0]));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      if (var3 == null) {
         var6.add("Cannot run mow from server console.");
      } else {
         int var7 = (Integer)var4[0];
         float var8 = (Float)var4[1];
         float var9 = var8 / 100.0F;
         Level var10 = var2.world.getLevel(var3);
         if (var7 > Math.max(var10.width, var10.height)) {
            var7 = Math.max(var10.width, var10.height);
         }

         int var11 = var3.playerMob.getX() / 32;
         int var12 = var3.playerMob.getY() / 32;
         int var13 = ObjectRegistry.getObjectID("grass");

         for(int var14 = var11 - var7; var14 <= var11 + var7; ++var14) {
            for(int var15 = var12 - var7; var15 <= var12 + var7; ++var15) {
               if (var10.getObjectID(var14, var15) == var13 && GameRandom.globalRandom.getChance(var9)) {
                  var10.sendObjectChangePacket(var2, var14, var15, 0);
               }
            }
         }

         var6.add("Mowed grass in a range of " + var7 + " around you.");
      }
   }
}
