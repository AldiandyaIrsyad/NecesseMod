package necesse.inventory.lootTable.lootItem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import necesse.engine.util.GameRandom;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootList;

public class CountOfTicketLootItems implements LootItemInterface {
   public static final int defaultTickets = 100;
   protected int count;
   protected ArrayList<TicketItem> items = new ArrayList();
   protected int totalTickets;

   public CountOfTicketLootItems(int var1, Object... var2) {
      this.count = var1;
      this.addAll(var2);
   }

   public CountOfTicketLootItems(CountOfTicketLootItems var1, Object... var2) {
      this.count = var1.count;
      this.addAll(var1);
      this.addAll(var2);
   }

   protected void addAll(Object... var1) {
      int var2 = 100;
      Object[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Object var6 = var3[var5];
         if (var6 instanceof Integer) {
            var2 = (Integer)var6;
         } else {
            if (!(var6 instanceof LootItemInterface)) {
               throw new IllegalArgumentException("Unknown object  " + var6 + ". Must be either Integer or LootItemInterface.");
            }

            this.addLoot(var2, (LootItemInterface)var6);
            var2 = 100;
         }
      }

   }

   public void addPossibleLoot(LootList var1, Object... var2) {
      Iterator var3 = this.items.iterator();

      while(var3.hasNext()) {
         TicketItem var4 = (TicketItem)var3.next();
         var4.loot.addPossibleLoot(var1, var2);
      }

   }

   public void addItems(List<InventoryItem> var1, GameRandom var2, float var3, Object... var4) {
      int var5 = this.totalTickets;
      ArrayList var6 = new ArrayList(this.items);

      for(int var7 = 0; var7 < this.count; ++var7) {
         if (var6.isEmpty()) {
            return;
         }

         int var8 = this.getTicketIndex(var2.nextInt(var5), var6);
         TicketItem var9 = (TicketItem)var6.remove(var8);
         var9.loot.addItems(var1, var2, var3, var4);
         var5 -= var9.tickets;
      }

   }

   protected int getTicketIndex(int var1, ArrayList<TicketItem> var2) {
      for(int var3 = 0; var3 < var2.size(); ++var3) {
         TicketItem var4 = (TicketItem)var2.get(var3);
         if (var1 < var4.tickets) {
            return var3;
         }

         var1 -= var4.tickets;
      }

      return -1;
   }

   public CountOfTicketLootItems addLoot(int var1, LootItemInterface var2) {
      this.items.add(new TicketItem(var1, var2));
      this.totalTickets += var1;
      return this;
   }

   public CountOfTicketLootItems addAll(CountOfTicketLootItems var1) {
      this.items.addAll(var1.items);
      this.totalTickets += var1.totalTickets;
      return this;
   }

   private static class TicketItem {
      public final LootItemInterface loot;
      public final int tickets;

      public TicketItem(int var1, LootItemInterface var2) {
         this.loot = var2;
         this.tickets = var1;
      }
   }
}
