package necesse.engine.commands.serverCommands;

import java.awt.Point;
import java.util.Comparator;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.MultiParameterHandler;
import necesse.engine.commands.parameterHandlers.ParameterHandler;
import necesse.engine.commands.parameterHandlers.PresetStringParameterHandler;
import necesse.engine.commands.parameterHandlers.ServerClientParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketPlayerMovement;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.WorldDeathLocation;
import necesse.level.gameObject.RespawnObject;

public class TeleportServerCommand extends ModularChatCommand {
   public TeleportServerCommand(String var1) {
      super(var1, "Teleports player1 to player2 or other location", PermissionLevel.ADMIN, true, new CmdParameter("player1", new ServerClientParameterHandler(true, false), true, new CmdParameter[0]), new CmdParameter("player2/home/death/spawn", new MultiParameterHandler(new ParameterHandler[]{new ServerClientParameterHandler(), new PresetStringParameterHandler(new String[]{"spawn", "home", "death"})})));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      ServerClient var7 = (ServerClient)var4[0];
      Object[] var8 = (Object[])var4[1];
      TeleportPos var9 = this.clientPos((ServerClient)var8[0]);
      String var10 = (String)var8[1];
      if (var7 == null) {
         var6.add("Missing player to teleport");
      } else {
         if (var9 == null) {
            switch (var10) {
               case "home":
                  var9 = this.spawnPos(var7, var2);
                  break;
               case "death":
                  var9 = this.deathPos(var7);
                  if (var9 == null) {
                     var6.add("Could not find death location");
                     return;
                  }
                  break;
               case "spawn":
                  var9 = new TeleportPos((ServerClient)null, "spawn", var2.world.worldEntity.spawnLevelIdentifier, (float)(var2.world.worldEntity.spawnTile.x * 32 + 16), (float)(var2.world.worldEntity.spawnTile.y * 32 + 16));
                  break;
               default:
                  var6.add("Could not find destination");
                  return;
            }
         }

         if (var7 == var9.client) {
            var6.add("Cannot teleport player to self");
         } else {
            var7.playerMob.dx = 0.0F;
            var7.playerMob.dy = 0.0F;
            var6.add("Teleported " + var7.getName() + " to " + var9.name);
            if (var7.isSamePlace(var9.levelIdentifier)) {
               var7.playerMob.setPos(var9.levelX, var9.levelY, true);
               var2.network.sendToClientsAt(new PacketPlayerMovement(var7, true), (ServerClient)var7);
            } else {
               Point var11 = new Point((int)var9.levelX, (int)var9.levelY);
               var7.changeLevel(var9.levelIdentifier, (var1x) -> {
                  return var11;
               }, true);
            }

         }
      }
   }

   private TeleportPos clientPos(ServerClient var1) {
      return var1 == null ? null : new TeleportPos(var1, var1.getName(), var1.getLevelIdentifier(), (float)var1.playerMob.getX(), (float)var1.playerMob.getY());
   }

   private TeleportPos spawnPos(ServerClient var1, Server var2) {
      var1.validateSpawnPoint(true);
      Point var3 = new Point(16, 16);
      if (!var1.isDefaultSpawnPoint()) {
         var3 = RespawnObject.calculateSpawnOffset(var2.world.getLevel(var1.spawnLevelIdentifier), var1.spawnTile.x, var1.spawnTile.y, var1);
      }

      return new TeleportPos((ServerClient)null, "spawn", var1.spawnLevelIdentifier, (float)(var1.spawnTile.x * 32 + var3.x), (float)(var1.spawnTile.y * 32 + var3.y));
   }

   private TeleportPos deathPos(ServerClient var1) {
      WorldDeathLocation var2 = (WorldDeathLocation)var1.streamDeathLocations().max(Comparator.comparingInt((var0) -> {
         return var0.deathTime;
      })).orElse((Object)null);
      return var2 == null ? null : new TeleportPos((ServerClient)null, "recent death", var2.levelIdentifier, (float)var2.x, (float)var2.y);
   }

   private static class TeleportPos {
      public final ServerClient client;
      public final String name;
      public final LevelIdentifier levelIdentifier;
      public final float levelX;
      public final float levelY;

      private TeleportPos(ServerClient var1, String var2, LevelIdentifier var3, float var4, float var5) {
         this.client = var1;
         this.name = var2;
         this.levelIdentifier = var3;
         this.levelX = var4;
         this.levelY = var5;
      }

      // $FF: synthetic method
      TeleportPos(ServerClient var1, String var2, LevelIdentifier var3, float var4, float var5, Object var6) {
         this(var1, var2, var3, var4, var5);
      }
   }
}
