package necesse.engine.events;

import necesse.engine.network.server.ServerClient;

public class ServerClientDisconnectEvent extends GameEvent {
   public final ServerClient client;

   public ServerClientDisconnectEvent(ServerClient var1) {
      this.client = var1;
   }
}
