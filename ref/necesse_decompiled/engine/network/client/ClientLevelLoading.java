package necesse.engine.network.client;

import java.awt.Point;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.function.Predicate;
import necesse.engine.GameTileRange;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.network.Packet;
import necesse.engine.network.packet.PacketRegionData;
import necesse.engine.network.packet.PacketRequestRegionData;
import necesse.engine.network.packet.PacketUnloadRegions;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.Region;
import necesse.level.maps.regionSystem.RegionPosition;

public class ClientLevelLoading {
   public static GameTileRange REGION_LOAD_RANGE = null;
   public static int MAX_PACKETS_PER_TICK_PRELOAD = 10;
   public static int MAX_PACKETS_PER_TICK_STREAMING = 5;
   public final Level level;
   protected Point lastPlayerRegionPos;
   protected boolean started;
   private int preloadElements;
   private PriorityUniqueArrayList<Point> queue;
   private PriorityUniqueArrayList<Point> requested;
   private HashSet<Point> loaded;

   public ClientLevelLoading(Level var1) {
      this.level = var1;
      this.reset();
   }

   public void reset() {
      this.started = false;
      this.preloadElements = 0;
      this.queue = new PriorityUniqueArrayList();
      this.requested = new PriorityUniqueArrayList();
      this.loaded = new HashSet();
      this.lastPlayerRegionPos = null;
   }

   public void start(PlayerMob var1) {
      this.reset();
      RegionPosition var2 = this.level.regionManager.getRegionPosByTile(var1.getTileX(), var1.getTileY());
      int var3 = 0;
      int var4 = Screen.getSceneWidth() / 32;
      int var5 = Screen.getSceneHeight() / 32;
      int var6 = Math.max(0, var1.getTileX() - var4 / 2);
      int var7 = Math.max(0, var1.getTileY() - var5 / 2);
      int var8 = Math.min(var6 + var4, this.level.width - 1);
      int var9 = Math.min(var7 + var5, this.level.height - 1);
      RegionPosition var10 = this.level.regionManager.getRegionPosByTile(var6, var7);
      RegionPosition var11 = this.level.regionManager.getRegionPosByTile(var8, var9);
      int var12;
      int var13;
      Point var14;
      if (Settings.instantLevelChange) {
         for(var12 = var10.regionX; var12 <= var11.regionX; ++var12) {
            for(var13 = var10.regionY; var13 <= var11.regionY; ++var13) {
               var14 = new Point(var12, var13);
               if (!this.isOrHasLoaded(var14)) {
                  this.sendRequest(var12, var13);
                  ++var3;
               }
            }
         }
      }

      if (REGION_LOAD_RANGE == null) {
         for(var12 = 0; var12 < this.level.regionManager.getRegionsWidth(); ++var12) {
            for(var13 = 0; var13 < this.level.regionManager.getRegionsHeight(); ++var13) {
               var14 = new Point(var12, var13);
               if (!this.isOrHasLoaded(var14)) {
                  int var15 = (int)GameMath.squareDistance((float)var12, (float)var13, (float)var2.regionX, (float)var2.regionY);
                  this.addLoadQueue((long)(var15 - 1000000), var14);
                  if (var14.x >= var10.regionX && var14.x <= var11.regionX && var14.y >= var10.regionY && var14.y <= var11.regionY) {
                     ++var3;
                  }
               }
            }
         }
      } else {
         this.refreshLoading(var1);
      }

      this.started = true;
      this.preloadElements = Settings.instantLevelChange ? 0 : var3;
   }

   public boolean isStarted() {
      return this.started;
   }

   private boolean isOrHasLoaded(Point var1) {
      return this.loaded.contains(var1) || this.requested.contains(var1) || this.queue.contains(var1);
   }

   public boolean isPreloading() {
      return this.loaded.size() < this.preloadElements;
   }

   public boolean isPreloadingDone() {
      return this.loaded.size() >= this.preloadElements;
   }

   public boolean isLoadingDone() {
      return this.queue.isEmpty() && this.requested.isEmpty();
   }

   public boolean isRegionLoaded(int var1, int var2) {
      return this.loaded.contains(new Point(var1, var2));
   }

   public boolean isRegionInQueue(int var1, int var2) {
      Point var3 = new Point(var1, var2);
      return this.requested.contains(var3) || this.queue.contains(var3);
   }

