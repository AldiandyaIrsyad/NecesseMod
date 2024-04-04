package necesse.engine.network.packet;

import necesse.engine.GlobalData;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.playerStats.PlayerStats;

public class PacketClientStatsUpdate extends Packet {
   public PacketClientStatsUpdate(byte[] var1) {
      super(var1);
   }

   public PacketClientStatsUpdate(PlayerStats var1) {
      var1.setupDirtyPacket(new PacketWriter(this));
   }

   public void processClient(NetworkPacket var1, Client var2) {
      GlobalData.stats().applyDirtyPacket(new PacketReader(this));
   }
}
