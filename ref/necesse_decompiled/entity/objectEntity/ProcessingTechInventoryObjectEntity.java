package necesse.entity.objectEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.presets.containerComponent.object.ProcessingHelp;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryAddConsumer;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.inventory.item.Item;
import necesse.inventory.recipe.CanCraft;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.ProcessingTechRecipeCraftedEvent;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;
import necesse.inventory.recipe.Tech;
import necesse.level.maps.Level;

public abstract class ProcessingTechInventoryObjectEntity extends ProcessingInventoryObjectEntity {
   public Tech[] techs;
   private int expectedCrafts;
   private ArrayList<InventoryItem> expectedResultItems;
   private TechProcessingHelp help = new TechProcessingHelp();

   public ProcessingTechInventoryObjectEntity(Level var1, String var2, int var3, int var4, int var5, int var6, Tech... var7) {
      super(var1, var2, var3, var4, var5, var6);
      this.techs = var7;
   }

   public boolean isValidInputItem(InventoryItem var1) {
      return var1 == null ? false : Recipes.streamRecipes(this.techs).anyMatch((var1x) -> {
         Ingredient[] var2 = var1x.ingredients;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Ingredient var5 = var2[var4];
            if (var5.matchesItem(var1.item)) {
               return true;
            }
         }

         return false;
      });
   }

   protected void onSlotUpdate(int var1) {
      super.onSlotUpdate(var1);
      this.help.forceUpdate = true;
      this.expectedResultItems = null;
   }

   public FutureCrafts getExpectedResults() {
      if (this.expectedResultItems == null) {
         this.expectedCrafts = 0;
         this.expectedResultItems = new ArrayList();
         Inventory var1 = this.inventory.copy();

         for(int var2 = 0; var2 < 1000; ++var2) {
            boolean var3 = false;

            for(int var4 = this.inputSlots - 1; var4 >= 0; --var4) {
               InventoryRange var5 = new InventoryRange(var1, var4, this.inputSlots - 1);
               Iterator var6 = Recipes.getRecipes(this.techs).iterator();

               while(var6.hasNext()) {
                  Recipe var7 = (Recipe)var6.next();
                  if (var7.canCraftRange(this.getLevel(), (PlayerMob)null, (InventoryRange)var5, false).canCraft()) {
                     InventoryItem var8 = var7.resultItem.copy(var7.resultAmount);
                     var8.combineOrAddToList(this.getLevel(), (PlayerMob)null, this.expectedResultItems, "add");
                     var7.craftRange(this.getLevel(), (PlayerMob)null, (InventoryRange)var5);
                     ++this.expectedCrafts;
                     var3 = true;
                     break;
                  }
               }

               if (var3) {
                  break;
               }
            }

            if (!var3) {
               break;
            }
         }
      }

      ArrayList var9 = new ArrayList(this.expectedResultItems.size());
      Iterator var10 = this.expectedResultItems.iterator();

      while(var10.hasNext()) {
         InventoryItem var11 = (InventoryItem)var10.next();
         var9.add(var11.copy());
      }

      return new FutureCrafts(this.expectedCrafts, var9);
   }

   public FutureCrafts getCurrentAndExpectedResults() {
      FutureCrafts var1 = this.getExpectedResults();

      for(int var2 = this.inputSlots; var2 < this.inventory.getSize(); ++var2) {
         InventoryItem var3 = this.inventory.getItem(var2);
         if (var3 != null) {
            var3.copy().combineOrAddToList(this.getLevel(), (PlayerMob)null, var1.items, "add");
         }
      }

      return var1;
   }

   public boolean canProcessInput() {
      InventoryRange var1 = new InventoryRange(this.inventory, 0, this.inputSlots - 1);
      return Recipes.streamRecipes(this.techs).anyMatch((var2) -> {
         return var2.canCraftRange(this.getLevel(), (PlayerMob)null, (InventoryRange)var1, false).canCraft();
      });
   }

   public boolean processInput() {
      for(int var1 = this.inputSlots - 1; var1 >= 0; --var1) {
         InventoryRange var2 = new InventoryRange(this.inventory, var1, this.inputSlots - 1);
         Iterator var3 = Recipes.getRecipes(this.techs).iterator();

         while(var3.hasNext()) {
            Recipe var4 = (Recipe)var3.next();
            if (var4.canCraftRange(this.getLevel(), (PlayerMob)null, (InventoryRange)var2, false).canCraft() && this.canAddOutput(new InventoryItem[]{var4.resultItem.copy(var4.resultAmount)})) {
               ProcessingTechRecipeCraftedEvent var5 = new ProcessingTechRecipeCraftedEvent(var4, var4.craftRange(this.getLevel(), (PlayerMob)null, (InventoryRange)var2), this);
               var4.submitCraftedEvent(var5);
               if (var5.resultItem != null) {
                  this.addOutput(new InventoryItem[]{var5.resultItem});
               }

               return true;
            }
         }
      }

      return false;
   }

   public ProcessingHelp getProcessingHelp() {
      return this.help;
   }

   public void onMouseHover(PlayerMob var1, boolean var2) {
      super.onMouseHover(var1, var2);
      if (var2) {
         FutureCrafts var3 = this.getCurrentAndExpectedResults();
         if (var3.crafts > 0 || !var3.items.isEmpty()) {
            StringTooltips var4 = new StringTooltips("Expected results from " + var3.crafts + " crafts:");
            Iterator var5 = var3.items.iterator();

            while(var5.hasNext()) {
               InventoryItem var6 = (InventoryItem)var5.next();
               var4.add("  " + var6.getAmount() + "x " + var6.getItemDisplayName());
            }

            Screen.addTooltip(var4, TooltipLocation.INTERACT_FOCUS);
         }
      }

   }

   private class TechProcessingHelp extends ProcessingHelp {
      public boolean forceUpdate;
      public boolean lastShowIngredientsAvailable;
      public Recipe currentRecipe;
      public CanCraft currentRecipeCanCraft;
      public HashSet<Integer> showRecipeTooltip;
      public HashMap<Integer, Ingredient> inputGhostItems;
      public HashMap<Integer, InventoryItem> outputGhostItems;

      private TechProcessingHelp() {
         this.forceUpdate = true;
         this.lastShowIngredientsAvailable = Settings.showIngredientsAvailable;
         this.showRecipeTooltip = new HashSet();
         this.inputGhostItems = new HashMap();
         this.outputGhostItems = new HashMap();
      }

      public void update() {
         this.forceUpdate = false;
         this.lastShowIngredientsAvailable = Settings.showIngredientsAvailable;
         this.currentRecipe = null;
         this.currentRecipeCanCraft = null;
         this.showRecipeTooltip.clear();
         this.inputGhostItems.clear();
         this.outputGhostItems.clear();
         InventoryRange var1 = ProcessingTechInventoryObjectEntity.this.getInputInventoryRange();

         int var2;
         InventoryRange var3;
         for(var2 = var1.endSlot; var2 >= var1.startSlot; --var2) {
            var3 = new InventoryRange(ProcessingTechInventoryObjectEntity.this.inventory, var2, var1.endSlot);
            Iterator var4 = Recipes.getRecipes(ProcessingTechInventoryObjectEntity.this.techs).iterator();

            while(var4.hasNext()) {
               Recipe var5 = (Recipe)var4.next();
               CanCraft var6 = var5.canCraftRange(ProcessingTechInventoryObjectEntity.this.getLevel(), (PlayerMob)null, (InventoryRange)var3, ProcessingTechInventoryObjectEntity.this.isClient());
               if (var6.canCraft()) {
                  this.currentRecipe = var5;
                  this.currentRecipeCanCraft = var6;
                  break;
               }
            }

            if (this.currentRecipe != null) {
               break;
            }
         }

         if (this.currentRecipe == null) {
            Iterator var7 = Recipes.getRecipes(ProcessingTechInventoryObjectEntity.this.techs).iterator();

            while(var7.hasNext()) {
               Recipe var8 = (Recipe)var7.next();
               CanCraft var11 = var8.canCraftRange(ProcessingTechInventoryObjectEntity.this.getLevel(), (PlayerMob)null, (InventoryRange)var1, ProcessingTechInventoryObjectEntity.this.isClient());
               if (var11.hasAnyItems()) {
                  this.currentRecipe = var8;
                  this.currentRecipeCanCraft = var11;
                  break;
               }
            }
         }

         if (this.currentRecipe != null) {
            int var13;
            InventoryItem var15;
            if (!this.currentRecipeCanCraft.hasAnyOfAllItems()) {
               for(var2 = 0; var2 < this.currentRecipe.ingredients.length; ++var2) {
                  if (!this.currentRecipeCanCraft.hasAnyIngredients(var2)) {
                     Ingredient var10 = this.currentRecipe.ingredients[var2];
                     boolean var12 = false;

                     for(var13 = var1.startSlot; var13 <= var1.endSlot; ++var13) {
                        var15 = var1.inventory.getItem(var13);
                        if (var15 != null && var10.matchesItem(var15.item)) {
                           this.inputGhostItems.put(var13, var10);
                           var12 = true;
                           break;
                        }
                     }

                     if (!var12) {
                        for(var13 = var1.startSlot; var13 <= var1.endSlot; ++var13) {
                           if (var1.inventory.isSlotClear(var13)) {
                              this.inputGhostItems.put(var13, var10);
                              this.showRecipeTooltip.add(var13);
                              break;
                           }
                        }
                     }
                  }
               }
            }

            if (this.currentRecipe != null) {
               InventoryItem var9 = this.currentRecipe.resultItem.copy(this.currentRecipe.resultAmount);
               var3 = ProcessingTechInventoryObjectEntity.this.getOutputInventoryRange();
               Inventory var14 = ProcessingTechInventoryObjectEntity.this.inventory.copy();
               var14.addItem(ProcessingTechInventoryObjectEntity.this.getLevel(), (PlayerMob)null, var9, var3.startSlot, var3.endSlot, false, "add", true, false, (InventoryAddConsumer)null);

               for(var13 = var3.startSlot; var13 <= var3.endSlot; ++var13) {
                  var15 = var14.getItem(var13);
                  if (var15 != null && var15.equals(ProcessingTechInventoryObjectEntity.this.getLevel(), this.currentRecipe.resultItem.copy(this.currentRecipe.resultAmount), true, false, "equals")) {
                     this.outputGhostItems.put(var13, this.currentRecipe.resultItem);
                     this.showRecipeTooltip.add(var13);
                  }
               }
            }
         }

      }

      public boolean isProcessing() {
         return ProcessingTechInventoryObjectEntity.this.isProcessing();
      }

      public float getProcessingProgress() {
         return ProcessingTechInventoryObjectEntity.this.getProcessingProgress();
      }

      public boolean needsFuel() {
         return false;
      }

      public InventoryItem getGhostItem(int var1) {
         if (this.shouldUpdate()) {
            this.update();
         }

         InventoryItem var2 = (InventoryItem)this.outputGhostItems.get(var1);
         if (var2 != null) {
            return var2;
         } else {
            Ingredient var3 = (Ingredient)this.inputGhostItems.get(var1);
            if (var3 != null) {
               Item var4 = var3.getDisplayItem();
               if (var4 != null) {
                  return var4.getDefaultItem((PlayerMob)null, 1);
               }
            }

            return null;
         }
      }

      public GameTooltips getTooltip(int var1, PlayerMob var2) {
         if (this.shouldUpdate()) {
            this.update();
         }

         return this.showRecipeTooltip.contains(var1) && this.currentRecipe != null ? this.currentRecipe.getTooltip(this.currentRecipeCanCraft, var2, new GameBlackboard()) : null;
      }

      public GameTooltips getCurrentRecipeTooltip(PlayerMob var1) {
         if (this.shouldUpdate()) {
            this.update();
         }

         return this.currentRecipe != null ? this.currentRecipe.getTooltip(this.currentRecipeCanCraft, var1, new GameBlackboard()) : null;
      }

      public boolean shouldUpdate() {
         if (this.forceUpdate) {
            return true;
         } else {
            return Settings.showIngredientsAvailable != this.lastShowIngredientsAvailable;
         }
      }

      // $FF: synthetic method
      TechProcessingHelp(Object var2) {
         this();
      }
   }

   public static class FutureCrafts {
      public final int crafts;
      public final ArrayList<InventoryItem> items;

      public FutureCrafts(int var1, ArrayList<InventoryItem> var2) {
         this.crafts = var1;
         this.items = var2;
      }
   }
}
