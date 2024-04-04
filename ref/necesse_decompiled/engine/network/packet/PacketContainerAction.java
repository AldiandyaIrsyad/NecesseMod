package necesse.engine.network.packet;

import necesse.engine.GameLog;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.ContainerAction;
import necesse.inventory.container.ContainerActionResult;

public class PacketContainerAction extends Packet {
   public final short containerSlot;
   public final ContainerAction action;
   public final int actionResult;

   public PacketContainerAction(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.containerSlot = var2.getNextShort();
      this.action = ContainerAction.getContainerAction(var2.getNextByteUnsigned());
      this.actionResult = var2.getNextInt();
   }

   public PacketContainerAction(int var1, ContainerAction var2, int var3) {
      this.containerSlot = (short)var1;
      this.action = var2;
      this.actionResult = var3;
      PacketWriter var4 = new PacketWriter(this);
      var4.putNextShort(this.containerSlot);
      var4.putNextByteUnsigned(var2.getID());
      var4.putNextInt(var3);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      ContainerActionResult var4 = var3.getContainer().applyContainerAction(this.containerSlot, this.action);
      if (var4.value != this.actionResult) {
         GameLog.warn.println(var3.getName() + " container action result did not match expected. Got: " + var4.value + ", Expected: " + this.actionResult);
         var3.getContainer().markFullDirty();
      }

   }
}
