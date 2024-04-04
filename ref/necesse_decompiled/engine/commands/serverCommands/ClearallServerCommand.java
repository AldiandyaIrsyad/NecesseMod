package necesse.engine.commands.serverCommands;

import java.util.Iterator;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.BoolParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.Entity;
import necesse.level.maps.Level;

public class ClearallServerCommand extends ModularChatCommand {
   public ClearallServerCommand() {
      super("clearall", "Clears all entities", PermissionLevel.ADMIN, true, new CmdParameter("global", new BoolParameterHandler(), true, new CmdParameter[0]));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      int var7 = 0;
      int var8 = 0;
      int var9 = 0;
      int var10 = 0;
      boolean var11 = (Boolean)var4[0];
      Level var13;
      if (!var11 && var3 != null) {
         var10 = 1;
         var7 += this.removeEntities(var2.world.getLevel(var3).entityManager.mobs);
         var8 += this.removeEntities(var2.world.getLevel(var3).entityManager.pickups);
         var9 += this.removeEntities(var2.world.getLevel(var3).entityManager.projectiles);
      } else {
         for(Iterator var12 = var2.world.levelManager.getLoadedLevels().iterator(); var12.hasNext(); var9 += this.removeEntities(var13.entityManager.projectiles)) {
            var13 = (Level)var12.next();
            ++var10;
            var7 += this.removeEntities(var13.entityManager.mobs);
            var8 += this.removeEntities(var13.entityManager.pickups);
         }
      }

      var6.add("Cleared " + var7 + " mobs, " + var8 + " items and " + var9 + " projectiles on " + var10 + " levels.");
   }

   private int removeEntities(Iterable<? extends Entity> var1) {
      int var2 = 0;

      for(Iterator var3 = var1.iterator(); var3.hasNext(); ++var2) {
         Entity var4 = (Entity)var3.next();
         var4.remove();
      }

      return var2;
   }
}
