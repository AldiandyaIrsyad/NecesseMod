package necesse.inventory.recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Consumer;
import necesse.engine.localization.Localization;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.HashMapSet;
import necesse.engine.util.MapIterator;
import necesse.engine.util.ObjectValue;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.SpacerGameTooltip;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryItemsRemoved;
import necesse.inventory.InventoryRange;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class Recipe {
   public final Ingredient[] ingredients;
   public final String resultID;
   public final int resultAmount;
   public final Tech tech;
   public final boolean isHidden;
   public final InventoryItem resultItem;
   private GNDItemMap gndData;
   private InventoryItem drawItem;
   private int drawAmount;
   protected String sortResultID;
   protected boolean sortBefore;
   protected ArrayList<Consumer<RecipeCraftedEvent>> craftedListeners;

   public Recipe(String var1, Tech var2, Ingredient[] var3) {
      this(var1, 1, var2, var3);
   }

   public Recipe(String var1, int var2, Tech var3, Ingredient[] var4) {
      this(var1, var2, var3, var4, false);
   }

   public Recipe(String var1, int var2, Tech var3, Ingredient[] var4, boolean var5) {
      this(var1, var2, var3, var4, var5, (GNDItemMap)null);
   }

   public Recipe(String var1, int var2, Tech var3, Ingredient[] var4, boolean var5, GNDItemMap var6) {
      this.craftedListeners = new ArrayList();
      this.resultID = var1;
      Item var7 = ItemRegistry.getItem(var1);
      if (var7 == null) {
         throw new NullPointerException("Could not find recipe result item: " + var1);
      } else {
         if (var2 <= 0) {
            var2 = 1;
         }

         this.resultAmount = var2;
         HashSet var8 = new HashSet();
         Ingredient[] var9 = var4;
         int var10 = var4.length;

         for(int var11 = 0; var11 < var10; ++var11) {
            Ingredient var12 = var9[var11];
            if (var8.contains(var12.ingredientStringID)) {
               throw new IllegalArgumentException("Duplicate ingredient '" + var12.ingredientStringID + "'");
            }

            var8.add(var12.ingredientStringID);
         }

         this.ingredients = var4;
         this.tech = var3;
         this.isHidden = var5;
         this.resultItem = new InventoryItem(var7, var2);
         this.drawItem = new InventoryItem(var7);
         this.drawAmount = var2;
         if (var6 == null) {
            var6 = new GNDItemMap();
         }

         this.gndData = var6;
         this.resultItem.setGndData(var6.copy());
         this.drawItem.setGndData(var6.copy());
      }
   }

   public static Recipe fromScript(String var0) {
      return (new RecipeData(new LoadData(var0))).validate();
   }

   public Recipe showBefore(String var1) {
      if (var1 != null && ItemRegistry.getItemID(var1) == -1) {
         throw new IllegalArgumentException("No item found with stringID " + var1);
      } else {
         this.sortResultID = var1;
         this.sortBefore = true;
         return this;
      }
   }

   public Recipe showAfter(String var1) {
      if (var1 != null && ItemRegistry.getItemID(var1) == -1) {
         throw new IllegalArgumentException("No item found with stringID " + var1);
      } else {
         this.sortResultID = var1;
         this.sortBefore = false;
         return this;
      }
   }

   public boolean shouldShowBefore(Recipe var1) {
      return this.sortBefore && var1.resultID.equals(this.sortResultID);
   }

   public boolean shouldShowAfter(Recipe var1) {
      return !this.sortBefore && var1.resultID.equals(this.sortResultID);
   }

   public boolean shouldBeSorted() {
      return this.sortResultID != null;
   }

   protected void setShowItem(String var1, int var2) {
      this.drawItem = new InventoryItem(var1 != null && !var1.equals("") ? var1 : this.resultID);
      this.drawAmount = var2;
   }

   public boolean matchTech(Tech var1) {
      return var1 == RecipeTechRegistry.ALL || this.tech == var1;
   }

   public boolean matchesTechs(Tech... var1) {
      return Arrays.stream(var1).anyMatch(this::matchTech);
   }

   public GNDItemMap getGndData() {
      return this.gndData;
   }

   public int getRecipeHash() {
      int var1 = 1;
      var1 = var1 * 19 + this.resultID.hashCode();
      var1 = var1 * 37 + this.tech.getStringID().hashCode();
      var1 = var1 * 29 + this.resultAmount;
      Ingredient[] var2 = this.ingredients;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Ingredient var5 = var2[var4];
         var1 = var1 * 13 + var5.getIngredientHash();
      }

      for(Iterator var6 = this.gndData.getKeySet().iterator(); var6.hasNext(); var1 = var1 * 23 + this.gndData.getItem(var3).toString().hashCode()) {
         var3 = (Integer)var6.next();
         var1 = var1 * 43 + var3;
      }

      if (this.sortResultID != null) {
         var1 = var1 * 31 + this.sortResultID.hashCode();
         var1 = var1 * 43 + (this.sortBefore ? 1 : 2);
      } else {
         var1 *= 41;
      }

      return var1;
   }

   public boolean doesShow(Level var1, PlayerMob var2, Inventory var3) {
      return this.doesShow(var1, var2, (Iterable)Collections.singletonList(var3));
   }

   public boolean doesShow(Level var1, PlayerMob var2, Iterable<Inventory> var3) {
      return this.doesShowRange(var1, var2, () -> {
         return new MapIterator(var3.iterator(), InventoryRange::new);
      });
   }

   public boolean doesShowRange(Level var1, PlayerMob var2, InventoryRange var3) {
      return this.doesShowRange(var1, var2, (Iterable)Collections.singletonList(var3));
   }

   public boolean doesShowRange(Level var1, PlayerMob var2, Iterable<InventoryRange> var3) {
      if (this.isHidden) {
         return this.canCraftRange(var1, var2, var3, false).canCraft();
      } else {
         Ingredient[] var4 = this.ingredients;
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Ingredient var7 = var4[var6];
            if (var7.requiredToShow() && !var7.hasIngredientRange(var1, var2, var3)) {
               return false;
            }
         }

         return true;
      }
   }

   public CanCraft canCraft(Level var1, PlayerMob var2, Inventory var3, boolean var4) {
      return this.canCraft(var1, var2, (Iterable)Collections.singletonList(var3), var4);
   }

   public CanCraft canCraft(Level var1, PlayerMob var2, Iterable<Inventory> var3, boolean var4) {
      return this.canCraftRange(var1, var2, () -> {
         return new MapIterator(var3.iterator(), InventoryRange::new);
      }, var4);
   }

   public CanCraft canCraftRange(Level var1, PlayerMob var2, InventoryRange var3, boolean var4) {
      return this.canCraftRange(var1, var2, (Iterable)Collections.singletonList(var3), var4);
   }

   public CanCraft canCraftRange(Level var1, PlayerMob var2, Iterable<InventoryRange> var3, boolean var4) {
      CanCraft var5 = new CanCraft(this, var4);
      HashMapSet var6 = new HashMapSet();
      LinkedList var7 = new LinkedList();
      LinkedList var8 = new LinkedList();

      for(int var9 = this.ingredients.length - 1; var9 >= 0; --var9) {
         Ingredient var10 = this.ingredients[var9];
         if (var10.getIngredientAmount() == 0) {
            var8.add(new ObjectValue(var9, var10));
         } else if (var10.isGlobalIngredient()) {
            var7.addLast(new ObjectValue(var9, var10));
         } else {
            var7.addFirst(new ObjectValue(var9, var10));
         }
      }

      Iterator var13 = var8.iterator();

      while(var13.hasNext()) {
         ObjectValue var11 = (ObjectValue)var13.next();
         var7.addFirst(var11);
      }

      var13 = var3.iterator();

      while(var13.hasNext()) {
         InventoryRange var12 = (InventoryRange)var13.next();
         if (var12.inventory.canBeUsedForCrafting()) {
            var12.inventory.countIngredientAmount(var1, var2, var12.startSlot, var12.endSlot, (var3x, var4x, var5x) -> {
               if (var3x == null || var3x.canBeUsedForCrafting()) {
                  if (!((HashSet)var6.get(var3x)).contains(var4x)) {
                     int var6x = 0;
                     Iterator var7x = var7.iterator();

                     while(var7x.hasNext()) {
                        ObjectValue var8 = (ObjectValue)var7x.next();
                        int var9 = (Integer)var8.object;
                        Ingredient var10 = (Ingredient)var8.value;
                        if (var10.matchesItem(var5x.item)) {
                           int var11 = var5x.getAmount() - var6x;
                           int var12 = var5.countAllIngredients ? var11 : Math.min(var11, var10.getIngredientAmount());
                           var5.addIngredient(var9, var12);
                           var6x += var12;
                           if (!var5.countAllIngredients && var6x >= var5x.getAmount()) {
                              break;
                           }
                        }
                     }

                     var6.add(var3x, var4x);
                  }
               }
            });
         }
      }

      return var5;
   }

   public ArrayList<InventoryItemsRemoved> craft(Level var1, PlayerMob var2, Inventory var3) {
      return this.craft(var1, var2, (Iterable)Collections.singletonList(var3));
   }

   public ArrayList<InventoryItemsRemoved> craft(Level var1, PlayerMob var2, Iterable<Inventory> var3) {
      return this.craftRange(var1, var2, () -> {
         return new MapIterator(var3.iterator(), InventoryRange::new);
      });
   }

   public ArrayList<InventoryItemsRemoved> craftRange(Level var1, PlayerMob var2, InventoryRange var3) {
      return this.craftRange(var1, var2, (Iterable)Collections.singletonList(var3));
   }

   public ArrayList<InventoryItemsRemoved> craftRange(Level var1, PlayerMob var2, Iterable<InventoryRange> var3) {
      ArrayList var4 = new ArrayList();
      Ingredient[] var5 = this.ingredients;
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         Ingredient var8 = var5[var7];
         if (var8.getIngredientAmount() > 0) {
            int var9 = var8.getIngredientAmount();
            Iterator var10 = var3.iterator();

            while(var10.hasNext()) {
               InventoryRange var11 = (InventoryRange)var10.next();
               if (var11.inventory.canBeUsedForCrafting()) {
                  if (var9 <= 0) {
                     break;
                  }

                  int var12 = var11.inventory.removeItems(var1, var2, (Ingredient)var8, var9, var11.startSlot, var11.endSlot, (Collection)var4);
                  var9 -= var12;
               }
            }
         }
      }

      return var4;
   }

   public void submitCraftedEvent(RecipeCraftedEvent var1) {
      for(int var2 = 0; var2 < this.craftedListeners.size(); ++var2) {
         ((Consumer)this.craftedListeners.get(var2)).accept(var1);
      }

   }

   public Recipe onCrafted(Consumer<RecipeCraftedEvent> var1) {
      this.craftedListeners.add(var1);
      return this;
   }

   public void draw(int var1, int var2, PlayerMob var3) {
      this.drawItem.draw(var3, var1, var2, false);
      int var4 = this.drawAmount >= 1 ? this.drawAmount : this.resultAmount;
      if (var4 > 1) {
         String var5 = "" + var4;
         if (var4 > 999) {
            var5 = "999+";
         }

         int var6 = FontManager.bit.getWidthCeil(var5, Item.tipFontOptions);
         FontManager.bit.drawString((float)(var1 + 28 - var6), (float)(var2 + 2), var5, Item.tipFontOptions);
      }

   }

   public GameTooltips getTooltip(PlayerMob var1, GameBlackboard var2) {
      return this.getTooltip((int[])null, false, var1, var2);
   }

   public GameTooltips getTooltip(CanCraft var1, PlayerMob var2, GameBlackboard var3) {
      if (var1 == null) {
         var1 = CanCraft.allTrue(this);
      }

      return this.getTooltip(var1.haveIngredients, var1.countAllIngredients, var2, var3);
   }

   public GameTooltips getTooltip(int[] var1, boolean var2, PlayerMob var3, GameBlackboard var4) {
      ListGameTooltips var5 = new ListGameTooltips();
      var5.add((Object)this.getResultItemTooltip(var3, var4));
      var5.add((Object)(new SpacerGameTooltip(10)));
      if (this.resultAmount == 1) {
         var5.add(Localization.translate("misc", "recipecostsing"));
      } else {
         var5.add(Localization.translate("misc", "recipecostmult", "amount", (Object)this.resultAmount));
      }

      for(int var6 = 0; var6 < this.ingredients.length; ++var6) {
         Ingredient var7 = this.ingredients[var6];
         var5.add((Object)var7.getTooltips(var1 == null ? var7.getIngredientAmount() : var1[var6], var2));
      }

      return var5;
   }

   public GameTooltips getResultItemTooltip(PlayerMob var1, GameBlackboard var2) {
      return this.resultItem.getTooltip(var1, var2);
   }

   public Packet getContentPacket() {
      Packet var1 = new Packet();
      PacketWriter var2 = new PacketWriter(var1);
      var2.putNextShortUnsigned(ItemRegistry.getItemID(this.resultID));
      var2.putNextShortUnsigned(this.resultAmount);
      Packet var3 = this.gndData == null ? new Packet() : this.gndData.getContentPacket();
      var2.putNextContentPacket(var3);
      var2.putNextByteUnsigned(this.tech.getID());
      var2.putNextBoolean(this.isHidden);
      var2.putNextBoolean(this.sortResultID != null);
      if (this.sortResultID != null) {
         var2.putNextShortUnsigned(ItemRegistry.getItemID(this.sortResultID));
         var2.putNextBoolean(this.sortBefore);
      }

      var2.putNextByteUnsigned(this.ingredients.length);
      Ingredient[] var4 = this.ingredients;
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Ingredient var7 = var4[var6];
         var7.writePacket(var2);
      }

      return var1;
   }

   public static Recipe getRecipeFromContentPacket(Packet var0) {
      PacketReader var1 = new PacketReader(var0);
      String var2 = ItemRegistry.getItem(var1.getNextShortUnsigned()).getStringID();
      int var3 = var1.getNextShortUnsigned();
      GNDItemMap var4 = new GNDItemMap(var1.getNextContentPacket());
      Tech var5 = RecipeTechRegistry.getTech(var1.getNextByteUnsigned());
      boolean var6 = var1.getNextBoolean();
      String var7 = null;
      boolean var8 = false;
      if (var1.getNextBoolean()) {
         var7 = ItemRegistry.getItem(var1.getNextShortUnsigned()).getStringID();
         var8 = var1.getNextBoolean();
      }

      int var9 = var1.getNextByteUnsigned();
      Ingredient[] var10 = new Ingredient[var9];

      for(int var11 = 0; var11 < var9; ++var11) {
         var10[var11] = new Ingredient(var1);
      }

      Recipe var12 = new Recipe(var2, var3, var5, var10, var6, var4);
      var12.sortResultID = var7;
      var12.sortBefore = var8;
      return var12;
   }
}
