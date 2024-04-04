package necesse.engine.commands.serverCommands;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;
import necesse.engine.GameDeathPenalty;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.EnumParameterHandler;
import necesse.engine.commands.parameterHandlers.MultiParameterHandler;
import necesse.engine.commands.parameterHandlers.ParameterHandler;
import necesse.engine.commands.parameterHandlers.PresetStringParameterHandler;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class DeathPenaltyServerCommand extends ModularChatCommand {
   public DeathPenaltyServerCommand() {
      super("deathpenalty", "Changes death penalty setting", PermissionLevel.ADMIN, false, new CmdParameter("list/penalty", new MultiParameterHandler(new ParameterHandler[]{new PresetStringParameterHandler(new String[]{"list"}), new EnumParameterHandler(GameDeathPenalty.values())}), false, new CmdParameter[0]));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      Object[] var7 = (Object[])var4[0];
      String var8 = (String)var7[0];
      GameDeathPenalty var9 = (GameDeathPenalty)var7[1];
      if (var8 != null) {
         var6.add("List of death penalties: " + (String)Arrays.stream(GameDeathPenalty.values()).map((var0) -> {
            return var0.name().toLowerCase(Locale.ENGLISH);
         }).collect(Collectors.joining(", ")));
      } else {
         if (var9 == null) {
            throw new NullPointerException("This should never happen");
         }

         GameMessageBuilder var10;
         if (var2.world.settings.deathPenalty == var9) {
            var10 = new GameMessageBuilder();
            var10.append("Death penalty is already ");
            var10.append(var9.displayName);
            var6.add((GameMessage)var10);
         } else {
            var2.world.settings.deathPenalty = var9;
            var2.world.settings.saveSettings();
            var2.world.settings.sendSettingsPacket();
            var10 = new GameMessageBuilder();
            var10.append("Changed death penalty to ");
            var10.append(var9.displayName);
            var6.add((GameMessage)var10);
         }
      }

   }
}
