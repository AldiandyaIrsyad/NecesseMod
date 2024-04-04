package necesse.inventory.container.settlement.events;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.ItemRegistry;
import necesse.inventory.container.events.ContainerEvent;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.itemFilter.ItemCategoriesFilter;

public class SettlementStorageLimitsEvent extends ContainerEvent {
   public final int tileX;
   public final int tileY;
   public final boolean isItems;
   public final Item item;
   public final ItemCategoriesFilter.ItemLimits limits;
   public final ItemCategory category;
   public final int maxItems;

   public SettlementStorageLimitsEvent(int var1, int var2, Item var3, ItemCategoriesFilter.ItemLimits var4) {
      this.tileX = var1;
      this.tileY = var2;
      this.isItems = true;
      this.limits = var4;
      this.item = var3;
      this.category = null;
      this.maxItems = 0;
   }

   public SettlementStorageLimitsEvent(int var1, int var2, ItemCategory var3, int var4) {
      this.tileX = var1;
      this.tileY = var2;
      this.isItems = false;
      this.item = null;
      this.limits = null;
      this.category = var3;
      this.maxItems = var4;
   }

   public SettlementStorageLimitsEvent(PacketReader var1) {
      super(var1);
      this.tileX = var1.getNextShortUnsigned();
      this.tileY = var1.getNextShortUnsigned();
      this.isItems = var1.getNextBoolean();
      if (this.isItems) {
         this.category = null;
         this.maxItems = 0;
         int var2 = var1.getNextShortUnsigned();
         this.limits = new ItemCategoriesFilter.ItemLimits();
         this.limits.readPacket(var1);
         this.item = ItemRegistry.getItem(var2);
      } else {
         this.item = null;
         this.limits = null;
         this.category = ItemCategory.getCategory(var1.getNextShortUnsigned());
         this.maxItems = var1.getNextInt();
      }

   }

   public void write(PacketWriter var1) {
      var1.putNextShortUnsigned(this.tileX);
      var1.putNextShortUnsigned(this.tileY);
      var1.putNextBoolean(this.isItems);
      if (this.isItems) {
         var1.putNextShortUnsigned(this.item.getID());
         this.limits.writePacket(var1);
      } else {
         var1.putNextShortUnsigned(this.category.id);
         var1.putNextInt(this.maxItems);
      }

   }
}
