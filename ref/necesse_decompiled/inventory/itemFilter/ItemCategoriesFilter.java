package necesse.inventory.itemFilter;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.stream.Stream;
import necesse.engine.GameLog;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.VersionMigration;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.level.maps.Level;

public class ItemCategoriesFilter {
   private HashMap<Integer, ItemCategoryFilter> itemIDCategories;
   private HashMap<Integer, ItemCategoryFilter> categoryIDs;
   public final ItemCategoryFilter master;
   public ItemLimitMode limitMode;
   public int minAmount;
   public int maxAmount;

   public ItemCategoriesFilter(ItemCategory var1, int var2, int var3, boolean var4) {
      this.itemIDCategories = new HashMap();
      this.categoryIDs = new HashMap();
      this.limitMode = ItemCategoriesFilter.ItemLimitMode.TOTAL_ITEMS;
      this.minAmount = var2;
      this.maxAmount = var3;
      this.master = new ItemCategoryFilter(var1, var4, (ItemCategoryFilter)null);
   }

   public ItemCategoriesFilter(int var1, int var2, boolean var3) {
      this(ItemCategory.masterCategory, var1, var2, var3);
   }

   public ItemCategoriesFilter(ItemCategory var1, boolean var2) {
      this(var1, Integer.MAX_VALUE, Integer.MAX_VALUE, var2);
   }

   public ItemCategoriesFilter(boolean var1) {
      this(ItemCategory.masterCategory, var1);
   }

   public ItemCategoriesFilter(ItemCategory var1, int var2, int var3) {
      this(var1, var2, var3, true);
   }

   public ItemCategoriesFilter(int var1, int var2) {
      this(ItemCategory.masterCategory, var1, var2);
   }

   public void addSaveData(SaveData var1) {
      var1.addEnum("limitMode", this.limitMode);
      if (this.minAmount != Integer.MAX_VALUE) {
         var1.addInt("minAmount", this.minAmount);
      }

      if (this.maxAmount != Integer.MAX_VALUE) {
         var1.addInt("maxAmount", this.maxAmount);
      }

      SaveData var2 = new SaveData("categories");
      LinkedList var3 = new LinkedList();
      SaveData var4 = new SaveData("items");
      this.master.addSaveData(var2, var3, var4);
      var1.addSaveData(var2);
      var1.addStringList("itemsAllowed", var3);
      if (!var4.isEmpty()) {
         var1.addSaveData(var4);
      }

   }

   public void applyLoadData(LoadData var1) {
      this.limitMode = (ItemLimitMode)var1.getEnum(ItemLimitMode.class, "limitMode", this.limitMode, false);
      this.minAmount = var1.getInt("minAmount", Integer.MAX_VALUE, 0, Integer.MAX_VALUE, false);
      this.maxAmount = var1.getInt("maxAmount", Integer.MAX_VALUE, 0, Integer.MAX_VALUE, false);
      boolean var2 = var1.getBoolean("allowUnfilteredItems", true, false);
      if (!var2) {
         this.master.setAllowed(false);
      }

      LoadData var3 = var1.getFirstLoadDataByName("categories");
      if (var3 != null) {
         this.master.applyLoadData(var3);
      }

      List var4 = var1.getStringList("itemsAllowed", new LinkedList(), false);
      Iterator var5 = var4.iterator();

      while(var5.hasNext()) {
         String var6 = (String)var5.next();
         var6 = VersionMigration.tryFixStringID(var6, VersionMigration.oldItemStringIDs);
         int var7 = ItemRegistry.getItemID(var6);
         if (var7 != -1) {
            Item var8 = ItemRegistry.getItem(var7);
            if (var8 != null && !this.isItemDisabled(var8)) {
               this.setItemAllowed(var8, true);
            }
         }
      }

      LoadData var13 = var1.getFirstLoadDataByName("items");
      Iterator var14;
      LoadData var15;
      if (var13 != null) {
         var14 = var13.getLoadData().iterator();

         while(var14.hasNext()) {
            var15 = (LoadData)var14.next();
            String var16 = var15.getUnsafeString("itemStringID", (String)null);
            if (var16 != null) {
               var16 = VersionMigration.tryFixStringID(var16, VersionMigration.oldItemStringIDs);
               int var9 = ItemRegistry.getItemID(var16);
               if (var9 != -1) {
                  Item var10 = ItemRegistry.getItem(var9);
                  if (var10 != null && !this.isItemDisabled(var10)) {
                     ItemLimits var11 = new ItemLimits();
                     var11.applyLoadData(var15);
                     this.setItemAllowed(var10, var11);
                  }
               }
            }
         }
      }

      if (var1.getFirstLoadDataByName("filter") == null && var3 == null && !var2) {
         throw new LoadDataException("Missing categories");
      } else {
         var14 = var1.getLoadDataByName("filter").iterator();

         while(var14.hasNext()) {
            var15 = (LoadData)var14.next();

            try {
               ItemFilter var17 = new ItemFilter(var15);
               Item var18 = ItemRegistry.getItem(var17.itemID);
               if (var18 != null) {
                  ItemCategoryFilter var19 = this.getItemCategory(var18);
                  var19.setItemAllowed(var18.getID(), var17.maxAmount > 0, var17.maxAmount);
               }
            } catch (LoadDataException var12) {
               GameLog.warn.println("Could not load item filter: " + var12.getMessage());
            }
         }

         this.master.fixAllowedVariablesChildren();
      }
   }

   public void writePacket(PacketWriter var1) {
      var1.putNextEnum(this.limitMode);
      boolean var2 = this.minAmount != Integer.MAX_VALUE || this.maxAmount != Integer.MAX_VALUE;
      var1.putNextBoolean(var2);
      if (var2) {
         var1.putNextInt(this.minAmount);
         var1.putNextInt(this.maxAmount);
      }

      this.master.writePacket(var1);
   }

