package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.PlayerInventory;

public class PacketPlayerInventoryPart extends Packet {
   public final int slot;
   public final int inventoryID;
   public final Packet inventoryContent;

   public PacketPlayerInventoryPart(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.slot = var2.getNextByteUnsigned();
      this.inventoryID = var2.getNextShortUnsigned();
      this.inventoryContent = var2.getNextContentPacket();
   }

   public PacketPlayerInventoryPart(ServerClient var1, PlayerInventory var2) {
      if (var2.player != var1.playerMob) {
         throw new NullPointerException("Invalid inventory and client match");
      } else {
         this.slot = var1.slot;
         this.inventoryID = var2.getInventoryID();
         this.inventoryContent = var2.getContentPacket();
         PacketWriter var3 = new PacketWriter(this);
         var3.putNextByteUnsigned(this.slot);
         var3.putNextShortUnsigned(this.inventoryID);
         var3.putNextContentPacket(this.inventoryContent);
      }
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getClient(this.slot) == null) {
         var2.network.sendPacket(new PacketRequestPlayerData(this.slot));
      } else {
         var2.getClient(this.slot).applyInventoryPartPacket(this);
      }

   }
}
