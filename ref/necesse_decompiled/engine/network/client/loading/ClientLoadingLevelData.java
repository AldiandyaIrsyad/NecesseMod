package necesse.engine.network.client.loading;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.packet.PacketLevelData;
import necesse.engine.network.packet.PacketRequestPacket;
import necesse.level.maps.Level;

public class ClientLoadingLevelData extends ClientLoadingAutoPhase {
   public ClientLoadingLevelData(ClientLoading var1) {
      super(var1, true);
   }

   public void submitLevelDataPacket(PacketLevelData var1) {
      if (this.client.getLevel() != null) {
         this.markDone();
      }

   }

   public GameMessage getLoadingMessage() {
      return new LocalMessage("loading", "connectlevel");
   }

   public void tick() {
      if (!this.isWaiting()) {
         this.client.network.sendPacket(new PacketRequestPacket(PacketRequestPacket.RequestType.LEVEL_DATA));
         this.setWait(200);
      }
   }

   public void end() {
   }

   public void reset() {
      super.reset();
      this.client.levelManager.setLevel((Level)null);
   }
}