   public void readPacket(PacketReader var1) {
      this.limitMode = (ItemLimitMode)var1.getNextEnum(ItemLimitMode.class);
      if (var1.getNextBoolean()) {
         this.minAmount = var1.getNextInt();
         this.maxAmount = var1.getNextInt();
      } else {
         this.minAmount = Integer.MAX_VALUE;
         this.maxAmount = Integer.MAX_VALUE;
      }

      this.master.readPacket(var1);
   }

   public boolean isEqualsFilter(ItemCategoriesFilter var1) {
      if (var1 == this) {
         return true;
      } else if (this.limitMode != var1.limitMode) {
         return false;
      } else if (this.maxAmount != var1.maxAmount) {
         return false;
      } else {
         return this.minAmount != var1.minAmount ? false : this.isEqualsFilter(this.master, var1.master);
      }
   }

   private boolean isEqualsFilter(ItemCategoryFilter var1, ItemCategoryFilter var2) {
      if (var1 == var2) {
         return true;
      } else if (var1.category != var2.category) {
         return false;
      } else if (var1.maxItems != var2.maxItems) {
         return false;
      } else if (var1.allChildrenAllowed != var2.allChildrenAllowed) {
         return false;
      } else if (var1.allItemsAllowed != var2.allItemsAllowed) {
         return false;
      } else if (var1.anyChildrenAllowed != var2.anyChildrenAllowed) {
         return false;
      } else if (var1.anyItemsAllowed != var2.anyItemsAllowed) {
         return false;
      } else if (var1.allChildrenDefault != var2.allChildrenDefault) {
         return false;
      } else if (var1.allItemsDefault != var2.allItemsDefault) {
         return false;
      } else if (var1.streamItems().anyMatch((var2x) -> {
         ItemLimits var3 = (ItemLimits)var1.itemIDsAllowed.get(var2x.getID());
         ItemLimits var4 = (ItemLimits)var2.itemIDsAllowed.get(var2x.getID());
         if (var3 == var4) {
            return false;
         } else if (var3 != null && var4 != null) {
            return !var3.isSame(var4);
         } else {
            return true;
         }
      })) {
         return false;
      } else {
         Iterator var3 = var1.children.entrySet().iterator();

         ItemCategoryFilter var5;
         ItemCategoryFilter var6;
         do {
            if (!var3.hasNext()) {
               return true;
            }

            Map.Entry var4 = (Map.Entry)var3.next();
            var5 = (ItemCategoryFilter)var4.getValue();
            var6 = (ItemCategoryFilter)var2.children.get(var4.getKey());
         } while(this.isEqualsFilter(var5, var6));

         return false;
      }
   }

   public ItemCategoriesFilter copy() {
      ItemCategoriesFilter var1 = new ItemCategoriesFilter(this.master.category, true);
      var1.loadFromCopy(this);
      return var1;
   }

   public boolean loadFromCopy(ItemCategoriesFilter var1) {
      boolean var2 = false;
      if (this.limitMode != var1.limitMode) {
         var2 = true;
      }

      this.limitMode = var1.limitMode;
      if (this.maxAmount != var1.maxAmount || this.minAmount != var1.minAmount) {
         var2 = true;
      }

      this.maxAmount = var1.maxAmount;
      this.minAmount = var1.minAmount;
      return this.applyFromCopyCategory(this.master, var1.master) || var2;
   }

   private boolean applyFromCopyCategory(ItemCategoryFilter var1, ItemCategoryFilter var2) {
      AtomicBoolean var3 = new AtomicBoolean();
      if (var1.maxItems != var2.maxItems) {
         var1.maxItems = var2.maxItems;
         var3.set(true);
      }

      var1.allChildrenAllowed = var2.allChildrenAllowed;
      var1.anyChildrenAllowed = var2.anyChildrenAllowed;
      var1.allItemsAllowed = var2.allItemsAllowed;
      var1.anyItemsAllowed = var2.anyItemsAllowed;
      var1.allChildrenDefault = var2.allChildrenDefault;
      var1.allItemsDefault = var2.allItemsDefault;
      Iterator var4 = var2.children.entrySet().iterator();

      while(var4.hasNext()) {
         Map.Entry var5 = (Map.Entry)var4.next();
         ItemCategoryFilter var6 = (ItemCategoryFilter)var1.children.get(var5.getKey());
         ItemCategoryFilter var7 = (ItemCategoryFilter)var5.getValue();
         if (var6 != null && this.applyFromCopyCategory(var6, var7)) {
            var3.set(true);
         }
      }

      var1.streamItems().forEach((var3x) -> {
         ItemLimits var4 = (ItemLimits)var2.itemIDsAllowed.get(var3x.getID());
         if (var1.setItemAllowed(var3x.getID(), var4)) {
            var3.set(true);
         }

      });
      return var3.get();
   }

   public boolean isItemDisabled(Item var1) {
      return !ItemRegistry.isObtainable(var1.getID()) && this.getItemCategory(var1) != null;
   }

   public ItemCategoryFilter getItemCategory(Item var1) {
      return (ItemCategoryFilter)this.itemIDCategories.get(var1.getID());
   }

   public ItemCategoryFilter getItemCategory(int var1) {
      return (ItemCategoryFilter)this.categoryIDs.get(var1);
   }

   public ItemLimits getItemLimits(Item var1) {
      if (this.isItemDisabled(var1)) {
         return null;
      } else {
         ItemCategoryFilter var2 = this.getItemCategory(var1);
         return var2 != null ? (ItemLimits)var2.itemIDsAllowed.get(var1.getID()) : null;
      }
   }

