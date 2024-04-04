package necesse.engine.commands.serverCommands;

import java.util.Iterator;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.BoolParameterHandler;
import necesse.engine.commands.parameterHandlers.StringParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Mob;
import necesse.level.maps.Level;

public class ClearMobsServerCommand extends ModularChatCommand {
   public ClearMobsServerCommand() {
      super("clearmobs", "Clears all mobs or a specific type on your level or on all loaded levels", PermissionLevel.ADMIN, true, new CmdParameter("global", new BoolParameterHandler(), true, new CmdParameter[]{new CmdParameter("type", new StringParameterHandler(), true, new CmdParameter[0])}));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      int var7 = 0;
      int var8 = 0;
      boolean var9 = (Boolean)var4[0];
      String var10 = (String)var4[1];
      Iterator var11;
      Level var12;
      if (var10 != null) {
         if (!var9 && var3 != null) {
            ++var8;
            var7 += this.removeMobType(var2.world.getLevel(var3), var10);
         } else {
            for(var11 = var2.world.levelManager.getLoadedLevels().iterator(); var11.hasNext(); var7 += this.removeMobType(var12, var10)) {
               var12 = (Level)var11.next();
               ++var8;
            }
         }
      } else if (!var9 && var3 != null) {
         ++var8;
         var7 += this.removeHostileMobs(var2.world.getLevel(var3));
      } else {
         for(var11 = var2.world.levelManager.getLoadedLevels().iterator(); var11.hasNext(); var7 += this.removeHostileMobs(var12)) {
            var12 = (Level)var11.next();
            ++var8;
         }
      }

      var6.add("Cleared " + var7 + " mobs on " + var8 + " levels.");
   }

   private int removeMobType(Level var1, String var2) {
      int var3 = 0;
      Iterator var4 = var1.entityManager.mobs.iterator();

      while(var4.hasNext()) {
         Mob var5 = (Mob)var4.next();
         if (var5.getStringID().contains(var2.toLowerCase())) {
            var5.remove();
            ++var3;
         }
      }

      return var3;
   }

   private int removeHostileMobs(Level var1) {
      int var2 = 0;
      Iterator var3 = var1.entityManager.mobs.iterator();

      while(var3.hasNext()) {
         Mob var4 = (Mob)var3.next();
         if (var4.isHostile) {
            var4.remove();
            ++var2;
         }
      }

      return var2;
   }
}
