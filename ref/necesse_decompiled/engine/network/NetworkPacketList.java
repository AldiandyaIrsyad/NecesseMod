package necesse.engine.network;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.function.Consumer;

public class NetworkPacketList {
   public final int timeout;
   private LinkedList<IncompletePacket> packets = new LinkedList();

   public NetworkPacketList(int var1) {
      this.timeout = var1;
   }

   public NetworkPacket submitPacket(NetworkPacket var1, Consumer<NetworkPacket> var2) {
      if (var1.isComplete()) {
         return var1;
      } else {
         this.tickTimeout(var2);
         ListIterator var3 = this.packets.listIterator();

         IncompletePacket var4;
         do {
            if (!var3.hasNext()) {
               this.packets.add(new IncompletePacket(System.currentTimeMillis() + (long)this.timeout, var1));
               return null;
            }

            var4 = (IncompletePacket)var3.next();
         } while(!var4.packet.canMerge(var1));

         var4.packet = var4.packet.mergePackets(var1);
         var4.expireTime = System.currentTimeMillis() + (long)this.timeout;
         if (var4.packet.isComplete()) {
            var3.remove();
            return var4.packet;
         } else {
            return null;
         }
      }
   }

   public void tickTimeout(Consumer<NetworkPacket> var1) {
      while(!this.packets.isEmpty() && ((IncompletePacket)this.packets.getFirst()).expireTime < System.currentTimeMillis()) {
         IncompletePacket var2 = (IncompletePacket)this.packets.removeFirst();
         if (var1 != null) {
            var1.accept(var2.packet);
         }
      }

   }

   private class IncompletePacket {
      public long expireTime;
      public NetworkPacket packet;

      public IncompletePacket(long var2, NetworkPacket var4) {
         this.expireTime = var2;
         this.packet = var4;
      }
   }
}