   public boolean isItemAllowed(Item var1) {
      if (this.isItemDisabled(var1)) {
         return false;
      } else {
         return this.getItemLimits(var1) != null;
      }
   }

   public boolean setItemAllowed(Item var1, boolean var2) {
      return this.setItemAllowed(var1, var2, 0);
   }

   public boolean setItemAllowed(Item var1, boolean var2, int var3) {
      return this.setItemAllowed(var1, var2 ? new ItemLimits(var3) : null);
   }

   public boolean setItemAllowed(Item var1, ItemLimits var2) {
      if (this.isItemDisabled(var1)) {
         return false;
      } else {
         ItemCategoryFilter var3 = this.getItemCategory(var1);
         return var3 != null ? var3.setItemAllowed(var1.getID(), var2) : false;
      }
   }

   /** @deprecated */
   @Deprecated
   public int getAddAmount(Level var1, InventoryItem var2, InventoryRange var3) {
      return this.getAddAmount(var1, var2, var3, false);
   }

   public int getAddAmount(Level var1, InventoryItem var2, InventoryRange var3, boolean var4) {
      if (this.isItemDisabled(var2.item)) {
         return 0;
      } else {
         ItemCategoryFilter var5 = this.getItemCategory(var2.item);
         if (var5 == null) {
            return 0;
         } else {
            ItemLimits var6 = (ItemLimits)var5.itemIDsAllowed.get(var2.item.getID());
            if (var6 == null) {
               return 0;
            } else {
               LinkedList var7 = new LinkedList();
               if (!var6.isDefault()) {
                  var7.add(new TotalLimitCounter("limits", (var1x) -> {
                     return var1x.item.getID() == var2.item.getID();
                  }, var6.getMaxItems()));
               }

               for(; var5 != null; var5 = var5.parent) {
                  if (!var5.isDefault()) {
                     var7.addFirst(new TotalLimitCounter(var5.category.stringID, (var1x) -> {
                        return var5.category.containsItemOrInChildren(var1x.item);
                     }, var5.getMaxItems()));
                  }
               }

               Object var8 = null;
               if (this.maxAmount != Integer.MAX_VALUE) {
                  switch (this.limitMode) {
                     case TOTAL_ITEMS:
                        var7.addFirst(new TotalLimitCounter("maxTotal", (var0) -> {
                           return true;
                        }, this.maxAmount));
                        break;
                     case TOTAL_STACKS:
                        var7.addFirst(new StackLimitCounter("maxTotalStacks", (var0) -> {
                           return true;
                        }, var2, this.maxAmount));
                        break;
                     case TOTAL_EACH_ITEM:
                        var8 = new TotalLimitCounter("maxTotalEach", (var1x) -> {
                           return var1x.item.getID() == var2.item.getID();
                        }, this.maxAmount);
                        break;
                     case TOTAL_STACKS_EACH_ITEM:
                        var8 = new StackLimitCounter("maxTotalStacksEach", (var1x) -> {
                           return var1x.item.getID() == var2.item.getID();
                        }, var2, this.maxAmount);
                  }
               }

               int var9 = 0;
               int var10 = var2.getAmount();
               if (this.minAmount != Integer.MAX_VALUE || !var7.isEmpty() || var8 != null) {
                  int var11;
                  int var18;
                  for(var11 = var3.startSlot; var11 <= var3.endSlot; ++var11) {
                     InventoryItem var12 = var3.inventory.getItem(var11);
                     if (var12 != null) {
                        var9 += var12.getAmount();
                        if (var9 >= this.minAmount && !var4) {
                           return 0;
                        }

                        int var15;
                        for(Iterator var13 = var7.iterator(); var13.hasNext(); var10 = Math.min(var15, var10)) {
                           LimitCounter var14 = (LimitCounter)var13.next();
                           if (!var14.shouldHandleItem(var1, var3.inventory, var11, var12)) {
                              break;
                           }

                           var15 = var14.handleAndGetAddAmount(var1, var3.inventory, var11, var12);
                           if (var15 <= 0) {
                              return 0;
                           }
                        }

                        if (var8 != null && ((LimitCounter)var8).shouldHandleItem(var1, var3.inventory, var11, var12)) {
                           var18 = ((LimitCounter)var8).handleAndGetAddAmount(var1, var3.inventory, var11, var12);
                           if (var18 <= 0) {
                              return 0;
                           }

                           var10 = Math.min(var18, var10);
                        }
                     }
                  }

                  for(Iterator var16 = var7.iterator(); var16.hasNext(); var10 = Math.min(var18, var10)) {
                     LimitCounter var17 = (LimitCounter)var16.next();
                     var18 = var17.getFinalAddAmount(var1, var3);
                     if (var18 <= 0) {
                        return 0;
                     }
                  }

                  if (var8 != null) {
                     var11 = ((LimitCounter)var8).getFinalAddAmount(var1, var3);
                     if (var11 <= 0) {
                        return 0;
                     }

                     var10 = Math.min(var11, var10);
                  }
               }

               return var10;
            }
         }
      }
   }

