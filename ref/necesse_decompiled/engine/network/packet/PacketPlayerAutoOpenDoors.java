package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.PlayerMob;

public class PacketPlayerAutoOpenDoors extends Packet {
   public final int slot;
   public final boolean autoOpenDoors;

   public PacketPlayerAutoOpenDoors(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.slot = var2.getNextByteUnsigned();
      this.autoOpenDoors = var2.getNextBoolean();
   }

   public PacketPlayerAutoOpenDoors(int var1, boolean var2) {
      this.slot = var1;
      this.autoOpenDoors = var2;
      PacketWriter var3 = new PacketWriter(this);
      var3.putNextByteUnsigned(var1);
      var3.putNextBoolean(var2);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      PlayerMob var3 = var2.getPlayer(this.slot);
      if (var3 != null) {
         var3.autoOpenDoors = this.autoOpenDoors;
      }

   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      if (var3.playerMob != null && var3.slot == this.slot) {
         var3.playerMob.autoOpenDoors = this.autoOpenDoors;
         var2.network.sendToAllClientsExcept(this, var3);
      }

   }
}
