package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.entity.mobs.PlayerMob;

public class PacketPlayerStopAttack extends Packet {
   public int slot;

   public PacketPlayerStopAttack(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.slot = var2.getNextByteUnsigned();
   }

   public PacketPlayerStopAttack(int var1) {
      this.slot = var1;
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextByteUnsigned(var1);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      PlayerMob var3 = var2.getPlayer(this.slot);
      if (var3 != null && var3.getLevel() != null) {
         var3.forceEndAttack();
      }

   }
}
