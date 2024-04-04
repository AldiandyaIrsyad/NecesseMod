package necesse.engine.network.client.loading;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.packet.PacketPlayerStats;
import necesse.engine.network.packet.PacketRequestPacket;

public class ClientLoadingPlayerStats extends ClientLoadingAutoPhase {
   public ClientLoadingPlayerStats(ClientLoading var1) {
      super(var1, false);
   }

   public void submitStatsPacket(PacketPlayerStats var1) {
      if (this.client.characterStats != null) {
         this.markDone();
      }

   }

   public GameMessage getLoadingMessage() {
      return new LocalMessage("loading", "connectstats");
   }

   public void tick() {
      if (!this.isWaiting()) {
         this.client.network.sendPacket(new PacketRequestPacket(PacketRequestPacket.RequestType.PLAYER_STATS));
         this.setWait(200);
      }
   }

   public void end() {
   }
}
