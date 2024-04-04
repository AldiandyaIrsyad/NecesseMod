package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Mob;

public class PacketPlayerBuffs extends Packet {
   public final int slot;
   public final Packet content;

   public PacketPlayerBuffs(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.slot = var2.getNextByteUnsigned();
      this.content = var2.getNextContentPacket();
   }

   public PacketPlayerBuffs(ServerClient var1) {
      this.slot = var1.slot;
      this.content = new Packet();
      var1.playerMob.buffManager.setupContentPacket(new PacketWriter(this.content));
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextByteUnsigned(this.slot);
      var2.putNextContentPacket(this.content);
   }

   public void apply(Mob var1) {
      var1.buffManager.applyContentPacket(new PacketReader(this.content));
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getClient(this.slot) == null) {
         var2.network.sendPacket(new PacketRequestPlayerData(this.slot));
      } else {
         var2.getClient(this.slot).applyPacketPlayerBuffs(this);
      }
   }
}
