package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.util.LevelDeathLocation;

public class PacketAddDeathLocation extends Packet {
   public final LevelDeathLocation location;

   public PacketAddDeathLocation(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      int var3 = var2.getNextInt();
      int var4 = var2.getNextInt();
      int var5 = var2.getNextInt();
      this.location = new LevelDeathLocation(var3, var4, var5);
   }

   public PacketAddDeathLocation(LevelDeathLocation var1) {
      this.location = var1;
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextInt(var1.secondsSince);
      var2.putNextInt(var1.x);
      var2.putNextInt(var1.y);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null) {
         var2.levelManager.addDeathLocation(this.location);
      }
   }
}
