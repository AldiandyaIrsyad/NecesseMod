package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.fishingEvent.FishingEvent;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

public class PacketFishingStatus extends Packet {
   public final int secondType;
   public final int eventUniqueID;
   public final Packet extraContent;

   public PacketFishingStatus(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.secondType = var2.getNextByteUnsigned();
      this.eventUniqueID = var2.getNextInt();
      this.extraContent = var2.getNextContentPacket();
   }

   private PacketFishingStatus(int var1, int var2, Packet var3) {
      this.secondType = var1;
      this.eventUniqueID = var2;
      this.extraContent = var3;
      PacketWriter var4 = new PacketWriter(this);
      var4.putNextByteUnsigned(var1);
      var4.putNextInt(var2);
      var4.putNextContentPacket(var3);
   }

   public FishingEvent getEvent(Level var1) {
      LevelEvent var2 = var1.entityManager.getLevelEvent(this.eventUniqueID, false);
      return var2 instanceof FishingEvent ? (FishingEvent)var2 : null;
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null) {
         synchronized(var2.getLevel().entityManager.lock) {
            FishingEvent var4 = this.getEvent(var2.getLevel());
            if (var4 != null) {
               if (this.secondType == 1) {
                  var4.reel();
               } else if (this.secondType == 2) {
                  PacketReader var5 = new PacketReader(this.extraContent);
                  int var6 = var5.getNextShortUnsigned();
                  int var7 = var5.getNextShortUnsigned();
                  InventoryItem var8 = InventoryItem.fromContentPacket(var5.getNextContentPacket());
                  var4.addCatch(var6, var7, var8);
               }
            } else {
               var2.network.sendPacket(new PacketRequestLevelEvent(this.eventUniqueID));
            }

         }
      }
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      Level var4 = var2.world.getLevel(var3);
      synchronized(var4.entityManager.lock) {
         FishingEvent var6 = this.getEvent(var4);
         if (var6 != null && var6.getMob() == var3.playerMob && this.secondType == 1) {
            var6.reel();
            var3.refreshAFKTimer();
            var2.network.sendToClientsAtExcept(this, (ServerClient)var3, var3);
         }

      }
   }

   public static PacketFishingStatus getReelPacket(FishingEvent var0) {
      return new PacketFishingStatus(1, var0.getUniqueID(), new Packet());
   }

   public static PacketFishingStatus getUpcomingCatchPacket(FishingEvent var0, int var1, int var2, InventoryItem var3) {
      Packet var4 = new Packet();
      PacketWriter var5 = new PacketWriter(var4);
      var5.putNextShortUnsigned(var1);
      var5.putNextShortUnsigned(var2);
      var5.putNextContentPacket(InventoryItem.getContentPacket(var3));
      return new PacketFishingStatus(2, var0.getUniqueID(), var4);
   }
}
