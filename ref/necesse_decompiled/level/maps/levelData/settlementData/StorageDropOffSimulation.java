package necesse.level.maps.levelData.settlementData;

import java.util.function.Predicate;
import java.util.function.Supplier;
import necesse.engine.util.GameLinkedList;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;

public class StorageDropOffSimulation {
   protected final LevelStorage storage;
   protected InventoryRange simulatedRange;
   protected GameLinkedList<StorageDropOff> dropOffs = new GameLinkedList();
   protected boolean isDirty;

   public StorageDropOffSimulation(LevelStorage var1) {
      this.storage = var1;
      this.isDirty = true;
   }

   protected void update() {
      InventoryRange var1 = this.storage.getInventoryRange();
      if (var1 == null) {
         this.simulatedRange = new InventoryRange(new Inventory(0));
      } else {
         this.simulatedRange = new InventoryRange(var1.inventory.copy(), var1.startSlot, var1.endSlot);
      }

      this.dropOffs.stream().filter((var1x) -> {
         return !var1x.isReserved(this.storage.level.getWorldEntity());
      }).forEach(StorageDropOff::remove);
      this.dropOffs.forEach(StorageDropOff::addItems);
      this.isDirty = false;
   }

   public StorageDropOff addFutureDropOff(LevelStorage var1, Supplier<InventoryItem> var2) {
      StorageDropOff var3 = new StorageDropOff(var1, this, var2);
      var3.init(this.dropOffs.addLast(var3), this.storage.level.getWorldEntity());
      var3.addItems();
      return var3;
   }

   public int canAddFutureDropOff(InventoryItem var1) {
      if (this.isDirty) {
         this.update();
      }

      return Math.min(this.storage.getFilter().getAddAmount(this.storage.level, var1, this.simulatedRange, false), Math.min(this.simulatedRange.inventory.canAddItem(this.storage.level, (PlayerMob)null, var1, this.simulatedRange.startSlot, this.simulatedRange.endSlot, "hauljob"), var1.getAmount()));
   }

   public int getItemCount(Predicate<InventoryItem> var1, int var2, boolean var3) {
      return getItemCount(var1, var2, var3 ? this.simulatedRange : this.storage.getInventoryRange());
   }

   public static int getItemCount(Predicate<InventoryItem> var0, int var1, InventoryRange var2) {
      int var3 = 0;
      if (var2 != null) {
         for(int var4 = var2.startSlot; var4 <= var2.endSlot; ++var4) {
            InventoryItem var5 = var2.inventory.getItem(var4);
            if (var5 != null && var0.test(var5)) {
               var3 += var5.getAmount();
               if (var3 >= var1) {
                  return var1;
               }
            }
         }
      }

      return var3;
   }
}
