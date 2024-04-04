package necesse.engine.commands.clientCommands;

import java.util.ArrayList;
import java.util.Collections;
import necesse.engine.commands.ChatCommand;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.CmdNameParameterHandler;
import necesse.engine.commands.parameterHandlers.IntParameterHandler;
import necesse.engine.commands.parameterHandlers.MultiParameterHandler;
import necesse.engine.commands.parameterHandlers.ParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameMath;
import necesse.gfx.GameColor;

public class HelpClientCommand extends ModularChatCommand {
   public HelpClientCommand() {
      super("help", "Lists all commands or gives information about a specific command", PermissionLevel.USER, false, new CmdParameter("page/command", new MultiParameterHandler(new ParameterHandler[]{new IntParameterHandler(1), new CmdNameParameterHandler(true)}), true, new CmdParameter[0]));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      Object[] var7 = (Object[])var4[0];
      int var8 = (Integer)var7[0];
      ChatCommand var9 = (ChatCommand)var7[1];
      if (var9 != null) {
         if (var9.havePermissions(var1, var2, var3)) {
            var6.add(GameColor.CYAN.getColorCode() + "Command \"" + var9.getName() + "\":");
            var6.add("Permission level: " + var9.permissionLevel.name.translate());
            var6.add(var9.getFullUsage(true));
            var6.add(var9.getFullAction());
         } else {
            var6.add(GameColor.RED.getColorCode() + "You do not have permissions for command: " + var9.getName());
         }
      } else {
         ArrayList var10 = new ArrayList(var1.commandsManager.getCommands());
         var10.removeIf((var3x) -> {
            return !var3x.shouldBeListed() || !var3x.havePermissions(var1, var2, var3);
         });
         Collections.sort(var10);
         byte var11 = 5;
         int var12 = var10.size() / var11;
         int var13 = GameMath.limit(var8 - 1, 0, var12) * var11;
         int var14 = Math.min(var10.size(), var13 + var11);
         var6.add(GameColor.CYAN.getColorCode() + "Commands page " + var8 + " of " + var12 + " (" + var1.getPermissionLevel().name.translate() + "):");

         for(int var15 = var13; var15 < var14; ++var15) {
            ChatCommand var16 = (ChatCommand)var10.get(var15);
            var6.add(var16.getFullHelp(true));
         }
      }

   }

   public boolean shouldBeListed() {
      return false;
   }
}
