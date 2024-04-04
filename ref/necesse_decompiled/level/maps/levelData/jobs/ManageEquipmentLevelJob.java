package necesse.level.maps.levelData.jobs;

import java.awt.Point;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.save.LoadData;
import necesse.engine.util.GameLinkedList;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.FoundJob;
import necesse.entity.mobs.job.JobSequence;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.job.LinkedListJobSequence;
import necesse.entity.mobs.job.activeJob.EquipItemActiveJob;
import necesse.entity.mobs.job.activeJob.PickupSettlementStorageActiveJob;
import necesse.entity.mobs.job.activeJob.UnequipItemActiveJob;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.SettlerWeaponItem;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.level.maps.levelData.settlementData.SettlementStoragePickupFuture;
import necesse.level.maps.levelData.settlementData.SettlementStoragePickupSlot;
import necesse.level.maps.levelData.settlementData.ZoneTester;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageEquipmentTypeIndex;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecord;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecords;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecordsRegionData;
import necesse.level.maps.levelData.settlementData.storage.ValidatedSettlementStorageRecord;

public class ManageEquipmentLevelJob extends LevelJob {
   public JobTypeHandler.SubHandler<?> handler;
   public ItemCategoriesFilter equipmentFilter;
   public boolean preferArmorSets;

   public ManageEquipmentLevelJob(int var1, int var2, JobTypeHandler.SubHandler<?> var3, ItemCategoriesFilter var4, boolean var5) {
      super(var1, var2);
      this.handler = var3;
      this.equipmentFilter = var4;
      this.preferArmorSets = var5;
   }

   public ManageEquipmentLevelJob(LoadData var1) {
      super(var1);
   }

   public boolean isWithinRestrictZone(ZoneTester var1) {
      return true;
   }

   public boolean shouldSave() {
      return false;
   }

   public boolean isSameJob(LevelJob var1) {
      return var1.getID() == this.getID();
   }

   public boolean isValid() {
      return true;
   }

   public int getFirstPriority() {
      return Integer.MAX_VALUE;
   }

