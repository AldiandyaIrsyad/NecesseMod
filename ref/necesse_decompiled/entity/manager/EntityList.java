package necesse.entity.manager;

import java.awt.Point;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Spliterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import necesse.engine.GameLog;
import necesse.engine.tickManager.Performance;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.ConcurrentHashMapQueue;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.Entity;
import necesse.level.maps.Level;
import necesse.level.maps.levelBuffManager.LevelBuffsEntityComponent;

public class EntityList<T extends Entity> implements Iterable<T> {
   protected final ConcurrentHashMap<Integer, T> map;
   protected final ConcurrentHashMapQueue<Integer, EntityList<T>.CachedEntity> cache;
   protected int cacheTTL;
   protected final EntityManager manager;
   protected final boolean useEntityComponents;
   protected final String entityName;
   protected final Function<T, Integer> uniqueIDGetter;
   protected int nextUniqueID;
   protected final Function<T, Boolean> isRemoved;
   protected final Consumer<T> dispose;
   protected final Consumer<T> onAdded;
   Consumer<T> onHiddenAdded;
   protected final Consumer<T> onRemoved;
   protected final Function<Integer, T[]> arraySupplier;
   protected final EntityRegionList<T> regionList;

   public EntityList(EntityManager var1, boolean var2, String var3, Function<T, Integer> var4, Function<T, Boolean> var5, Consumer<T> var6, Consumer<T> var7, Consumer<T> var8, Function<T, Point> var9, Function<Integer, T[]> var10) {
      this.map = new ConcurrentHashMap();
      this.cache = new ConcurrentHashMapQueue();
      this.cacheTTL = 5000;
      this.manager = var1;
      this.useEntityComponents = var2;
      this.entityName = var3;
      this.uniqueIDGetter = var4;
      this.isRemoved = var5;
      this.dispose = var6;
      this.onAdded = var7;
      this.onRemoved = var8;
      this.arraySupplier = var10;
      if (var9 != null) {
         this.regionList = new EntityRegionList(this.getLevel(), var9);
      } else {
         this.regionList = null;
      }

   }

   public EntityList(EntityManager var1, boolean var2, String var3, Consumer<T> var4, Consumer<T> var5, Function<T, Point> var6, Function<Integer, T[]> var7) {
      this(var1, var2, var3, Entity::getUniqueID, Entity::removed, (var0) -> {
         if (!var0.isDisposed()) {
            var0.dispose();
         }

      }, var4, var5, var6, var7);
   }

   protected Level getLevel() {
      return this.manager.level;
   }

   public Iterator<T> iterator() {
      return this.map.values().iterator();
   }

   public void forEach(Consumer<? super T> var1) {
      this.map.values().forEach(var1);
   }

   public Spliterator<T> spliterator() {
      return this.map.values().spliterator();
   }

   public Stream<T> stream() {
      synchronized(this.manager.lock) {
         return this.map.values().stream();
      }
   }

   public T[] array() {
      synchronized(this.manager.lock) {
         return (Entity[])this.map.values().toArray((Entity[])this.arraySupplier.apply(0));
      }
   }

   public int count() {
      return this.map.size();
   }

   public int countCache() {
      return this.cache.size();
   }

