package necesse.engine.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;
import necesse.engine.network.packet.PacketNetworkUpdate;
import necesse.engine.registries.PacketRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;

public abstract class PacketManager {
   public static int networkTrackingTime = 5;
   private static Comparator<LatencyPacket> packetComparator = Comparator.comparingInt((var0) -> {
      return var0.packet.timestamp == 0L ? 0 : 1;
   });
   public float dropPacketChance;
   public int minSimulatedLatency;
   public int maxSimulatedLatency;
   private final NetworkPacketList incompletePackets;
   private List<LatencyPacket> receivedPackets;
   private StatPacket[] totalInPacketTypes;
   private StatPacket[] totalOutPacketTypes;
   private long totalInBytes;
   private long totalOutBytes;
   private long totalInPackets;
   private long totalOutPackets;
   private long packetsIncomplete;
   private long packetsInLost;
   private long packetsOutLost;
   private long trackInBytes;
   private long trackOutBytes;
   private List<SizePacket> trackInPackets;
   private List<SizePacket> trackOutPackets;

   public PacketManager(float var1, int var2, int var3) {
      this.incompletePackets = new NetworkPacketList(5000);
      this.dropPacketChance = var1;
      this.minSimulatedLatency = var2;
      this.maxSimulatedLatency = var3;
      this.reset();
   }

   public PacketManager() {
      this(0.0F, 0, 0);
   }

   public void reset() {
      this.receivedPackets = Collections.synchronizedList(new LinkedList());
      this.resetTotalTracking();
      this.resetAverageTracking();
   }

   public void resetTotalTracking() {
      int var1 = PacketRegistry.getTotalRegistered();
      this.totalInPacketTypes = new StatPacket[var1];
      this.totalOutPacketTypes = new StatPacket[var1];

      for(int var2 = 0; var2 < this.totalInPacketTypes.length; ++var2) {
         this.totalInPacketTypes[var2] = new StatPacket(var2);
         this.totalOutPacketTypes[var2] = new StatPacket(var2);
      }

      this.totalInBytes = 0L;
      this.totalOutBytes = 0L;
      this.totalInPackets = 0L;
      this.totalOutPackets = 0L;
      this.packetsIncomplete = 0L;
      this.packetsInLost = 0L;
      this.packetsOutLost = 0L;
   }

   public void resetAverageTracking() {
      this.trackInBytes = 0L;
      this.trackOutBytes = 0L;
      this.trackInPackets = Collections.synchronizedList(new ArrayList());
      this.trackOutPackets = Collections.synchronizedList(new ArrayList());
   }

   public void clearReceivedPackets() {
      this.receivedPackets.clear();
   }

   public void submitInPacket(NetworkPacket var1) {
      synchronized(this) {
         var1 = this.incompletePackets.submitPacket(var1, (var1x) -> {
            ++this.packetsIncomplete;
            int var2 = var1x.getCurrentByteSize();
            this.totalInBytes += (long)var2;
            this.trackInPackets.add(new SizePacket(var1x.type, var2));
            this.trackInBytes += (long)var2;
         });
         if (var1 != null) {
            ++this.totalInPackets;
            this.totalInBytes += (long)var1.getByteSize();
            ++this.totalInPacketTypes[var1.type].amount;
            StatPacket var10000 = this.totalInPacketTypes[var1.type];
            var10000.bytes += var1.getByteSize();
            this.trackInPackets.add(new SizePacket(var1.type, var1.getByteSize()));
            this.trackInBytes += (long)var1.getByteSize();
            int var3 = this.minSimulatedLatency;
            if (this.maxSimulatedLatency > this.minSimulatedLatency) {
               var3 += GameRandom.globalRandom.nextInt(this.maxSimulatedLatency - this.minSimulatedLatency);
            }

            if (var3 == 0 && PacketRegistry.processInstantly(var1.type)) {
               this.processInstantly(var1);
            } else {
               GameUtils.insertSortedList((List)this.receivedPackets, new LatencyPacket(var1, var3), packetComparator);
            }

         }
      }
   }

   public abstract void processInstantly(NetworkPacket var1);

   public void submitOutPacket(NetworkPacket var1) {
      this.totalOutBytes += (long)var1.getByteSize();
      ++this.totalOutPackets;
      ++this.totalOutPacketTypes[var1.type].amount;
      StatPacket var10000 = this.totalOutPacketTypes[var1.type];
      var10000.bytes += var1.getByteSize();
      this.trackOutPackets.add(new SizePacket(var1.type, var1.getByteSize()));
      this.trackOutBytes += (long)var1.getByteSize();
   }

