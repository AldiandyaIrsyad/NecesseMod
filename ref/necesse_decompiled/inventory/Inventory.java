package necesse.inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import necesse.engine.GameLog;
import necesse.engine.GameState;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameLinkedList;
import necesse.engine.world.GameClock;
import necesse.engine.world.WorldSettings;
import necesse.entity.Entity;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.item.Item;
import necesse.inventory.item.TickItem;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.IngredientCounter;
import necesse.level.maps.Level;

public class Inventory {
   private int size;
   private InventoryItem[] items;
   private HashSet<Integer> tickSlots = new HashSet();
   private float lastSpoilRateModifier = 1.0F;
   private NextSpoilSlotList nextSpoilSlots = new NextSpoilSlotList();
   private int totalDirty;
   private boolean[] dirtySlots;
   public InventoryFilter filter;
   public float spoilRateModifier = 1.0F;
   private GameLinkedList<InventoryUpdateListener> slotUpdateListeners = new GameLinkedList();

   public Inventory(int var1) {
      this.size = var1;
      this.items = new InventoryItem[var1];
      this.dirtySlots = new boolean[var1];
   }

   public Inventory copy() {
      Inventory var1 = new Inventory(this.size);

      for(int var2 = 0; var2 < this.size; ++var2) {
         var1.items[var2] = this.items[var2] == null ? null : this.items[var2].copy();
         var1.dirtySlots[var2] = this.dirtySlots[var2];
      }

      var1.totalDirty = this.totalDirty;
      var1.filter = this.filter;
      return var1;
   }

   public Inventory copy(final int var1, int var2) {
      Inventory var3 = new Inventory(var2 - var1 + 1);
      int var4 = 0;

      for(int var5 = var1; var5 < var2; ++var5) {
         var3.items[var5 - var1] = this.items[var5] == null ? null : this.items[var5].copy();
         if (this.dirtySlots[var5]) {
            var3.dirtySlots[var5 - var1] = true;
            ++var4;
         }
      }

      var3.totalDirty = var4;
      final InventoryFilter var6 = this.filter;
      var3.filter = new InventoryFilter() {
         public boolean isItemValid(int var1x, InventoryItem var2) {
            return var6.isItemValid(var1x + var1, var2);
         }

         public int getItemStackLimit(int var1x, InventoryItem var2) {
            return var6.getItemStackLimit(var1x + var1, var2);
         }
      };
      return var3;
   }

   public void changeSize(int var1) {
      if (var1 != this.getSize()) {
         InventoryItem[] var2 = new InventoryItem[var1];
         boolean[] var3 = new boolean[var1];
         this.totalDirty = 0;

         for(int var4 = 0; var4 < this.items.length && var4 < var2.length; ++var4) {
            var2[var4] = this.items[var4];
            var3[var4] = this.dirtySlots[var4];
            if (this.dirtySlots[var4]) {
               ++this.totalDirty;
            }
         }

         this.items = var2;
         this.dirtySlots = var3;
         this.size = var1;
      }
   }

   public void override(Inventory var1) {
      this.override(var1, false, true);
   }

   public void override(Inventory var1, boolean var2, boolean var3) {
      if (var2 && this.getSize() != var1.getSize()) {
         this.changeSize(var1.getSize());
      }

      for(int var4 = 0; var4 < this.getSize() && var1.getSize() > var4; ++var4) {
         this.setItem(var4, var1.getItem(var4), var3);
      }

   }

   public boolean adjustSize(int var1, int var2, int var3) {
      if (var2 < 0) {
         var2 = Integer.MAX_VALUE;
      }

      if (var2 < var1) {
         var2 = var1;
      }

      int var4 = 0;
      int var5 = 0;

      int var6;
      for(var6 = 0; var6 < this.getSize(); ++var6) {
         if (this.isSlotClear(var6)) {
            ++var4;
         } else {
            var5 = var6;
         }
      }

      var1 = Math.max(var1, var5 + 1);
      var2 = Math.max(var2, var5 + 1);
      if (var4 < var3) {
         var6 = Math.min(this.getSize() + var3 - var4, var2);
      } else {
         int var7 = var4 - var3;
         var6 = Math.min(Math.max(var1, this.getSize() - var7), var2);
      }

      if (var6 != this.getSize()) {
         this.changeSize(var6);
         return true;
      } else {
         return false;
      }
   }

   public int getSize() {
      return this.size;
   }

   public void tickItems(Entity var1) {
      this.tickItems(var1, var1, var1, var1 == null ? null : var1.getWorldSettings());
   }

