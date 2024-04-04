package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PacketRequestPlayerData extends Packet {
   public final int slot;

   public PacketRequestPlayerData(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.slot = var2.getNextByteUnsigned();
   }

   public PacketRequestPlayerData(int var1) {
      this.slot = var1;
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextByteUnsigned(var1);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      ServerClient var4 = var2.getClient(this.slot);
      if (var4 == null) {
         var2.network.sendPacket(new PacketDisconnect(this.slot, PacketDisconnect.Code.MISSING_CLIENT), (ServerClient)var3);
      } else {
         if (this.slot == var3.slot) {
            var3.requestSelf();
            var3.sendPacket(new PacketAdventurePartyUpdate(var3));
         }

         var2.network.sendPacket(new PacketPlayerGeneral(var4), (ServerClient)var3);
      }

   }
}
