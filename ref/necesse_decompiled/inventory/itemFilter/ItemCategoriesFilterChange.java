package necesse.inventory.itemFilter;

import necesse.engine.GameLog;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.ItemRegistry;
import necesse.inventory.item.Item;

public class ItemCategoriesFilterChange {
   private Packet packet;

   private ItemCategoriesFilterChange(Packet var1) {
      this.packet = var1;
   }

   public boolean applyTo(ItemCategoriesFilter var1) {
      if (var1 == null) {
         return false;
      } else {
         PacketReader var2 = new PacketReader(this.packet);
         ChangeType[] var3 = ItemCategoriesFilterChange.ChangeType.values();
         int var4 = var2.getNextMaxValue(var3.length + 1);
         if (var4 >= 0 && var4 < var3.length) {
            ChangeType var5 = var3[var4];
            int var6;
            ItemCategoriesFilter.ItemCategoryFilter var8;
            switch (var5) {
               case ITEMS_ALLOWED:
                  var6 = var2.getNextShortUnsigned();
                  Item[] var16 = new Item[var6];

                  for(int var18 = 0; var18 < var16.length; ++var18) {
                     var16[var18] = ItemRegistry.getItem(var2.getNextShortUnsigned());
                     if (var16[var18] == null) {
                        return false;
                     }
                  }

                  boolean var19 = var2.getNextBoolean();
                  boolean var9 = false;
                  Item[] var10 = var16;
                  int var11 = var16.length;

                  for(int var12 = 0; var12 < var11; ++var12) {
                     Item var13 = var10[var12];
                     var9 = var1.setItemAllowed(var13, var19) || var9;
                  }

                  return var9;
               case ITEM_LIMITS:
                  var6 = var2.getNextShortUnsigned();
                  Item var15 = ItemRegistry.getItem(var6);
                  if (var15 == null) {
                     return false;
                  }

                  ItemCategoriesFilter.ItemLimits var17 = new ItemCategoriesFilter.ItemLimits();
                  var17.readPacket(var2);
                  return var1.setItemAllowed(var15, var17);
               case CATEGORY_ALLOWED:
                  var6 = var2.getNextShortUnsigned();
                  boolean var14 = var2.getNextBoolean();
                  var8 = var1.getItemCategory(var6);
                  if (var8 != null) {
                     if (var14 && !var8.isAllAllowed()) {
                        var8.setAllowed(true);
                        return true;
                     } else if (!var14 && var8.isAnyAllowed()) {
                        var8.setAllowed(false);
                        return true;
                     }
                  }
               default:
                  return true;
               case CATEGORY_LIMIT:
                  var6 = var2.getNextShortUnsigned();
                  int var7 = var2.getNextInt();
                  var8 = var1.getItemCategory(var6);
                  if (var8 != null && var8.getMaxItems() != var7) {
                     var8.setMaxItems(var7);
                     return true;
                  }

                  return false;
               case FULL:
                  var1.readPacket(var2);
                  return true;
            }
         } else {
            GameLog.warn.println("Tried to apply invalid ItemCategoriesFilterChange to type index " + var4);
            return false;
         }
      }
   }

   public void write(PacketWriter var1) {
      var1.putNextContentPacket(this.packet);
   }

   public static ItemCategoriesFilterChange fromPacket(PacketReader var0) {
      return new ItemCategoriesFilterChange(var0.getNextContentPacket());
   }

   public static ItemCategoriesFilterChange itemsAllowed(Item[] var0, boolean var1) {
      Packet var2 = new Packet();
      PacketWriter var3 = new PacketWriter(var2);
      var3.putNextMaxValue(ItemCategoriesFilterChange.ChangeType.ITEMS_ALLOWED.ordinal(), ItemCategoriesFilterChange.ChangeType.values().length + 1);
      var3.putNextShortUnsigned(var0.length);
      Item[] var4 = var0;
      int var5 = var0.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Item var7 = var4[var6];
         var3.putNextShortUnsigned(var7.getID());
      }

      var3.putNextBoolean(var1);
      return new ItemCategoriesFilterChange(var2);
   }

   public static ItemCategoriesFilterChange itemLimits(Item var0, ItemCategoriesFilter.ItemLimits var1) {
      Packet var2 = new Packet();
      PacketWriter var3 = new PacketWriter(var2);
      var3.putNextMaxValue(ItemCategoriesFilterChange.ChangeType.ITEM_LIMITS.ordinal(), ItemCategoriesFilterChange.ChangeType.values().length + 1);
      var3.putNextShortUnsigned(var0.getID());
      var1.writePacket(var3);
      return new ItemCategoriesFilterChange(var2);
   }

   public static ItemCategoriesFilterChange categoryAllowed(ItemCategoriesFilter.ItemCategoryFilter var0, boolean var1) {
      Packet var2 = new Packet();
      PacketWriter var3 = new PacketWriter(var2);
      var3.putNextMaxValue(ItemCategoriesFilterChange.ChangeType.CATEGORY_ALLOWED.ordinal(), ItemCategoriesFilterChange.ChangeType.values().length + 1);
      var3.putNextShortUnsigned(var0.category.id);
      var3.putNextBoolean(var1);
      return new ItemCategoriesFilterChange(var2);
   }

   public static ItemCategoriesFilterChange categoryLimit(ItemCategoriesFilter.ItemCategoryFilter var0, int var1) {
      Packet var2 = new Packet();
      PacketWriter var3 = new PacketWriter(var2);
      var3.putNextMaxValue(ItemCategoriesFilterChange.ChangeType.CATEGORY_LIMIT.ordinal(), ItemCategoriesFilterChange.ChangeType.values().length + 1);
      var3.putNextShortUnsigned(var0.category.id);
      var3.putNextInt(var1);
      return new ItemCategoriesFilterChange(var2);
   }

   public static ItemCategoriesFilterChange fullChange(ItemCategoriesFilter var0) {
      Packet var1 = new Packet();
      PacketWriter var2 = new PacketWriter(var1);
      var2.putNextMaxValue(ItemCategoriesFilterChange.ChangeType.FULL.ordinal(), ItemCategoriesFilterChange.ChangeType.values().length + 1);
      var0.writePacket(var2);
      return new ItemCategoriesFilterChange(var1);
   }

   private static enum ChangeType {
      ITEMS_ALLOWED,
      ITEM_LIMITS,
      CATEGORY_ALLOWED,
      CATEGORY_LIMIT,
      FULL;

      private ChangeType() {
      }

      // $FF: synthetic method
      private static ChangeType[] $values() {
         return new ChangeType[]{ITEMS_ALLOWED, ITEM_LIMITS, CATEGORY_ALLOWED, CATEGORY_LIMIT, FULL};
      }
   }
}
