package necesse.engine.network.client.loading;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.packet.PacketQuests;
import necesse.engine.network.packet.PacketRequestQuests;

public class ClientLoadingQuests extends ClientLoadingAutoPhase {
   private boolean loaded = false;

   public ClientLoadingQuests(ClientLoading var1) {
      super(var1, false);
   }

   public GameMessage getLoadingMessage() {
      return new LocalMessage("loading", "connectquests");
   }

   public void tick() {
      if (this.loaded) {
         this.markDone();
      } else {
         if (this.isWaiting()) {
            return;
         }

         this.client.network.sendPacket(new PacketRequestQuests());
         this.setWait(250);
      }

   }

   public void submitQuestsPacket(PacketQuests var1) {
      this.client.quests.clearQuests();
      var1.readQuests((var1x) -> {
         this.client.quests.addQuest(var1x, false);
      });
      this.loaded = true;
   }

   public void end() {
   }
}
