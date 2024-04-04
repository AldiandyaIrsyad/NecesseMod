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

public class TimeServerCommand extends ModularChatCommand {
   public TimeServerCommand() {
      super("time", "Sets/adds world time (can use (mid)day or (mid)night)", PermissionLevel.ADMIN, true, new CmdParameter("set/add", new PresetStringParameterHandler(new String[]{"set", "add", "day", "dawn", "morning", "noon", "midday", "dusk", "night", "midnight"})), new CmdParameter("amount", new IntParameterHandler(-1), true, new CmdParameter[0]));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      String var7 = (String)var4[0];
      int var8 = (Integer)var4[1];
      long var9 = var2.world.worldEntity.getWorldTime();
      switch (var7) {
         case "set":
            if (var8 < 0) {
               var6.add("Amount must be a positive number.");
               return;
            }

            long var13 = (long)(var2.world.worldEntity.getDayTimeMax() - var2.world.worldEntity.getDayTimeInt() + var8) * 1000L;
            var2.world.addWorldTime(var13);
            break;
         case "add":
            if (var8 <= 0) {
               var6.add("Amount must be above 0.");
               return;
            }

            var2.world.addWorldTime((long)var8 * 1000L);
            break;
         case "dawn":
            var2.world.setDawn();
            break;
         case "day":
         case "morning":
            var2.world.setMorning();
            break;
         case "noon":
         case "midday":
            var2.world.setMidday();
            break;
         case "dusk":
            var2.world.setDusk();
            break;
         case "night":
            var2.world.setNight();
            break;
         case "midnight":
            var2.world.setMidnight();
      }

      if (var2.world.worldEntity.getWorldTime() != var9) {
         var2.world.simulateWorldTime(var2.world.worldEntity.getWorldTime() - var9, true);
         var6.add("Time changed to " + var2.world.worldEntity.getDayTimeInt() + ", day " + var2.world.worldEntity.getDay());
      }

   }
}
