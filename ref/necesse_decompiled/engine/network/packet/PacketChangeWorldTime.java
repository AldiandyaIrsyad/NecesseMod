package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;

public class PacketChangeWorldTime extends Packet {
   public final long worldTime;
   public final boolean isSleeping;

   public PacketChangeWorldTime(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.worldTime = var2.getNextLong();
      this.isSleeping = var2.getNextBoolean();
   }

   public PacketChangeWorldTime(Server var1) {
      this.worldTime = var1.world.worldEntity.getWorldTime();
      this.isSleeping = var1.world.worldEntity.isSleeping();
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextLong(this.worldTime);
      var2.putNextBoolean(this.isSleeping);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.worldEntity != null) {
         var2.worldEntity.applyChangeWorldTimePacket(this);
      }
   }
}
