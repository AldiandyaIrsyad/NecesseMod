package necesse.engine.network.packet;

import necesse.engine.GameLog;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.PlayerMob;

public class PacketPlayerInventoryAction extends Packet {
   public final int slot;
   public final int selectedSlot;
   public final boolean inventoryExtended;

   public PacketPlayerInventoryAction(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.slot = var2.getNextByteUnsigned();
      this.selectedSlot = var2.getNextByteUnsigned();
      this.inventoryExtended = var2.getNextBoolean();
   }

   public PacketPlayerInventoryAction(int var1, PlayerMob var2) {
      var2.resetUpdateInventoryAction();
      this.slot = var1;
      this.selectedSlot = var2.getSelectedSlot();
      this.inventoryExtended = var2.isInventoryExtended();
      PacketWriter var3 = new PacketWriter(this);
      var3.putNextByteUnsigned(var1);
      var3.putNextByteUnsigned(this.selectedSlot);
      var3.putNextBoolean(this.inventoryExtended);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      if (var3.slot == this.slot) {
         var3.playerMob.setInventoryExtended(this.inventoryExtended);
         var3.playerMob.setSelectedSlot(this.selectedSlot);
         var2.network.sendToClientsAtExcept(this, (ServerClient)var3, var3);
      } else {
         GameLog.warn.println("Client " + var3.getName() + " tried to manipulate wrong slot with inventory action");
      }

   }

   public void processClient(NetworkPacket var1, Client var2) {
      PlayerMob var3 = var2.getPlayer(this.slot);
      if (var3 != null) {
         var3.setInventoryExtended(this.inventoryExtended);
         var3.setSelectedSlot(this.selectedSlot);
      }

   }
}
