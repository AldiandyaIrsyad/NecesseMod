package necesse.engine.commands.serverCommands;

import java.util.Iterator;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.RestStringParameterHandler;
import necesse.engine.commands.parameterHandlers.ServerClientParameterHandler;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketPlayerAppearance;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameUtils;

public class ChangeNameServerCommand extends ModularChatCommand {
   public ChangeNameServerCommand() {
      super("changename", "Changes the name of a player", PermissionLevel.ADMIN, false, new CmdParameter("player", new ServerClientParameterHandler()), new CmdParameter("name", new RestStringParameterHandler()));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      ServerClient var7 = (ServerClient)var4[0];
      String var8 = ((String)var4[1]).trim();
      GameMessage var9 = GameUtils.isValidPlayerName(var8);
      if (var9 != null) {
         var6.add(var8 + " is an invalid name");
      } else {
         Iterator var10 = var2.usedNames.values().iterator();

         while(var10.hasNext()) {
            String var11 = (String)var10.next();
            if (var11.equalsIgnoreCase(var8)) {
               var6.add(var8 + " is already in use");
               return;
            }
         }

         String var12 = var7.getName();
         var2.usedNames.put(var7.authentication, var8);
         var7.playerMob.playerName = var8;
         var2.network.sendToAllClients(new PacketPlayerAppearance(var7));
         var6.add("Changed " + var12 + "'s name to " + var7.getName());
         if (var3 != var7) {
            var7.sendChatMessage("Changed your name to " + var7.getName());
         }
      }

   }
}
