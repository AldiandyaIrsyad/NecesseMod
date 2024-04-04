package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.BuffParameterHandler;
import necesse.engine.commands.parameterHandlers.FloatParameterHandler;
import necesse.engine.commands.parameterHandlers.ServerClientParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

public class BuffServerCommand extends ModularChatCommand {
   public BuffServerCommand() {
      super("buff", "Gives buff to player", PermissionLevel.ADMIN, true, new CmdParameter("player", new ServerClientParameterHandler(true, false), true, new CmdParameter[0]), new CmdParameter("buff", new BuffParameterHandler(false)), new CmdParameter("seconds", new FloatParameterHandler(1.0F), true, new CmdParameter[0]));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      ServerClient var7 = (ServerClient)var4[0];
      Buff var8 = (Buff)var4[1];
      float var9 = (Float)var4[2];
      if (var7 == null) {
         var6.add("Must specify <player>");
      } else {
         var7.playerMob.buffManager.addBuff(new ActiveBuff(var8, var7.playerMob, var9, (Attacker)null), true);
         var6.add("Gave " + var9 + " seconds of " + var8.getDisplayName() + " to " + var7.getName());
      }
   }
}
