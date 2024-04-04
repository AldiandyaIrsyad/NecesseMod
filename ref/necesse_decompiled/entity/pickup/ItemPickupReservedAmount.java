package necesse.entity.pickup;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Consumer;
import necesse.engine.util.GameLinkedList;
import necesse.engine.world.WorldEntity;
import necesse.entity.Entity;
import necesse.inventory.InventoryItem;

public class ItemPickupReservedAmount {
   public final ItemPickupEntity entity;
   private GameLinkedList<ItemPickupReservedAmount>.Element element;
   public final int pickupAmount;
   protected int prevAmountReserved;
   protected long reserveTick;
   protected LinkedList<Consumer<ItemPickupReservedCombinedEvent>> pickedUpListeners = new LinkedList();

   protected ItemPickupReservedAmount(ItemPickupEntity var1, int var2, int var3) {
      this.entity = var1;
      this.pickupAmount = var2;
      this.prevAmountReserved = var3;
   }

   protected void init(GameLinkedList<ItemPickupReservedAmount>.Element var1, WorldEntity var2) {
      if (this.element != null) {
         throw new IllegalStateException("Storage slot already initialized");
      } else {
         this.element = var1;
         this.reserve(var2);
      }
   }

   public void submitCombinedEvent(ItemPickupReservedCombinedEvent var1) {
      Iterator var2 = this.pickedUpListeners.iterator();

      while(var2.hasNext()) {
         Consumer var3 = (Consumer)var2.next();
         var3.accept(var1);
      }

   }

   public void onItemCombined(Consumer<ItemPickupReservedCombinedEvent> var1) {
      this.pickedUpListeners.add(var1);
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

   public void remove() {
      ItemPickupReservedAmount var10000;
      for(GameLinkedList.Element var1 = this.element; var1.hasPrev(); var10000.prevAmountReserved -= this.pickupAmount) {
         var1 = var1.prev();
         var10000 = (ItemPickupReservedAmount)var1.object;
      }

      this.element.remove();
   }

   public boolean isValid() {
      if (this.element.isRemoved()) {
         return false;
      } else if (this.entity.removed()) {
         return false;
      } else if (this.entity.item.getAmount() < this.pickupAmount) {
         this.remove();
         return false;
      } else {
         return true;
      }
   }

   public InventoryItem pickupItem() {
      if (this.element.isRemoved()) {
         return null;
      } else if (this.entity.removed()) {
         this.remove();
         return null;
      } else {
         InventoryItem var1 = this.entity.item.copy(Math.min(this.entity.item.getAmount(), this.pickupAmount));
         this.entity.item.setAmount(this.entity.item.getAmount() - var1.getAmount());
         if (this.entity.item.getAmount() <= 0) {
            this.entity.remove();
         } else {
            this.entity.onItemUpdated();
            this.entity.markDirty();
         }

         this.remove();
         return var1;
      }
   }

   public boolean isRemoved() {
      return this.element.isRemoved();
   }
}
