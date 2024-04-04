package necesse.engine.commands.serverCommands;

import java.util.Iterator;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.level.maps.Level;

public class LevelsServerCommand extends ModularChatCommand {
   public LevelsServerCommand() {
      super("levels", "Lists currently loaded levels", PermissionLevel.MODERATOR, false);
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      var6.add("Loaded levels: " + var2.world.levelManager.getLoadedLevelsNum());
      int var7 = 0;
      Iterator var8 = var2.world.levelManager.getLoadedLevels().iterator();

      while(var8.hasNext()) {
         Level var9 = (Level)var8.next();
         ++var7;
         int var10 = var9.entityManager.mobs.count();
         int var11 = var9.entityManager.pickups.count();
         int var12 = var9.entityManager.projectiles.count();
         int var13 = var9.entityManager.objectEntities.count();
         long var14 = var2.streamClients().filter((var1x) -> {
            return var1x.isSamePlace(var9);
         }).count();
         var6.add("Level " + var7 + ": " + var9.getIdentifier() + ". Cave: " + var9.isCave + ", Mobs: " + var10 + ", Pickups: " + var11 + ", Projectiles: " + var12 + ", ObjectEnts: " + var13 + ", Players: " + var14);
      }

   }
}
