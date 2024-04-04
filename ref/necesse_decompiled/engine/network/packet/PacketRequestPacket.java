package necesse.engine.network.packet;

import java.util.Collections;
import java.util.function.BiFunction;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PacketRequestPacket extends Packet {
   public final RequestType request;

   public PacketRequestPacket(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      RequestType[] var3 = PacketRequestPacket.RequestType.values();
      int var4 = var2.getNextByteUnsigned();
      if (var4 >= 0 && var4 < var3.length) {
         this.request = var3[var4];
      } else {
         this.request = PacketRequestPacket.RequestType.WORLD_DATA;
      }

   }

   public PacketRequestPacket(RequestType var1) {
      this.request = var1;
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextByteUnsigned(var1.ordinal());
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      Packet var4 = (Packet)this.request.packetProducer.apply(var2, var3);
      if (var4 != null) {
         var2.network.sendPacket(var4, var3);
      }

   }

   public static enum RequestType {
      WORLD_DATA((var0, var1) -> {
         return new PacketWorldData(var0.world.worldEntity);
      }),
      PLAYER_STATS((var0, var1) -> {
         return new PacketPlayerStats(var1.characterStats());
      }),
      LEVEL_DATA((var0, var1) -> {
         return new PacketLevelData(var0.world.getLevel(var1), var1, Collections.emptyList());
      });

      public final BiFunction<Server, ServerClient, Packet> packetProducer;

      private RequestType(BiFunction var3) {
         this.packetProducer = var3;
      }

      // $FF: synthetic method
      private static RequestType[] $values() {
         return new RequestType[]{WORLD_DATA, PLAYER_STATS, LEVEL_DATA};
      }
   }
}