   public int getRemoveAmount(Level var1, InventoryItem var2, InventoryRange var3) {
      if (this.isItemDisabled(var2.item)) {
         return var2.getAmount();
      } else {
         ItemCategoryFilter var4 = this.getItemCategory(var2.item);
         if (var4 == null) {
            return 0;
         } else {
            ItemLimits var5 = (ItemLimits)var4.itemIDsAllowed.get(var2.item.getID());
            if (var5 == null) {
               return var2.getAmount();
            } else {
               LinkedList var6 = new LinkedList();
               if (!var5.isDefault()) {
                  var6.add(new TotalLimitCounter("limits", (var1x) -> {
                     return var1x.item.getID() == var2.item.getID();
                  }, var5.getMaxItems()));
               }

               for(; var4 != null; var4 = var4.parent) {
                  if (!var4.isDefault()) {
                     var6.addFirst(new TotalLimitCounter(var4.category.stringID, (var1x) -> {
                        return var4.category.containsItemOrInChildren(var1x.item);
                     }, var4.getMaxItems()));
                  }
               }

               Object var7 = null;
               if (this.maxAmount != Integer.MAX_VALUE) {
                  switch (this.limitMode) {
                     case TOTAL_ITEMS:
                        var6.addFirst(new TotalLimitCounter("maxTotal", (var0) -> {
                           return true;
                        }, this.maxAmount));
                        break;
                     case TOTAL_STACKS:
                        var6.addFirst(new StackLimitCounter("maxTotalStacks", (var0) -> {
                           return true;
                        }, var2, this.maxAmount));
                        break;
                     case TOTAL_EACH_ITEM:
                        var7 = new TotalLimitCounter("maxTotalEach", (var1x) -> {
                           return var1x.item.getID() == var2.item.getID();
                        }, this.maxAmount);
                        break;
                     case TOTAL_STACKS_EACH_ITEM:
                        var7 = new StackLimitCounter("maxTotalStacksEach", (var1x) -> {
                           return var1x.item.getID() == var2.item.getID();
                        }, var2, this.maxAmount);
                  }
               }

               int var8 = 0;
               if (!var6.isEmpty() || var7 != null) {
                  for(int var9 = var3.startSlot; var9 <= var3.endSlot; ++var9) {
                     InventoryItem var10 = var3.inventory.getItem(var9);
                     if (var10 != null) {
                        int var14;
                        for(Iterator var11 = var6.iterator(); var11.hasNext(); var8 = Math.max(var14, var8)) {
                           LimitCounter var12 = (LimitCounter)var11.next();
                           if (!var12.shouldHandleItem(var1, var3.inventory, var9, var10)) {
                              break;
                           }

                           int var13 = var12.handleAndGetRemoveAmount(var1, var3.inventory, var9, var10);
                           var14 = Math.min(var13, var2.getAmount());
                        }

                        if (var7 != null && ((LimitCounter)var7).shouldHandleItem(var1, var3.inventory, var9, var10)) {
                           int var15 = ((LimitCounter)var7).handleAndGetRemoveAmount(var1, var3.inventory, var9, var10);
                           int var16 = Math.min(var15, var2.getAmount());
                           var8 = Math.max(var16, var8);
                        }
                     }
                  }
               }

               return var8;
            }
         }
      }
   }

   public boolean matchesItem(InventoryItem var1) {
      return this.isItemAllowed(var1.item);
   }

   public static enum ItemLimitMode {
      TOTAL_ITEMS(new LocalMessage("ui", "storagelimittotal"), (GameMessage)null, new LocalMessage("ui", "storagelimit")),
      TOTAL_STACKS(new LocalMessage("ui", "storagelimittotalstacks"), (GameMessage)null, new LocalMessage("ui", "storagelimitstacks")),
      TOTAL_EACH_ITEM(new LocalMessage("ui", "storagelimitperitem"), (GameMessage)null, new LocalMessage("ui", "storagelimit")),
      TOTAL_STACKS_EACH_ITEM(new LocalMessage("ui", "storagelimitperitemstacks"), (GameMessage)null, new LocalMessage("ui", "storagelimitstacks"));

      public GameMessage displayName;
      public GameMessage tooltip;
      public GameMessage inputPlaceholder;

      private ItemLimitMode(GameMessage var3, GameMessage var4, GameMessage var5) {
         this.displayName = var3;
         this.tooltip = var4;
         this.inputPlaceholder = var5;
      }

      // $FF: synthetic method
      private static ItemLimitMode[] $values() {
         return new ItemLimitMode[]{TOTAL_ITEMS, TOTAL_STACKS, TOTAL_EACH_ITEM, TOTAL_STACKS_EACH_ITEM};
      }
   }

   public class ItemCategoryFilter implements Comparable<ItemCategoryFilter> {
      public final ItemCategory category;
      public final ItemCategoryFilter parent;
      public final boolean hasAnyItems;
      private int maxItems = Integer.MAX_VALUE;
      private HashMap<String, ItemCategoryFilter> children = new HashMap();
      private boolean allItemsAllowed;
      private boolean allChildrenAllowed;
      private boolean anyItemsAllowed;
      private boolean anyChildrenAllowed;
      private boolean allItemsDefault;
      private boolean allChildrenDefault;
      private HashMap<Integer, ItemLimits> itemIDsAllowed = new HashMap();

      public ItemCategoryFilter(ItemCategory var2, boolean var3, ItemCategoryFilter var4) {
         this.parent = var4;
         this.category = var2;
         ItemCategoriesFilter.this.categoryIDs.put(var2.id, this);
         this.hasAnyItems = this.streamItems().findAny().isPresent();
         this.allItemsAllowed = var3 || !this.hasAnyItems;
         this.anyItemsAllowed = var3 && this.hasAnyItems;
         this.allItemsDefault = var3 || !this.hasAnyItems;
         if (this.anyItemsAllowed) {
            for(ItemCategoryFilter var5 = var4; var5 != null && !var5.anyChildrenAllowed; var5 = var5.parent) {
               var5.anyChildrenAllowed = true;
            }
         }

         this.anyChildrenAllowed = false;
         Iterator var7 = var2.getChildren().iterator();

         while(var7.hasNext()) {
            ItemCategory var6 = (ItemCategory)var7.next();
            this.children.put(var6.stringID, ItemCategoriesFilter.this.new ItemCategoryFilter(var6, var3, this));
         }

         this.allChildrenAllowed = var3 || this.children.isEmpty();
         this.allChildrenDefault = var3 || this.children.isEmpty();
         this.streamItems().forEach((var2x) -> {
            ItemCategoriesFilter.this.itemIDCategories.put(var2x.getID(), this);
            if (var3) {
               this.itemIDsAllowed.put(var2x.getID(), new ItemLimits());
            }

         });
      }

