package necesse.engine.commands.serverCommands;

import java.awt.Point;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.IntParameterHandler;
import necesse.engine.commands.parameterHandlers.MultiParameterHandler;
import necesse.engine.commands.parameterHandlers.ParameterHandler;
import necesse.engine.commands.parameterHandlers.PresetStringParameterHandler;
import necesse.engine.commands.parameterHandlers.StringParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.friendly.human.HumanMob;

public class HealServerCommand extends ModularChatCommand {
   public HealServerCommand() {
      super("healmobs", "Heals mobs around you", PermissionLevel.ADMIN, true, new CmdParameter("health", new IntParameterHandler(-1)), new CmdParameter("range", new IntParameterHandler(-1), true, new CmdParameter[0]), new CmdParameter("filter", new MultiParameterHandler(new ParameterHandler[]{new PresetStringParameterHandler(new String[]{"hostile", "passive", "boss", "settler"}), new StringParameterHandler()}), true, new CmdParameter[0]));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      int var7 = (Integer)var4[0];
      int var8 = (Integer)var4[1];
      Object[] var9 = (Object[])var4[2];
      String var10 = (String)var9[0];
      String var11 = (String)var9[1];
      if (var3 == null) {
         var6.add("Command cannot be run from server");
      } else {
         Predicate var13 = (var0) -> {
            return true;
         };
         String var12;
         if (var10 != null) {
            switch (var10) {
               case "hostile":
                  var13 = (var0) -> {
                     return var0.isHostile;
                  };
                  break;
               case "passive":
                  var13 = (var0) -> {
                     return !var0.isHostile;
                  };
                  break;
               case "boss":
                  var13 = Mob::isBoss;
                  break;
               case "settler":
                  var13 = (var0) -> {
                     return var0 instanceof HumanMob && ((HumanMob)var0).isSettler();
                  };
            }

            var12 = var10;
         } else if (var11 != null && !var11.isEmpty()) {
            var13 = (var1x) -> {
               return var1x.getStringID().contains(var11);
            };
            var12 = var11;
         } else {
            var12 = null;
         }

         AtomicInteger var14 = new AtomicInteger(0);
         if (var8 < 0) {
            var3.getLevel().entityManager.mobs.stream().filter(var13).forEach((var2x) -> {
               var14.addAndGet(1);
               var2x.setHealth(var2x.getHealth() + var7);
            });
            var6.add("Healed " + var14.get() + (var12 == null ? "" : " " + var12) + " mobs for " + var7 + " health");
         } else {
            Point var16 = new Point(var3.playerMob.getX(), var3.playerMob.getY());
            var3.getLevel().entityManager.mobs.streamAreaTileRange(var16.x, var16.y, var8).filter((var2x) -> {
               return var2x.getDistance((float)var16.x, (float)var16.y) <= (float)(var8 * 32);
            }).filter(var13).forEach((var2x) -> {
               var14.addAndGet(1);
               var2x.setHealth(var2x.getHealth() + var7);
            });
            var6.add("Healed " + var14.get() + (var12 == null ? "" : " " + var12) + " mobs " + var8 + " tiles around you for " + var7 + " health");
         }

      }
   }
}
