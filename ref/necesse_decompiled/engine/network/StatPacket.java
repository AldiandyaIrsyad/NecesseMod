package necesse.engine.network;

import necesse.engine.util.GameUtils;

public class StatPacket {
   public final int type;
   public int amount;
   public int bytes;

   public StatPacket(int var1) {
      this.type = var1;
      this.amount = 0;
      this.bytes = 0;
   }

   public String getBytes() {
      return GameUtils.getByteString((long)this.bytes);
   }
}
