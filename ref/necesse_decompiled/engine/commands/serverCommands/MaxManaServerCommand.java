package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.IntParameterHandler;
import necesse.engine.commands.parameterHandlers.ServerClientParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketPlayerGeneral;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class MaxManaServerCommand extends ModularChatCommand {
   public MaxManaServerCommand() {
      super("maxmana", "Sets the max mana of player", PermissionLevel.ADMIN, true, new CmdParameter("player", new ServerClientParameterHandler(true, false), true, new CmdParameter[0]), new CmdParameter("mana", new IntParameterHandler()));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      ServerClient var7 = (ServerClient)var4[0];
      int var8 = (Integer)var4[1];
      if (var7 == null) {
         var6.add("Must specify <player>");
      } else {
         var7.playerMob.setMaxMana(var8);
         var2.network.sendToAllClients(new PacketPlayerGeneral(var7));
         var6.add("Set " + var7.getName() + " max mana to " + var7.playerMob.getMaxManaFlat());
      }
   }
}
