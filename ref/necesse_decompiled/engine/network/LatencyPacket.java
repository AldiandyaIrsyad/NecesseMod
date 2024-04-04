package necesse.engine.network;

public class LatencyPacket {
   public NetworkPacket packet;
   private long time;
   private int latency;

   public LatencyPacket(NetworkPacket var1, int var2) {
      this.packet = var1;
      this.latency = var2;
      this.time = System.currentTimeMillis();
   }

   public long getTime() {
      return this.time;
   }

   public int getLatency() {
      return this.latency;
   }

   public long getReadyTime() {
      return this.time + (long)this.latency;
   }

   public boolean isReady() {
      return this.getReadyTime() <= System.currentTimeMillis();
   }
}
