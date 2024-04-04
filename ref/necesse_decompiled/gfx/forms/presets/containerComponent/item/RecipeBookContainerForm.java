package necesse.gfx.forms.presets.containerComponent.item;

import java.awt.Rectangle;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.Input;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.client.Client;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.containerSlot.FormContainerMaterialSlot;
import necesse.gfx.forms.components.lists.FormIngredientRecipeList;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.presets.containerComponent.ContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.InputTooltip;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.item.RecipeBookContainer;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;

public class RecipeBookContainerForm<T extends RecipeBookContainer> extends ContainerForm<T> {
   public FormTextInput searchFilter;
   public FormContainerMaterialSlot ingredientSlot;
   public FormIngredientRecipeList ingredientList;
   public int itemID;

   public RecipeBookContainerForm(Client var1, final T var2) {
      super(var1, 400, 320, var2);
      InventoryItem var3 = var2.guideSlot.getItem(var2.client.playerMob.getInv());
      this.addComponent(new FormLocalLabel((GameMessage)(var3 == null ? new StaticMessage("NULL") : var3.getItemLocalization()), new FontOptions(20), -1, 10, 10));
      this.addComponent(this.ingredientSlot = new FormContainerMaterialSlot(var1, var2, var2.INGREDIENT_SLOT, this.getWidth() - 60, this.getHeight() - 50));
      this.addComponent(this.ingredientList = new FormIngredientRecipeList(6, this.getHeight() - 280 - 6, this.getWidth() - 6, this.getHeight() - 80, var1) {
         public void onRecipeClicked(Recipe var1, InputEvent var2x) {
            var2.clearingIngredientSlot.runAndSend();
            RecipeBookContainerForm.this.ingredientSlot.ghostItem = var1.resultItem;
            RecipeBookContainerForm.this.searchFilter.setText("");
            this.playTickSound();
            ControllerInput.submitNextRefreshFocusEvent();
         }

         public void addTooltips(Recipe var1, ListGameTooltips var2x) {
            super.addTooltips(var1, var2x);
            if (Input.lastInputIsController) {
               var2x.add((Object)(new InputTooltip(ControllerInput.MENU_SELECT, Localization.translate("controls", "inspecttip"))));
            } else {
               var2x.add((Object)(new InputTooltip(-100, Localization.translate("controls", "inspecttip"))));
            }

         }
      });
      this.searchFilter = (FormTextInput)this.addComponent(new FormTextInput(26, this.getHeight() - 50, FormInputSize.SIZE_32_TO_40, this.getWidth() - 106, this.getHeight() - 20));
      this.searchFilter.placeHolder = new LocalMessage("ui", "searchtip");
      this.searchFilter.rightClickToClear = true;
      this.searchFilter.onChange((var1x) -> {
         this.updateFilter();
      });
      this.updateFilter();
   }

   public void updateFilter() {
      Stream var1;
      if (this.itemID == -1) {
         var1 = Recipes.streamRecipes();
      } else {
         var1 = Recipes.getResultItems(this.itemID).stream();
      }

      String var2 = this.searchFilter.getText();
      var1 = var1.filter((var2x) -> {
         if (var2x.resultItem.item.matchesSearch(var2x.resultItem, this.client.getPlayer(), var2)) {
            return true;
         } else if (var2x.tech.displayName.translate().toLowerCase().contains(var2)) {
            return true;
         } else {
            Ingredient[] var3 = var2x.ingredients;
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               Ingredient var6 = var3[var5];
               if (var6.matchesSearch(this.client.getPlayer(), var2)) {
                  return true;
               }
            }

            return false;
         }
      });
      this.ingredientList.setRecipes((Collection)var1.collect(Collectors.toList()));
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      InventoryItem var4 = this.ingredientSlot.ghostItem != null ? this.ingredientSlot.ghostItem : this.ingredientSlot.getContainerSlot().getItem();
      int var5 = var4 == null ? -1 : var4.item.getID();
      if (this.itemID != var5) {
         this.itemID = var5;
         this.updateFilter();
      }

      super.draw(var1, var2, var3);
   }
}
