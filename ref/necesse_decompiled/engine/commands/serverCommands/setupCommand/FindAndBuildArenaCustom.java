package necesse.engine.commands.serverCommands.setupCommand;

import java.awt.Point;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameRandom;
import necesse.level.maps.Level;

public abstract class FindAndBuildArenaCustom extends FindArenaWorldSetup {
   public final int size;

   public FindAndBuildArenaCustom(int var1, int var2, String... var3) {
      super(var2, var3);
      this.size = var1;
   }

   public Point findArenaSpawnTile(Server var1, ServerClient var2, Level var3) {
      for(int var4 = 0; var4 < 100; ++var4) {
         int var5 = GameRandom.globalRandom.getIntBetween(this.size, var3.width - 1 - this.size * 2);
         int var6 = GameRandom.globalRandom.getIntBetween(this.size, var3.height - 1 - this.size * 2);
         if (!var3.getTile(var5, var6).isLiquid) {
            this.buildArena(var1, var2, var3, var5, var6, this.size);
            WorldSetup.updateClientsLevel(var3, var5, var6, this.size + 15);
            return new Point(var5, var6);
         }
      }

      return null;
   }

   public abstract void buildArena(Server var1, ServerClient var2, Level var3, int var4, int var5, int var6);
}
