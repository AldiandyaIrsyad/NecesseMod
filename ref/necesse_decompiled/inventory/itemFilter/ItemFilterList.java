package necesse.inventory.itemFilter;

import java.util.ArrayList;
import java.util.Iterator;
import necesse.engine.GameLog;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.engine.util.ComputedObjectValue;
import necesse.engine.util.GameMath;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.level.maps.Level;

public class ItemFilterList implements Iterable<ItemFilter> {
   public final ArrayList<ItemFilter> filters;
   public final int minAmount;
   public final int maxAmount;
   public final boolean allowUnfilteredItems;

   public ItemFilterList(int var1, int var2, int var3, boolean var4) {
      this.filters = new ArrayList(var1);
      this.minAmount = var2;
      this.maxAmount = var3;
      this.allowUnfilteredItems = var4;
   }

   public ItemFilterList(int var1, int var2, boolean var3) {
      this(0, var1, var2, var3);
   }

   public ItemFilterList() {
      this(Integer.MAX_VALUE, Integer.MAX_VALUE, true);
   }

   public ItemFilterList(LoadData var1) {
      this.filters = new ArrayList();
      this.minAmount = var1.getInt("minAmount", Integer.MAX_VALUE, 0, Integer.MAX_VALUE, false);
      this.maxAmount = var1.getInt("maxAmount", Integer.MAX_VALUE, 0, Integer.MAX_VALUE, false);
      this.allowUnfilteredItems = var1.getBoolean("allowUnfilteredItems", true, false);
      Iterator var2 = var1.getLoadDataByName("filter").iterator();

      while(var2.hasNext()) {
         LoadData var3 = (LoadData)var2.next();

         try {
            this.add(new ItemFilter(var3));
         } catch (LoadDataException var5) {
            GameLog.warn.println("Could not load item filter: " + var5.getMessage());
         }
      }

   }

   public void addSaveData(SaveData var1) {
      if (this.minAmount != Integer.MAX_VALUE) {
         var1.addInt("minAmount", this.minAmount);
      }

      if (this.maxAmount != Integer.MAX_VALUE) {
         var1.addInt("maxAmount", this.maxAmount);
      }

      var1.addBoolean("allowUnfilteredItems", this.allowUnfilteredItems);
      Iterator var2 = this.iterator();

      while(var2.hasNext()) {
         ItemFilter var3 = (ItemFilter)var2.next();
         SaveData var4 = new SaveData("filter");
         var3.addSaveData(var4);
         var1.addSaveData(var4);
      }

   }

   public ItemFilterList(PacketReader var1) {
      this.minAmount = var1.getNextInt();
      this.maxAmount = var1.getNextInt();
      this.allowUnfilteredItems = var1.getNextBoolean();
      int var2 = var1.getNextShortUnsigned();
      this.filters = new ArrayList(var2);

      for(int var3 = 0; var3 < var2; ++var3) {
         this.add(new ItemFilter(var1));
      }

   }

   public void writePacket(PacketWriter var1) {
      var1.putNextInt(this.minAmount);
      var1.putNextInt(this.maxAmount);
      var1.putNextBoolean(this.allowUnfilteredItems);
      var1.putNextShortUnsigned(this.size());
      Iterator var2 = this.iterator();

      while(var2.hasNext()) {
         ItemFilter var3 = (ItemFilter)var2.next();
         var3.writePacket(var1);
      }

   }

   public Iterator<ItemFilter> iterator() {
      return this.filters.iterator();
   }

   public boolean add(ItemFilter var1) {
      if (this.hasFilter(var1)) {
         return false;
      } else {
         this.filters.add(var1);
         return true;
      }
   }

   public boolean hasFilter(ItemFilter var1) {
      return this.filters.stream().anyMatch((var1x) -> {
         return var1x.isSameFilter(var1);
      });
   }

   public int size() {
      return this.filters.size();
   }

   public ItemFilter get(int var1) {
      return (ItemFilter)this.filters.get(var1);
   }

   public void clear() {
      this.filters.clear();
   }

   public ItemFilterList copy() {
      Packet var1 = new Packet();
      this.writePacket(new PacketWriter(var1));
      return new ItemFilterList(new PacketReader(var1));
   }

   public int getAddAmount(Level var1, InventoryItem var2, InventoryRange var3) {
      if (this.filters.isEmpty() && !this.allowUnfilteredItems) {
         return 0;
      } else {
         ComputedObjectValue var4 = new ComputedObjectValue(var3, () -> {
            int var1 = 0;

            for(int var2 = var3.startSlot; var2 <= var3.endSlot; ++var2) {
               var1 += var3.inventory.getAmount(var2);
            }

            return var1;
         });
         if ((Integer)var4.get() >= this.minAmount) {
            return 0;
         } else {
            int var5 = var2.getAmount();
            if (this.maxAmount != Integer.MAX_VALUE) {
               var5 = GameMath.limit(this.maxAmount - (Integer)var4.get(), 0, var2.getAmount());
            }

            if (this.filters.isEmpty()) {
               return Math.min(var2.getAmount(), var5);
            } else {
               boolean var6 = false;
               int var7 = 0;
               Iterator var8 = this.iterator();

               while(var8.hasNext()) {
                  ItemFilter var9 = (ItemFilter)var8.next();
                  if (var9.matchesItem(var2)) {
                     var6 = true;
                     var7 = Math.max(var9.getAddAmount(var1, var2, var3), var7);
                     if (var7 >= var2.getAmount()) {
                        break;
                     }
                  }
               }

               if (var6) {
                  return GameMath.limit(var7, 0, var5);
               } else {
                  return this.allowUnfilteredItems ? var2.getAmount() : 0;
               }
            }
         }
      }
   }

   public int getRemoveAmount(Level var1, InventoryItem var2, InventoryRange var3) {
      if (this.filters.isEmpty() && !this.allowUnfilteredItems) {
         return var2.getAmount();
      } else {
         int var4 = 0;
         int var6;
         if (this.maxAmount != Integer.MAX_VALUE) {
            int var5 = 0;

            for(var6 = var3.startSlot; var6 <= var3.endSlot; ++var6) {
               var5 += var3.inventory.getAmount(var6);
            }

            var4 = Math.max(var5 - this.maxAmount, 0);
         }

         if (this.filters.isEmpty()) {
            return var4;
         } else {
            boolean var9 = false;
            var6 = var2.getAmount();
            Iterator var7 = this.iterator();

            while(var7.hasNext()) {
               ItemFilter var8 = (ItemFilter)var7.next();
               if (var8.matchesItem(var2)) {
                  var9 = true;
                  var6 = Math.min(var8.getRemoveAmount(var1, var2, var3), var6);
                  if (var6 <= 0) {
                     break;
                  }
               }
            }

            if (var9) {
               return GameMath.limit(var6, var4, var2.getAmount());
            } else {
               return this.allowUnfilteredItems ? 0 : var2.getAmount();
            }
         }
      }
   }

   public boolean matchesItem(InventoryItem var1) {
      if (this.filters.isEmpty()) {
         return this.allowUnfilteredItems;
      } else {
         Iterator var2 = this.iterator();

         ItemFilter var3;
         do {
            if (!var2.hasNext()) {
               return this.allowUnfilteredItems;
            }

            var3 = (ItemFilter)var2.next();
         } while(!var3.matchesItem(var1));

         return true;
      }
   }
}