   public void tickItems(GameClock var1, GameState var2, Entity var3, WorldSettings var4) {
      LinkedList var5 = new LinkedList();
      Integer[] var6 = (Integer[])this.tickSlots.toArray(new Integer[0]);
      int var7 = var6.length;

      int var8;
      int var9;
      for(var8 = 0; var8 < var7; ++var8) {
         var9 = var6[var8];
         InventoryItem var10 = this.getItem(var9);
         if (var10 != null && var10.item.isTickItem()) {
            ((TickItem)var10.item).tick(this, var9, var10, var1, var2, var3, var4, (var2x) -> {
               this.setItem(var9, var2x);
            });
         } else {
            var5.add(var9);
         }
      }

      Iterator var14 = var5.iterator();

      while(var14.hasNext()) {
         var7 = (Integer)var14.next();
         if (this.tickSlots.remove(var7)) {
            GameLog.debug.println("Had to remove slot " + var7 + " from tickSlots: " + this.getItem(var7) + ", " + var3);
         }
      }

      if ((var4 == null || var4.survivalMode) && !this.nextSpoilSlots.isEmpty()) {
         if (this.spoilRateModifier != this.lastSpoilRateModifier) {
            this.lastSpoilRateModifier = this.spoilRateModifier;
            ArrayList var15 = new ArrayList(this.nextSpoilSlots.size());
            Iterator var17 = this.nextSpoilSlots.iterator();

            while(var17.hasNext()) {
               SlotSpoilTime var18 = (SlotSpoilTime)var17.next();
               var15.add(var18.slot);
            }

            this.nextSpoilSlots.clear();
            var17 = var15.iterator();

            while(var17.hasNext()) {
               var8 = (Integer)var17.next();
               InventoryItem var20 = this.getItem(var8);
               if (var20 != null && var20.item.shouldSpoilTick(var20)) {
                  long var21 = var20.item.tickSpoilTime(var20, var1, this.spoilRateModifier, (var2x) -> {
                     this.setItem(var8, var2x);
                  });
                  this.addSpoilSlotSorted(var8, var21);
               }
            }
         } else {
            long var16 = var1 == null ? 0L : var1.getWorldTime();

            GameLinkedList.Element var22;
            for(GameLinkedList.Element var19 = this.nextSpoilSlots.getFirstElement(); var19 != null; var19 = var22) {
               var9 = ((SlotSpoilTime)var19.object).slot;
               var22 = var19.next();
               if (((SlotSpoilTime)var19.object).tickWorldTime <= var16) {
                  InventoryItem var11 = this.getItem(var9);
                  if (var11 != null && var11.item.shouldSpoilTick(var11)) {
                     long var12 = var11.item.tickSpoilTime(var11, var1, this.spoilRateModifier, (var2x) -> {
                        this.setItem(var9, var2x);
                     });
                     if (!var19.isRemoved()) {
                        var19.remove();
                     }

                     var11 = this.getItem(var9);
                     if (var11 != null && var11.item.shouldSpoilTick(var11)) {
                        this.addSpoilSlotSorted(var9, var12);
                     }
                  } else {
                     this.nextSpoilSlots.removeFirst();
                     GameLog.debug.println("Had to remove slot " + var9 + " from spoilSlots: " + var11 + ", " + var3);
                  }
               }

               if (var22 == null || ((SlotSpoilTime)var22.object).tickWorldTime != ((SlotSpoilTime)var19.object).tickWorldTime) {
                  break;
               }
            }
         }
      }

   }

   private void addSpoilSlotSorted(int var1, long var2) {
      Iterator var4 = this.nextSpoilSlots.elementIterator();

      GameLinkedList.Element var5;
      do {
         if (!var4.hasNext()) {
            this.nextSpoilSlots.addLast(new SlotSpoilTime(var1, var2));
            return;
         }

         var5 = (GameLinkedList.Element)var4.next();
      } while(((SlotSpoilTime)var5.object).tickWorldTime < var2);

      var5.insertBefore(new SlotSpoilTime(var1, var2));
   }

   public void clean(int var1) {
      if (var1 >= 0 && var1 < this.dirtySlots.length) {
         if (this.dirtySlots[var1]) {
            --this.totalDirty;
         }

         this.dirtySlots[var1] = false;
      }
   }

   public void clean() {
      this.dirtySlots = new boolean[this.size];
      this.totalDirty = 0;
   }

   public void markDirty(int var1) {
      if (var1 >= 0 && var1 < this.dirtySlots.length) {
         if (!this.isDirty(var1)) {
            ++this.totalDirty;
         }

         this.dirtySlots[var1] = true;
      }
   }

   public void markFullDirty() {
      Arrays.fill(this.dirtySlots, true);
      this.totalDirty = this.getSize();
   }

   public boolean isDirty(int var1) {
      return var1 >= 0 && var1 < this.dirtySlots.length ? this.dirtySlots[var1] : false;
   }

   public boolean isDirty() {
      return this.totalDirty > 0;
   }

   public boolean isFullDirty() {
      return this.getSize() > 0 && this.totalDirty >= this.getSize();
   }

   public void clearSlot(int var1) {
      this.setItem(var1, (InventoryItem)null);
   }

   public boolean isSlotClear(int var1) {
      if (var1 >= 0 && var1 < this.items.length) {
         return this.items[var1] == null;
      } else {
         return true;
      }
   }

   public void clearInventory() {
      for(int var1 = 0; var1 < this.size; ++var1) {
         this.clearSlot(var1);
      }

   }

   public void setItem(int var1, InventoryItem var2) {
      this.setItem(var1, var2, true);
   }

   public void setItem(int var1, InventoryItem var2, boolean var3) {
      if (var1 >= 0 && var1 < this.items.length) {
         if (var2 != null && var2.getAmount() <= 0) {
            var2 = null;
         }

         if (var2 != null && !this.canLockItem(var1)) {
            var2.setLocked(false);
         }

         if (!var3 && var2 != null && !this.isSlotClear(var1) && this.getItemSlot(var1).getID() == var2.item.getID()) {
            var2.setNew(this.getItem(var1).isNew());
         }

         this.items[var1] = var2;
         this.updateSlot(var1);
      }
   }

   public InventoryItem getItem(int var1) {
      return var1 >= 0 && var1 < this.items.length ? this.items[var1] : null;
   }

   public String getItemStringID(int var1) {
      return this.getItemSlot(var1).getStringID();
   }

   public int getItemID(int var1) {
      return this.getItemSlot(var1) == null ? -1 : this.getItemSlot(var1).getID();
   }

   public void setAmount(int var1, int var2) {
      if (var1 >= 0 && var1 < this.items.length && this.items[var1] != null) {
         this.items[var1].setAmount(var2);
         if (this.items[var1].getAmount() <= 0) {
            this.items[var1] = null;
         }

         this.updateSlot(var1);
      }
   }