   public NetworkPacket nextPacket() {
      synchronized(this) {
         if (this.receivedPackets.size() != 0) {
            LatencyPacket var2 = (LatencyPacket)this.receivedPackets.get(0);
            if (var2 == null) {
               this.receivedPackets.remove(0);
               return null;
            } else if (var2.isReady()) {
               this.receivedPackets.remove(0);
               return var2.packet;
            } else {
               return null;
            }
         } else {
            return null;
         }
      }
   }

   public long getTotalInPackets() {
      return this.totalInPackets;
   }

   public long getTotalOutPackets() {
      return this.totalOutPackets;
   }

   public long getTotalInBytes() {
      return this.totalInBytes;
   }

   public long getTotalOutBytes() {
      return this.totalOutBytes;
   }

   public String getTotalIn() {
      return GameUtils.getByteString(this.getTotalInBytes());
   }

   public String getTotalOut() {
      return GameUtils.getByteString(this.getTotalOutBytes());
   }

   public StatPacket getTotalInStats(int var1) {
      return this.totalInPacketTypes[var1];
   }

   public StatPacket getTotalOutStats(int var1) {
      return this.totalOutPacketTypes[var1];
   }

   public int getTotalInTypesAmount(int var1) {
      return this.totalInPacketTypes[var1].amount;
   }

   public int getTotalOutTypesAmount(int var1) {
      return this.totalOutPacketTypes[var1].amount;
   }

   public int getTotalInTypesBytes(int var1) {
      return this.totalInPacketTypes[var1].bytes;
   }

   public int getTotalOutTypesBytes(int var1) {
      return this.totalOutPacketTypes[var1].bytes;
   }

   public long getAverageInBytes() {
      return this.trackInBytes / (long)networkTrackingTime;
   }

   public long getAverageOutBytes() {
      return this.trackOutBytes / (long)networkTrackingTime;
   }

   public String getAverageOut() {
      return GameUtils.getByteString(this.getAverageOutBytes());
   }

   public String getAverageIn() {
      return GameUtils.getByteString(this.getAverageInBytes());
   }

   public long getAverageOutPackets() {
      return (long)this.trackOutPackets.size();
   }

   public long getAverageInPackets() {
      return (long)this.trackInPackets.size();
   }

   public Iterable<SizePacket> getRecentInPackets() {
      return this.trackInPackets;
   }

   public Iterable<SizePacket> getRecentOutPackets() {
      return this.trackInPackets;
   }

   public Stream<SizePacket> streamRecentInPackets() {
      return this.trackInPackets.stream();
   }

   public Stream<SizePacket> streamRecentOutPackets() {
      return this.trackInPackets.stream();
   }

   public void tickNetworkManager() {
      try {
         List var1 = this.trackOutPackets;
         synchronized(var1) {
            while(!var1.isEmpty()) {
               SizePacket var3 = (SizePacket)var1.get(0);
               if (var3.isExpired()) {
                  this.trackOutBytes -= (long)var3.byteSize;
                  var1.remove(0);
                  continue;
               }
            }
         }

         List var2 = this.trackInPackets;
         synchronized(var2) {
            while(!var2.isEmpty()) {
               SizePacket var4 = (SizePacket)var2.get(0);
               if (!var4.isExpired()) {
                  break;
               }

               this.trackInBytes -= (long)var4.byteSize;
               var2.remove(0);
            }
         }
      } catch (Exception var8) {
         System.err.println("Error ticking network tracking: " + var8.getClass().getSimpleName() + " : " + var8.getMessage());
      }

   }

   public void applyNetworkUpdate(PacketNetworkUpdate var1) {
      this.packetsInLost = this.getTotalInPackets() - var1.totalOutPackets - 1L;
      this.packetsOutLost = this.getTotalOutPackets() - var1.totalInPackets - 1L;
   }

   public long getTotalIncompleteDropped() {
      return this.packetsIncomplete;
   }

   public long getLostInPackets() {
      return this.packetsInLost;
   }

   public long getLostOutPackets() {
      return this.packetsOutLost;
   }

   static {
      packetComparator = packetComparator.thenComparing(LatencyPacket::getReadyTime);
   }
}
