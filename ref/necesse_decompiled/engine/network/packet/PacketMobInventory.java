package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobInventory;
import necesse.inventory.Inventory;

public class PacketMobInventory extends Packet {
   public final int mobUniqueID;
   public final Packet inventoryContent;

   public PacketMobInventory(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.mobUniqueID = var2.getNextInt();
      this.inventoryContent = var2.getNextContentPacket();
   }

   public PacketMobInventory(MobInventory var1) {
      this.mobUniqueID = ((Mob)var1).getUniqueID();
      this.inventoryContent = var1.getInventory().getContentPacket();
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextInt(this.mobUniqueID);
      var2.putNextContentPacket(this.inventoryContent);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null) {
         Mob var3 = GameUtils.getLevelMob(this.mobUniqueID, var2.getLevel());
         if (var3 instanceof MobInventory) {
            ((MobInventory)var3).getInventory().override(Inventory.getInventory(this.inventoryContent));
         }

      }
   }
}
