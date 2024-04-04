package necesse.engine.commands.serverCommands.setupCommand;

import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameRandom;
import necesse.level.maps.Level;

public class FindBiomeAndBuildArena extends FindAndBuildArenaCustom {
   public FindBiomeAndBuildArena(int var1, int var2, String... var3) {
      super(var1, var2, var3);
   }

   public void buildArena(Server var1, ServerClient var2, Level var3, int var4, int var5, int var6) {
      WorldSetup.buildRandomArena(var3, GameRandom.globalRandom, var4, var5, var6 - 5, var6 + 5);
   }
}
