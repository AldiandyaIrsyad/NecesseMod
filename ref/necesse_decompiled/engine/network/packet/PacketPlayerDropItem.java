package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PacketPlayerDropItem extends Packet {
   public final int inventoryID;
   public final int inventorySlot;
   public final int amount;

   public PacketPlayerDropItem(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.inventoryID = var2.getNextShortUnsigned();
      this.inventorySlot = var2.getNextShortUnsigned();
      this.amount = var2.getNextShortUnsigned();
   }

   public PacketPlayerDropItem(int var1, int var2, int var3) {
      this.inventoryID = var1;
      this.inventorySlot = var2;
      this.amount = var3;
      PacketWriter var4 = new PacketWriter(this);
      var4.putNextShortUnsigned(var1);
      var4.putNextShortUnsigned(var2);
      var4.putNextShortUnsigned(var3);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      var3.playerMob.getInv().dropItem(this.inventoryID, this.inventorySlot, this.amount);
   }
}
