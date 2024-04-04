package necesse.engine.network.client.loading;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.packet.PacketRequestPlayerData;

public class ClientLoadingClient extends ClientLoadingAutoPhase {
   private int oldSelectedInventory;

   public ClientLoadingClient(ClientLoading var1) {
      super(var1, true);
   }

   public GameMessage getLoadingMessage() {
      return new LocalMessage("loading", "connectclient");
   }

   public void tick() {
      ClientClient var1 = this.client.getClient();
      if (var1 != null && var1.playerMob != null && var1.loadedPlayer) {
         this.markDone();
      } else {
         if (this.isWaiting()) {
            return;
         }

         this.client.network.sendPacket(new PacketRequestPlayerData(this.client.getSlot()));
         this.setWait(200);
      }

   }

   public void end() {
      this.client.getPlayer().setSelectedSlot(this.oldSelectedInventory);
      this.client.initInventoryContainer();
   }

   public void reset() {
      super.reset();
      if (this.client.getPlayer() != null) {
         this.oldSelectedInventory = this.client.getPlayer().getSelectedSlot();
      } else {
         this.oldSelectedInventory = 0;
      }

   }
}
