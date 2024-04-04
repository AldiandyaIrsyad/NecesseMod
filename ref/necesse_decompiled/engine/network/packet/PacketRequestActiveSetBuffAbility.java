package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PacketRequestActiveSetBuffAbility extends Packet {
   public final int slot;

   public PacketRequestActiveSetBuffAbility(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.slot = var2.getNextByteUnsigned();
   }

   public PacketRequestActiveSetBuffAbility(int var1) {
      this.slot = var1;
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextByteUnsigned(var1);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      ServerClient var4 = var2.getClient(this.slot);
      if (var4 != null) {
         if (var4.playerMob != null) {
            var4.playerMob.sendActiveSetBuffAbilityState(var2, var3);
         }
      } else {
         var3.sendPacket(new PacketDisconnect(this.slot, PacketDisconnect.Code.MISSING_CLIENT));
      }

   }
}
