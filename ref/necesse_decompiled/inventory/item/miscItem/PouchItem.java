package necesse.inventory.item.miscItem;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Supplier;
import necesse.engine.GameState;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.util.ComparableSequence;
import necesse.engine.world.GameClock;
import necesse.engine.world.WorldSettings;
import necesse.entity.Entity;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryAddConsumer;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryItemsRemoved;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.SlotPriority;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerActionResult;
import necesse.inventory.container.item.ItemInventoryContainer;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemUsed;
import necesse.inventory.item.TickItem;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.IngredientCounter;
import necesse.level.maps.Level;

public abstract class PouchItem extends Item implements InternalInventoryItemInterface, TickItem {
   public HashSet<String> combinePurposes = new HashSet();
   public boolean isCombinePurposesBlacklist = false;
   public HashSet<String> insertPurposes = new HashSet();
   public boolean isInsertPurposesBlacklist = false;
   public HashSet<String> requestPurposes = new HashSet();
   public boolean isRequestPurposeBlacklist = true;
   public boolean drawStoredItems = true;
   public boolean canUseHealthPotionsFromPouch = false;
   public boolean canUseManaPotionsFromPouch = false;
   public boolean canEatFoodFromPouch = false;
   public boolean canUseBuffPotionsFromPouch = false;
   public boolean allowRestockFrom = true;
   protected GameTexture fullTexture;

   protected boolean isValidPurpose(HashSet<String> var1, boolean var2, String var3) {
      return var2 != var1.contains(var3);
   }

   public PouchItem() {
      super(1);
      this.setItemCategory(new String[]{"misc", "pouches"});
      this.combinePurposes.add("leftclick");
      this.combinePurposes.add("leftclickinv");
      this.combinePurposes.add("rightclick");
      this.combinePurposes.add("lootall");
      this.combinePurposes.add("restockfrom");
      this.insertPurposes.add("itempickup");
      this.insertPurposes.add("lootall");
      this.insertPurposes.add("restockfrom");
      this.requestPurposes.add("quickstackto");
      this.worldDrawSize = 32;
      this.incinerationTimeMillis = 60000;
   }

   public void tick(Inventory var1, int var2, InventoryItem var3, GameClock var4, GameState var5, Entity var6, WorldSettings var7, Consumer<InventoryItem> var8) {
      this.tickInternalInventory(var3, var4, var5, var6, var7);
   }

   public abstract boolean isValidPouchItem(InventoryItem var1);

   public boolean isValidAddItem(InventoryItem var1) {
      return this.isValidPouchItem(var1);
   }

   public abstract boolean isValidRequestItem(Item var1);

   public abstract boolean isValidRequestType(Item.Type var1);

   protected int getStoredItemAmounts(InventoryItem var1) {
      Inventory var2 = this.getInternalInventory(var1);
      int var3 = 0;

      for(int var4 = 0; var4 < var2.getSize(); ++var4) {
         var3 += var2.getAmount(var4);
      }

      return var3;
   }

   public void draw(InventoryItem var1, PlayerMob var2, int var3, int var4, boolean var5) {
      super.draw(var1, var2, var3, var4, var5);
      if (this.drawStoredItems) {
         int var6 = this.getStoredItemAmounts(var1);
         if (var6 > 0 || var5) {
            if (var6 > 999) {
               var6 = 999;
            }

            String var7 = String.valueOf(var6);
            int var8 = FontManager.bit.getWidthCeil(var7, tipFontOptions);
            FontManager.bit.drawString((float)(var3 + 28 - var8), (float)(var4 + 16), var7, tipFontOptions);
         }
      }

   }

   public GameSprite getItemSprite(InventoryItem var1, PlayerMob var2) {
      if (this.fullTexture != null) {
         Inventory var3 = this.getInternalInventory(var1);

         for(int var4 = 0; var4 < var3.getSize(); ++var4) {
            if (!var3.isSlotClear(var4)) {
               return new GameSprite(this.fullTexture, 32);
            }
         }
      }

      return super.getItemSprite(var1, var2);
   }

   protected void loadItemTextures() {
      super.loadItemTextures();

      try {
         this.fullTexture = GameTexture.fromFileRaw("items/" + this.getStringID() + "_full");
      } catch (FileNotFoundException var2) {
         this.fullTexture = null;
      }

   }

   public void setDrawAttackRotation(InventoryItem var1, ItemAttackDrawOptions var2, float var3, float var4, float var5) {
      var2.swingRotation(var5);
   }

   public GameMessage getLocalization(InventoryItem var1) {
      String var2 = this.getPouchName(var1);
      return (GameMessage)(var2 != null ? new StaticMessage(var2) : super.getLocalization(var1));
   }

