package necesse.engine.commands.serverCommands.setupCommand;

import java.awt.Point;
import necesse.engine.commands.CommandLog;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.objectEntity.PortalObjectEntity;
import necesse.level.maps.Level;

public abstract class FindArenaWorldSetup implements WorldSetup {
   public final int dimension;
   public final String[] biomeIDs;

   public FindArenaWorldSetup(int var1, String... var2) {
      this.dimension = var1;
      this.biomeIDs = var2;
   }

   public void apply(Server var1, ServerClient var2, boolean var3, CommandLog var4) {
      Point var5 = WorldSetup.findClosestBiome(var2, this.dimension, var3, this.biomeIDs);
      if (var5 == null) {
         var4.add("Could not find a close biome");
      } else {
         var2.changeIsland(var5.x, var5.y, this.dimension, (var4x) -> {
            Point var5 = this.findArenaSpawnTile(var1, var2, var4x);
            if (var5 == null) {
               var4.add("Could not find arena location");
               return null;
            } else if (var4x.getObjectID(var5.x, var5.y) == 0) {
               return new Point(var5.x * 32 + 16, var5.y * 32 + 16);
            } else {
               Point var6 = PortalObjectEntity.getTeleportDestinationAroundObject(var4x, var2.playerMob, var5.x, var5.y, true);
               if (var6 == null) {
                  var6 = new Point(var5.x * 32 + 16, var5.y * 32 + 16);
               }

               return var6;
            }
         }, true);
      }
   }

   public abstract Point findArenaSpawnTile(Server var1, ServerClient var2, Level var3);
}
