package necesse.inventory.itemFilter;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.VersionMigration;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class ItemFilter {
   public final int itemID;
   public final int maxAmount;

   public ItemFilter(int var1, int var2) {
      this.itemID = var1;
      this.maxAmount = var2;
   }

   public ItemFilter(int var1) {
      this(var1, Integer.MAX_VALUE);
   }

   public ItemFilter(LoadData var1) throws LoadDataException {
      String var2 = var1.getUnsafeString("itemStringID", (String)null, false);
      if (var2 == null) {
         throw new LoadDataException("Could not find itemStringID");
      } else {
         var2 = VersionMigration.tryFixStringID(var2, VersionMigration.oldItemStringIDs);
         this.itemID = ItemRegistry.getItemID(var2);
         if (this.itemID == -1) {
            throw new LoadDataException("Could not find item with stringID " + var2);
         } else {
            this.maxAmount = var1.getInt("maxAmount", Integer.MAX_VALUE, 0, Integer.MAX_VALUE, false);
         }
      }
   }

   public void addSaveData(SaveData var1) {
      var1.addUnsafeString("itemStringID", ItemRegistry.getItemStringID(this.itemID));
      if (this.maxAmount != Integer.MAX_VALUE) {
         var1.addInt("maxAmount", this.maxAmount);
      }

   }

   public ItemFilter(PacketReader var1) {
      this.itemID = var1.getNextShortUnsigned();
      this.maxAmount = var1.getNextInt();
   }

   public void writePacket(PacketWriter var1) {
      var1.putNextShortUnsigned(this.itemID);
      var1.putNextInt(this.maxAmount);
   }

   public int getAddAmount(Level var1, InventoryItem var2, InventoryRange var3) {
      if (this.maxAmount == Integer.MAX_VALUE) {
         return var2.getAmount();
      } else {
         int var4 = var3.inventory.getAmount(var1, (PlayerMob)null, (Item)ItemRegistry.getItem(this.itemID), var3.startSlot, var3.endSlot, "filteramount");
         return GameMath.limit(this.maxAmount - var4, 0, var2.getAmount());
      }
   }

   public int getRemoveAmount(Level var1, InventoryItem var2, InventoryRange var3) {
      if (this.maxAmount == Integer.MAX_VALUE) {
         return 0;
      } else {
         int var4 = var3.inventory.getAmount(var1, (PlayerMob)null, (Item)ItemRegistry.getItem(this.itemID), var3.startSlot, var3.endSlot, "filteramount");
         return Math.max(0, var4 - this.maxAmount);
      }
   }

   public boolean matchesItem(InventoryItem var1) {
      return var1.item.getID() == this.itemID;
   }

   public boolean isSameFilter(ItemFilter var1) {
      return this.itemID == var1.itemID;
   }

   public boolean equals(Object var1) {
      return var1 instanceof ItemFilter ? this.isSameFilter((ItemFilter)var1) : super.equals(var1);
   }
}
