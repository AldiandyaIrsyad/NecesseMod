package necesse.engine.network.packet;

import necesse.engine.achievements.AchievementManager;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.playerStats.PlayerStats;

public class PacketClientStats extends Packet {
   public final PlayerStats stats;
   public final AchievementManager achievements;

   public PacketClientStats(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.stats = new PlayerStats(false, PlayerStats.Mode.READ_AND_WRITE);
      this.stats.applyContentPacket(var2);
      this.achievements = new AchievementManager(this.stats);
      this.achievements.applyContentPacket(var2);
   }

   public PacketClientStats(PlayerStats var1, AchievementManager var2) {
      this.stats = var1;
      this.achievements = var2;
      PacketWriter var3 = new PacketWriter(this);
      var1.setupContentPacket(var3);
      var2.setupContentPacket(var3);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      var3.applyClientStatsPacket(this);
   }
}
