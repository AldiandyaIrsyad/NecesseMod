package necesse.inventory.container.settlement.events;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.ItemRegistry;
import necesse.inventory.container.events.ContainerEvent;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;

public class SettlementStorageChangeAllowedEvent extends ContainerEvent {
   public final int tileX;
   public final int tileY;
   public final boolean isItems;
   public final boolean allowed;
   public final Item[] items;
   public final ItemCategory category;

   public SettlementStorageChangeAllowedEvent(int var1, int var2, Item[] var3, boolean var4) {
      this.tileX = var1;
      this.tileY = var2;
      this.allowed = var4;
      this.isItems = true;
      this.items = var3;
      this.category = null;
   }

   public SettlementStorageChangeAllowedEvent(int var1, int var2, ItemCategory var3, boolean var4) {
      this.tileX = var1;
      this.tileY = var2;
      this.allowed = var4;
      this.isItems = false;
      this.items = null;
      this.category = var3;
   }

   public SettlementStorageChangeAllowedEvent(PacketReader var1) {
      super(var1);
      this.tileX = var1.getNextShortUnsigned();
      this.tileY = var1.getNextShortUnsigned();
      this.allowed = var1.getNextBoolean();
      this.isItems = var1.getNextBoolean();
      if (this.isItems) {
         this.category = null;
         int var2 = var1.getNextShortUnsigned();
         this.items = new Item[var2];

         for(int var3 = 0; var3 < var2; ++var3) {
            int var4 = var1.getNextShortUnsigned();
            this.items[var3] = ItemRegistry.getItem(var4);
         }
      } else {
         this.items = null;
         this.category = ItemCategory.getCategory(var1.getNextShortUnsigned());
      }

   }

   public void write(PacketWriter var1) {
      var1.putNextShortUnsigned(this.tileX);
      var1.putNextShortUnsigned(this.tileY);
      var1.putNextBoolean(this.allowed);
      var1.putNextBoolean(this.isItems);
      if (this.isItems) {
         var1.putNextShortUnsigned(this.items.length);
         Item[] var2 = this.items;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Item var5 = var2[var4];
            var1.putNextShortUnsigned(var5.getID());
         }
      } else {
         var1.putNextShortUnsigned(this.category.id);
      }

   }
}
