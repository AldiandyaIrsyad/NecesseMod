package necesse.inventory.lootTable.lootItem;

import java.util.Iterator;
import java.util.List;
import necesse.engine.util.GameRandom;
import necesse.engine.util.ProtectedTicketSystemList;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootList;

public class OneOfTicketLootItems extends ProtectedTicketSystemList<LootItemInterface> implements LootItemInterface {
   public static final int defaultTickets = 100;

   public OneOfTicketLootItems(Object... var1) {
      this.addAll(var1);
   }

   public OneOfTicketLootItems(OneOfTicketLootItems var1, Object... var2) {
      super(var1);
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
      Iterator var3 = this.getTicketItems().iterator();

      while(var3.hasNext()) {
         ProtectedTicketSystemList.TicketObject var4 = (ProtectedTicketSystemList.TicketObject)var3.next();
         ((LootItemInterface)var4.object).addPossibleLoot(var1, var2);
      }

   }

   public void addItems(List<InventoryItem> var1, GameRandom var2, float var3, Object... var4) {
      ((LootItemInterface)this.getRandomObject(var2)).addItems(var1, var2, var3, var4);
   }

   public OneOfTicketLootItems addLoot(int var1, LootItemInterface var2) {
      this.addObject(var1, var2);
      return this;
   }

   public OneOfTicketLootItems addAll(OneOfTicketLootItems var1) {
      super.addAll(var1);
      return this;
   }
}
