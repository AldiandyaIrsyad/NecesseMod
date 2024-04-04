package necesse.entity.mobs.job;

import java.util.ListIterator;
import java.util.stream.Stream;
import necesse.inventory.InventoryItem;

public interface WorkInventory {
   ListIterator<InventoryItem> listIterator();

   Iterable<InventoryItem> items();

   Stream<InventoryItem> stream();

   void markDirty();

   void add(InventoryItem var1);

   int getCanAddAmount(InventoryItem var1);

   boolean isFull();

   int getTotalItemStacks();

   boolean isEmpty();
}
