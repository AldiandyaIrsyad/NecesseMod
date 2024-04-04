package necesse.entity.pickup;

public class ItemPickupReservedCombinedEvent {
   public final ItemPickupReservedAmount reserved;
   public final ItemPickupEntity next;
   public final int combinedAmount;

   public ItemPickupReservedCombinedEvent(ItemPickupReservedAmount var1, ItemPickupEntity var2, int var3) {
      this.reserved = var1;
      this.next = var2;
      this.combinedAmount = var3;
   }
}
