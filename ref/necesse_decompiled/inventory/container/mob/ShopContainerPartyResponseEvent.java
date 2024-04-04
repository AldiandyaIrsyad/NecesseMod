package necesse.inventory.container.mob;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;

public class ShopContainerPartyResponseEvent extends ContainerEvent {
   public GameMessage error;

   public ShopContainerPartyResponseEvent(GameMessage var1) {
      this.error = var1;
   }

   public ShopContainerPartyResponseEvent(PacketReader var1) {
      super(var1);
      if (var1.getNextBoolean()) {
         this.error = GameMessage.fromPacket(var1);
      } else {
         this.error = null;
      }

   }

   public void write(PacketWriter var1) {
      if (this.error != null) {
         var1.putNextBoolean(true);
         this.error.writePacket(var1);
      } else {
         var1.putNextBoolean(false);
      }

   }
}
