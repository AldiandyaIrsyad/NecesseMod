package necesse.engine.network;

public class SizePacket {
   public final int type;
   public final int byteSize;
   private long getTime;

   public SizePacket(int var1, int var2) {
      this.type = var1;
      this.byteSize = var2;
      this.getTime = System.currentTimeMillis();
   }

   public boolean isExpired() {
      return this.getTime + (long)(PacketManager.networkTrackingTime * 1000) <= System.currentTimeMillis();
   }
}
