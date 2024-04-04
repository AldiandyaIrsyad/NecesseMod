package necesse.inventory.container.mob;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.inventory.container.events.ContainerEvent;

public class ShopContainerPartyUpdateEvent extends ContainerEvent {
   public int mobUniqueID;
   public boolean canJoinAdventureParties;
   public boolean isInAdventureParty;
   public boolean isInYourAdventureParty;

   public ShopContainerPartyUpdateEvent(HumanMob var1, ServerClient var2) {
      this.mobUniqueID = var1.getUniqueID();
      this.canJoinAdventureParties = var1.canJoinAdventureParties;
      this.isInAdventureParty = var1.adventureParty.isInAdventureParty();
      this.isInYourAdventureParty = var1.adventureParty.getServerClient() == var2;
   }

   public ShopContainerPartyUpdateEvent(PacketReader var1) {
      super(var1);
      this.canJoinAdventureParties = var1.getNextBoolean();
      this.isInAdventureParty = var1.getNextBoolean();
      this.isInYourAdventureParty = var1.getNextBoolean();
   }

   public void write(PacketWriter var1) {
      var1.putNextBoolean(this.canJoinAdventureParties);
      var1.putNextBoolean(this.isInAdventureParty);
      var1.putNextBoolean(this.isInYourAdventureParty);
   }

   public void applyUpdate(ShopContainer var1) {
      var1.canJoinAdventureParties = this.canJoinAdventureParties;
      var1.isInAdventureParty = this.isInAdventureParty;
      var1.isInYourAdventureParty = this.isInYourAdventureParty;
   }

   public static void sendAndApplyUpdate(HumanMob var0) {
      if (var0.isServer()) {
         constructApplyAndSendToClients(var0.getLevel().getServer(), (var1) -> {
            return new ShopContainerPartyUpdateEvent(var0, var1);
         });
      }

   }
}
