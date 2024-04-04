package necesse.level.maps.levelData.settlementData;

import java.util.function.Supplier;
import necesse.engine.util.GameLinkedList;
import necesse.engine.world.WorldEntity;
import necesse.entity.Entity;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.InventoryAddConsumer;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.inventory.itemFilter.ItemCategoriesFilter;

public class StorageDropOff {
   public final LevelStorage storage;
   private final StorageDropOffSimulation simulation;
   private GameLinkedList<StorageDropOff>.Element element;
   private InventoryItem lastItem;
   private int canAddAmount;
   protected Supplier<InventoryItem> itemSupplier;
   protected long reserveTick;

   protected StorageDropOff(LevelStorage var1, StorageDropOffSimulation var2, Supplier<InventoryItem> var3) {
      this.storage = var1;
      this.simulation = var2;
      this.itemSupplier = var3;
   }

   protected void init(GameLinkedList<StorageDropOff>.Element var1, WorldEntity var2) {
      if (this.element != null) {
         throw new IllegalStateException("DropOff already initialized");
      } else {
         this.element = var1;
         this.reserve(var2);
      }
   }

   public void reserve(WorldEntity var1) {
      this.reserveTick = var1.getGameTicks();
   }

   public void reserve(Entity var1) {
      this.reserve(var1.getWorldEntity());
   }

   public boolean isReserved(WorldEntity var1) {
      return this.reserveTick >= var1.getGameTicks() - 2L;
   }

   protected void addItems() {
      InventoryItem var1 = (InventoryItem)this.itemSupplier.get();
      this.lastItem = var1;
      if (var1 != null && this.simulation.simulatedRange != null) {
         ItemCategoriesFilter var2 = this.simulation.storage.getFilter();
         int var3 = var2 == null ? var1.getAmount() : var2.getAddAmount(this.simulation.storage.level, var1, this.simulation.simulatedRange, true);
         if (var3 <= 0) {
            this.canAddAmount = 0;
         } else {
            InventoryItem var4 = var1.copy();
            this.simulation.simulatedRange.inventory.addItem(this.simulation.storage.level, (PlayerMob)null, var4, this.simulation.simulatedRange.startSlot, this.simulation.simulatedRange.endSlot, "hauljob", (InventoryAddConsumer)null);
            this.canAddAmount = var1.getAmount() - var4.getAmount();
         }
      } else {
         this.canAddAmount = -1;
      }
   }

   public int canAddAmount() {
      if (this.element.isRemoved()) {
         return -1;
      } else {
         InventoryItem var1 = (InventoryItem)this.itemSupplier.get();
         if (this.simulation.isDirty) {
            this.simulation.update();
         } else if (this.lastItem != var1 && (var1 == null || this.lastItem == null || var1.getAmount() != this.lastItem.getAmount() || !var1.equals(this.storage.level, this.lastItem, true, false, "dropoffs"))) {
            this.simulation.update();
         }

         return this.canAddAmount;
      }
   }

   public boolean canAddFullAmount() {
      int var1 = this.canAddAmount();
      if (this.lastItem == null) {
         return false;
      } else {
         return var1 >= this.lastItem.getAmount();
      }
   }

   public void remove() {
      this.simulation.isDirty = true;
      if (!this.element.isRemoved()) {
         this.element.remove();
      }

   }

   public int addItem(InventoryItem var1) {
      InventoryRange var2 = this.simulation.storage.getInventoryRange();
      ItemCategoriesFilter var3 = this.simulation.storage.getFilter();
      if (var2 != null) {
         int var4 = var3 == null ? var1.getAmount() : Math.min(var3.getAddAmount(this.simulation.storage.level, var1, var2, true), var1.getAmount());
         if (var4 > 0) {
            InventoryItem var5 = var1.copy(var4);
            var2.inventory.addItem(this.simulation.storage.level, (PlayerMob)null, var5, var2.startSlot, var2.endSlot, "hauljob", (InventoryAddConsumer)null);
            this.remove();
            return var4 - var5.getAmount();
         }
      }

      return 0;
   }

   public InventoryItem getItem() {
      return (InventoryItem)this.itemSupplier.get();
   }
}
