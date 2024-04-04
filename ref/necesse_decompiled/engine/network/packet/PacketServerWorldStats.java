package necesse.engine.network.packet;

import necesse.engine.GlobalData;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.state.MainGame;

public class PacketServerWorldStats extends Packet {
   public final PlayerStats stats;

   public PacketServerWorldStats(byte[] var1) {
      super(var1);
      this.stats = new PlayerStats(false, PlayerStats.Mode.READ_ONLY);
      this.stats.applyContentPacket(new PacketReader(this));
   }

   public PacketServerWorldStats(PlayerStats var1) {
      this.stats = var1;
      var1.setupContentPacket(new PacketWriter(this));
   }

   public PacketServerWorldStats() {
      this.stats = null;
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      var3.sendPacket(new PacketServerWorldStats(var2.world.worldEntity.worldStats));
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (GlobalData.getCurrentState() instanceof MainGame) {
         ((MainGame)GlobalData.getCurrentState()).formManager.pauseMenu.applyServerWorldStatsPacket(this);
      }

   }
}
