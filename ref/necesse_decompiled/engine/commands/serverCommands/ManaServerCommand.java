package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.IntParameterHandler;
import necesse.engine.commands.parameterHandlers.ServerClientParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketMobMana;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class ManaServerCommand extends ModularChatCommand {
   public ManaServerCommand() {
      super("mana", "Sets the mana of player", PermissionLevel.ADMIN, true, new CmdParameter("player", new ServerClientParameterHandler(true, false), true, new CmdParameter[0]), new CmdParameter("mana", new IntParameterHandler()));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      ServerClient var7 = (ServerClient)var4[0];
      int var8 = (Integer)var4[1];
      if (var7 == null) {
         var6.add("Must specify <player>");
      } else {
         var7.playerMob.setMana((float)var8);
         var2.network.sendToClientsAt(new PacketMobMana(var7.playerMob, true), (ServerClient)var7);
         var6.add("Set " + var7.getName() + " mana to " + var7.playerMob.getMana());
      }
   }
}
