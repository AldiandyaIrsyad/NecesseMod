package necesse.inventory.container.settlement.events;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.inventory.itemFilter.ItemCategoriesFilter;

public class SettlementStoragePriorityLimitEvent extends ContainerEvent {
   public final int tileX;
   public final int tileY;
   public final boolean isPriority;
   public final int priority;
   public final ItemCategoriesFilter.ItemLimitMode limitMode;
   public final int limit;

   public SettlementStoragePriorityLimitEvent(int var1, int var2, int var3) {
      this.tileX = var1;
      this.tileY = var2;
      this.isPriority = true;
      this.priority = var3;
      this.limitMode = null;
      this.limit = 0;
   }

   public SettlementStoragePriorityLimitEvent(int var1, int var2, ItemCategoriesFilter.ItemLimitMode var3, int var4) {
      this.tileX = var1;
      this.tileY = var2;
      this.isPriority = false;
      this.limitMode = var3;
      this.limit = var4;
      this.priority = 0;
   }

   public SettlementStoragePriorityLimitEvent(PacketReader var1) {
      super(var1);
      this.tileX = var1.getNextShortUnsigned();
      this.tileY = var1.getNextShortUnsigned();
      this.isPriority = var1.getNextBoolean();
      if (this.isPriority) {
         this.priority = var1.getNextInt();
         this.limitMode = null;
         this.limit = 0;
      } else {
         this.priority = 0;
         this.limitMode = (ItemCategoriesFilter.ItemLimitMode)var1.getNextEnum(ItemCategoriesFilter.ItemLimitMode.class);
         this.limit = var1.getNextInt();
      }

   }

   public void write(PacketWriter var1) {
      var1.putNextShortUnsigned(this.tileX);
      var1.putNextShortUnsigned(this.tileY);
      var1.putNextBoolean(this.isPriority);
      if (this.isPriority) {
         var1.putNextInt(this.priority);
      } else {
         var1.putNextEnum(this.limitMode);
         var1.putNextInt(this.limit);
      }

   }
}
