package necesse.engine.commands.clientCommands;

import necesse.engine.Settings;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.PresetStringParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class ObjectHitboxesClientCommand extends ModularChatCommand {
   public ObjectHitboxesClientCommand() {
      super("objecthitboxes", "Changes object hitbox mode", PermissionLevel.USER, false, new CmdParameter("mode", new PresetStringParameterHandler(new String[]{"tile", "custom"}), false, new CmdParameter[0]));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      String var7 = (String)var4[0];
      if (var7.equals("tile")) {
         if (Settings.useTileObjectHitboxes) {
            var6.add("Object hitbox mode already set to \"" + var7 + "\"");
         } else {
            Settings.useTileObjectHitboxes = true;
            var6.add("Object hitbox mode changed to \"" + var7 + "\"");
            Settings.saveClientSettings();
         }
      } else if (var7.equals("custom")) {
         if (!Settings.useTileObjectHitboxes) {
            var6.add("Object hitbox mode already set to \"" + var7 + "\"");
         } else {
            Settings.useTileObjectHitboxes = false;
            var6.add("Object hitbox mode changed to \"" + var7 + "\"");
            Settings.saveClientSettings();
         }
      }

   }
}
