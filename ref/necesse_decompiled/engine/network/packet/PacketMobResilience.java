package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;

public class PacketMobResilience extends Packet {
   public final int mobUniqueID;
   public final boolean isFull;
   private final PacketReader reader;

   public PacketMobResilience(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.mobUniqueID = var2.getNextInt();
      this.isFull = var2.getNextBoolean();
      this.reader = var2;
   }

   public PacketMobResilience(Mob var1, boolean var2) {
      this.mobUniqueID = var1.getUniqueID();
      this.isFull = var2;
      PacketWriter var3 = new PacketWriter(this);
      var3.putNextInt(this.mobUniqueID);
      var3.putNextBoolean(var2);
      this.reader = new PacketReader(var3);
      var1.setupResiliencePacket(var3, var2);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null) {
         Mob var3 = GameUtils.getLevelMob(this.mobUniqueID, var2.getLevel());
         if (var3 == null) {
            var2.network.sendPacket(new PacketRequestMobData(this.mobUniqueID));
         } else {
            var3.applyResiliencePacket(new PacketReader(this.reader), this.isFull);
         }

      }
   }
}
