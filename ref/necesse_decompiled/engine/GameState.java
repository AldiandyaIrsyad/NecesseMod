package necesse.engine;

import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;

public interface GameState {
   boolean isClient();

   Client getClient();

   boolean isServer();

   Server getServer();

   default String getHostString() {
      boolean var1 = this.isServer();
      boolean var2 = this.isClient();
      if (var1 && var2) {
         return "BOTH";
      } else if (var1) {
         return "SERVER";
      } else {
         return var2 ? "CLIENT" : "NONE";
      }
   }
}
