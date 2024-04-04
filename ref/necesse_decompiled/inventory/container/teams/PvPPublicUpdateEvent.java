package necesse.inventory.container.teams;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;

public class PvPPublicUpdateEvent extends ContainerEvent {
   public boolean isPublic;

   public PvPPublicUpdateEvent(boolean var1) {
      this.isPublic = var1;
   }

   public PvPPublicUpdateEvent(PacketReader var1) {
      super(var1);
      this.isPublic = var1.getNextBoolean();
   }

   public void write(PacketWriter var1) {
      var1.putNextBoolean(this.isPublic);
   }
}
