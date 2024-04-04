package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.PlayerMob;

public class PacketSwapInventorySlots extends Packet {
   public final int itemIDInSlot1;
   public final int itemIDInSlot2;
   public final int slot1;
   public final int slot2;

   public PacketSwapInventorySlots(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.itemIDInSlot1 = var2.getNextShortUnsigned() - 1;
      this.itemIDInSlot2 = var2.getNextShortUnsigned() - 1;
      this.slot1 = var2.getNextShortUnsigned();
      this.slot2 = var2.getNextShortUnsigned();
   }

   public PacketSwapInventorySlots(PlayerMob var1, int var2, int var3) {
      this(var1.getInv().main.getItemID(var2), var1.getInv().main.getItemID(var3), var2, var3);
   }

   public PacketSwapInventorySlots(int var1, int var2, int var3, int var4) {
      this.itemIDInSlot1 = var1;
      this.itemIDInSlot2 = var2;
      this.slot1 = var3;
      this.slot2 = var4;
      PacketWriter var5 = new PacketWriter(this);
      var5.putNextShortUnsigned(var1 + 1);
      var5.putNextShortUnsigned(var2 + 1);
      var5.putNextShortUnsigned(var3);
      var5.putNextShortUnsigned(var4);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      int var4 = var3.playerMob.getInv().main.getItemID(this.slot1);
      int var5 = var3.playerMob.getInv().main.getItemID(this.slot2);
      if (this.itemIDInSlot1 == var4 && this.itemIDInSlot2 == var5) {
         var3.playerMob.getInv().main.swapItems(this.slot1, this.slot2);
      } else {
         var3.sendPacket(new PacketPlayerInventory(var3));
      }

   }
}
