package necesse.engine.network.client.loading;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.packet.PacketRequestPacket;
import necesse.engine.network.packet.PacketWorldData;

public class ClientLoadingWorld extends ClientLoadingAutoPhase {
   public ClientLoadingWorld(ClientLoading var1) {
      super(var1, false);
   }

   public void submitWorldDataPacket(PacketWorldData var1) {
      if (this.client.worldEntity != null) {
         this.markDone();
      }

   }

   public GameMessage getLoadingMessage() {
      return new LocalMessage("loading", "connectworld");
   }

   public void tick() {
      if (!this.isWaiting()) {
         this.client.network.sendPacket(new PacketRequestPacket(PacketRequestPacket.RequestType.WORLD_DATA));
         this.setWait(200);
      }
   }

   public void end() {
   }
}
