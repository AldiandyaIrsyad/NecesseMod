package necesse.engine.network.client.loading;

import necesse.engine.network.client.Client;

public abstract class ClientLoadingUtil {
   public final ClientLoading loading;
   public final Client client;
   private long waitTime;

   public ClientLoadingUtil(ClientLoading var1) {
      this.loading = var1;
      this.client = var1.client;
   }

   protected final void setWait(int var1) {
      this.waitTime = System.currentTimeMillis() + (long)var1;
   }

   protected final boolean isWaiting() {
      return System.currentTimeMillis() < this.waitTime;
   }
}
