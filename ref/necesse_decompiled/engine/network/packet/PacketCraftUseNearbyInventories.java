package necesse.engine.network.packet;

import necesse.engine.Settings;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PacketCraftUseNearbyInventories extends Packet {
   public final boolean useNearby;

   public PacketCraftUseNearbyInventories(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.useNearby = var2.getNextBoolean();
   }

   public PacketCraftUseNearbyInventories() {
      this.useNearby = (Boolean)Settings.craftingUseNearby.get();
      PacketWriter var1 = new PacketWriter(this);
      var1.putNextBoolean(this.useNearby);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      var3.craftingUsesNearbyInventories = this.useNearby;
   }
}
