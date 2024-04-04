package necesse.inventory.item.placeableItem.consumableItem;

import java.util.ArrayList;
import java.util.Comparator;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.ComparableSequence;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.SlotPriority;
import necesse.level.maps.Level;

public interface AdventurePartyConsumableItem {
   default boolean canAddToPartyInventory(InventoryItem var1, NetworkClient var2, Inventory var3, int var4) {
      return true;
   }

   boolean canAndShouldPartyConsume(Level var1, HumanMob var2, ServerClient var3, InventoryItem var4, String var5);

   InventoryItem onPartyConsume(Level var1, HumanMob var2, ServerClient var3, InventoryItem var4, String var5);

   default boolean shouldPreventHit(Level var1, HumanMob var2, ServerClient var3, InventoryItem var4) {
      return false;
   }

   default ComparableSequence<Integer> getPartyPriority(Level var1, HumanMob var2, ServerClient var3, Inventory var4, int var5, InventoryItem var6, String var7) {
      return new ComparableSequence(var5);
   }

   static ArrayList<SlotPriority> getPartyPriorityList(Level var0, HumanMob var1, ServerClient var2, Inventory var3, String var4) {
      ArrayList var5 = new ArrayList();

      for(int var6 = 0; var6 <= var3.getSize(); ++var6) {
         InventoryItem var7 = var3.getItem(var6);
         if (var7 != null && var7.item instanceof AdventurePartyConsumableItem) {
            AdventurePartyConsumableItem var8 = (AdventurePartyConsumableItem)var7.item;
            if (var8.canAndShouldPartyConsume(var0, var1, var2, var7, var4)) {
               var5.add(new SlotPriority(var6, var8.getPartyPriority(var0, var1, var2, var3, var6, var7, var4)));
            }
         }
      }

      Comparator var9 = Comparator.comparing((var0x) -> {
         return var0x.comparable;
      });
      var5.sort(var9);
      return var5;
   }
}