      public void addSaveData(SaveData var1, List<String> var2, SaveData var3) {
         if (!this.isDefault()) {
            var1.addInt("maxItems", this.maxItems);
         }

         var1.addBoolean("allChildrenAllowed", this.allChildrenAllowed && !this.children.isEmpty());
         var1.addBoolean("allChildrenDefault", this.allChildrenDefault);
         SaveData var7;
         if ((!this.allChildrenAllowed || !this.allChildrenDefault) && !this.children.isEmpty()) {
            SaveData var4 = new SaveData("children");
            Iterator var5 = this.children.entrySet().iterator();

            while(var5.hasNext()) {
               Map.Entry var6 = (Map.Entry)var5.next();
               var7 = new SaveData((String)var6.getKey());
               ((ItemCategoryFilter)var6.getValue()).addSaveData(var7, var2, var3);
               var4.addSaveData(var7);
            }

            var1.addSaveData(var4);
         }

         var1.addBoolean("allItemsAllowed", this.allItemsAllowed && this.hasAnyItems);
         var1.addBoolean("allItemsDefault", this.allItemsDefault);
         if (!this.allItemsAllowed || !this.allItemsDefault) {
            Iterator var8 = this.itemIDsAllowed.entrySet().iterator();

            while(var8.hasNext()) {
               Map.Entry var9 = (Map.Entry)var8.next();
               Item var10 = ItemRegistry.getItem((Integer)var9.getKey());
               if (var10 != null && !ItemCategoriesFilter.this.isItemDisabled(var10)) {
                  if (((ItemLimits)var9.getValue()).isDefault()) {
                     var2.add(var10.getStringID());
                  } else {
                     var7 = new SaveData("item");
                     var7.addUnsafeString("itemStringID", var10.getStringID());
                     ((ItemLimits)var9.getValue()).addSaveData(var7);
                     var3.addSaveData(var7);
                  }
               }
            }
         }

      }

      public void applyLoadData(LoadData var1) {
         this.allChildrenAllowed = var1.getBoolean("allChildrenAllowed", this.allChildrenAllowed);
         if (this.allChildrenAllowed) {
            this.setAllowed(true);
         }

         this.allChildrenDefault = var1.getBoolean("allChildrenDefault", true, false);
         if (!this.children.isEmpty() && !this.allChildrenAllowed || !this.allChildrenDefault) {
            LoadData var2 = var1.getFirstLoadDataByName("children");
            if (var2 != null) {
               Iterator var3 = this.children.entrySet().iterator();

               label33:
               while(true) {
                  while(true) {
                     if (!var3.hasNext()) {
                        break label33;
                     }

                     Map.Entry var4 = (Map.Entry)var3.next();
                     LoadData var5 = var2.getFirstLoadDataByName((String)var4.getKey());
                     if (var5 != null && var5.isArray()) {
                        ((ItemCategoryFilter)var4.getValue()).applyLoadData(var5);
                     } else {
                        ((ItemCategoryFilter)var4.getValue()).setAllowed(false);
                     }
                  }
               }
            }
         }

         this.maxItems = var1.getInt("maxItems", Integer.MAX_VALUE, false);
         this.fixAnyChildrenAllowed();
         this.allItemsAllowed = var1.getBoolean("allItemsAllowed", this.allItemsAllowed);
         this.allItemsDefault = var1.getBoolean("allItemsDefault", true, false);
         if (this.allItemsAllowed) {
            this.anyItemsAllowed = this.hasAnyItems;
            this.streamItems().forEach((var1x) -> {
               this.itemIDsAllowed.put(var1x.getID(), new ItemLimits());
            });
         } else {
            this.anyItemsAllowed = false;
            this.streamItems().forEach((var1x) -> {
               this.itemIDsAllowed.remove(var1x.getID());
            });
         }

      }

      public void writePacket(PacketWriter var1) {
         var1.putNextBoolean(this.isDefault());
         if (!this.isDefault()) {
            var1.putNextInt(this.maxItems);
         }

         var1.putNextBoolean(this.allChildrenAllowed);
         var1.putNextBoolean(this.allChildrenDefault);
         Iterator var2;
         if (!this.allChildrenAllowed || !this.allChildrenDefault) {
            var1.putNextBoolean(this.anyChildrenAllowed);
            if (this.anyChildrenAllowed) {
               var2 = this.getChildrenTree().iterator();

               while(var2.hasNext()) {
                  ItemCategoryFilter var3 = (ItemCategoryFilter)var2.next();
                  var3.writePacket(var1);
               }
            }
         }

         var1.putNextBoolean(this.allItemsAllowed);
         var1.putNextBoolean(this.allItemsDefault);
         if (!this.allItemsAllowed || !this.allItemsDefault) {
            var1.putNextBoolean(this.anyItemsAllowed);
            if (this.anyItemsAllowed) {
               var2 = this.getItemsTree().iterator();

               while(var2.hasNext()) {
                  Item var5 = (Item)var2.next();
                  ItemLimits var4 = (ItemLimits)this.itemIDsAllowed.get(var5.getID());
                  var1.putNextBoolean(var4 != null);
                  if (var4 != null) {
                     var4.writePacket(var1);
                  }
               }
            }
         }

      }

