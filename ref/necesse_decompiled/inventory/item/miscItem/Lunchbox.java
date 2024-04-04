package necesse.inventory.item.miscItem;

import java.util.Iterator;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.ComparableSequence;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.SlotPriority;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.consumableItem.AdventurePartyConsumableItem;
import necesse.inventory.item.placeableItem.consumableItem.food.FoodConsumableItem;
import necesse.inventory.item.placeableItem.consumableItem.food.FoodMatItem;
import necesse.level.maps.Level;

public class Lunchbox extends PouchItem implements AdventurePartyConsumableItem {
   public Lunchbox() {
      this.rarity = Item.Rarity.UNCOMMON;
      this.canEatFoodFromPouch = true;
      this.canUseBuffPotionsFromPouch = true;
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "lunchboxtip1"));
      var4.add(Localization.translate("itemtooltip", "lunchboxtip2"));
      var4.add(Localization.translate("itemtooltip", "rclickinvopentip"));
      var4.add(Localization.translate("itemtooltip", "storedfood", "items", (Object)this.getStoredItemAmounts(var1)));
      return var4;
   }

   public void setDrawAttackRotation(InventoryItem var1, ItemAttackDrawOptions var2, float var3, float var4, float var5) {
      var2.swingRotationInv(var5);
   }

   public InventoryItem onAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      boolean var11 = var1.getWorldSettings() == null || var1.getWorldSettings().playerHunger();
      ActiveBuff var12 = var4.buffManager.getBuff((Buff)BuffRegistry.FOOD_BUFF);
      if (var4.hungerLevel < 1.0F && var11 || var12 == null) {
         Inventory var13 = this.getInternalInventory(var6);
         boolean var14 = false;

         for(int var15 = 0; var15 < var13.getSize(); ++var15) {
            if (!var13.isSlotClear(var15)) {
               InventoryItem var16 = var13.getItem(var15);
               if (var16.item.isFoodItem()) {
                  FoodConsumableItem var17 = (FoodConsumableItem)var16.item;
                  if (var17.consume(var1, var4, var16)) {
                     var17.playConsumeSound(var1, var4, var16);
                     var13.setItem(var15, var16);
                     var14 = true;
                     break;
                  }
               }
            }
         }

         if (var14) {
            this.saveInternalInventory(var6, var13);
         }
      }

      return super.onAttack(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   public boolean isValidPouchItem(InventoryItem var1) {
      return this.isValidRequestItem(var1.item);
   }

   public boolean isValidAddItem(InventoryItem var1) {
      if (var1.item.isFoodItem()) {
         FoodConsumableItem var2 = (FoodConsumableItem)var1.item;
         return !var2.isDebuff;
      } else {
         return false;
      }
   }

   public boolean isValidRequestItem(Item var1) {
      return var1 != null && var1.isFoodItem() || var1 instanceof FoodMatItem;
   }

   public boolean isValidRequestType(Item.Type var1) {
      return false;
   }

   public int getInternalInventorySize() {
      return 10;
   }

   public Inventory getInternalInventory(InventoryItem var1) {
      Inventory var2 = super.getInternalInventory(var1);
      var2.spoilRateModifier = 0.5F;
      return var2;
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
