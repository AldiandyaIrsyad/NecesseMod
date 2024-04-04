package necesse.engine.events;

import necesse.engine.network.server.ServerClient;

public class ServerClientConnectedEvent extends GameEvent {
   public final ServerClient client;

   public ServerClientConnectedEvent(ServerClient var1) {
      this.client = var1;
   }
}