   public void addAmount(int var1, int var2) {
      if (var1 >= 0 && var1 < this.items.length && this.items[var1] != null) {
         this.items[var1].setAmount(this.items[var1].getAmount() + var2);
         if (this.getAmount(var1) > this.getItem(var1).itemStackSize()) {
            this.setAmount(var1, this.getItem(var1).itemStackSize());
         }

      }
   }

   public int getAmount(int var1) {
      return var1 >= 0 && var1 < this.items.length && this.items[var1] != null ? this.items[var1].getAmount() : 0;
   }

   public Item getItemSlot(int var1) {
      return var1 >= 0 && var1 < this.items.length && this.items[var1] != null ? this.items[var1].item : null;
   }

   public int getAmount(Level var1, PlayerMob var2, Item var3, String var4) {
      return this.getAmount(var1, var2, (Item)var3, 0, this.getSize() - 1, var4);
   }

   public int getAmount(Level var1, PlayerMob var2, Item var3, int var4, int var5, String var6) {
      if (var4 < 0) {
         var4 = 0;
      }

      if (var5 > this.items.length - 1) {
         var5 = this.items.length - 1;
      }

      int var7 = 0;

      for(int var8 = var4; var8 <= var5; ++var8) {
         if (!this.isSlotClear(var8)) {
            InventoryItem var9 = this.getItem(var8);
            var7 += var9.item.getInventoryAmount(var1, var2, var9, var3, var6);
         }
      }

      return var7;
   }

   public int getAmount(Level var1, PlayerMob var2, Item.Type var3, String var4) {
      return this.getAmount(var1, var2, (Item.Type)var3, 0, this.getSize() - 1, var4);
   }

   public int getAmount(Level var1, PlayerMob var2, Item.Type var3, int var4, int var5, String var6) {
      if (var4 < 0) {
         var4 = 0;
      }

      if (var5 > this.items.length - 1) {
         var5 = this.items.length - 1;
      }

      int var7 = 0;

      for(int var8 = var4; var8 <= var5; ++var8) {
         if (!this.isSlotClear(var8)) {
            InventoryItem var9 = this.getItem(var8);
            var7 += var9.item.getInventoryAmount(var1, var2, var9, var3, var6);
         }
      }

      return var7;
   }

   public void countIngredientAmount(Level var1, PlayerMob var2, IngredientCounter var3) {
      this.countIngredientAmount(var1, var2, 0, this.getSize() - 1, var3);
   }

   public void countIngredientAmount(Level var1, PlayerMob var2, int var3, int var4, IngredientCounter var5) {
      if (var3 < 0) {
         var3 = 0;
      }

      if (var4 > this.items.length - 1) {
         var4 = this.items.length - 1;
      }

      for(int var6 = var3; var6 <= var4; ++var6) {
         if (!this.isSlotClear(var6)) {
            InventoryItem var7 = this.getItem(var6);
            var7.item.countIngredientAmount(var1, var2, this, var6, var7, var5);
         }
      }

   }

   public ArrayList<SlotPriority> getPriorityList(Level var1, PlayerMob var2, int var3, int var4, String var5) {
      if (var3 < 0) {
         var3 = 0;
      }

      if (var4 > this.items.length - 1) {
         var4 = this.items.length - 1;
      }

      ArrayList var6 = new ArrayList();

      for(int var7 = var3; var7 <= var4; ++var7) {
         if (!this.isSlotClear(var7)) {
            InventoryItem var8 = this.getItem(var7);
            var6.add(new SlotPriority(var7, var8.item.getInventoryPriority(var1, var2, this, var7, var8, var5)));
         }
      }

      Comparator var9 = Comparator.comparing((var0) -> {
         return var0.comparable;
      });
      var6.sort(var9);
      return var6;
   }

   public ArrayList<SlotPriority> getPriorityList(Level var1, PlayerMob var2, Iterable<Integer> var3, String var4) {
      ArrayList var5 = new ArrayList();
      Iterator var6 = var3.iterator();

      while(var6.hasNext()) {
         int var7 = (Integer)var6.next();
         if (var7 >= 0 && var7 < this.items.length && !this.isSlotClear(var7)) {
            InventoryItem var8 = this.getItem(var7);
            var5.add(new SlotPriority(var7, var8.item.getInventoryPriority(var1, var2, this, var7, var8, var4)));
         }
      }

      Comparator var9 = Comparator.comparing((var0) -> {
         return var0.comparable;
      });
      var5.sort(var9);
      return var5;
   }

   public ArrayList<SlotPriority> getPriorityAddList(Level var1, PlayerMob var2, InventoryItem var3, Iterable<Integer> var4, String var5) {
      ArrayList var6 = new ArrayList();
      Iterator var7 = var4.iterator();

      while(var7.hasNext()) {
         int var8 = (Integer)var7.next();
         if (!this.isSlotClear(var8)) {
            InventoryItem var9 = this.getItem(var8);
            var6.add(new SlotPriority(var8, var9.item.getInventoryAddPriority(var1, var2, this, var8, var9, var3, var5)));
         }
      }

      Comparator var10 = Comparator.comparing((var0) -> {
         return var0.comparable;
      });
      var6.sort(var10);
      return var6;
   }

   public ArrayList<SlotPriority> getPriorityAddList(Level var1, PlayerMob var2, InventoryItem var3, int var4, int var5, String var6) {
      if (var4 < 0) {
         var4 = 0;
      }

      if (var5 > this.items.length - 1) {
         var5 = this.items.length - 1;
      }

      ArrayList var7 = new ArrayList();

      for(int var8 = var4; var8 <= var5; ++var8) {
         if (!this.isSlotClear(var8)) {
            InventoryItem var9 = this.getItem(var8);
            var7.add(new SlotPriority(var8, var9.item.getInventoryAddPriority(var1, var2, this, var8, var9, var3, var6)));
         }
      }

      Comparator var10 = Comparator.comparing((var0) -> {
         return var0.comparable;
      });
      var7.sort(var10);
      return var7;
   }

