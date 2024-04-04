package necesse.inventory.container.settlement.events;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.inventory.container.events.ContainerEvent;
import necesse.inventory.item.Item;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.level.maps.levelData.settlementData.SettlementInventory;

public class SettlementOpenStorageConfigEvent extends ContainerEvent {
   public final int tileX;
   public final int tileY;
   public final Packet filterContent;
   public final int priority;

   public SettlementOpenStorageConfigEvent(SettlementInventory var1) {
      this.tileX = var1.tileX;
      this.tileY = var1.tileY;
      if (var1.filter == null) {
         this.filterContent = null;
      } else {
         this.filterContent = new Packet();
         var1.filter.writePacket(new PacketWriter(this.filterContent));
      }

      this.priority = var1.priority;
   }

   public SettlementOpenStorageConfigEvent(PacketReader var1) {
      super(var1);
      this.tileX = var1.getNextInt();
      this.tileY = var1.getNextInt();
      boolean var2 = var1.getNextBoolean();
      if (var2) {
         this.priority = var1.getNextInt();
         this.filterContent = var1.getNextContentPacket();
      } else {
         this.filterContent = null;
         this.priority = 0;
      }

   }

   public ItemCategoriesFilter getFilter(final OEInventory var1) {
      ItemCategoriesFilter var2 = new ItemCategoriesFilter(false) {
         public boolean isItemDisabled(Item var1x) {
            if (super.isItemDisabled(var1x)) {
               return true;
            } else {
               return var1 != null && var1.isSettlementStorageItemDisabled(var1x);
            }
         }
      };
      var2.readPacket(new PacketReader(this.filterContent));
      return var2;
   }

   public void write(PacketWriter var1) {
      var1.putNextInt(this.tileX);
      var1.putNextInt(this.tileY);
      var1.putNextBoolean(this.filterContent != null);
      if (this.filterContent != null) {
         var1.putNextInt(this.priority);
         var1.putNextContentPacket(this.filterContent);
      }

   }
}