      public void readPacket(PacketReader var1) {
         boolean var2 = var1.getNextBoolean();
         if (var2) {
            this.clearMaxItems();
         } else {
            this.maxItems = var1.getNextInt();
         }

         this.allChildrenAllowed = var1.getNextBoolean();
         this.allChildrenDefault = var1.getNextBoolean();
         Iterator var3;
         ItemCategoryFilter var4;
         if (this.allChildrenAllowed && this.allChildrenDefault) {
            var3 = this.getChildren().iterator();

            while(var3.hasNext()) {
               var4 = (ItemCategoryFilter)var3.next();
               var4.setAllowed(true, false);
            }

            this.fixAnyChildrenAllowed();
         } else {
            this.anyChildrenAllowed = var1.getNextBoolean();
            if (this.anyChildrenAllowed) {
               var3 = this.getChildrenTree().iterator();

               while(var3.hasNext()) {
                  var4 = (ItemCategoryFilter)var3.next();
                  var4.readPacket(var1);
               }
            } else {
               var3 = this.getChildren().iterator();

               while(var3.hasNext()) {
                  var4 = (ItemCategoryFilter)var3.next();
                  var4.setAllowed(false, false);
               }
            }
         }

         this.allItemsAllowed = var1.getNextBoolean();
         this.allItemsDefault = var1.getNextBoolean();
         if (this.allItemsAllowed && this.allItemsDefault) {
            this.anyItemsAllowed = this.hasAnyItems;
            this.streamItems().forEach((var1x) -> {
               this.itemIDsAllowed.put(var1x.getID(), new ItemLimits());
            });
         } else {
            this.anyItemsAllowed = var1.getNextBoolean();
            if (this.anyItemsAllowed) {
               var3 = this.getItemsTree().iterator();

               while(var3.hasNext()) {
                  Item var7 = (Item)var3.next();
                  boolean var5 = var1.getNextBoolean();
                  if (var5) {
                     ItemLimits var6 = new ItemLimits();
                     var6.readPacket(var1);
                     this.itemIDsAllowed.put(var7.getID(), var6);
                  } else {
                     this.itemIDsAllowed.remove(var7.getID());
                  }
               }
            } else {
               this.streamItems().forEach((var1x) -> {
                  this.itemIDsAllowed.remove(var1x.getID());
               });
            }
         }

      }

      public void fixAllowedVariablesChildren() {
         this.fixAllowedVariables(true);
         Iterator var1 = this.children.values().iterator();

         while(var1.hasNext()) {
            ItemCategoryFilter var2 = (ItemCategoryFilter)var1.next();
            var2.fixAllowedVariablesChildren();
         }

      }

      protected void fixAllChildrenAllowed() {
         if (!this.children.isEmpty()) {
            this.allChildrenAllowed = this.children.values().stream().allMatch((var0) -> {
               return var0.allItemsAllowed && var0.allChildrenAllowed;
            });
         } else {
            this.allChildrenAllowed = true;
         }

      }

      protected void fixAnyChildrenAllowed() {
         if (this.children.isEmpty()) {
            this.anyChildrenAllowed = false;
         } else {
            this.anyChildrenAllowed = this.children.values().stream().anyMatch((var0) -> {
               return var0.anyChildrenAllowed || var0.anyItemsAllowed;
            });
         }

      }

      protected void fixAllChildrenDefault() {
         if (!this.children.isEmpty()) {
            this.allChildrenDefault = this.children.values().stream().allMatch((var0) -> {
               return var0.allItemsDefault && var0.allChildrenDefault && var0.isDefault();
            });
         } else {
            this.allChildrenDefault = true;
         }

      }

      public void fixAllowedVariables(boolean var1) {
         this.fixAllChildrenAllowed();
         this.fixAnyChildrenAllowed();
         this.allItemsAllowed = true;
         this.anyItemsAllowed = false;
         this.fixAllChildrenDefault();
         this.allItemsDefault = true;
         this.streamItems().forEach((var1x) -> {
            ItemLimits var2 = (ItemLimits)this.itemIDsAllowed.get(var1x.getID());
            if (var2 != null) {
               this.anyItemsAllowed = true;
               if (!var2.isDefault()) {
                  this.allItemsDefault = false;
               }
            } else {
               this.allItemsAllowed = false;
               this.allItemsDefault = false;
            }

         });
         if (var1) {
            for(ItemCategoryFilter var2 = this.parent; var2 != null; var2 = var2.parent) {
               var2.fixAllowedVariables(var1);
            }
         }

      }

      public boolean isAllAllowed() {
         return this.allItemsAllowed && this.allChildrenAllowed;
      }

      public boolean isAnyAllowed() {
         return this.anyItemsAllowed || this.anyChildrenAllowed;
      }

      public boolean isAllDefault() {
         return this.allItemsDefault && this.allChildrenDefault;
      }

      public boolean isDefault() {
         return this.maxItems == Integer.MAX_VALUE;
      }

      public int getMaxItems() {
         return this.maxItems;
      }

      public void clearMaxItems() {
         this.maxItems = Integer.MAX_VALUE;
         if (this.allChildrenDefault && this.allItemsDefault) {
            for(ItemCategoryFilter var1 = this.parent; var1 != null; var1 = var1.parent) {
               var1.fixAllChildrenDefault();
            }
         }

      }

      public void setMaxItems(int var1) {
         if (!this.isAnyAllowed()) {
            this.setAllowed(true);
         }

         this.maxItems = var1 <= 0 ? Integer.MAX_VALUE : var1;
         if (this.allChildrenDefault && this.allItemsDefault) {
            for(ItemCategoryFilter var2 = this.parent; var2 != null; var2 = var2.parent) {
               var2.fixAllChildrenDefault();
            }
         }

      }

      public void setAllowed(boolean var1) {
         this.setAllowed(var1, true);
      }