   public Item getFirstItem(Level var1, PlayerMob var2, Item[] var3, String var4) {
      return this.getFirstItem(var1, var2, (Item[])var3, 0, this.getSize() - 1, var4);
   }

   public Item getFirstItem(Level var1, PlayerMob var2, Item[] var3, int var4, int var5, String var6) {
      Iterator var7 = this.getPriorityList(var1, var2, var4, var5, var6).iterator();

      Item var10;
      do {
         if (!var7.hasNext()) {
            return null;
         }

         SlotPriority var8 = (SlotPriority)var7.next();
         InventoryItem var9 = this.getItem(var8.slot);
         var10 = var9.item.getInventoryFirstItem(var1, var2, var9, var3, var6);
      } while(var10 == null);

      return var10;
   }

   public Item getFirstItem(Level var1, PlayerMob var2, Item.Type var3, String var4) {
      return this.getFirstItem(var1, var2, (Item.Type)var3, 0, this.getSize() - 1, var4);
   }

   public Item getFirstItem(Level var1, PlayerMob var2, Item.Type var3, int var4, int var5, String var6) {
      Iterator var7 = this.getPriorityList(var1, var2, var4, var5, var6).iterator();

      Item var10;
      do {
         if (!var7.hasNext()) {
            return null;
         }

         SlotPriority var8 = (SlotPriority)var7.next();
         InventoryItem var9 = this.getItem(var8.slot);
         var10 = var9.item.getInventoryFirstItem(var1, var2, var9, var3, var6);
      } while(var10 == null);

      return var10;
   }

   public int removeItems(Level var1, PlayerMob var2, Item var3, int var4, String var5) {
      return this.removeItems(var1, var2, (Item)var3, var4, 0, this.getSize() - 1, (String)var5);
   }

   public int removeItems(Level var1, PlayerMob var2, Item var3, int var4, int var5, int var6, String var7) {
      int var8 = var4;
      Iterator var9 = this.getPriorityList(var1, var2, var5, var6, var7).iterator();

      while(var9.hasNext()) {
         SlotPriority var10 = (SlotPriority)var9.next();
         if (var8 == 0) {
            return var4;
         }

         InventoryItem var11 = this.getItem(var10.slot);
         int var12 = var11.item.removeInventoryAmount(var1, var2, var11, var3, var8, var7);
         var8 -= var12;
         if (var11.getAmount() <= 0) {
            this.setItem(var10.slot, (InventoryItem)null);
         }

         if (var12 > 0) {
            this.updateSlot(var10.slot);
         }
      }

      return var4 - var8;
   }

   public int removeItems(Level var1, PlayerMob var2, Item.Type var3, int var4, String var5) {
      return this.removeItems(var1, var2, (Item.Type)var3, var4, 0, this.getSize() - 1, (String)var5);
   }

   public int removeItems(Level var1, PlayerMob var2, Item.Type var3, int var4, int var5, int var6, String var7) {
      int var8 = var4;
      Iterator var9 = this.getPriorityList(var1, var2, var5, var6, var7).iterator();

      while(var9.hasNext()) {
         SlotPriority var10 = (SlotPriority)var9.next();
         if (var8 == 0) {
            return var4;
         }

         InventoryItem var11 = this.getItem(var10.slot);
         int var12 = var11.item.removeInventoryAmount(var1, var2, var11, var3, var8, var7);
         var8 -= var12;
         if (var11.getAmount() <= 0) {
            this.setItem(var10.slot, (InventoryItem)null);
         }

         if (var12 > 0) {
            this.updateSlot(var10.slot);
         }
      }

      return var4 - var8;
   }

   public Item removeItem(Level var1, PlayerMob var2, Item.Type var3, String var4) {
      return this.removeItem(var1, var2, var3, 0, this.getSize() - 1, var4);
   }

   public Item removeItem(Level var1, PlayerMob var2, Item.Type var3, int var4, int var5, String var6) {
      Iterator var7 = this.getPriorityList(var1, var2, var4, var5, var6).iterator();

      SlotPriority var8;
      InventoryItem var9;
      int var10;
      do {
         if (!var7.hasNext()) {
            return null;
         }

         var8 = (SlotPriority)var7.next();
         var9 = this.getItem(var8.slot);
         var10 = var9.item.removeInventoryAmount(var1, var2, var9, (Item.Type)var3, 1, var6);
         if (var9.getAmount() <= 0) {
            this.setItem(var8.slot, (InventoryItem)null);
         }
      } while(var10 <= 0);

      this.updateSlot(var8.slot);
      return var9.item;
   }

   public int removeItems(Level var1, PlayerMob var2, Ingredient var3, int var4, Collection<InventoryItemsRemoved> var5) {
      return this.removeItems(var1, var2, (Ingredient)var3, var4, 0, this.getSize() - 1, (Collection)var5);
   }

   public int removeItems(Level var1, PlayerMob var2, Ingredient var3, int var4, int var5, int var6, Collection<InventoryItemsRemoved> var7) {
      int var8 = var4;
      Iterator var9 = this.getPriorityList(var1, var2, var5, var6, "crafting").iterator();

      while(var9.hasNext()) {
         SlotPriority var10 = (SlotPriority)var9.next();
         if (var8 == 0) {
            return var4;
         }

         if (!this.isSlotClear(var10.slot)) {
            InventoryItem var11 = this.getItem(var10.slot);
            int var12 = var11.item.removeInventoryAmount(var1, var2, var11, this, var10.slot, var3, var8, var7);
            var8 -= var12;
            if (var11.getAmount() <= 0) {
               this.setItem(var10.slot, (InventoryItem)null);
            }

            if (var12 > 0) {
               this.updateSlot(var10.slot);
            }
         }
      }

      return var4 - var8;
   }