   public static <T extends ManageEquipmentLevelJob> JobSequence getJobSequence(EntityJobWorker var0, FoundJob<T> var1, HumanMob var2) {
      Predicate var3 = (var1x) -> {
         return ((ManageEquipmentLevelJob)var1.job).equipmentFilter == null || ((ManageEquipmentLevelJob)var1.job).equipmentFilter.isItemAllowed(var1x.item);
      };
      SettlementStorageRecords var4 = PickupSettlementStorageActiveJob.getStorageRecords(var0);
      if (var4 == null) {
         return null;
      } else {
         Inventory var5 = var2.getInventory();
         InventoryItem var6 = var5.getItem(6);
         SettlerWeaponItem var7 = getValidWeaponItem(var6, var2);
         boolean var8 = var7 != null && var3.test(var6);
         ValidatedStorageEquipmentRecord var9 = ManageEquipmentLevelJob.ValidatedStorageEquipmentRecord.WeaponItem(var7 != null && var8 ? var6 : null, var2);
         InventoryItem var10 = var5.getItem(0);
         ArmorItem var11 = getValidArmorItem(var10, ArmorItem.ArmorType.HEAD, var2);
         boolean var12 = var11 != null && var3.test(var10);
         ValidatedStorageEquipmentRecord var13 = ManageEquipmentLevelJob.ValidatedStorageEquipmentRecord.ArmorItem(var11 != null && var12 ? var10 : null, var2);
         InventoryItem var14 = var5.getItem(1);
         ArmorItem var15 = getValidArmorItem(var14, ArmorItem.ArmorType.CHEST, var2);
         boolean var16 = var15 != null && var3.test(var14);
         ValidatedStorageEquipmentRecord var17 = ManageEquipmentLevelJob.ValidatedStorageEquipmentRecord.ArmorItem(var15 != null && var16 ? var14 : null, var2);
         InventoryItem var18 = var5.getItem(2);
         ArmorItem var19 = getValidArmorItem(var18, ArmorItem.ArmorType.FEET, var2);
         boolean var20 = var19 != null && var3.test(var18);
         ValidatedStorageEquipmentRecord var21 = ManageEquipmentLevelJob.ValidatedStorageEquipmentRecord.ArmorItem(var19 != null && var20 ? var18 : null, var2);
         float var22 = getScore(var13, var17, var21, ((ManageEquipmentLevelJob)var1.job).preferArmorSets);
         SettlementStorageEquipmentTypeIndex var23 = (SettlementStorageEquipmentTypeIndex)var4.getIndex(SettlementStorageEquipmentTypeIndex.class);
         HashMap var24 = getAvailableWeaponItems(var0, var2, var23, var3);
         HashMap var25 = getAvailableArmorItems(var0, var2, var23, SettlementStorageEquipmentTypeIndex.EquipmentType.HEAD, var3);
         HashMap var26 = getAvailableArmorItems(var0, var2, var23, SettlementStorageEquipmentTypeIndex.EquipmentType.CHEST, var3);
         HashMap var27 = getAvailableArmorItems(var0, var2, var23, SettlementStorageEquipmentTypeIndex.EquipmentType.FEET, var3);
         var24.put(-1, var9);
         var25.put(-1, var13);
         var26.put(-1, var17);
         var27.put(-1, var21);
         float var28 = var22;
         ValidatedStorageEquipmentRecord var29 = (ValidatedStorageEquipmentRecord)var24.values().stream().max(Comparator.comparingDouble((var0x) -> {
            return (double)var0x.equipmentValue;
         })).orElse((Object)null);
         ValidatedStorageEquipmentRecord var30 = var13;
         ValidatedStorageEquipmentRecord var31 = var17;
         ValidatedStorageEquipmentRecord var32 = var21;
         if (((ManageEquipmentLevelJob)var1.job).preferArmorSets) {
            Iterator var33 = var25.values().iterator();

            while(var33.hasNext()) {
               ValidatedStorageEquipmentRecord var34 = (ValidatedStorageEquipmentRecord)var33.next();
               Iterator var35 = var26.values().iterator();

               while(var35.hasNext()) {
                  ValidatedStorageEquipmentRecord var36 = (ValidatedStorageEquipmentRecord)var35.next();
                  Iterator var37 = var27.values().iterator();

                  while(var37.hasNext()) {
                     ValidatedStorageEquipmentRecord var38 = (ValidatedStorageEquipmentRecord)var37.next();
                     float var39 = getScore(var34, var36, var38, ((ManageEquipmentLevelJob)var1.job).preferArmorSets);
                     if (var39 > var28) {
                        var28 = var39;
                        var30 = var34;
                        var31 = var36;
                        var32 = var38;
                     }
                  }
               }
            }
         } else {
            var30 = (ValidatedStorageEquipmentRecord)var25.values().stream().max(Comparator.comparingDouble((var0x) -> {
               return (double)var0x.equipmentValue;
            })).orElse((Object)null);
            var31 = (ValidatedStorageEquipmentRecord)var26.values().stream().max(Comparator.comparingDouble((var0x) -> {
               return (double)var0x.equipmentValue;
            })).orElse((Object)null);
            var32 = (ValidatedStorageEquipmentRecord)var27.values().stream().max(Comparator.comparingDouble((var0x) -> {
               return (double)var0x.equipmentValue;
            })).orElse((Object)null);
         }

         final LinkedListJobSequence var40 = new LinkedListJobSequence((GameMessage)null);
         if (var6 != null && !var8) {
            var40.add(new UnequipItemActiveJob(var0, var1.priority, 6, var2));
         }

         if (var10 != null && !var12) {
            var40.add(new UnequipItemActiveJob(var0, var1.priority, 0, var2));
         }

         if (var14 != null && !var16) {
            var40.add(new UnequipItemActiveJob(var0, var1.priority, 1, var2));
         }

         if (var18 != null && !var20) {
            var40.add(new UnequipItemActiveJob(var0, var1.priority, 2, var2));
         }

         AtomicReference var41;
         final LocalMessage var42;
         SettlementStoragePickupSlot var43;
         if (var29 != null && var29.futurePickup != null) {
            var41 = new AtomicReference();
            var42 = new LocalMessage("activities", "equipping", "item", var29.invItem.getItemLocalization());
            var43 = var29.futurePickup.accept(1);
            var40.add(new PickupSettlementStorageActiveJob(var0, var1.priority, var43.storage.tileX, var43.storage.tileY, var43, var41) {
               public void onMadeCurrent() {
                  super.onMadeCurrent();
                  var40.setActivityDescription(var42);
               }
            });
            var40.add(new EquipItemActiveJob(var0, var1.priority, var41, 6, var2) {
               public boolean useItem(InventoryItem var1, ListIterator<InventoryItem> var2) {
                  var40.setActivityDescription((GameMessage)null);
                  return super.useItem(var1, var2);
               }
            });
         }

         if (var30 != null && var30.futurePickup != null) {
            var41 = new AtomicReference();
            var42 = new LocalMessage("activities", "equipping", "item", var30.invItem.getItemLocalization());
            var43 = var30.futurePickup.accept(1);
            var40.add(new PickupSettlementStorageActiveJob(var0, var1.priority, var43.storage.tileX, var43.storage.tileY, var43, var41) {
               public void onMadeCurrent() {
                  super.onMadeCurrent();
                  var40.setActivityDescription(var42);
               }
            });
            var40.add(new EquipItemActiveJob(var0, var1.priority, var41, 0, var2) {
               public boolean useItem(InventoryItem var1, ListIterator<InventoryItem> var2) {
                  var40.setActivityDescription((GameMessage)null);
                  return super.useItem(var1, var2);
               }
            });
         }

         if (var31 != null && var31.futurePickup != null) {
            var41 = new AtomicReference();
            var42 = new LocalMessage("activities", "equipping", "item", var31.invItem.getItemLocalization());
            var43 = var31.futurePickup.accept(1);
            var40.add(new PickupSettlementStorageActiveJob(var0, var1.priority, var43.storage.tileX, var43.storage.tileY, var43, var41) {
               public void onMadeCurrent() {
                  super.onMadeCurrent();
                  var40.setActivityDescription(var42);
               }
            });
            var40.add(new EquipItemActiveJob(var0, var1.priority, var41, 1, var2) {
               public boolean useItem(InventoryItem var1, ListIterator<InventoryItem> var2) {
                  var40.setActivityDescription((GameMessage)null);
                  return super.useItem(var1, var2);
               }
            });
         }

         if (var32 != null && var32.futurePickup != null) {
            var41 = new AtomicReference();
            var42 = new LocalMessage("activities", "equipping", "item", var32.invItem.getItemLocalization());
            var43 = var32.futurePickup.accept(1);
            var40.add(new PickupSettlementStorageActiveJob(var0, var1.priority, var43.storage.tileX, var43.storage.tileY, var43, var41) {
               public void onMadeCurrent() {
                  super.onMadeCurrent();
                  var40.setActivityDescription(var42);
               }
            });
            var40.add(new EquipItemActiveJob(var0, var1.priority, var41, 2, var2) {
               public boolean useItem(InventoryItem var1, ListIterator<InventoryItem> var2) {
                  var40.setActivityDescription((GameMessage)null);
                  return super.useItem(var1, var2);
               }
            });
         }

         if (var40.isEmpty()) {
            return null;
         } else {
            var0.setPrioritizeNextJob(HasStorageLevelJob.class, true);
            return var40;
         }
      }
   }