      protected void setAllowed(boolean var1, boolean var2) {
         ItemCategoryFilter var3;
         ItemCategoryFilter var4;
         Iterator var5;
         if (var1) {
            this.maxItems = Integer.MAX_VALUE;
            this.allItemsAllowed = true;
            this.allChildrenAllowed = true;
            this.anyItemsAllowed = this.hasAnyItems;
            this.anyChildrenAllowed = false;
            this.allChildrenDefault = true;
            this.allItemsDefault = true;
            if (this.anyItemsAllowed) {
               for(var3 = this.parent; var3 != null && !var3.anyChildrenAllowed; var3 = var3.parent) {
                  var3.anyChildrenAllowed = true;
               }
            }

            var5 = this.children.values().iterator();

            while(var5.hasNext()) {
               var4 = (ItemCategoryFilter)var5.next();
               var4.setAllowed(true, false);
            }

            this.streamItems().forEach((var1x) -> {
               this.itemIDsAllowed.put(var1x.getID(), new ItemLimits());
            });
            if (var2) {
               for(var3 = this.parent; var3 != null; var3 = var3.parent) {
                  var3.fixAllChildrenAllowed();
                  var3.fixAllChildrenDefault();
               }
            }
         } else {
            this.maxItems = Integer.MAX_VALUE;
            this.itemIDsAllowed.clear();
            this.allItemsAllowed = !this.hasAnyItems;
            this.allChildrenAllowed = this.children.isEmpty();
            this.anyItemsAllowed = false;
            this.anyChildrenAllowed = false;
            this.allChildrenDefault = this.children.isEmpty();
            this.allItemsDefault = !this.hasAnyItems;
            var5 = this.children.values().iterator();

            while(var5.hasNext()) {
               var4 = (ItemCategoryFilter)var5.next();
               var4.setAllowed(false, false);
            }

            if (var2) {
               for(var3 = this.parent; var3 != null; var3 = var3.parent) {
                  var3.allChildrenAllowed = var3.children.isEmpty();
                  var3.fixAnyChildrenAllowed();
                  var3.allChildrenDefault = var3.children.isEmpty();
               }
            }
         }

      }

      public boolean setItemAllowed(int var1, boolean var2) {
         return this.setItemAllowed(var1, var2, 0);
      }

      public boolean setItemAllowed(int var1, boolean var2, int var3) {
         return this.setItemAllowed(var1, var2 ? new ItemLimits(var3) : null);
      }

      public boolean setItemAllowed(int var1, ItemLimits var2) {
         Item var3 = ItemRegistry.getItem(var1);
         if (var3 != null && this.category.containsItem(var3) && !ItemCategoriesFilter.this.isItemDisabled(var3)) {
            ItemLimits var4 = (ItemLimits)this.itemIDsAllowed.get(var1);
            if (var2 != var4 && (var2 == null || var4 == null || !var2.isSame(var4))) {
               ItemCategoryFilter var5;
               if (var2 != null) {
                  this.itemIDsAllowed.put(var1, var2);
                  this.anyItemsAllowed = true;
                  this.allItemsAllowed = this.streamItems().allMatch((var1x) -> {
                     return this.itemIDsAllowed.containsKey(var1x.getID());
                  });
                  if (this.allItemsAllowed && this.allChildrenAllowed) {
                     for(var5 = this.parent; var5 != null; var5 = var5.parent) {
                        var5.fixAllChildrenAllowed();
                        if (!var5.allChildrenAllowed) {
                           break;
                        }
                     }
                  }

                  if (var2.isDefault()) {
                     this.allItemsDefault = this.streamItems().allMatch((var1x) -> {
                        ItemLimits var2 = (ItemLimits)this.itemIDsAllowed.get(var1x.getID());
                        return var2 != null && var2.isDefault();
                     });
                     if (this.allItemsDefault && this.allChildrenDefault) {
                        for(var5 = this.parent; var5 != null; var5 = var5.parent) {
                           var5.fixAllChildrenDefault();
                        }
                     }
                  } else {
                     this.allItemsDefault = false;

                     for(var5 = this.parent; var5 != null; var5 = var5.parent) {
                        var5.fixAllChildrenDefault();
                     }
                  }

                  for(var5 = this.parent; var5 != null; var5 = var5.parent) {
                     var5.anyChildrenAllowed = true;
                  }
               } else {
                  this.itemIDsAllowed.remove(var1);
                  this.allItemsAllowed = false;
                  this.anyItemsAllowed = this.streamItems().anyMatch((var1x) -> {
                     return this.itemIDsAllowed.containsKey(var1x.getID());
                  });
                  this.allItemsDefault = false;
                  this.fixAnyChildrenAllowed();

                  for(var5 = this.parent; var5 != null; var5 = var5.parent) {
                     var5.allChildrenAllowed = false;
                     var5.fixAnyChildrenAllowed();
                  }
               }

               return true;
            } else {
               return false;
            }
         } else {
            return false;
         }
      }

      public Collection<ItemCategoryFilter> getChildren() {
         return this.children.values();
      }

      public TreeSet<ItemCategoryFilter> getChildrenTree() {
         return (TreeSet)this.children.values().stream().collect(() -> {
            return new TreeSet(Comparator.comparingInt((var0) -> {
               return var0.category.id;
            }));
         }, TreeSet::add, TreeSet::addAll);
      }

      public Stream<Item> streamItems() {
         return this.category.streamItems().filter((var1) -> {
            return !ItemCategoriesFilter.this.isItemDisabled(var1);
         });
      }

      public TreeSet<Item> getItemsTree() {
         return (TreeSet)this.streamItems().collect(() -> {
            return new TreeSet(Comparator.comparingInt(Item::getID));
         }, TreeSet::add, TreeSet::addAll);
      }

