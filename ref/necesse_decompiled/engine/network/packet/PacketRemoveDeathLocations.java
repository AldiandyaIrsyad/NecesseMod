package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PacketRemoveDeathLocations extends Packet {
   public final int islandX;
   public final int islandY;

   public PacketRemoveDeathLocations(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.islandX = var2.getNextInt();
      this.islandY = var2.getNextInt();
   }

   public PacketRemoveDeathLocations(int var1, int var2) {
      this.islandX = var1;
      this.islandY = var2;
      PacketWriter var3 = new PacketWriter(this);
      var3.putNextInt(var1);
      var3.putNextInt(var2);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      var3.removeDeathLocations(this.islandX, this.islandY);
   }
}
