package necesse.engine.network.packet;

import necesse.engine.GameLog;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.util.LevelIdentifier;
import necesse.level.maps.Level;

public class PacketLevelGNDData extends Packet {
   public final LevelIdentifier levelIdentifier;
   public final Packet content;

   public PacketLevelGNDData(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.levelIdentifier = new LevelIdentifier(var2);
      this.content = var2.getNextContentPacket();
   }

   public PacketLevelGNDData(Level var1) {
      this.levelIdentifier = var1.getIdentifier();
      this.content = new Packet();
      var1.gndData.writePacket(new PacketWriter(this.content));
      PacketWriter var2 = new PacketWriter(this);
      this.levelIdentifier.writePacket(var2);
      var2.putNextContentPacket(this.content);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null) {
         if (var2.getLevel().getIdentifier().equals(this.levelIdentifier)) {
            var2.getLevel().gndData.readPacket(new PacketReader(this.content));
         } else {
            GameLog.warn.println("Received level GND data packet for wrong level identifier: " + this.levelIdentifier + ", my level: " + var2.getLevel().getIdentifier());
         }

      }
   }
}