      public int compareTo(ItemCategoryFilter var1) {
         return this.category.compareTo(var1.category);
      }

      public GameTooltips getDebugTooltip() {
         ListGameTooltips var1 = new ListGameTooltips();
         var1.add("HasAnyItems: " + this.hasAnyItems);
         var1.add("allItemsAllowed: " + this.allItemsAllowed);
         var1.add("allChildrenAllowed: " + this.allChildrenAllowed);
         var1.add("anyItemsAllowed: " + this.anyItemsAllowed);
         var1.add("anyChildrenAllowed: " + this.anyChildrenAllowed);
         var1.add("allChildrenDefault: " + this.allChildrenDefault);
         var1.add("allItemsDefault: " + this.allItemsDefault);
         return var1;
      }

      // $FF: synthetic method
      // $FF: bridge method
      public int compareTo(Object var1) {
         return this.compareTo((ItemCategoryFilter)var1);
      }
   }

   public static class ItemLimits {
      private int maxItems;

      public ItemLimits() {
         this.maxItems = Integer.MAX_VALUE;
      }

      public ItemLimits(int var1) {
         this.maxItems = var1 <= 0 ? Integer.MAX_VALUE : var1;
      }

      public void addSaveData(SaveData var1) {
         var1.addInt("maxItems", this.maxItems);
      }

      public void applyLoadData(LoadData var1) {
         this.maxItems = var1.getInt("maxItems", Integer.MAX_VALUE);
         if (this.maxItems <= 0) {
            this.maxItems = Integer.MAX_VALUE;
         }

      }

      public void writePacket(PacketWriter var1) {
         boolean var2 = this.isDefault();
         var1.putNextBoolean(var2);
         if (!var2) {
            var1.putNextInt(this.maxItems);
         }

      }

      public void readPacket(PacketReader var1) {
         boolean var2 = var1.getNextBoolean();
         if (var2) {
            this.maxItems = Integer.MAX_VALUE;
         } else {
            this.maxItems = var1.getNextInt();
         }

      }

      public boolean isDefault() {
         return this.maxItems == Integer.MAX_VALUE;
      }

      public int getMaxItems() {
         return this.maxItems;
      }

      public boolean isSame(ItemLimits var1) {
         return var1.maxItems == this.maxItems;
      }
   }

   private static class TotalLimitCounter extends LimitCounter {
      public final Predicate<InventoryItem> isValidItem;
      public final int maxItems;
      public int items;

      public TotalLimitCounter(String var1, Predicate<InventoryItem> var2, int var3) {
         super(var1);
         this.isValidItem = var2;
         this.maxItems = var3;
      }

      public boolean shouldHandleItem(Level var1, Inventory var2, int var3, InventoryItem var4) {
         return this.isValidItem.test(var4);
      }

      public int handleAndGetAddAmount(Level var1, Inventory var2, int var3, InventoryItem var4) {
         this.items += var4.getAmount();
         return this.maxItems - this.items;
      }

      public int getFinalAddAmount(Level var1, InventoryRange var2) {
         return this.maxItems - this.items;
      }

      public int handleAndGetRemoveAmount(Level var1, Inventory var2, int var3, InventoryItem var4) {
         this.items += var4.getAmount();
         return this.items - this.maxItems;
      }
   }

   private static class StackLimitCounter extends LimitCounter {
      public final Predicate<InventoryItem> isValidItem;
      public final int maxStacks;
      public final InventoryItem inputItem;
      public int stacks;
      public int inputItemsAddedOrRemoved;

      public StackLimitCounter(String var1, Predicate<InventoryItem> var2, InventoryItem var3, int var4) {
         super(var1);
         this.isValidItem = var2;
         this.maxStacks = var4;
         this.inputItem = var3;
      }

      public boolean shouldHandleItem(Level var1, Inventory var2, int var3, InventoryItem var4) {
         return this.isValidItem.test(var4);
      }

      public int handleAndGetAddAmount(Level var1, Inventory var2, int var3, InventoryItem var4) {
         if (this.stacks < this.maxStacks && var4.canCombine(var1, (PlayerMob)null, this.inputItem, "hauljob")) {
            int var5 = var2.getItemStackLimit(var3, var4);
            int var6 = var5 - var4.getAmount();
            if (var6 > 0) {
               this.inputItemsAddedOrRemoved += var6;
            }
         }

         ++this.stacks;
         return Integer.MAX_VALUE;
      }

      public int getFinalAddAmount(Level var1, InventoryRange var2) {
         if (this.stacks < this.maxStacks) {
            for(int var3 = var2.startSlot; var3 <= var2.endSlot; ++var3) {
               if (var2.inventory.isSlotClear(var3)) {
                  this.inputItemsAddedOrRemoved += var2.inventory.getItemStackLimit(var3, this.inputItem);
                  ++this.stacks;
                  if (this.stacks >= this.maxStacks) {
                     break;
                  }
               }
            }
         }

         return this.inputItemsAddedOrRemoved;
      }

      public int handleAndGetRemoveAmount(Level var1, Inventory var2, int var3, InventoryItem var4) {
         ++this.stacks;
         return this.stacks > this.maxStacks ? var4.getAmount() : 0;
      }
   }

   private abstract static class LimitCounter {
      public final String debugName;

      public LimitCounter(String var1) {
         this.debugName = var1;
      }

      public abstract boolean shouldHandleItem(Level var1, Inventory var2, int var3, InventoryItem var4);

      public abstract int handleAndGetAddAmount(Level var1, Inventory var2, int var3, InventoryItem var4);

      public abstract int getFinalAddAmount(Level var1, InventoryRange var2);

      public abstract int handleAndGetRemoveAmount(Level var1, Inventory var2, int var3, InventoryItem var4);
   }
}