   /** @deprecated */
   @Deprecated
   public boolean addItem(Level var1, PlayerMob var2, InventoryItem var3, String var4) {
      return this.addItem(var1, var2, var3, var4, (InventoryAddConsumer)null);
   }

   public boolean addItem(Level var1, PlayerMob var2, InventoryItem var3, String var4, InventoryAddConsumer var5) {
      return this.addItem(var1, var2, var3, 0, this.getSize() - 1, var4, var5);
   }

   /** @deprecated */
   @Deprecated
   public boolean addItem(Level var1, PlayerMob var2, InventoryItem var3, int var4, int var5, String var6) {
      return this.addItem(var1, var2, var3, var4, var5, var6, (InventoryAddConsumer)null);
   }

   public boolean addItem(Level var1, PlayerMob var2, InventoryItem var3, int var4, int var5, String var6, InventoryAddConsumer var7) {
      return this.addItem(var1, var2, var3, var4, var5, false, var6, false, false, var7);
   }

   /** @deprecated */
   @Deprecated
   public boolean addItem(Level var1, PlayerMob var2, InventoryItem var3, int var4, boolean var5, String var6, Inventory var7) {
      return this.addItem(var1, var2, var3, var4, var5, var6, (InventoryAddConsumer)null, var7);
   }

   public boolean addItem(Level var1, PlayerMob var2, InventoryItem var3, int var4, boolean var5, String var6, InventoryAddConsumer var7, Inventory var8) {
      if (var4 >= 0 && var4 < this.getSize() && this.isItemValid(var4, var3)) {
         if (this.isSlotClear(var4)) {
            int var16 = this.getItemStackLimit(var4, var3);
            int var15 = Math.min(var3.getAmount(), var16);
            InventoryItem var17 = var3.copy(var15);
            this.setItem(var4, var17);
            if (var7 != null) {
               var7.add(this, var4, var15);
            }

            if (this.canLockItem(var4)) {
               var17.setLocked(var5);
            }

            var3.setAmount(var3.getAmount() - var15);
            return true;
         } else {
            boolean var9 = false;
            InventoryItem var10 = this.getItem(var4);
            int var11;
            int var12;
            int var13;
            if (var10.equals(var1, var3, true, false, "equals")) {
               var11 = this.getItemStackLimit(var4, var10);
               var12 = var11 - var10.getAmount();
               var13 = Math.min(var3.getAmount(), var12);
               if (var13 > 0) {
                  ItemCombineResult var14 = var10.combine(var1, var2, this, var4, var3, var13, false, "add", var7);
                  if (var14.success) {
                     if (this.canLockItem(var4)) {
                        var10.setLocked(var5);
                     }

                     this.updateSlot(var4);
                     var9 = true;
                  }
               }
            } else {
               InventoryItem var18;
               if (var8 == this) {
                  var11 = var8.canAddItem(var1, var2, var10, 0, var4 - 1, "move") + var8.canAddItem(var1, var2, var10, var4 + 1, var8.getSize() - 1, "move");
                  if (var11 >= var10.getAmount()) {
                     var8.addItem(var1, var2, var10, 0, var4 - 1, "move", var7);
                     var8.addItem(var1, var2, var10, var4 + 1, var8.getSize() - 1, "move", var7);
                     if (var10.getAmount() <= 0) {
                        this.clearSlot(var4);
                        var12 = this.getItemStackLimit(var4, var3);
                        var13 = Math.min(var3.getAmount(), var12);
                        var18 = var3.copy(var13);
                        this.setItem(var4, var18);
                        if (this.canLockItem(var4)) {
                           var18.setLocked(var5);
                        }

                        var3.setAmount(var3.getAmount() - var13);
                        if (var7 != null) {
                           var7.add(this, var4, var13);
                        }
                     }
                  }
               } else {
                  var11 = var8.canAddItem(var1, var2, var10, "move");
                  if (var11 >= var10.getAmount()) {
                     var8.addItem(var1, var2, var10, "move", var7);
                     if (var10.getAmount() <= 0) {
                        this.clearSlot(var4);
                        var12 = this.getItemStackLimit(var4, var3);
                        var13 = Math.min(var3.getAmount(), var12);
                        var18 = var3.copy(var13);
                        this.setItem(var4, var18);
                        if (this.canLockItem(var4)) {
                           var18.setLocked(var5);
                        }

                        var3.setAmount(var3.getAmount() - var13);
                        if (var7 != null) {
                           var7.add(this, var4, var13);
                        }
                     }
                  }
               }
            }

            if (var3.getAmount() > 0) {
               if (var8 != null) {
                  var9 = var8.addItem(var1, var2, var3, var6, var7) || var9;
               } else {
                  var9 = this.addItem(var1, var2, var3, var6, var7) || var9;
               }
            }

            return var9;
         }
      } else {
         return this.addItem(var1, var2, var3, var6, var7);
      }
   }

   /** @deprecated */
   @Deprecated
   public boolean addItem(Level var1, PlayerMob var2, InventoryItem var3, int var4, int var5, boolean var6, String var7, boolean var8, boolean var9) {
      return this.addItem(var1, var2, var3, var4, var5, var6, var7, var8, var9, (InventoryAddConsumer)null);
   }

