package necesse.engine.commands.clientCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.ClientClientParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.level.maps.Level;

public class DebugClientClientCommand extends ModularChatCommand {
   public DebugClientClientCommand() {
      super("debugclient", "Prints debug information about a client", PermissionLevel.USER, false, new CmdParameter("player", new ClientClientParameterHandler(), false, new CmdParameter[0]));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      ClientClient var7 = (ClientClient)var4[0];
      ClientClient var8 = var1.getClient();
      Level var9 = var1.getLevel();
      var6.add("hasSpawned: " + var7.hasSpawned());
      var6.add("isVisible: " + var7.playerMob.isVisible());
      var6.add("isSamePlace: " + var7.isSamePlace(var9));
      var6.add("isDead: " + var7.isDead());
      var6.add("isRemoved: " + var7.playerMob.removed());
      var6.add("isDisposed: " + var7.playerMob.isDisposed());
      String var10 = (String)var9.entityManager.players.streamArea(var8.playerMob.x, var8.playerMob.y, (int)Math.sqrt((double)(var9.width * 2 * 32 + var9.height * 2 * 32))).filter((var1x) -> {
         return var1x == var7.playerMob;
      }).map((var0) -> {
         return "YES";
      }).findFirst().orElse("NO");
      var6.add("On level: " + var10);
   }

   public boolean shouldBeListed() {
      return false;
   }
}
