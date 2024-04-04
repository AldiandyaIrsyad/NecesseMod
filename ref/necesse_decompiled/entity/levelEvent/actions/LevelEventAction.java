package necesse.entity.levelEvent.actions;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketLevelEventAction;
import necesse.entity.levelEvent.LevelEvent;
import necesse.level.maps.Level;

public abstract class LevelEventAction {
   private LevelEvent event;
   private int id = -1;

   public LevelEventAction() {
   }

   protected void onRegister(LevelEvent var1, int var2) {
      if (this.event != null) {
         throw new IllegalStateException("Cannot register same action twice");
      } else {
         this.event = var1;
         this.id = var2;
      }
   }

   public LevelEvent getEvent() {
      return this.event;
   }

   public abstract void executePacket(PacketReader var1);

   protected void runAndSendAction(Packet var1) {
      if (this.event != null) {
         if (this.event.isServer()) {
            this.executePacket(new PacketReader(var1));
            this.event.level.getServer().network.sendToClientsAt(new PacketLevelEventAction(this.event, this.id, var1), (Level)this.event.level);
         } else if (!this.event.level.isClient()) {
            this.executePacket(new PacketReader(var1));
         } else {
            System.err.println("Cannot send level event actions from client. Only server handles when level event actions are ran");
         }
      } else {
         System.err.println("Cannot run level event action that hasn't been registered");
      }

   }
}