   public float getBrokerValue(InventoryItem var1) {
      float var2 = super.getBrokerValue(var1);
      Inventory var3 = this.getInternalInventory(var1);

      for(int var4 = 0; var4 < var3.getSize(); ++var4) {
         if (!var3.isSlotClear(var4)) {
            var2 += var3.getItem(var4).getBrokerValue();
         }
      }

      return var2;
   }

   public Supplier<ContainerActionResult> getInventoryRightClickAction(Container var1, InventoryItem var2, int var3, ContainerSlot var4) {
      return () -> {
         PlayerInventorySlot var3 = null;
         if (var4.getInventory() == var1.getClient().playerMob.getInv().main) {
            var3 = new PlayerInventorySlot(var1.getClient().playerMob.getInv().main, var4.getInventorySlot());
         }

         if (var4.getInventory() == var1.getClient().playerMob.getInv().cloud) {
            var3 = new PlayerInventorySlot(var1.getClient().playerMob.getInv().cloud, var4.getInventorySlot());
         }

         if (var3 != null) {
            if (var1.getClient().isServer()) {
               ServerClient var4x = var1.getClient().getServerClient();
               this.openContainer(var4x, var3);
            }

            return new ContainerActionResult(-1002911334);
         } else {
            return new ContainerActionResult(208675834, Localization.translate("itemtooltip", "rclickinvopenerror"));
         }
      };
   }

   protected void openContainer(ServerClient var1, PlayerInventorySlot var2) {
      PacketOpenContainer var3 = new PacketOpenContainer(ContainerRegistry.ITEM_INVENTORY_CONTAINER, ItemInventoryContainer.getContainerContent(this, var2));
      ContainerRegistry.openAndSendContainer(var1, var3);
   }

   public boolean canCombineItem(Level var1, PlayerMob var2, InventoryItem var3, InventoryItem var4, String var5) {
      if (var4 == null) {
         return false;
      } else {
         return this.isSameItem(var1, var3, var4, var5) || this.isValidPurpose(this.combinePurposes, this.isCombinePurposesBlacklist, var5) && this.isValidPouchItem(var4);
      }
   }

   public boolean ignoreCombineStackLimit(Level var1, PlayerMob var2, InventoryItem var3, InventoryItem var4, String var5) {
      if (var4 == null) {
         return false;
      } else {
         return this.isValidPurpose(this.combinePurposes, this.isCombinePurposesBlacklist, var5) && this.isValidPouchItem(var4);
      }
   }