   protected static SettlerWeaponItem getValidWeaponItem(InventoryItem var0, HumanMob var1) {
      if (var0 == null) {
         return null;
      } else if (!(var0.item instanceof SettlerWeaponItem)) {
         return null;
      } else {
         SettlerWeaponItem var2 = (SettlerWeaponItem)var0.item;
         return var2.getSettlerCanUseError(var1, var0) != null ? null : var2;
      }
   }

   protected static HashMap<Integer, ValidatedStorageEquipmentRecord> getAvailableWeaponItems(EntityJobWorker var0, HumanMob var1, SettlementStorageEquipmentTypeIndex var2, Predicate<InventoryItem> var3) {
      SettlementStorageRecordsRegionData var4 = var2.getEquipmentType(SettlementStorageEquipmentTypeIndex.EquipmentType.WEAPON);
      if (var4 == null) {
         return new HashMap();
      } else {
         GameLinkedList var5 = var4.getRecords(var0);
         return (HashMap)var5.stream().sorted(Comparator.comparingDouble((var1x) -> {
            return ((Point)var1x.getKey()).distance((double)var0.getMobWorker().getTileX(), (double)var0.getMobWorker().getTileY());
         })).flatMap((var3x) -> {
            return ((GameLinkedList)var3x.getValue()).streamElements().map((var3xx) -> {
               InventoryItem var4x = var4.validateItem((SettlementStorageRecord)var3xx.object, (Runnable)null);
               if (var4x != null) {
                  if (!var3.test(var4x)) {
                     return null;
                  } else {
                     SettlerWeaponItem var5 = (SettlerWeaponItem)var4x.item;
                     if (var5.getSettlerCanUseError(var1, var4x) != null) {
                        return null;
                     } else {
                        ValidatedStorageEquipmentRecord var6 = ManageEquipmentLevelJob.ValidatedStorageEquipmentRecord.WeaponItem(var3xx, var4x, var1);
                        return var6.futurePickup == null ? null : var6;
                     }
                  }
               } else {
                  return null;
               }
            });
         }).filter(Objects::nonNull).reduce(new HashMap(), (var0x, var1x) -> {
            if (var0x == null) {
               var0x = new HashMap();
            }

            var0x.compute(var1x.invItem.item.getID(), (var1, var2) -> {
               return var2 != null && !(var2.equipmentValue < var1x.equipmentValue) ? var2 : var1x;
            });
            return var0x;
         }, (var0x, var1x) -> {
            var0x.putAll(var1x);
            return var0x;
         });
      }
   }

