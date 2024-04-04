package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.playerStats.PlayerStats;

public class PacketPlayerStats extends Packet {
   public final PlayerStats stats;

   public PacketPlayerStats(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.stats = new PlayerStats(false, PlayerStats.Mode.READ_ONLY);
      this.stats.applyContentPacket(var2);
   }

   public PacketPlayerStats(PlayerStats var1) {
      this.stats = var1;
      PacketWriter var2 = new PacketWriter(this);
      var1.setupContentPacket(var2);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.characterStats == null) {
         var2.characterStats = this.stats;
      }

      var2.loading.statsPhase.submitStatsPacket(this);
   }
}