   private boolean addLoadQueue(long var1, Point var3) {
      if (var3.x >= 0 && var3.y >= 0 && var3.x < this.level.regionManager.getRegionsWidth() && var3.y < this.level.regionManager.getRegionsHeight()) {
         if (!this.loaded.contains(var3) && !this.requested.contains(var3)) {
            this.queue.add(var1, var3);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean tickLoading(PlayerMob var1) {
      if (var1 == null) {
         return false;
      } else {
         if (REGION_LOAD_RANGE != null) {
            this.refreshLoading(var1);
         }

         if (this.isLoadingDone()) {
            return false;
         } else {
            RegionPosition var2 = this.level.regionManager.getRegionPosByTile(var1.getTileX(), var1.getTileY());
            long var3 = System.currentTimeMillis() - 2000L;

            while(!this.requested.isEmpty()) {
               long var5 = this.requested.getFirstPriority();
               if (var5 >= var3) {
                  break;
               }

               Point var7 = (Point)this.requested.getFirst();
               this.requested.removeFirst();
               int var8 = (int)GameMath.squareDistance((float)var7.x, (float)var7.y, (float)var2.regionX, (float)var2.regionY);
               this.queue.add((long)var8, var7);
            }

            boolean var9 = false;
            int var6;
            if (this.isPreloading()) {
               if (this.isPreloadingDone()) {
                  return false;
               } else {
                  for(var6 = 0; var6 < MAX_PACKETS_PER_TICK_PRELOAD && this.requestNextRegion(); ++var6) {
                     var9 = true;
                  }

                  return var9;
               }
            } else if (this.isLoadingDone()) {
               return false;
            } else {
               for(var6 = 0; var6 < MAX_PACKETS_PER_TICK_STREAMING && this.requestNextRegion(); ++var6) {
                  var9 = true;
               }

               return var9;
            }
         }
      }
   }

   private boolean requestNextRegion() {
      if (this.queue.isEmpty()) {
         return false;
      } else {
         Point var1 = (Point)this.queue.removeFirst();
         if (var1 != null) {
            if (this.loaded.contains(var1)) {
               System.err.println("Loaded map region in load queue");
               return false;
            } else if (this.requested.contains(var1)) {
               System.err.println("Loaded map region in sent loaded queue");
               return false;
            } else {
               this.sendRequest(var1.x, var1.y);
               return true;
            }
         } else {
            return false;
         }
      }
   }

   protected void refreshLoading(PlayerMob var1) {
      RegionPosition var2 = this.level.regionManager.getRegionPosByTile(var1.getTileX(), var1.getTileY());
      Point var3 = new Point(var2.regionX, var2.regionY);
      if (this.lastPlayerRegionPos == null || !this.lastPlayerRegionPos.equals(var3)) {
         int var4 = Screen.getSceneWidth() / 32;
         int var5 = Screen.getSceneHeight() / 32;
         int var6 = Math.max(0, var1.getTileX() - var4 / 2);
         int var7 = Math.max(0, var1.getTileY() - var5 / 2);
         int var8 = Math.min(var6 + var4, this.level.width - 1);
         int var9 = Math.min(var7 + var5, this.level.height - 1);
         RegionPosition var10 = this.level.regionManager.getRegionPosByTile(var6, var7);
         RegionPosition var11 = this.level.regionManager.getRegionPosByTile(var8, var9);
         int var12 = var11.regionX - var10.regionX;
         int var13 = var11.regionY - var10.regionY;
         HashSet var14 = new HashSet();
         Iterator var15 = REGION_LOAD_RANGE.getValidTiles(var10.regionX, var10.regionY).iterator();

         while(var15.hasNext()) {
            Point var16 = (Point)var15.next();

            for(int var17 = 0; var17 <= var12; ++var17) {
               for(int var18 = 0; var18 <= var13; ++var18) {
                  Point var19 = new Point(var16.x + var17, var16.y + var18);
                  var14.add(var19);
                  if (!this.isOrHasLoaded(var19)) {
                     int var20 = (int)GameMath.squareDistance((float)var19.x, (float)var19.y, (float)var2.regionX, (float)var2.regionY);
                     this.addLoadQueue((long)var20, var19);
                  }
               }
            }
         }

         HashSet var21 = new HashSet();
         Iterator var22 = this.loaded.iterator();

         Point var23;
         while(var22.hasNext()) {
            var23 = (Point)var22.next();
            if (!var14.contains(var23)) {
               var21.add(var23);
            }
         }

         this.loaded.removeAll(var21);
         var21.addAll(this.requested.removeIfAndGetRemoved((var1x) -> {
            return !var14.contains(var1x);
         }));
         var21.addAll(this.queue.removeIfAndGetRemoved((var1x) -> {
            return !var14.contains(var1x);
         }));
         if (!var21.isEmpty()) {
            this.level.getClient().network.sendPacket(new PacketUnloadRegions(this.level, var21));
            var22 = var21.iterator();

            while(var22.hasNext()) {
               var23 = (Point)var22.next();
               Region var24 = this.level.regionManager.getRegion(var23.x, var23.y);
               if (var24 != null) {
                  this.level.entityManager.onRegionUnloaded(var24);
                  var24.unloadRegion();
               }
            }
         }

         this.lastPlayerRegionPos = var3;
      }

   }

   public void sendRequest(int var1, int var2) {
      this.level.getClient().network.sendPacket(new PacketRequestRegionData(this.level, var1, var2));
      this.requested.add(System.currentTimeMillis(), new Point(var1, var2));
      this.queue.remove(new Point(var1, var2));
   }

   public void applyRegionData(PacketRegionData var1) {
      this.applyRegionData(var1.regionX, var1.regionY, var1.regionData);
   }

   public void applyRegionData(HashMap<Point, Packet> var1) {
      Iterator var2 = var1.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry var3 = (Map.Entry)var2.next();
         Point var4 = (Point)var3.getKey();
         this.applyRegionData(var4.x, var4.y, (Packet)var3.getValue());
      }

   }

   public void applyRegionData(int var1, int var2, Packet var3) {
      Region var4 = this.level.regionManager.getRegion(var1, var2);
      if (var4 != null) {
         boolean var5 = var4.applyRegionDataPacket(var3);
         Point var6 = new Point(var1, var2);
         if (var5) {
            this.queue.remove(var6);
            this.loaded.add(var6);
         } else {
            this.queue.add(0L, var6);
         }

         this.requested.remove(var6);
      } else {
         System.err.println("Could not find region at " + var1 + ", " + var2 + " for region data.");
      }

   }

   public int getRegionsLoadedCount() {
      return this.loaded.size();
   }

   public int getRegionsLoadQueueCount() {
      return this.queue.size();
   }

   public int getRegionsRequestedCount() {
      return this.requested.size();
   }

   public float getPercentLoaded() {
      return (float)this.loaded.size() / (float)(this.level.regionManager.getRegionsWidth() * this.level.regionManager.getRegionsHeight());
   }

   public float getPercentPresumedLoaded() {
      return (float)(this.loaded.size() + this.requested.size()) / (float)(this.level.regionManager.getRegionsWidth() * this.level.regionManager.getRegionsHeight());
   }

   public float getPercentPreloaded() {
      return Math.min((float)this.loaded.size() / (float)this.preloadElements, 1.0F);
   }

   public float getPercentPresumedPreloaded() {
      return Math.min((float)(this.loaded.size() + this.requested.size()) / (float)this.preloadElements, 1.0F);
   }

   private static class PriorityUniqueArrayList<E> {
      private GameLinkedList<PriorityUniqueArrayList<E>.PriorityValue> queue;
      private HashMap<E, GameLinkedList<PriorityUniqueArrayList<E>.PriorityValue>.Element> values;

      private PriorityUniqueArrayList() {
         this.queue = new GameLinkedList();
         this.values = new HashMap();
      }

      public void add(long var1, E var3) {
         if (!this.values.containsKey(var3)) {
            GameLinkedList.Element var4 = GameUtils.insertSortedList((GameLinkedList)this.queue, new PriorityValue(var1, var3), Comparator.comparingLong((var0) -> {
               return var0.priority;
            }));
            this.values.put(var3, var4);
         }
      }

      public boolean contains(E var1) {
         return this.values.containsKey(var1);
      }

      public E remove(E var1) {
         GameLinkedList.Element var2 = (GameLinkedList.Element)this.values.remove(var1);
         if (var2 != null) {
            var2.remove();
            return ((PriorityValue)var2.object).value;
         } else {
            return null;
         }
      }

      public Iterable<E> getValues() {
         return this.values.keySet();
      }

      public Iterable<E> getPriorityValues() {
         return GameUtils.mapIterable(this.queue.iterator(), (var0) -> {
            return var0.value;
         });
      }

      public HashSet<E> removeIfAndGetRemoved(Predicate<E> var1) {
         HashSet var2 = new HashSet();
         ListIterator var3 = this.queue.listIterator();

         while(var3.hasNext()) {
            PriorityValue var4 = (PriorityValue)var3.next();
            if (var1.test(var4.value)) {
               var2.add(var4.value);
               this.values.remove(var4.value);
               var3.remove();
            }
         }

         return var2;
      }

      public long getFirstPriority() {
         return ((PriorityValue)this.queue.getFirst()).priority;
      }

      public E getFirst() {
         return ((PriorityValue)this.queue.getFirst()).value;
      }

      public E removeFirst() {
         Object var1 = ((PriorityValue)this.queue.removeFirst()).value;
         this.values.remove(var1);
         return var1;
      }

      public int size() {
         return this.values.size();
      }

      public boolean isEmpty() {
         return this.values.isEmpty();
      }

      // $FF: synthetic method
      PriorityUniqueArrayList(Object var1) {
         this();
      }

      private class PriorityValue {
         public long priority;
         public E value;

         public PriorityValue(long var2, E var4) {
            this.value = var4;
            this.priority = var2;
         }
      }
   }
}
