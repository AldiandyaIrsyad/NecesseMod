package necesse.entity.mobs;

import necesse.engine.network.packet.PacketMobInventory;
import necesse.engine.network.packet.PacketMobInventoryUpdate;
import necesse.engine.network.server.Server;
import necesse.inventory.Inventory;
import necesse.level.maps.Level;

public interface MobInventory {
   Inventory getInventory();

   default void serverTickInventorySync(Server var1, Mob var2) {
      if (var1 != null) {
         Inventory var3 = this.getInventory();
         if (var3.isDirty()) {
            if (var3.isFullDirty()) {
               var1.network.sendToClientsAt(new PacketMobInventory(this), (Level)var2.getLevel());
               var3.clean();
            } else {
               for(int var4 = 0; var4 < var3.getSize(); ++var4) {
                  if (var3.isDirty(var4)) {
                     var1.network.sendToClientsAt(new PacketMobInventoryUpdate(this, var4), (Level)var2.getLevel());
                     var3.clean(var4);
                  }
               }
            }
         }

      }
   }
}
