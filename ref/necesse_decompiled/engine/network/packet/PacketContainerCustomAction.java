package necesse.engine.network.packet;

import necesse.engine.GameLog;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.Container;

public class PacketContainerCustomAction extends Packet {
   public final int containerUniqueSeed;
   public final int actionID;
   public final Packet actionContent;

   public PacketContainerCustomAction(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.containerUniqueSeed = var2.getNextInt();
      this.actionID = var2.getNextShort();
      this.actionContent = var2.getNextContentPacket();
   }

   public PacketContainerCustomAction(Container var1, int var2, Packet var3) {
      this.containerUniqueSeed = var1.uniqueSeed;
      this.actionID = var2;
      this.actionContent = var3;
      PacketWriter var4 = new PacketWriter(this);
      var4.putNextInt(this.containerUniqueSeed);
      var4.putNextShort((short)var2);
      var4.putNextContentPacket(this.actionContent);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      Container var4 = var3.getContainer();
      if (var4.uniqueSeed == this.containerUniqueSeed) {
         var4.runCustomAction(this.actionID, new PacketReader(this.actionContent));
      } else {
         GameLog.warn.println("Received invalid container update for " + var3.getName() + " with unique seed " + this.containerUniqueSeed);
      }

   }

   public void processClient(NetworkPacket var1, Client var2) {
      Container var3 = var2.getContainer();
      if (var3 != null) {
         if (var3.uniqueSeed == this.containerUniqueSeed) {
            var3.runCustomAction(this.actionID, new PacketReader(this.actionContent));
         } else {
            GameLog.warn.println("Received invalid container update with unique seed " + this.containerUniqueSeed);
         }
      }

   }
}
