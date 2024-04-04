package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.registries.WorldEventRegistry;
import necesse.engine.world.WorldEntity;
import necesse.engine.world.worldEvent.WorldEvent;

public class PacketWorldEvent extends Packet {
   public final int eventID;
   public final Packet spawnContent;

   public PacketWorldEvent(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.eventID = var2.getNextShortUnsigned();
      this.spawnContent = var2.getNextContentPacket();
   }

   public PacketWorldEvent(WorldEvent var1) {
      this.eventID = var1.getID();
      this.spawnContent = new Packet();
      var1.setupSpawnPacket(new PacketWriter(this.spawnContent));
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextShortUnsigned(this.eventID);
      var2.putNextContentPacket(this.spawnContent);
   }

   public WorldEvent getEvent(WorldEntity var1) {
      WorldEvent var2 = WorldEventRegistry.getEvent(this.eventID);
      if (var2 != null) {
         var2.world = var1;
         var2.applySpawnPacket(new PacketReader(this.spawnContent));
      }

      return var2;
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.worldEntity != null) {
         var2.worldEntity.addWorldEvent(this.getEvent(var2.worldEntity));
      }
   }
}
