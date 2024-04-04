package necesse.engine.network.client;

@FunctionalInterface
public interface ClientDisconnectEvent {
   void apply(Client var1);
}
