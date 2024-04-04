package necesse.entity.objectEntity;

import java.awt.Point;
import necesse.engine.network.packet.PacketTrapTriggered;
import necesse.level.maps.Level;

public class TrapObjectEntity extends ObjectEntity {
   public long cooldown;
   private long startCooldownTime;

   public TrapObjectEntity(Level var1, int var2, int var3, long var4) {
      super(var1, "trap", var2, var3);
      this.cooldown = var4;
      this.startCooldownTime = 0L;
   }

   public boolean shouldRequestPacket() {
      return false;
   }

   public void triggerTrap(int var1, int var2) {
   }

   public boolean onCooldown() {
      return this.startCooldownTime + this.cooldown > this.getWorldEntity().getTime();
   }

   public void startCooldown() {
      this.startCooldownTime = this.getWorldEntity().getTime();
   }

   public long getTimeSinceActivated() {
      return this.getWorldEntity().getTime() - this.startCooldownTime;
   }

   public void onClientTrigger() {
   }

   public void sendClientTriggerPacket() {
      if (this.isServer()) {
         this.getLevel().getServer().network.sendToClientsWithTile(new PacketTrapTriggered(this.getTileX(), this.getTileY()), this.getLevel(), this.getTileX(), this.getTileY());
      }
   }

   public Point getPos(int var1, int var2, int var3) {
      if (var3 == 0) {
         return new Point(var1, var2 - 1);
      } else if (var3 == 1) {
         return new Point(var1 + 1, var2);
      } else if (var3 == 2) {
         return new Point(var1, var2 + 1);
      } else {
         return var3 == 3 ? new Point(var1 - 1, var2) : new Point(var1, var2);
      }
   }

   public boolean otherWireActive(int var1) {
      for(int var2 = 0; var2 < 4; ++var2) {
         if (var2 != var1 && this.getLevel().wireManager.isWireActive(this.getX(), this.getY(), var2)) {
            return true;
         }
      }

      return false;
   }
}
