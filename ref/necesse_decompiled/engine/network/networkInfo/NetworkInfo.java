package necesse.engine.network.networkInfo;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Stream;
import necesse.engine.network.server.ServerClient;

public abstract class NetworkInfo {
   public NetworkInfo() {
   }

   public ServerClient getClient(Stream<ServerClient> var1) {
      return (ServerClient)var1.filter(Objects::nonNull).filter((var1x) -> {
         return Objects.equals(var1x.networkInfo, this);
      }).findFirst().orElse((Object)null);
   }

   public abstract void send(byte[] var1) throws IOException;

   public abstract String getDisplayName();

   public abstract void closeConnection();

   public abstract void resetConnection();

   public abstract boolean equals(Object var1);

   public abstract int hashCode();
}
