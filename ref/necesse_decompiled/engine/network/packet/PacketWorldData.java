package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.world.WorldEntity;

public class PacketWorldData extends Packet {
   public final long time;
   public final long worldTime;
   public final boolean isSleeping;

   public PacketWorldData(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.time = var2.getNextLong();
      this.worldTime = var2.getNextLong();
      this.isSleeping = var2.getNextBoolean();
   }

   public PacketWorldData(WorldEntity var1) {
      this.time = var1.getTime();
      this.worldTime = var1.getWorldTime();
      this.isSleeping = var1.isSleeping();
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextLong(this.time);
      var2.putNextLong(this.worldTime);
      var2.putNextBoolean(this.isSleeping);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.worldEntity == null) {
         var2.worldEntity = WorldEntity.getClientWorldEntity(var2);
      }

      var2.worldEntity.applyWorldPacket(this);
      var2.loading.worldPhase.submitWorldDataPacket(this);
   }
}