   public boolean onCombine(Level var1, PlayerMob var2, Inventory var3, int var4, InventoryItem var5, InventoryItem var6, int var7, int var8, boolean var9, String var10, InventoryAddConsumer var11) {
      boolean var12 = false;
      if (this.isValidPurpose(this.combinePurposes, this.isCombinePurposesBlacklist, var10)) {
         if (var10.equals("lootall")) {
            var12 = this.isValidAddItem(var6);
         } else {
            var12 = this.isValidPouchItem(var6);
         }
      }

      if (var12) {
         Inventory var13 = this.getInternalInventory(var5);
         if (var10.equals("restockfrom")) {
            if (var13.restockFrom(var1, var2, var6, 0, var13.getSize(), var10, false, var11)) {
               this.saveInternalInventory(var5, var13);
               return true;
            } else {
               return false;
            }
         } else {
            int var14 = Math.min(var8, var6.getAmount());
            InventoryItem var15 = var6.copy(var14);
            var13.addItem(var1, var2, var15, "pouchinsert", var11);
            if (var15.getAmount() != var14) {
               int var16 = var14 - var15.getAmount();
               var6.setAmount(var6.getAmount() - var16);
               this.saveInternalInventory(var5, var13);
               return true;
            } else {
               return false;
            }
         }
      } else {
         return super.onCombine(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
      }
   }

   public ComparableSequence<Integer> getInventoryAddPriority(Level var1, PlayerMob var2, Inventory var3, int var4, InventoryItem var5, InventoryItem var6, String var7) {
      ComparableSequence var8 = super.getInventoryAddPriority(var1, var2, var3, var4, var5, var6, var7);
      if (!this.isValidAddItem(var6) || !this.isValidPurpose(this.insertPurposes, this.isInsertPurposesBlacklist, var7) && (!this.allowRestockFrom || !var7.equals("restockfrom"))) {
         return var8;
      } else {
         var8 = var8.beforeBy((int)-10000);
         Inventory var9 = this.getInternalInventory(var5);
         ArrayList var10 = var9.getPriorityAddList(var1, var2, var6, 0, var9.getSize() - 1, var7);
         Iterator var11 = var10.iterator();

         boolean var13;
         int var14;
         InventoryItem var15;
         do {
            if (!var11.hasNext()) {
               return var8.thenBy((int)10000);
            }

            SlotPriority var12 = (SlotPriority)var11.next();
            var13 = var9.isItemValid(var12.slot, var6);
            var14 = var9.getItemStackLimit(var12.slot, var6);
            var15 = var9.getItem(var12.slot);
         } while(var15.item.inventoryCanAddItem(var1, var2, var15, var6, var7, var13, var14) <= 0);

         return var8.thenBy((Comparable)var4).thenBy(((SlotPriority)var10.get(0)).comparable);
      }
   }

   public ComparableSequence<Integer> getInventoryPriority(Level var1, PlayerMob var2, Inventory var3, int var4, InventoryItem var5, String var6) {
      return super.getInventoryPriority(var1, var2, var3, var4, var5, var6).beforeBy((int)-10000);
   }

   public int getInventoryAmount(Level var1, PlayerMob var2, InventoryItem var3, Item var4, String var5) {
      int var6 = super.getInventoryAmount(var1, var2, var3, var4, var5);
      if (this.isValidPurpose(this.requestPurposes, this.isRequestPurposeBlacklist, var5) && this.isValidRequestItem(var4)) {
         Inventory var7 = this.getInternalInventory(var3);
         var6 += var7.getAmount(var1, var2, var4, var5);
      }

      return var6;
   }

   public int getInventoryAmount(Level var1, PlayerMob var2, InventoryItem var3, Item.Type var4, String var5) {
      int var6 = super.getInventoryAmount(var1, var2, var3, var4, var5);
      if (this.isValidRequestType(var4)) {
         Inventory var7 = this.getInternalInventory(var3);
         var6 += var7.getAmount(var1, var2, var4, var5);
      }

      return var6;
   }

   public void countIngredientAmount(Level var1, PlayerMob var2, Inventory var3, int var4, InventoryItem var5, IngredientCounter var6) {
      this.getInternalInventory(var5).countIngredientAmount(var1, var2, var6);
      super.countIngredientAmount(var1, var2, var3, var4, var5, var6);
   }

   public Item getInventoryFirstItem(Level var1, PlayerMob var2, InventoryItem var3, Item[] var4, String var5) {
      if (this.isValidPurpose(this.requestPurposes, this.isRequestPurposeBlacklist, var5) && Arrays.stream(var4).anyMatch(this::isValidRequestItem)) {
         Inventory var6 = this.getInternalInventory(var3);
         Item var7 = var6.getFirstItem(var1, var2, var4, var5);
         if (var7 != null) {
            return var7;
         }
      }

      return super.getInventoryFirstItem(var1, var2, var3, var4, var5);
   }

   public Item getInventoryFirstItem(Level var1, PlayerMob var2, InventoryItem var3, Item.Type var4, String var5) {
      if (this.isValidRequestType(var4)) {
         Inventory var6 = this.getInternalInventory(var3);
         Item var7 = var6.getFirstItem(var1, var2, var4, var5);
         if (var7 != null) {
            return var7;
         }
      }

      return super.getInventoryFirstItem(var1, var2, var3, var4, var5);
   }

   public boolean inventoryAddItem(Level var1, PlayerMob var2, Inventory var3, int var4, InventoryItem var5, InventoryItem var6, String var7, boolean var8, int var9, boolean var10, InventoryAddConsumer var11) {
      if (this.isValidAddItem(var6) && this.isValidPurpose(this.insertPurposes, this.isInsertPurposesBlacklist, var7)) {
         Inventory var12 = this.getInternalInventory(var5);
         boolean var13 = var12.addItem(var1, var2, var6, var7, var11);
         if (var13) {
            this.saveInternalInventory(var5, var12);
            return true;
         }
      }

      return super.inventoryAddItem(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   public int inventoryCanAddItem(Level var1, PlayerMob var2, InventoryItem var3, InventoryItem var4, String var5, boolean var6, int var7) {
      if (this.isValidAddItem(var4)) {
         Inventory var8 = this.getInternalInventory(var3);
         return var8.canAddItem(var1, var2, var4, var5);
      } else {
         return super.inventoryCanAddItem(var1, var2, var3, var4, var5, var6, var7);
      }
   }

   public int removeInventoryAmount(Level var1, PlayerMob var2, InventoryItem var3, Item var4, int var5, String var6) {
      int var7 = 0;
      if (this.isValidPurpose(this.requestPurposes, this.isRequestPurposeBlacklist, var6) && this.isValidRequestItem(var4)) {
         Inventory var8 = this.getInternalInventory(var3);
         var7 = var8.removeItems(var1, var2, var4, var5, var6);
         if (var7 > 0) {
            this.saveInternalInventory(var3, var8);
         }
      }

      return var7 < var5 ? var7 + super.removeInventoryAmount(var1, var2, var3, var4, var5, var6) : var7;
   }

   public int removeInventoryAmount(Level var1, PlayerMob var2, InventoryItem var3, Item.Type var4, int var5, String var6) {
      int var7 = 0;
      if (this.isValidRequestType(var4)) {
         Inventory var8 = this.getInternalInventory(var3);
         var7 = var8.removeItems(var1, var2, var4, var5, var6);
         if (var7 > 0) {
            this.saveInternalInventory(var3, var8);
         }
      }

      return var7 < var5 ? var7 + super.removeInventoryAmount(var1, var2, var3, var4, var5, var6) : var7;
   }

   public int removeInventoryAmount(Level var1, PlayerMob var2, InventoryItem var3, Inventory var4, int var5, Ingredient var6, int var7, Collection<InventoryItemsRemoved> var8) {
      Inventory var9 = this.getInternalInventory(var3);
      int var10 = var9.removeItems(var1, var2, var6, var7, var8);
      if (var10 > 0) {
         this.saveInternalInventory(var3, var9);
      }

      return var10 < var7 ? var10 + super.removeInventoryAmount(var1, var2, var3, var4, var5, var6, var7, var8) : var10;
   }

   public boolean isValidItem(InventoryItem var1) {
      return var1 == null ? true : this.isValidPouchItem(var1);
   }

   public ItemUsed useHealthPotion(Level var1, PlayerMob var2, InventoryItem var3) {
      if (this.canUseHealthPotionsFromPouch) {
         Inventory var4 = this.getInternalInventory(var3);
         Iterator var5 = var4.getPriorityList(var1, var2, 0, var4.getSize() - 1, "usehealthpotion").iterator();

         while(var5.hasNext()) {
            SlotPriority var6 = (SlotPriority)var5.next();
            if (!var4.isSlotClear(var6.slot)) {
               ItemUsed var7 = var4.getItemSlot(var6.slot).useHealthPotion(var1, var2, var4.getItem(var6.slot));
               var4.setItem(var6.slot, var7.item);
               if (var7.used) {
                  this.saveInternalInventory(var3, var4);
                  return new ItemUsed(true, var3);
               }
            }
         }
      }

      return super.useHealthPotion(var1, var2, var3);
   }

   public ItemUsed useManaPotion(Level var1, PlayerMob var2, InventoryItem var3) {
      if (this.canUseManaPotionsFromPouch) {
         Inventory var4 = this.getInternalInventory(var3);
         Iterator var5 = var4.getPriorityList(var1, var2, 0, var4.getSize() - 1, "usemanapotion").iterator();

         while(var5.hasNext()) {
            SlotPriority var6 = (SlotPriority)var5.next();
            if (!var4.isSlotClear(var6.slot)) {
               ItemUsed var7 = var4.getItemSlot(var6.slot).useManaPotion(var1, var2, var4.getItem(var6.slot));
               var4.setItem(var6.slot, var7.item);
               if (var7.used) {
                  this.saveInternalInventory(var3, var4);
                  return new ItemUsed(true, var3);
               }
            }
         }
      }

      return super.useManaPotion(var1, var2, var3);
   }

   public ItemUsed eatFood(Level var1, PlayerMob var2, InventoryItem var3) {
      if (this.canEatFoodFromPouch) {
         Inventory var4 = this.getInternalInventory(var3);
         Iterator var5 = var4.getPriorityList(var1, var2, 0, var4.getSize() - 1, "eatfood").iterator();

         while(var5.hasNext()) {
            SlotPriority var6 = (SlotPriority)var5.next();
            if (!var4.isSlotClear(var6.slot)) {
               ItemUsed var7 = var4.getItemSlot(var6.slot).eatFood(var1, var2, var4.getItem(var6.slot));
               var4.setItem(var6.slot, var7.item);
               if (var7.used) {
                  this.saveInternalInventory(var3, var4);
                  return new ItemUsed(true, var3);
               }
            }
         }
      }

      return super.eatFood(var1, var2, var3);
   }

   public ItemUsed useBuffPotion(Level var1, PlayerMob var2, InventoryItem var3) {
      if (!this.canUseBuffPotionsFromPouch) {
         return super.useBuffPotion(var1, var2, var3);
      } else {
         Inventory var4 = this.getInternalInventory(var3);
         boolean var5 = false;
         Iterator var6 = var4.getPriorityList(var1, var2, 0, var4.getSize() - 1, "usebuffpotion").iterator();

         while(true) {
            SlotPriority var7;
            do {
               if (!var6.hasNext()) {
                  if (var5) {
                     this.saveInternalInventory(var3, var4);
                  }

                  return new ItemUsed(var5, var3);
               }

               var7 = (SlotPriority)var6.next();
            } while(var4.isSlotClear(var7.slot));

            ItemUsed var8 = var4.getItemSlot(var7.slot).useBuffPotion(var1, var2, var4.getItem(var7.slot));
            var5 = var5 || var8.used;
            var4.setItem(var7.slot, var8.item);
         }
      }
   }
}
