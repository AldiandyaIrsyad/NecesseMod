package necesse.level.maps.levelData.settlementData;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.HashMapGameLinkedList;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.inventory.itemFilter.ItemFilter;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.jobs.HaulFromLevelJob;

public abstract class LevelStorage {
   public final Level level;
   public final int tileX;
   public final int tileY;
   protected LinkedList<HaulFromLevelJob> haulFromLevelJobs = new LinkedList();
   protected StorageDropOffSimulation dropOffSimulation = new StorageDropOffSimulation(this);
   protected HashMapGameLinkedList<Integer, SettlementStoragePickupSlot> pickupSlots = new HashMapGameLinkedList();

   public LevelStorage(Level var1, int var2, int var3) {
      this.level = var1;
      this.tileX = var2;
      this.tileY = var3;
   }

   public abstract InventoryRange getInventoryRange();

   public abstract ItemCategoriesFilter getFilter();

   public abstract GameMessage getInventoryName();

   public boolean isValid() {
      return this.getInventoryRange() != null;
   }

   public void removeInvalidPickups() {
      InventoryRange var1 = this.getInventoryRange();
      Iterator var2;
      int var3;
      if (var1 != null) {
         var2 = this.pickupSlots.keySet().iterator();

         while(var2.hasNext()) {
            var3 = (Integer)var2.next();
            boolean var4 = var3 >= var1.startSlot && var3 <= var1.endSlot;
            InventoryItem var5 = var1.inventory.getItem(var3);
            GameLinkedList var6 = (GameLinkedList)this.pickupSlots.get(var3);
            var6.removeIf((var3x) -> {
               return !var4 || var5 == null || !var5.equals(this.level, var3x.item, true, false, "pickups") || !var3x.isReserved(this.level.getWorldEntity());
            });
         }
      } else {
         var2 = this.pickupSlots.keySet().iterator();

         while(var2.hasNext()) {
            var3 = (Integer)var2.next();
            ((GameLinkedList)this.pickupSlots.get(var3)).elements().forEach(GameLinkedList.Element::remove);
         }
      }

   }

   public SettlementStoragePickupSlot reserve(int var1, InventoryItem var2, int var3) {
      GameLinkedList var4 = (GameLinkedList)this.pickupSlots.get(var1);
      int var5 = var4.stream().filter((var1x) -> {
         return var1x.isReserved(this.level.getWorldEntity());
      }).mapToInt((var0) -> {
         return var0.item.getAmount();
      }).sum();
      if (var5 >= var2.getAmount()) {
         return null;
      } else {
         int var6 = var2.getAmount() - var5;
         var3 = Math.min(var3, var6);
         SettlementStoragePickupSlot var7 = new SettlementStoragePickupSlot(this, var1, var2.copy(var3), var5);
         var7.init(var4.addFirst(var7), this.level.getWorldEntity());
         return var7;
      }
   }

   public SettlementStoragePickupFuture getFutureReserve(final int var1, final InventoryItem var2, final int var3, final Consumer<SettlementStoragePickupSlot> var4) {
      final GameLinkedList var5 = (GameLinkedList)this.pickupSlots.get(var1);
      final int var6 = var5.stream().filter((var1x) -> {
         return var1x.isReserved(this.level.getWorldEntity());
      }).mapToInt((var0) -> {
         return var0.item.getAmount();
      }).sum();
      if (var6 >= var2.getAmount()) {
         return null;
      } else {
         int var7 = var2.getAmount() - var6;
         return new SettlementStoragePickupFuture(this, var2.copy(Math.min(var7, var3))) {
            public SettlementStoragePickupSlot accept(int var1x) {
               InventoryItem var2x = var2.copy(Math.min(var1x, var3));
               SettlementStoragePickupSlot var3x = new SettlementStoragePickupSlot(this.storage, var1, var2x, var6);
               var3x.init(var5.addFirst(var3x), LevelStorage.this.level.getWorldEntity());
               LevelStorage.this.haulFromLevelJobs.stream().filter((var2xx) -> {
                  return var2xx.item.equals(LevelStorage.this.level, var2x, true, false, "pickups");
               }).findFirst().ifPresent((var1xx) -> {
                  var1xx.item.setAmount(var1xx.item.getAmount() - var2x.getAmount());
               });
               var4.accept(var3x);
               return var3x;
            }
         };
      }
   }