   protected static ArmorItem getValidArmorItem(InventoryItem var0, ArmorItem.ArmorType var1, HumanMob var2) {
      if (var0 == null) {
         return null;
      } else if (!var0.item.isArmorItem()) {
         return null;
      } else {
         ArmorItem var3 = (ArmorItem)var0.item;
         if (var3.armorType != var1) {
            return null;
         } else {
            return var3.getSettlerEquipmentValue(var0, var2) <= 0.0F ? null : var3;
         }
      }
   }

   protected static HashMap<Integer, ValidatedStorageEquipmentRecord> getAvailableArmorItems(EntityJobWorker var0, HumanMob var1, SettlementStorageEquipmentTypeIndex var2, SettlementStorageEquipmentTypeIndex.EquipmentType var3, Predicate<InventoryItem> var4) {
      SettlementStorageRecordsRegionData var5 = var2.getEquipmentType(var3);
      if (var5 == null) {
         return new HashMap();
      } else {
         GameLinkedList var6 = var5.getRecords(var0);
         return (HashMap)var6.stream().sorted(Comparator.comparingDouble((var1x) -> {
            return ((Point)var1x.getKey()).distance((double)var0.getMobWorker().getTileX(), (double)var0.getMobWorker().getTileY());
         })).flatMap((var3x) -> {
            return ((GameLinkedList)var3x.getValue()).streamElements().map((var3) -> {
               InventoryItem var4x = var5.validateItem((SettlementStorageRecord)var3.object, (Runnable)null);
               if (var4x != null) {
                  if (!var4.test(var4x)) {
                     return null;
                  } else if (((ArmorItem)var4x.item).getSettlerEquipmentValue(var4x, var1) <= 0.0F) {
                     return null;
                  } else {
                     ValidatedStorageEquipmentRecord var5x = ManageEquipmentLevelJob.ValidatedStorageEquipmentRecord.ArmorItem(var3, var4x, var1);
                     return var5x.futurePickup == null ? null : var5x;
                  }
               } else {
                  return null;
               }
            });
         }).filter(Objects::nonNull).reduce(new HashMap(), (var0x, var1x) -> {
            if (var0x == null) {
               var0x = new HashMap();
            }

            var0x.compute(var1x.invItem.item.getID(), (var1, var2) -> {
               return var2 != null && !(var2.equipmentValue < var1x.equipmentValue) ? var2 : var1x;
            });
            return var0x;
         }, (var0x, var1x) -> {
            var0x.putAll(var1x);
            return var0x;
         });
      }
   }

