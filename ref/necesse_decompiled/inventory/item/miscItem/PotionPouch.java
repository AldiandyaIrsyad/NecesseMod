package necesse.inventory.item.miscItem;

import java.util.Iterator;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.ComparableSequence;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.SlotPriority;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemUsed;
import necesse.inventory.item.placeableItem.consumableItem.AdventurePartyConsumableItem;
import necesse.level.maps.Level;

public class PotionPouch extends PouchItem implements AdventurePartyConsumableItem {
   public PotionPouch() {
      this.rarity = Item.Rarity.UNCOMMON;
      this.canUseHealthPotionsFromPouch = true;
      this.canUseManaPotionsFromPouch = true;
      this.canEatFoodFromPouch = true;
      this.canUseBuffPotionsFromPouch = true;
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "potionpouchtip1"));
      var4.add(Localization.translate("itemtooltip", "potionpouchtip2"));
      var4.add(Localization.translate("itemtooltip", "rclickinvopentip"));
      var4.add(Localization.translate("itemtooltip", "storedpotions", "items", (Object)this.getStoredItemAmounts(var1)));
      return var4;
   }

   public void setDrawAttackRotation(InventoryItem var1, ItemAttackDrawOptions var2, float var3, float var4, float var5) {
      var2.swingRotationInv(var5);
   }

   public InventoryItem onAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      Inventory var11 = this.getInternalInventory(var6);
      boolean var12 = false;
      Iterator var13 = var11.getPriorityList(var1, var4, 0, var11.getSize() - 1, "usebuffpotion").iterator();

      while(true) {
         SlotPriority var14;
         do {
            if (!var13.hasNext()) {
               if (var12) {
                  this.saveInternalInventory(var6, var11);
               }

               return super.onAttack(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
            }

            var14 = (SlotPriority)var13.next();
         } while(var11.isSlotClear(var14.slot));

         ItemUsed var15 = var11.getItemSlot(var14.slot).useBuffPotion(var1, var4, var11.getItem(var14.slot));
         var12 = var12 || var15.used;
         var11.setItem(var14.slot, var15.item);
      }
   }

   public boolean isValidPouchItem(InventoryItem var1) {
      return this.isValidRequestItem(var1.item);
   }

   public boolean isValidRequestItem(Item var1) {
      return var1.isPotion();
   }

   public boolean isValidRequestType(Item.Type var1) {
      return false;
   }

   public int getInternalInventorySize() {
      return 10;
   }

   public boolean canAndShouldPartyConsume(Level var1, HumanMob var2, ServerClient var3, InventoryItem var4, String var5) {
      return true;
   }

   public InventoryItem onPartyConsume(Level var1, HumanMob var2, ServerClient var3, InventoryItem var4, String var5) {
      Inventory var6 = this.getInternalInventory(var4);
      Iterator var7 = AdventurePartyConsumableItem.getPartyPriorityList(var1, var2, var3, var6, var5).iterator();
      if (var7.hasNext()) {
         SlotPriority var8 = (SlotPriority)var7.next();
         InventoryItem var9 = var6.getItem(var8.slot);
         AdventurePartyConsumableItem var10 = (AdventurePartyConsumableItem)var9.item;
         InventoryItem var11 = var9.copy();
         var10.onPartyConsume(var2.getLevel(), var2, var3, var9, var5);
         if (var9.getAmount() <= 0) {
            var6.clearSlot(var8.slot);
         }

         return var11;
      } else {
         return null;
      }
   }

   public ComparableSequence<Integer> getPartyPriority(Level var1, HumanMob var2, ServerClient var3, Inventory var4, int var5, InventoryItem var6, String var7) {
      return AdventurePartyConsumableItem.super.getPartyPriority(var1, var2, var3, var4, var5, var6, var7).beforeBy((int)-10000);
   }
}