   public boolean hasUniqueID(int var1, boolean var2) {
      if (this.uniqueIDGetter == null) {
         return false;
      } else {
         synchronized(this.manager.lock) {
            if (this.map.containsKey(var1)) {
               return true;
            }
         }

         if (var2) {
            synchronized(this.manager.lock) {
               if (this.cache.containsKey(var1)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public T get(int var1, boolean var2) {
      if (this.uniqueIDGetter == null) {
         return null;
      } else {
         synchronized(this.manager.lock) {
            Entity var4 = (Entity)this.map.get(var1);
            if (var4 != null) {
               return var4;
            }
         }

         if (var2) {
            synchronized(this.manager.lock) {
               CachedEntity var9 = (CachedEntity)this.cache.get(var1);
               if (var9 != null) {
                  return var9.entity;
               }
            }
         }

         return null;
      }
   }

   public void add(T var1) {
      this.addHidden(var1);
      if (this.onAdded != null) {
         this.onAdded.accept(var1);
      }

   }

   public void addHidden(T var1) {
      if (this.uniqueIDGetter != null) {
         int var2 = (Integer)this.uniqueIDGetter.apply(var1);
         var1.setUniqueID(var2);
         synchronized(this.manager.lock) {
            this.map.compute(var2, (var2x, var3x) -> {
               if (var3x != null && var3x != var1) {
                  this.runRemoveLogic(var3x);
               }

               return var1;
            });
            if (this.useEntityComponents) {
               this.manager.componentManager.add(var2, var1);
               if (var1 instanceof LevelBuffsEntityComponent) {
                  this.manager.level.buffManager.updateBuffs();
               }
            }
         }
      } else {
         synchronized(this.manager.lock) {
            if (this.map.containsValue(var1)) {
               return;
            }

            int var3 = this.nextUniqueID++;
            var1.setUniqueID(var3);
            this.map.compute(var3, (var2x, var3x) -> {
               if (var3x != null && var3x != var1) {
                  this.runRemoveLogic(var3x);
               }

               return var1;
            });
            if (this.useEntityComponents) {
               this.manager.componentManager.add(var3, var1);
               if (var1 instanceof LevelBuffsEntityComponent) {
                  this.getLevel().buffManager.updateBuffs();
               }
            }
         }
      }

      var1.setLevel(this.getLevel());
      if (!var1.isInitialized()) {
         var1.init();
         var1.postInit();
      } else {
         var1.onLevelChanged();
      }

      if (this.onHiddenAdded != null) {
         this.onHiddenAdded.accept(var1);
      }

      if (this.regionList != null) {
         synchronized(this.manager.lock) {
            this.regionList.updateRegion(var1);
         }
      }

   }

   public EntityRegionList<T> getRegionList() {
      return this.regionList;
   }

   public GameLinkedList<T> getInRegion(int var1, int var2) {
      synchronized(this.manager.lock) {
         if (this.regionList == null) {
            throw new IllegalStateException(this.entityName + " list region data not supported");
         } else {
            return this.regionList.getInRegion(var1, var2);
         }
      }
   }

   public Stream<T> streamInRegionsShape(Shape var1, int var2) {
      synchronized(this.manager.lock) {
         if (this.regionList == null) {
            throw new IllegalStateException(this.entityName + " list region data not supported");
         } else {
            return this.regionList.streamInRegionsShape(var1, var2);
         }
      }
   }

   public Stream<T> streamInRegionsInRange(float var1, float var2, int var3) {
      synchronized(this.manager.lock) {
         if (this.regionList == null) {
            throw new IllegalStateException(this.entityName + " list region data not supported");
         } else {
            return this.regionList.streamInRegionsInRange(var1, var2, var3);
         }
      }
   }

   public Stream<T> streamInRegionsInTileRange(int var1, int var2, int var3) {
      synchronized(this.manager.lock) {
         if (this.regionList == null) {
            throw new IllegalStateException(this.entityName + " list region data not supported");
         } else {
            return this.regionList.streamInRegionsInTileRange(var1, var2, var3);
         }
      }
   }

   public GameAreaStream<T> streamArea(float var1, float var2, int var3) {
      synchronized(this.manager.lock) {
         if (this.regionList == null) {
            throw new IllegalStateException(this.entityName + " list region data not supported");
         } else {
            return this.regionList.streamArea(var1, var2, var3);
         }
      }
   }

   public GameAreaStream<T> streamAreaTileRange(int var1, int var2, int var3) {
      synchronized(this.manager.lock) {
         if (this.regionList == null) {
            throw new IllegalStateException(this.entityName + " list region data not supported");
         } else {
            return this.regionList.streamAreaTileRange(var1, var2, var3);
         }
      }
   }

   public GameLinkedList<T> getInRegionTileByTile(int var1, int var2) {
      synchronized(this.manager.lock) {
         if (this.regionList == null) {
            throw new IllegalStateException(this.entityName + " list region data not supported");
         } else {
            return this.regionList.getInRegionTileByTile(var1, var2);
         }
      }
   }

   public ArrayList<T> getInRegionRange(int var1, int var2, int var3) {
      synchronized(this.manager.lock) {
         if (this.regionList == null) {
            throw new IllegalStateException(this.entityName + " list region data not supported");
         } else {
            return this.regionList.getInRegionRange(var1, var2, var3);
         }
      }
   }

   public ArrayList<T> getInRegionRangeByTile(int var1, int var2, int var3) {
      synchronized(this.manager.lock) {
         if (this.regionList == null) {
            throw new IllegalStateException(this.entityName + " list region data not supported");
         } else {
            return this.regionList.getInRegionRangeByTile(var1, var2, var3);
         }
      }
   }

   public ArrayList<T> getInRegionByTileRange(int var1, int var2, int var3) {
      synchronized(this.manager.lock) {
         if (this.regionList == null) {
            throw new IllegalStateException(this.entityName + " list region data not supported");
         } else {
            return this.regionList.getInRegionByTileRange(var1, var2, var3);
         }
      }
   }

   public void frameTick(TickManager var1, BiConsumer<T, Float> var2) {
      Level var3 = this.getLevel();
      if (var2 != null) {
         float var4 = var1.getDelta();
         Performance.record(var3.tickManager(), "movement", (Runnable)(() -> {
            Performance.record(var3.tickManager(), this.entityName, (Runnable)(() -> {
               synchronized(this.manager.lock) {
                  Iterator var4x = this.map.values().iterator();

                  while(var4x.hasNext()) {
                     Entity var5 = (Entity)var4x.next();
                     if (var5 != null && !(Boolean)this.isRemoved.apply(var5)) {
                        var2.accept(var5, var4);
                     }
                  }

               }
            }));
         }));
      }

      if (this.regionList != null) {
         Performance.record(var3.tickManager(), "regionEnt", (Runnable)(() -> {
            Performance.record(var3.tickManager(), this.entityName, (Runnable)(() -> {
               synchronized(this.manager.lock) {
                  Iterator var2 = this.map.values().iterator();

                  while(var2.hasNext()) {
                     Entity var3 = (Entity)var2.next();
                     if (var3 != null) {
                        this.regionList.updateRegion(var3);
                     }
                  }

               }
            }));
         }));
      }

   }

   public void clientTick(Consumer<T> var1, List<Entity> var2) {
      synchronized(this.manager.lock) {
         while(!this.cache.isEmpty()) {
            CachedEntity var4 = (CachedEntity)this.cache.getFirst();
            if (!var4.shouldDie()) {
               break;
            }

            this.cache.removeFirst();
         }
      }

      synchronized(this.manager.lock) {
         LinkedList var12 = new LinkedList();
         this.map.forEach((var4x, var5x) -> {
            if (var5x == null) {
               var12.add(var4x);
            } else if ((Boolean)this.isRemoved.apply(var5x)) {
               var12.add(var4x);
               this.runRemoveLogic(var5x);
               if (this.cacheTTL > 0) {
                  this.cache.addLast(var4x, new CachedEntity(var4x, var5x));
               }
            } else if (var5x.getUniqueID() != var4x) {
               GameLog.warn.println(this.entityName + " has changed uniqueID from " + var4x + " to " + var5x.getUniqueID() + ", removing it from level");
               var12.add(var4x);
               this.runRemoveLogic(var5x);
            } else {
               if (var5x.shouldDrawOnMap()) {
                  var2.add(var5x);
               }

               var1.accept(var5x);
               var5x.updateRegionPos();
            }

         });
         boolean var5 = false;
         Iterator var6 = var12.iterator();

         while(var6.hasNext()) {
            int var7 = (Integer)var6.next();
            Entity var8 = (Entity)this.map.remove(var7);
            if (this.useEntityComponents && var8 != null) {
               this.manager.componentManager.remove(var7, var8);
               if (var8 instanceof LevelBuffsEntityComponent) {
                  var5 = true;
               }
            }
         }

         if (var5) {
            this.getLevel().buffManager.updateBuffs();
         }

      }
   }

   public void serverTick(Consumer<T> var1, List<Entity> var2) {
      synchronized(this.manager.lock) {
         while(!this.cache.isEmpty()) {
            CachedEntity var4 = (CachedEntity)this.cache.getFirst();
            if (!var4.shouldDie()) {
               break;
            }

            this.cache.removeFirst();
         }
      }

      synchronized(this.manager.lock) {
         LinkedList var12 = new LinkedList();
         this.map.forEach((var4x, var5x) -> {
            if (var5x == null) {
               var12.add(var4x);
            } else if ((Boolean)this.isRemoved.apply(var5x)) {
               var12.add(var4x);
               this.runRemoveLogic(var5x);
               if (this.cacheTTL > 0) {
                  this.cache.addLast(var4x, new CachedEntity(var4x, var5x));
               }
            } else if (var5x.getUniqueID() != var4x) {
               GameLog.warn.println(this.entityName + " has changed uniqueID from " + var4x + " to " + var5x.getUniqueID() + ", removing it from level");
               var12.add(var4x);
               this.runRemoveLogic(var5x);
            } else {
               if (var5x.shouldDrawOnMap()) {
                  var2.add(var5x);
               }

               var1.accept(var5x);
            }

         });
         boolean var5 = false;
         Iterator var6 = var12.iterator();

         while(var6.hasNext()) {
            int var7 = (Integer)var6.next();
            Entity var8 = (Entity)this.map.remove(var7);
            if (this.useEntityComponents && var8 != null) {
               this.manager.componentManager.remove(var7, var8);
               if (var8 instanceof LevelBuffsEntityComponent) {
                  var5 = true;
               }
            }
         }

         if (var5) {
            this.getLevel().buffManager.updateBuffs();
         }

      }
   }

   protected void runRemoveLogic(T var1) {
      if (this.onRemoved != null) {
         this.onRemoved.accept(var1);
      }

      if (!var1.removed()) {
         var1.remove();
      }

      var1.onRemovedFromManager();
      if (this.dispose != null) {
         this.dispose.accept(var1);
      }

   }

   public void onLoadingComplete() {
      synchronized(this.manager.lock) {
         this.map.values().forEach(Entity::onLoadingComplete);
      }
   }

   public void onUnloading() {
      synchronized(this.manager.lock) {
         this.map.values().forEach(Entity::onUnloading);
      }
   }

   public void dispose() {
      synchronized(this.manager.lock) {
         if (this.dispose != null) {
            this.map.values().forEach(this.dispose);
         }

      }
   }

   private class CachedEntity {
      public final int uniqueID;
      public final T entity;
      public final long endTime;

      public CachedEntity(int var2, T var3) {
         this.uniqueID = var2;
         this.entity = var3;
         this.endTime = EntityList.this.getLevel().getWorldEntity().getTime() + (long)EntityList.this.cacheTTL;
      }

      public boolean shouldDie() {
         return this.endTime <= EntityList.this.getLevel().getWorldEntity().getTime();
      }
   }
}