   protected static float getScore(ValidatedStorageEquipmentRecord var0, ValidatedStorageEquipmentRecord var1, ValidatedStorageEquipmentRecord var2, boolean var3) {
      float var4 = 0.0F;
      if (var2 != null) {
         var4 += var2.equipmentValue;
      }

      if (var1 != null) {
         var4 += var1.equipmentValue;
      }

      if (var0 != null) {
         var4 += var0.equipmentValue;
         if (var3 && var0.invItem != null && var1 != null && var1.invItem != null && var2 != null && var2.invItem != null && ((ArmorItem)var0.invItem.item).hasSet(var0.invItem, var1.invItem, var2.invItem)) {
            var4 *= 1.5F;
         }
      }

      return var4;
   }

   public static JobTypeHandler.JobStreamSupplier<? extends ManageEquipmentLevelJob> getJobStreamer(Supplier<ItemCategoriesFilter> var0, Supplier<Boolean> var1) {
      return (var2, var3) -> {
         Mob var4 = var2.getMobWorker();
         return Stream.of(new ManageEquipmentLevelJob(var4.getTileX(), var4.getTileY(), var3, (ItemCategoriesFilter)var0.get(), (Boolean)var1.get()));
      };
   }

   protected static class ValidatedStorageEquipmentRecord extends ValidatedSettlementStorageRecord {
      public float equipmentValue;
      public SettlementStoragePickupFuture futurePickup;

      private ValidatedStorageEquipmentRecord(SettlementStorageRecord var1, InventoryItem var2, float var3) {
         super(var1, var2);
         this.equipmentValue = var3;
      }

      private ValidatedStorageEquipmentRecord(GameLinkedList<SettlementStorageRecord>.Element var1, InventoryItem var2, float var3) {
         super(var1, var2);
         this.equipmentValue = var3;
         if (var2 != null && this.record != null) {
            this.futurePickup = this.record.storage.getFutureReserve(this.record.inventorySlot, var2.copy(1), 1, (var2x) -> {
               SettlementStorageRecord var10000 = this.record;
               var10000.itemAmount -= var2x.item.getAmount();
               if (this.record.itemAmount <= 0 && !var1.isRemoved()) {
                  var1.remove();
               }

            });
         }

      }

      public static ValidatedStorageEquipmentRecord WeaponItem(InventoryItem var0, HumanMob var1) {
         float var2 = var0 == null ? 0.0F : ((SettlerWeaponItem)var0.item).getSettlerWeaponValue(var1, var0);
         return new ValidatedStorageEquipmentRecord((SettlementStorageRecord)null, var0, var2);
      }

      public static ValidatedStorageEquipmentRecord WeaponItem(GameLinkedList<SettlementStorageRecord>.Element var0, InventoryItem var1, HumanMob var2) {
         float var3 = var1 == null ? 0.0F : ((SettlerWeaponItem)var1.item).getSettlerWeaponValue(var2, var1);
         return new ValidatedStorageEquipmentRecord(var0, var1, var3);
      }

      public static ValidatedStorageEquipmentRecord ArmorItem(InventoryItem var0, HumanMob var1) {
         float var2 = var0 == null ? 0.0F : ((ArmorItem)var0.item).getSettlerEquipmentValue(var0, var1);
         return new ValidatedStorageEquipmentRecord((SettlementStorageRecord)null, var0, var2);
      }

      public static ValidatedStorageEquipmentRecord ArmorItem(GameLinkedList<SettlementStorageRecord>.Element var0, InventoryItem var1, HumanMob var2) {
         float var3 = var1 == null ? 0.0F : ((ArmorItem)var1.item).getSettlerEquipmentValue(var1, var2);
         return new ValidatedStorageEquipmentRecord(var0, var1, var3);
      }
   }
}
