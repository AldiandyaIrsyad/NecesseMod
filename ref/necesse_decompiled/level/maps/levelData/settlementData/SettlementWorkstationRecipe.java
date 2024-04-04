package necesse.level.maps.levelData.settlementData;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.settlement.events.SettlementWorkstationRecipeUpdateEvent;
import necesse.inventory.item.Item;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;

public class SettlementWorkstationRecipe {
   public final int uniqueID;
   public String name;
   public Recipe recipe;
   public ItemCategoriesFilter ingredientFilter;
   public Mode mode;
   public int modeCount;

   public SettlementWorkstationRecipe(int var1, Recipe var2) {
      this.uniqueID = var1;
      this.name = null;
      this.recipe = var2;
      this.refreshIngredientFilter();
      this.mode = SettlementWorkstationRecipe.Mode.DO_UNTIL;
      this.modeCount = var2.resultItem.itemStackSize();
   }

   private void refreshIngredientFilter() {
      this.ingredientFilter = new ItemCategoriesFilter(true) {
         public boolean isItemDisabled(Item var1) {
            if (super.isItemDisabled(var1)) {
               return true;
            } else {
               Recipe var2 = SettlementWorkstationRecipe.this.recipe;
               Ingredient[] var3 = var2.ingredients;
               int var4 = var3.length;

               for(int var5 = 0; var5 < var4; ++var5) {
                  Ingredient var6 = var3[var5];
                  if (var6.isGlobalIngredient() && var6.matchesItem(var1)) {
                     return false;
                  }
               }

               return true;
            }
         }
      };
   }

   public void addSaveData(SaveData var1, boolean var2) {
      if (var2) {
         var1.addInt("uniqueID", this.uniqueID);
      }

      if (this.name != null) {
         var1.addSafeString("name", this.name);
      }

      var1.addInt("recipeHash", this.recipe.getRecipeHash());
      if (!this.ingredientFilter.master.isAllAllowed()) {
         SaveData var3 = new SaveData("ingredientFilter");
         this.ingredientFilter.addSaveData(var3);
         var1.addSaveData(var3);
      }

      var1.addEnum("mode", this.mode);
      var1.addInt("modeCount", this.modeCount);
   }

   public SettlementWorkstationRecipe(LoadData var1, boolean var2) throws LoadDataException {
      if (var2) {
         this.uniqueID = var1.getInt("uniqueID", -1);
         if (this.uniqueID == -1) {
            throw new LoadDataException("Missing recipe uniqueID");
         }
      } else {
         this.uniqueID = -1;
      }

      this.name = var1.getSafeString("name", (String)null, false);
      int var3 = var1.getInt("recipeHash", -1);
      if (var3 == -1) {
         throw new LoadDataException("Missing recipe hash");
      } else {
         this.recipe = this.findRecipe(var3);
         if (this.recipe == null) {
            throw new LoadDataException("Could not find recipe with hash " + var3);
         } else {
            this.refreshIngredientFilter();
            LoadData var4 = var1.getFirstLoadDataByName("ingredientFilter");
            if (var4 != null && var4.isArray()) {
               this.ingredientFilter.applyLoadData(var4);
            }

            this.mode = (Mode)var1.getEnum(Mode.class, "mode", SettlementWorkstationRecipe.Mode.DO_COUNT, false);
            this.modeCount = var1.getInt("modeCount", 1, 0, 65535, false);
         }
      }
   }

   public void writePacket(PacketWriter var1) {
      var1.putNextBoolean(this.name != null);
      if (this.name != null) {
         var1.putNextString(this.name);
      }

      var1.putNextInt(this.recipe.getRecipeHash());
      this.ingredientFilter.writePacket(var1);
      var1.putNextByteUnsigned(this.mode.ordinal());
      var1.putNextShortUnsigned(this.modeCount);
   }

   public SettlementWorkstationRecipe(int var1, PacketReader var2) throws LoadDataException {
      this.uniqueID = var1;
      this.applyPacket(var2);
   }

   public void applyPacket(PacketReader var1) throws LoadDataException {
      if (var1.getNextBoolean()) {
         this.name = var1.getNextString();
      } else {
         this.name = null;
      }

      int var2 = var1.getNextInt();
      this.recipe = this.findRecipe(var2);
      if (this.recipe == null) {
         throw new LoadDataException("Could not find recipe with hash " + var2);
      } else {
         this.refreshIngredientFilter();
         this.ingredientFilter.readPacket(var1);
         this.mode = SettlementWorkstationRecipe.Mode.values()[var1.getNextByteUnsigned()];
         this.modeCount = var1.getNextShortUnsigned();
      }
   }

   private Recipe findRecipe(int var1) {
      Iterator var2 = Recipes.getRecipes().iterator();

      Recipe var3;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         var3 = (Recipe)var2.next();
      } while(var3.getRecipeHash() != var1);

      return var3;
   }

   public boolean canConfigureIngredientFilter() {
      return Arrays.stream(this.recipe.ingredients).anyMatch(Ingredient::isGlobalIngredient);
   }

   public boolean canUseItem(Ingredient var1, InventoryItem var2) {
      if (!var1.matchesItem(var2.item)) {
         return false;
      } else {
         return var1.isGlobalIngredient() ? this.ingredientFilter.isItemAllowed(var2.item) : true;
      }
   }

   public void onCrafted(SettlementWorkstation var1, int var2) {
      if (this.mode == SettlementWorkstationRecipe.Mode.DO_COUNT) {
         this.modeCount = Math.max(0, this.modeCount - var2);
      }

      SettlementWorkstationLevelObject var3 = var1.getWorkstationObject();
      if (var3 != null) {
         var3.onCraftFinished(this.recipe);
      }

      (new SettlementWorkstationRecipeUpdateEvent(var1.tileX, var1.tileY, this)).applyAndSendToClientsAt(var1.data.getLevel());
   }

   public static enum Mode {
      DO_COUNT((var0) -> {
         return new LocalMessage("ui", "recipedocount", "count", var0);
      }),
      DO_UNTIL((var0) -> {
         return new LocalMessage("ui", "recipedountil", "count", var0);
      }),
      DO_FOREVER((var0) -> {
         return new LocalMessage("ui", "recipedoforever", "count", var0);
      });

      public final Function<String, GameMessage> countMessageFunction;

      private Mode(Function var3) {
         this.countMessageFunction = var3;
      }

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{DO_COUNT, DO_UNTIL, DO_FOREVER};
      }
   }
}