   public boolean addItem(Level var1, PlayerMob var2, InventoryItem var3, int var4, int var5, boolean var6, String var7, boolean var8, boolean var9, InventoryAddConsumer var10) {
      boolean var11 = this.addItemOnlyCombine(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
      if (var4 < 0) {
         var4 = 0;
      }

      if (var5 > this.items.length - 1) {
         var5 = this.items.length - 1;
      }

      for(int var12 = var4; var12 <= var5 && var3.getAmount() > 0; ++var12) {
         if (this.isSlotClear(var12) && (var8 || this.isItemValid(var12, var3))) {
            int var13 = var9 ? var3.itemStackSize() : this.getItemStackLimit(var12, var3);
            int var14 = Math.min(var3.getAmount(), var13);
            if (var14 > 0) {
               InventoryItem var15 = var3.copy(var14);
               this.setItem(var12, var15);
               var3.setAmount(var3.getAmount() - var14);
               if (var10 != null) {
                  var10.add(this, var12, var14);
               }

               var11 = true;
            }
         }
      }

      return var11;
   }

   /** @deprecated */
   @Deprecated
   public boolean addItemOnlyCombine(Level var1, PlayerMob var2, InventoryItem var3, int var4, int var5, boolean var6, String var7, boolean var8, boolean var9) {
      return this.addItemOnlyCombine(var1, var2, var3, var4, var5, var6, var7, var8, var9, (InventoryAddConsumer)null);
   }

   public boolean addItemOnlyCombine(Level var1, PlayerMob var2, InventoryItem var3, int var4, int var5, boolean var6, String var7, boolean var8, boolean var9, InventoryAddConsumer var10) {
      boolean var11 = false;
      Iterator var12 = this.getPriorityAddList(var1, var2, var3, var4, var5, var7).iterator();

      while(var12.hasNext()) {
         SlotPriority var13 = (SlotPriority)var12.next();
         if (var3.getAmount() <= 0) {
            break;
         }

         boolean var14 = var8 || this.isItemValid(var13.slot, var3);
         int var15 = var9 ? var3.itemStackSize() : this.getItemStackLimit(var13.slot, var3);
         InventoryItem var16 = this.getItem(var13.slot);
         if (var16.item.inventoryAddItem(var1, var2, this, var13.slot, var16, var3, var7, var14, var15, var6, var10)) {
            var11 = true;
            this.updateSlot(var13.slot);
         }
      }

      return var11;
   }

   public int canAddItem(Level var1, PlayerMob var2, InventoryItem var3, String var4) {
      return this.canAddItem(var1, var2, var3, 0, this.size - 1, var4);
   }

   public int canAddItem(Level var1, PlayerMob var2, InventoryItem var3, int var4, int var5, String var6) {
      return this.canAddItem(var1, var2, var3, var4, var5, var6, false, false);
   }

   public int canAddItem(Level var1, PlayerMob var2, InventoryItem var3, int var4, int var5, String var6, boolean var7, boolean var8) {
      int var9 = 0;
      if (var4 < 0) {
         var4 = 0;
      }

      if (var5 > this.items.length - 1) {
         var5 = this.items.length - 1;
      }

      for(int var10 = var4; var10 <= var5 && var9 < var3.getAmount(); ++var10) {
         boolean var11;
         if (this.isSlotClear(var10)) {
            var11 = var7 || this.isItemValid(var10, var3);
            if (var11) {
               var9 += var8 ? var3.itemStackSize() : this.getItemStackLimit(var10, var3);
            }
         } else {
            var11 = var7 || this.isItemValid(var10, var3);
            int var12 = var8 ? var3.itemStackSize() : this.getItemStackLimit(var10, var3);
            InventoryItem var13 = this.getItem(var10);
            var9 += var13.item.inventoryCanAddItem(var1, var2, var13, var3, var6, var11, var12);
         }
      }

      return Math.min(var3.getAmount(), var9);
   }

   public int canAddItemOnlyCombine(Level var1, PlayerMob var2, InventoryItem var3, int var4, int var5, String var6, boolean var7, boolean var8) {
      int var9 = 0;
      if (var4 < 0) {
         var4 = 0;
      }

      if (var5 > this.items.length - 1) {
         var5 = this.items.length - 1;
      }

      for(int var10 = var4; var10 <= var5 && var9 < var3.getAmount(); ++var10) {
         if (!this.isSlotClear(var10)) {
            boolean var11 = var7 || this.isItemValid(var10, var3);
            int var12 = var8 ? var3.itemStackSize() : this.getItemStackLimit(var10, var3);
            InventoryItem var13 = this.getItem(var10);
            var9 += var13.item.inventoryCanAddItem(var1, var2, var13, var3, var6, var11, var12);
         }
      }

      return Math.min(var3.getAmount(), var9);
   }

   /** @deprecated */
   @Deprecated
   public boolean restockFrom(Level var1, PlayerMob var2, InventoryItem var3, int var4, int var5, String var6, boolean var7) {
      return this.restockFrom(var1, var2, var3, var4, var5, var6, var7, (InventoryAddConsumer)null);
   }

   public boolean restockFrom(Level var1, PlayerMob var2, InventoryItem var3, int var4, int var5, String var6, boolean var7, InventoryAddConsumer var8) {
      boolean var9 = false;
      Iterator var10 = this.getPriorityAddList(var1, var2, var3, var4, var5, var6).iterator();

      while(var10.hasNext()) {
         SlotPriority var11 = (SlotPriority)var10.next();
         if (var3.getAmount() <= 0) {
            break;
         }

         int var12 = var7 ? var3.itemStackSize() : this.getItemStackLimit(var11.slot, var3);
         InventoryItem var13 = this.getItem(var11.slot);
         if (var3 != var13 && var13.canCombine(var1, var2, var3, var6) && var13.item.onCombine(var1, var2, this, var11.slot, var13, var3, var12, var3.getAmount(), false, var6, var8)) {
            this.updateSlot(var11.slot);
            var9 = true;
         }
      }

      return var9;
   }

   public void swapItems(int var1, int var2) {
      InventoryItem var3 = this.getItem(var1);
      this.setItem(var1, this.getItem(var2));
      this.setItem(var2, var3);
      this.updateSlot(var1);
      this.updateSlot(var2);
   }

   /** @deprecated */
   @Deprecated
   public ItemCombineResult combineItems(Level var1, PlayerMob var2, int var3, int var4, int var5, boolean var6, String var7) {
      return this.combineItems(var1, var2, var3, var4, var5, var6, var7, (InventoryAddConsumer)null);
   }

   public ItemCombineResult combineItems(Level var1, PlayerMob var2, int var3, int var4, int var5, boolean var6, String var7, InventoryAddConsumer var8) {
      if (!this.isSlotClear(var3) && !this.isSlotClear(var4)) {
         ItemCombineResult var9 = this.getItem(var3).combine(var1, var2, this, var3, this.getItem(var4), var5, var6, var7, var8);
         if (var9.success) {
            this.updateSlot(var3);
            this.updateSlot(var4);
         }

         return var9;
      } else {
         return ItemCombineResult.failure();
      }
   }

   /** @deprecated */
   @Deprecated
   public ItemCombineResult combineItem(Level var1, PlayerMob var2, int var3, InventoryItem var4, int var5, boolean var6, String var7) {
      return this.combineItem(var1, var2, var3, var4, var5, var6, var7, (InventoryAddConsumer)null);
   }

   public ItemCombineResult combineItem(Level var1, PlayerMob var2, int var3, InventoryItem var4, int var5, boolean var6, String var7, InventoryAddConsumer var8) {
      if (!this.isSlotClear(var3) && var4 != null) {
         ItemCombineResult var9 = this.getItem(var3).combine(var1, var2, this, var3, var4, var5, var6, var7, var8);
         if (var9.success) {
            this.updateSlot(var3);
         }

         return var9;
      } else {
         return ItemCombineResult.failure();
      }
   }

   /** @deprecated */
   @Deprecated
   public ItemCombineResult combineItem(Level var1, PlayerMob var2, InventoryItem var3, int var4, int var5, boolean var6, String var7) {
      return this.combineItem(var1, var2, var3, var4, var5, var6, var7, (InventoryAddConsumer)null);
   }

   public ItemCombineResult combineItem(Level var1, PlayerMob var2, InventoryItem var3, int var4, int var5, boolean var6, String var7, InventoryAddConsumer var8) {
      if (var3 != null && !this.isSlotClear(var4)) {
         ItemCombineResult var9 = var3.combine(var1, var2, this, var4, this.getItem(var4), var5, var6, var7, var8);
         if (var9.success) {
            this.updateSlot(var4);
         }

         return var9;
      } else {
         return ItemCombineResult.failure();
      }
   }

   public void updateSlot(int var1) {
      InventoryItem var2 = this.getItem(var1);
      if (var2 == null) {
         this.tickSlots.remove(var1);
      } else if (var2.item.isTickItem()) {
         this.tickSlots.add(var1);
      } else {
         this.tickSlots.remove(var1);
      }

      if (var2 == null) {
         this.nextSpoilSlots.removeSlot(var1);
      } else if (var2.item.shouldSpoilTick(var2)) {
         this.nextSpoilSlots.addFirst(new SlotSpoilTime(var1, Long.MIN_VALUE));
      } else {
         this.nextSpoilSlots.removeSlot(var1);
      }

      ListIterator var3 = this.slotUpdateListeners.listIterator();
      if (var3.hasNext()) {
         InventoryUpdateListener var4 = (InventoryUpdateListener)var3.next();
         if (var4.isDisposed()) {
            var3.remove();
         } else {
            var4.onSlotUpdate(var1);
         }
      }

      this.markDirty(var1);
   }

   public InventoryUpdateListener addSlotUpdateListener(InventoryUpdateListener var1) {
      GameLinkedList.Element var2 = this.slotUpdateListeners.addLast(var1);
      var1.init(() -> {
         if (!var2.isRemoved()) {
            var2.remove();
         }

      });
      return var1;
   }

   public int getSlotUpdateListenersSize() {
      return this.slotUpdateListeners.size();
   }

   public void cleanSlotUpdateListeners() {
      this.slotUpdateListeners.removeIf(InventoryUpdateListener::isDisposed);
   }

   public boolean canBeUsedForCrafting() {
      return true;
   }

   public boolean canLockItem(int var1) {
      return false;
   }

   public boolean isItemLocked(int var1) {
      return !this.isSlotClear(var1) && this.canLockItem(var1) && this.getItem(var1).isLocked();
   }

   public void setItemLocked(int var1, boolean var2) {
      if (!this.isSlotClear(var1)) {
         if (!this.canLockItem(var1)) {
            this.getItem(var1).setLocked(false);
         } else {
            this.getItem(var1).setLocked(var2);
         }

      }
   }

   public void sortItems(int var1, int var2) {
      if (var1 < 0) {
         var1 = 0;
      }

      if (var2 > this.items.length - 1) {
         var2 = this.items.length - 1;
      }

      if (var2 < var1) {
         throw new IllegalArgumentException("End slot parameter cannot lower than start slot");
      } else if (var2 - var1 != 0) {
         Inventory var3 = new Inventory(var2 - var1 + 1);
         boolean[] var4 = new boolean[this.getSize()];

         for(int var5 = var1; var5 <= var2; ++var5) {
            var4[var5] = this.isItemLocked(var5);
            if (!this.isSlotClear(var5) && !this.isItemLocked(var5)) {
               if (this.getAmount(var5) > this.getItemSlot(var5).getStackSize()) {
                  this.setAmount(var5, this.getItemSlot(var5).getStackSize());
               }

               var3.addItem((Level)null, (PlayerMob)null, this.getItem(var5), "sort", (InventoryAddConsumer)null);
            }
         }

         ArrayList var8 = new ArrayList(var3.size);

         int var6;
         for(var6 = 0; var6 < var3.getSize(); ++var6) {
            if (!var3.isSlotClear(var6)) {
               var8.add(var3.getItem(var6));
            }
         }

         Collections.sort(var8);
         var6 = 0;

         for(int var7 = var1; var7 <= var2; ++var7) {
            if (!var4[var7]) {
               if (var6 < var8.size()) {
                  this.setItem(var7, (InventoryItem)var8.get(var6++), false);
               } else {
                  this.setItem(var7, (InventoryItem)null);
               }
            }
         }

      }
   }

   public void sortItems() {
      this.sortItems(0, this.getSize() - 1);
   }

   public void compressItems(int var1, int var2) {
      if (var1 < 0) {
         var1 = 0;
      }

      if (var2 > this.items.length - 1) {
         var2 = this.items.length - 1;
      }

      if (var2 < var1) {
         throw new IllegalArgumentException("End slot parameter cannot lower than start slot");
      } else if (var2 - var1 != 0) {
         Inventory var3 = new Inventory(var2 - var1 + 1);
         boolean[] var4 = new boolean[this.getSize()];

         int var5;
         for(var5 = var1; var5 <= var2; ++var5) {
            var4[var5] = this.isItemLocked(var5);
            if (!this.isSlotClear(var5) && !this.isItemLocked(var5)) {
               if (this.getAmount(var5) > this.getItemSlot(var5).getStackSize()) {
                  this.setAmount(var5, this.getItemSlot(var5).getStackSize());
               }

               var3.addItem((Level)null, (PlayerMob)null, this.getItem(var5), "sort", (InventoryAddConsumer)null);
            }
         }

         var5 = 0;

         for(int var6 = var1; var6 <= var2; ++var6) {
            if (!var4[var6]) {
               InventoryItem var7 = var3.getItem(var5++);
               if (var7 != null) {
                  this.setItem(var6, var7, false);
               } else {
                  this.setItem(var6, (InventoryItem)null);
               }
            }
         }

      }
   }

   public void compressItems() {
      this.compressItems(0, this.getSize() - 1);
   }

   public final boolean isItemValid(int var1, InventoryItem var2) {
      return this.filter == null || this.filter.isItemValid(var1, var2);
   }

   public final int getItemStackLimit(int var1, InventoryItem var2) {
      if (var2 == null) {
         return Integer.MAX_VALUE;
      } else {
         return this.filter == null ? var2.itemStackSize() : this.filter.getItemStackLimit(var1, var2);
      }
   }

   public Inventory getTempClone() {
      Inventory var1 = new Inventory(this.getSize()) {
         public boolean canLockItem(int var1) {
            return true;
         }
      };

      for(int var2 = 0; var2 < this.getSize(); ++var2) {
         if (this.isSlotClear(var2)) {
            var1.items[var2] = null;
         } else {
            var1.items[var2] = this.getItem(var2).copy(this.getAmount(var2), this.isItemLocked(var2));
         }
      }

      return var1;
   }

   public Packet getContentPacket() {
      Packet var1 = new Packet();
      this.writeContent(new PacketWriter(var1));
      return var1;
   }

   public void writeContent(PacketWriter var1) {
      var1.putNextShortUnsigned(this.getSize());

      for(int var2 = 0; var2 < this.getSize(); ++var2) {
         boolean var3 = !this.isSlotClear(var2);
         var1.putNextBoolean(var3);
         if (var3) {
            InventoryItem.addPacketContent(this.getItem(var2), var1);
         }
      }

   }

   public static Inventory getInventory(Packet var0) {
      return getInventory(new PacketReader(var0));
   }

   public static Inventory getInventory(PacketReader var0) {
      int var1 = var0.getNextShortUnsigned();
      Inventory var2 = new Inventory(var1) {
         public boolean canLockItem(int var1) {
            return true;
         }
      };

      for(int var3 = 0; var3 < var2.getSize(); ++var3) {
         if (var0.getNextBoolean()) {
            var2.setItem(var3, InventoryItem.fromContentPacket(var0));
         }
      }

      return var2;
   }

   private static class NextSpoilSlotList extends GameLinkedList<SlotSpoilTime> {
      private HashMap<Integer, GameLinkedList<SlotSpoilTime>.Element> nextSpoilSlotsElements = new HashMap();

      public NextSpoilSlotList() {
      }

      public void onAdded(GameLinkedList<SlotSpoilTime>.Element var1) {
         super.onAdded(var1);
         GameLinkedList.Element var2 = (GameLinkedList.Element)this.nextSpoilSlotsElements.remove(((SlotSpoilTime)var1.object).slot);
         if (var2 != null && !var2.isRemoved()) {
            var2.remove();
         }

         this.nextSpoilSlotsElements.put(((SlotSpoilTime)var1.object).slot, var1);
      }

      public void onRemoved(GameLinkedList<SlotSpoilTime>.Element var1) {
         super.onRemoved(var1);
         this.nextSpoilSlotsElements.remove(((SlotSpoilTime)var1.object).slot);
      }

      public void removeSlot(int var1) {
         GameLinkedList.Element var2 = (GameLinkedList.Element)this.nextSpoilSlotsElements.remove(var1);
         if (var2 != null && !var2.isRemoved()) {
            var2.remove();
         }

      }

      public void clear() {
         super.clear();
         this.nextSpoilSlotsElements.clear();
      }
   }

   private static class SlotSpoilTime {
      public final int slot;
      public final long tickWorldTime;

      public SlotSpoilTime(int var1, long var2) {
         this.slot = var1;
         this.tickWorldTime = var2;
      }
   }
}
