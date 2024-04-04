package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.LevelIdentifier;

public class PacketRemoveDeathLocation extends Packet {
   public final LevelIdentifier levelIdentifier;
   public final int x;
   public final int y;

   public PacketRemoveDeathLocation(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.levelIdentifier = new LevelIdentifier(var2);
      this.x = var2.getNextInt();
      this.y = var2.getNextInt();
   }

   public PacketRemoveDeathLocation(LevelIdentifier var1, int var2, int var3) {
      this.levelIdentifier = var1;
      this.x = var2;
      this.y = var3;
      PacketWriter var4 = new PacketWriter(this);
      var1.writePacket(var4);
      var4.putNextInt(var2);
      var4.putNextInt(var3);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      var3.removeDeathLocation(this.levelIdentifier, this.x, this.y);
   }
}
