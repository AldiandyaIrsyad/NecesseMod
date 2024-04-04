package necesse.engine.network.client.loading;

import java.util.LinkedList;
import necesse.engine.GameLog;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketObjectEntity;
import necesse.engine.network.packet.PacketObjectEntityError;
import necesse.engine.network.packet.PacketRequestObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;

public class ClientObjectEntityLoading extends ClientLoadingUtil {
   private final LinkedList<Requested> requests = new LinkedList();

   public ClientObjectEntityLoading(ClientLoading var1) {
      super(var1);
   }

   public void tick() {
      if (!this.isWaiting() && !this.requests.isEmpty()) {
         long var1 = this.client.worldEntity.getLocalTime();

         while(!this.requests.isEmpty()) {
            Requested var3 = (Requested)this.requests.getFirst();
            if (var3.time + 2000L >= var1) {
               break;
            }

            this.client.network.sendPacket(new PacketRequestObjectEntity(this.client.getLevel(), var3.x, var3.y));
            this.requests.removeFirst();
            this.requests.add(new Requested(var3.x, var3.y, var1));
         }

         this.setWait(250);
      }
   }

   public void reset() {
      this.requests.clear();
   }

   public void addObjectEntityRequest(int var1, int var2) {
      this.client.network.sendPacket(new PacketRequestObjectEntity(this.client.getLevel(), var1, var2));
      this.requests.add(new Requested(var1, var2, this.client.worldEntity.getLocalTime()));
   }

   public void submitObjectEntityPacket(PacketObjectEntity var1) {
      if (this.client.getLevel().getObjectID(var1.tileX, var1.tileY) == var1.objectID) {
         ObjectEntity var2 = this.client.getLevel().entityManager.getObjectEntity(var1.tileX, var1.tileY);
         if (var2 != null) {
            var2.applyContentPacket(new PacketReader(var1.content));
         } else {
            GameLog.warn.println("Client received unknown object entity packet at " + var1.tileX + ", " + var1.tileY);
         }
      } else {
         GameLog.warn.println("Client received wrong object entity packet at " + var1.tileX + ", " + var1.tileY);
      }

      this.requests.removeIf((var1x) -> {
         return var1x.x == var1.tileX && var1x.y == var1.tileY;
      });
   }

   public void submitObjectEntityErrorPacket(PacketObjectEntityError var1) {
      this.requests.removeIf((var1x) -> {
         return var1x.x == var1.x && var1x.y == var1.y;
      });
   }

   private static class Requested {
      public final int x;
      public final int y;
      public long time;

      public Requested(int var1, int var2, long var3) {
         this.x = var1;
         this.y = var2;
         this.time = var3;
      }
   }
}
