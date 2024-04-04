package necesse.inventory.item.miscItem;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashSet;
import java.util.function.Supplier;
import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.ComparableSequence;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryAddConsumer;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryItemsRemoved;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerActionResult;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.Item;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.IngredientCounter;
import necesse.level.maps.Level;

public class CoinPouch extends Item {
   public HashSet<String> combinePurposes = new HashSet();

   public CoinPouch() {
      super(1);
      this.setItemCategory(new String[]{"misc", "pouches"});
      this.rarity = Item.Rarity.RARE;
      this.combinePurposes.add("leftclick");
      this.combinePurposes.add("leftclickinv");
      this.combinePurposes.add("rightclick");
      this.combinePurposes.add("lootall");
      this.combinePurposes.add("pouchtransfer");
      this.worldDrawSize = 32;
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "coinpouchtip1"));
      var4.add(Localization.translate("itemtooltip", "coinpouchtip2"));
      var4.add(Localization.translate("itemtooltip", "coinpouchstored", "coins", GameUtils.metricNumber((long)getCurrentCoins(var1))));
      return var4;
   }

   public void setDrawAttackRotation(InventoryItem var1, ItemAttackDrawOptions var2, float var3, float var4, float var5) {
      var2.swingRotation(var5);
   }

   public float getBrokerValue(InventoryItem var1) {
      return super.getBrokerValue(var1) + (float)getCurrentCoins(var1);
   }

   public InventoryItem onAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      int var11 = getCurrentCoins(var6);
      int var12 = Math.min(1000, var11);
      if (var12 > 0) {
         setCurrentCoins(var6, var11 - var12);
         if (var1.isServer()) {
            Point2D.Float var13 = GameMath.normalize((float)var2 - var4.x, (float)var3 - var4.y);
            var1.entityManager.pickups.add((new InventoryItem("coin", var12)).getPickupEntity(var1, var4.x, var4.y, var13.x * 175.0F, var13.y * 175.0F));
         } else if (var1.isClient()) {
            Screen.playSound(GameResources.coins, SoundEffect.effect(var4));
         }
      }

      return var6;
   }

   public Supplier<ContainerActionResult> getInventoryRightClickAction(Container var1, InventoryItem var2, int var3, ContainerSlot var4) {
      return var1.client.playerMob.isInventoryExtended() ? () -> {
         int var2x = getCurrentCoins(var2);
         if (var2x > 0) {
            ContainerSlot var3 = var1.getClientDraggingSlot();
            Item var4 = ItemRegistry.getItem("coin");
            int var5 = Math.min(var2x, var4.getStackSize());
            InventoryItem var6 = new InventoryItem(var4, var5);
            if (var3.isClear()) {
               setCurrentCoins(var2, var2x - var6.getAmount());
               var3.setItem(var6);
               return new ContainerActionResult(2657165);
            } else {
               if (var3.getItem().canCombine(var1.client.playerMob.getLevel(), var1.client.playerMob, var6, "pouchtransfer") && var3.getItem().combine(var1.client.playerMob.getLevel(), var1.client.playerMob, var3.getInventory(), var3.getInventorySlot(), var6, var6.getAmount(), false, "pouchtransfer", (InventoryAddConsumer)null).success) {
                  int var7 = var5 - var6.getAmount();
                  setCurrentCoins(var2, var2x - var7);
               }

               return new ContainerActionResult(10619587);
            }
         } else {
            return new ContainerActionResult(3401846);
         }
      } : null;
   }

   public boolean isSameGNDData(Level var1, InventoryItem var2, InventoryItem var3, String var4) {
      return var2.getGndData().sameKeys(var3.getGndData(), "coins");
   }

   public boolean canCombineItem(Level var1, PlayerMob var2, InventoryItem var3, InventoryItem var4, String var5) {
      if (var4 == null) {
         return false;
      } else {
         return this.isSameItem(var1, var3, var4, var5) || this.combinePurposes.contains(var5) && var4.item.getStringID().equals("coin");
      }
   }

   public boolean onCombine(Level var1, PlayerMob var2, Inventory var3, int var4, InventoryItem var5, InventoryItem var6, int var7, int var8, boolean var9, String var10, InventoryAddConsumer var11) {
      if (this.combinePurposes.contains(var10) && var6.item.getStringID().equals("coin")) {
         setCurrentCoins(var5, getCurrentCoins(var5) + var8);
         var6.setAmount(var6.getAmount() - var8);
         if (var11 != null) {
            var11.add((Inventory)null, 0, var8);
         }

         return true;
      } else {
         return super.onCombine(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
      }
   }

   public ComparableSequence<Integer> getInventoryAddPriority(Level var1, PlayerMob var2, Inventory var3, int var4, InventoryItem var5, InventoryItem var6, String var7) {
      ComparableSequence var8 = super.getInventoryAddPriority(var1, var2, var3, var4, var5, var6, var7);
      return var6.item.getStringID().equals("coin") && var7.equals("itempickup") ? var8.beforeBy((int)-10000) : var8;
   }

   public int getInventoryAmount(Level var1, PlayerMob var2, InventoryItem var3, Item var4, String var5) {
      return var5.equals("buy") && var4.getStringID().equals("coin") ? getCurrentCoins(var3) : super.getInventoryAmount(var1, var2, var3, var4, var5);
   }

   public void countIngredientAmount(Level var1, PlayerMob var2, Inventory var3, int var4, InventoryItem var5, IngredientCounter var6) {
      var6.handle((Inventory)null, var4, new InventoryItem("coin", getCurrentCoins(var5)));
      super.countIngredientAmount(var1, var2, var3, var4, var5, var6);
   }

   public boolean inventoryAddItem(Level var1, PlayerMob var2, Inventory var3, int var4, InventoryItem var5, InventoryItem var6, String var7, boolean var8, int var9, boolean var10, InventoryAddConsumer var11) {
      if (var6.item.getStringID().equals("coin") && (var7.equals("itempickup") || var7.equals("sell"))) {
         int var12 = var6.getAmount();
         setCurrentCoins(var5, getCurrentCoins(var5) + var12);
         if (var11 != null) {
            var11.add((Inventory)null, 0, var12);
         }

         var6.setAmount(0);
         return true;
      } else {
         return super.inventoryAddItem(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
      }
   }

   public int inventoryCanAddItem(Level var1, PlayerMob var2, InventoryItem var3, InventoryItem var4, String var5, boolean var6, int var7) {
      return var4.item.getStringID().equals("coin") ? var4.getAmount() : super.inventoryCanAddItem(var1, var2, var3, var4, var5, var6, var7);
   }

   public int removeInventoryAmount(Level var1, PlayerMob var2, InventoryItem var3, Item var4, int var5, String var6) {
      return var4.getStringID().equals("coin") ? removeCoins(var3, var5) : super.removeInventoryAmount(var1, var2, var3, var4, var5, var6);
   }

   public int removeInventoryAmount(Level var1, PlayerMob var2, final InventoryItem var3, Inventory var4, int var5, Ingredient var6, int var7, Collection<InventoryItemsRemoved> var8) {
      Item var9 = ItemRegistry.getItem("coin");
      if (var6.matchesItem(var9)) {
         final int var10 = removeCoins(var3, var7);
         if (var10 > 0 && var8 != null) {
            var8.add(new InventoryItemsRemoved(var4, var5, new InventoryItem("coin"), var10) {
               public void revert() {
                  CoinPouch.setCurrentCoins(var3, CoinPouch.getCurrentCoins(var3) + var10);
               }
            });
         }

         return var10;
      } else {
         return super.removeInventoryAmount(var1, var2, var3, var4, var5, var6, var7, var8);
      }
   }

   public static int removeCoins(InventoryItem var0, int var1) {
      int var2 = getCurrentCoins(var0);
      int var3 = Math.min(var2, var1);
      var2 -= var3;
      setCurrentCoins(var0, var2);
      return var3;
   }

   public static int getCurrentCoins(InventoryItem var0) {
      return var0.getGndData().getInt("coins");
   }

   public static void setCurrentCoins(InventoryItem var0, int var1) {
      var0.getGndData().setInt("coins", var1);
   }
}
