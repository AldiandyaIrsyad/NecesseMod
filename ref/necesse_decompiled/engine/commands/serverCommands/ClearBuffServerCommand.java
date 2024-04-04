package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.BuffParameterHandler;
import necesse.engine.commands.parameterHandlers.ServerClientParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

public class ClearBuffServerCommand extends ModularChatCommand {
   public ClearBuffServerCommand() {
      super("clearbuff", "Clears buff from player", PermissionLevel.ADMIN, true, new CmdParameter("player", new ServerClientParameterHandler(true, false), true, new CmdParameter[0]), new CmdParameter("buff", new BuffParameterHandler(false)));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      ServerClient var7 = (ServerClient)var4[0];
      Buff var8 = (Buff)var4[1];
      if (var7 == null) {
         var6.add("Must specify <player>");
      } else {
         if (var7.playerMob.buffManager.hasBuff(var8.getID())) {
            var7.playerMob.buffManager.removeBuff(var8.getID(), true);
            var6.add("Cleared " + var8.getDisplayName() + " from " + var7.getName());
         } else {
            var6.add(var7.getName() + " does not have " + var8.getDisplayName());
         }

      }
   }
}
