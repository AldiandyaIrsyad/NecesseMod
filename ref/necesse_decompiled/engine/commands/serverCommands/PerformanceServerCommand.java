package necesse.engine.commands.serverCommands;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import necesse.engine.GameSystemInfo;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.BoolParameterHandler;
import necesse.engine.commands.parameterHandlers.IntParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketPerformanceResult;
import necesse.engine.network.packet.PacketPerformanceStart;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.tickManager.PerformanceTimerUtils;
import necesse.engine.tickManager.PerformanceTotal;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;

public class PerformanceServerCommand extends ModularChatCommand {
   public PerformanceServerCommand() {
      super("performance", "Records server performance over some seconds and creates a file with the results", PermissionLevel.USER, false, new CmdParameter("includeServer", new BoolParameterHandler(true), true, new CmdParameter[0]), new CmdParameter("seconds", new IntParameterHandler(10), true, new CmdParameter[0]));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      boolean var7 = (Boolean)var4[0];
      int var8 = (Integer)var4[1];
      if (var8 > 60) {
         var6.add("Cannot record performance for more than 60 seconds");
      } else {
         int var9 = GameRandom.globalRandom.nextInt();
         if (var7 && var3 != null && var3.getPermissionLevel().getLevel() < PermissionLevel.ADMIN.getLevel()) {
            var6.add("Only admins can record server performance.");
            var7 = false;
         }

         if (var3 == null) {
            var7 = true;
         }

         if (var7) {
            String var10 = (new SimpleDateFormat("yyyy-MM-dd HH'h'mm'm'ss's'")).format(new Date());
            var2.tickManager().runPerformanceDump(var8, (var6x) -> {
               ByteArrayOutputStream var7 = new ByteArrayOutputStream();
               PrintStream var8x = new PrintStream(var7);
               var8x.println("Server performance recording ran for " + var8 + " seconds starting at " + var10);
               PerformanceTotal var9x = PerformanceTimerUtils.combineTimers(var6x);
               if (var9x != null) {
                  var8x.println("A total of " + var9x.getTotalFrames() + " frames were recorded.");
                  var8x.println();
                  var9x.print(var8x);
                  if (var3 == null || var2.getLocalServerClient() != var3) {
                     var8x.println();
                     var8x.println();
                     GameSystemInfo.printSystemInfo(var8x);
                  }
               }

               if (var3 != null) {
                  var3.sendPacket(new PacketPerformanceResult(var9, var7.toString()));
               } else {
                  File var10x = new File("server performance " + var10 + ".txt");

                  try {
                     GameUtils.saveByteFile(var7.toByteArray(), var10x);
                     var6.add("Printed performance to file:");
                     var6.add(var10x.getAbsolutePath());
                  } catch (IOException var12) {
                     var6.add("Error printing performance file: " + var12);
                  }
               }

            });
            if (var3 == null) {
               var6.add("Recording server performance for the next " + var8 + " seconds...");
            }
         }

         if (var3 != null) {
            var3.sendPacket(new PacketPerformanceStart(var9, var8, var7));
         }

      }
   }
}