   public LinkedList<SettlementStoragePickupSlot> findUnreservedSlots(Predicate<InventoryItem> var1, int var2, int var3) {
      InventoryRange var4 = this.getInventoryRange();
      if (var4 == null) {
         return new LinkedList();
      } else {
         LinkedList var5 = new LinkedList();
         int var6 = 0;

         for(int var7 = var4.endSlot; var7 >= var4.startSlot; --var7) {
            InventoryItem var8 = var4.inventory.getItem(var7);
            if (var8 != null && var1.test(var8)) {
               GameLinkedList var9 = (GameLinkedList)this.pickupSlots.get(var7);
               int var10 = var9.stream().filter((var1x) -> {
                  return var1x.isReserved(this.level.getWorldEntity());
               }).mapToInt((var0) -> {
                  return var0.item.getAmount();
               }).sum();
               if (var10 < var8.getAmount()) {
                  int var11 = Math.min(var3 - var6, var8.getAmount());
                  if (var11 > 0) {
                     var6 += var11;
                     SettlementStoragePickupSlot var12 = new SettlementStoragePickupSlot(this, var7, var8.copy(var11), var10);
                     var12.init(var9.addFirst(var12), this.level.getWorldEntity());
                     var5.addFirst(var12);
                     if (var6 >= var3) {
                        break;
                     }
                  }
               }
            }
         }

         if (var6 < var2) {
            var5.forEach(SettlementStoragePickupSlot::remove);
            return null;
         } else {
            return var5.isEmpty() ? null : var5;
         }
      }
   }

   public LinkedList<SettlementStoragePickupSlot> findUnreservedSlots(ItemFilter var1, int var2, int var3) {
      Objects.requireNonNull(var1);
      return this.findUnreservedSlots(var1::matchesItem, var2, var3);
   }

   public LinkedList<SettlementStoragePickupSlot> findUnreservedSlots(InventoryItem var1, int var2, int var3) {
      return this.findUnreservedSlots((var2x) -> {
         return var2x.canCombine(this.level, (PlayerMob)null, var1, "hauljob");
      }, var2, var3);
   }

   public Stream<SettlementStoragePickupFuture> findFutureUnreservedSlots() {
      InventoryRange var1 = this.getInventoryRange();
      if (var1 != null) {
         Stream.Builder var2 = Stream.builder();

         for(final int var3 = var1.endSlot; var3 >= var1.startSlot; --var3) {
            final InventoryItem var5 = var1.inventory.getItem(var3);
            if (var5 != null) {
               final GameLinkedList var6 = (GameLinkedList)this.pickupSlots.get(var3);
               final int var7 = var6.stream().filter((var1x) -> {
                  return var1x.isReserved(this.level.getWorldEntity());
               }).mapToInt((var0) -> {
                  return var0.item.getAmount();
               }).sum();
               if (var7 < var5.getAmount()) {
                  final int var8 = var5.getAmount() - var7;
                  var2.add(new SettlementStoragePickupFuture(this, var5.copy(var8)) {
                     public SettlementStoragePickupSlot accept(int var1) {
                        InventoryItem var2 = var5.copy(Math.min(var1, var8));
                        SettlementStoragePickupSlot var3x = new SettlementStoragePickupSlot(this.storage, var3, var2, var7);
                        var3x.init(var6.addFirst(var3x), LevelStorage.this.level.getWorldEntity());
                        LevelStorage.this.haulFromLevelJobs.stream().filter((var2x) -> {
                           return var2x.item.equals(LevelStorage.this.level, var2, true, false, "pickups");
                        }).findFirst().ifPresent((var1x) -> {
                           var1x.item.setAmount(var1x.item.getAmount() - var2.getAmount());
                        });
                        return var3x;
                     }
                  });
               }
            }
         }

         return var2.build();
      } else {
         return Stream.empty();
      }
   }

   public int canAddFutureDropOff(InventoryItem var1) {
      return this.dropOffSimulation.canAddFutureDropOff(var1);
   }

   public StorageDropOff addFutureDropOff(Supplier<InventoryItem> var1) {
      return this.dropOffSimulation.addFutureDropOff(this, var1);
   }

   public int getItemCount(Predicate<InventoryItem> var1, int var2, boolean var3) {
      return this.dropOffSimulation.getItemCount(var1, var2, var3);
   }
}
